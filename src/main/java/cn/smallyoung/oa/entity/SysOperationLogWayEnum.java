package cn.smallyoung.oa.entity;


/**
 * @author smallyoung
 */

public enum SysOperationLogWayEnum {

    /**
     * 记录变化
     */
    RecordChanges("记录变化"),

    /**
     * 仅记录
     */
    RecordOnly("仅记录"),

    /**
     * 记录变化后的值
     */
    RecordTheChange("记录变化后的值"),

    /**
     * 记录变换之前的值
     */
    RecordTheBefore("记录变换之前的值");

    SysOperationLogWayEnum(String str) {
    }
}


