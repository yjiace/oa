package cn.smallyoung.oa.entity;


/**
 * @author smallyoung
 */

public enum SysOperationLogWayEnum {

    /**
     * 记录变化
     */
    RecordBeforeAndAfterChanges,

    /**
     * 仅记录
     */
    RecordOnly,

    /**
     * 使用变化后的返回值
     */
    UserAfter;
}


