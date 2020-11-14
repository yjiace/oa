package cn.smallyoung.oa.dao;

import cn.smallyoung.oa.base.BaseDao;
import cn.smallyoung.oa.entity.AttachmentFile;

import java.util.List;
import java.util.Set;

/**
 * @author smallyoung
 * @data 2020/11/12
 */
public interface AttachmentFileDao extends BaseDao<AttachmentFile, Long> {

    /**
     * 根据MD5集合查询是否存在相同的文件
     * @param md5List  MD5集合
     * @return  符合条件的MD5集合
     */
    List<AttachmentFile> findByMd5In(Set<String> md5List);

}
