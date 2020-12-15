package cn.smallyoung.oa.dao;

import cn.smallyoung.oa.base.BaseDao;
import cn.smallyoung.oa.entity.AttachmentFile;

import java.util.List;

/**
 * @author smallyoung
 * @data 2020/11/12
 */
public interface AttachmentFileDao extends BaseDao<AttachmentFile, Long> {

    /**
     * 根据ID集合查询文件列表
     * @param idList ID集合
     * @return  符合条件的ID集合
     */
    List<AttachmentFile> findByIdIn(List<Long> idList);
}
