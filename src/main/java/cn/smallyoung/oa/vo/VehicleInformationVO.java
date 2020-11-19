package cn.smallyoung.oa.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author smallyoung
 * @data 2020/11/18
 */
@Getter
@Setter
@ToString
public class VehicleInformationVO implements Serializable {

    private static final long serialVersionUID = -4103150040066001046L;

    private Long id;

    private String name;

    private String number;

    private String plateNumber;

    private String company;

    private String model;
}
