package cn.smallyoung.oa.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author smallyoung
 * @data 2020/12/14
 */
@Getter
@Setter
@ToString
public class CarStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1788153446142599385L;
    private Integer index;
    private LocalDateTime applicationTime;
    private String transportUnit;
    private String vehicleNumber;
    private String model;
    private String carCadre;
    private String drivingRoute;
    private LocalDateTime playingTime;
    private LocalDateTime returnTime;
    private String carDispatcher;
    private String approvedChief;
}
