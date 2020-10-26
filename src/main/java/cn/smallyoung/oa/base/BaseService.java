package cn.smallyoung.oa.base;

import cn.hutool.core.util.IdUtil;
import cn.smallyoung.oa.base.specification.SimpleSpecificationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.Id;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author yangn
 */

public abstract class BaseService<T, ID extends Serializable> {

    @Autowired
    public BaseDao<T, ID> baseDao;

    public T findOne(ID id) {
        return baseDao.getOne(id);
    }

    public Optional<T> findById(ID id) {
        return baseDao.findById(id);
    }

    public List<T> findAll() {
        return baseDao.findAll();
    }

    public List<T> findAll(Map<String, Object> map) {
        return baseDao.findAll(new SimpleSpecificationBuilder<T>(map).getSpecification());
    }

    public List<T> findAll(Map<String, Object> map, Sort sort) {
        return baseDao.findAll(new SimpleSpecificationBuilder<T>(map).getSpecification(), sort);
    }

    public Page<T> findAll(Pageable pageable) {
        return baseDao.findAll(pageable);
    }

    public Page<T> findAll(Map<String, Object> map, Pageable pageable) {
        return baseDao.findAll(new SimpleSpecificationBuilder<T>(map).getSpecification(), pageable);
    }

    public T save(T t) {
        setId(t);
        return baseDao.save(t);
    }

    public <S extends T> List<S> save(Iterable<S> s) {
        s.forEach(this::setId);
        return baseDao.saveAll(s);
    }

    public void delete(ID id) {
        baseDao.deleteById(id);
    }

    public void delete(T t) {
        baseDao.delete(t);
    }

    public void delete(Iterable<? extends T> t) {
        baseDao.deleteAll(t);
    }

    public void deleteAll() {
        baseDao.deleteAll();
    }

    public void setId(T t) {
        Optional<Field> fieldOptional = Stream.of(t.getClass().getDeclaredFields())
                .filter(f -> f.getAnnotation(Id.class) != null && "String".equals(f.getType().getSimpleName())).findFirst();
        if(fieldOptional.isPresent()){
            try {
                Field field = fieldOptional.get();
                field.setAccessible(true);
                if (field.get(t) == null) {
                    field.set(t, IdUtil.objectId());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}