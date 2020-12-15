package cn.smallyoung.oa.service;

import cn.hutool.core.util.NumberUtil;
import cn.smallyoung.oa.base.BaseService;
import cn.smallyoung.oa.dao.IndexDao;
import cn.smallyoung.oa.entity.Index;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @author smallyoung
 * @data 2020/12/15
 */
@Service
@Transactional(readOnly = true)
public class IndexService extends BaseService<Index, String> {

    @Resource
    private IndexDao indexDao;

    @Transactional(rollbackFor = Exception.class)
    public Long getIndex(String name){
        String years = LocalDate.now().getYear()+"";
        Index index = indexDao.findByNameAndYears(name, years);
        long maxId = 1L;
        if(index != null){
            maxId = index.getMaxId() + 1;
            index.setMaxId(maxId);
        }
        if(index == null){
            index = new Index();
            index.setName(name);
            index.setMaxId(1L);
            index.setYears(years);
        }
        indexDao.save(index);
        String idStr = years + NumberUtil.decimalFormat("00000", maxId);
        return Long.parseLong(idStr);
    }
}
