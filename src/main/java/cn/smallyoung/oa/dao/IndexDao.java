package cn.smallyoung.oa.dao;

import cn.smallyoung.oa.base.BaseDao;
import cn.smallyoung.oa.entity.Index;

public interface IndexDao extends BaseDao<Index, String> {

    Index findByNameAndYears(String name, String now);
}
