package cn.smallyoung.oa.dao;

import cn.smallyoung.oa.base.BaseDao;
import cn.smallyoung.oa.entity.VehicleInformation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author smallyoung
 * @data 2020/11/18
 */
public interface VehicleInformationDao extends BaseDao<VehicleInformation, Long> {

    VehicleInformation findByPlateNumber(String plateNumber);

    @Modifying
    @Query(value = "update VehicleInformation set status = ?2 where plateNumber = ?1")
    void updateStatusByPlateNumber(String plateNumber, String status);
}
