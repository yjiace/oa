package cn.smallyoung.oa.base.specification;

import lombok.Getter;

import java.util.stream.Stream;

/**
 * 描述:
 *
 * @author smallyoung
 * @create 2018-03-21 21:10
 */
@Getter
public class SpecificationOperator {

    public enum Operator {
        /**
         * 相等
         */
        EQ,
        /**
         * 相等并且不为空
         */
        EQANDNOTNULL,
        /**
         * 相等或者为空
         */
        EQORISNULL,
        /**
         * 不相等
         */
        NOTEQ,
        /**
         * 不相等且不为空
         */
        NOTEQANDNOTNULL,
        /**
         * 不相等或者为空
         */
        NOTEQORISNULL,
        /**
         * 模糊匹配
         **/
        LIKE,
        /**
         * 模糊查询 非
         */
        NOTLIKE,
        /**
         * 左模糊匹配
         **/
        LEFTLIKE,
        /**
         * 右模糊匹配
         **/
        RIGHTLIKE,
        /**
         * 空
         */
        ISNULL,
        /**
         * 非空
         */
        ISNOTNULL,
        /**
         * 大于
         **/
        GT,
        /**
         * 大于等于
         **/
        GE,
        /**
         * 小于
         **/
        LT,
        /**
         * 小于等于
         **/
        LE,
        /**
         * 区间
         **/
        BETWEEN,
        /**
         * 左连接
         */
        LEFTJOIN,
        /**
         * 右连接
         */
        RIGHTJOIN,
        /**
         * 全连接
         */
        INNERJOIN,
        /**
         * 包含
         */
        IN,
    }

    /**
     * 连接的方式：and或者or
     */
    public String join;

    /**
     * 操作符的key，如查询时的name,id之类
     */
    private String key;
    /**
     * 操作符的value，具体要查询的值
     */
    private Object value;
    /**
     * 操作符，自己定义的一组操作符，用来方便查询
     */
    private Operator operator;

    public SpecificationOperator(String key, Object value, Operator operator, String join) {
        this.key = key;
        this.value = value;
        this.operator = operator;
        this.join = join;
    }

    public static Operator stringToOperator(String data) {
        return Stream.of(Operator.values()).filter(operator -> operator.name().equals(data)).findFirst().orElse(Operator.EQ);
    }
}
