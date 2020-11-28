package cn.smallyoung.oa.dao;

import cn.smallyoung.oa.base.BaseDao;
import cn.smallyoung.oa.entity.CarRecord;
import org.springframework.data.jpa.repository.Query;

/**
 * @author smallyoung
 * @data 2020/11/18
 */
public interface CarRecordDao extends BaseDao<CarRecord, Long> {

    @Query(value = "select * from t_car_record where approval_id = ?1 order by create_time desc limit 1", nativeQuery = true)
    CarRecord findByApproval(Long approvalId);
}
