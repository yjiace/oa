package cn.smallyoung.oa.base.specification;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author smallyoung
 * 描述:
 * 创建SimpleSpecification来实现Specification接口，
 * 并且根据条件生成Specification对象，因为在最后查询的时候需要这个对象
 * SimpleSpecification是核心类型，
 * 用来根据条件生成Specification对象，这个SimpleSpecification直接存储了具体的查询条件。
 * @create 2018-03-21 21:14
 */

public class SimpleSpecification<T> implements Specification<T> {

    private static final long serialVersionUID = 9101383631123058909L;
    /**
     * 查询的条件列表，是一组列表
     */
    private final List<SpecificationOperator> operators;

    SimpleSpecification(List<SpecificationOperator> operators) {
        this.operators = operators;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Predicate resultPre = null;
        if (operators != null) {
            Map<String, List<SpecificationOperator>> map = operators.stream().collect(Collectors.groupingBy(SpecificationOperator::getJoin));
            for (Map.Entry<String, List<SpecificationOperator>> entry : map.entrySet()) {
                Predicate predicate = null;
                for (SpecificationOperator so : entry.getValue()) {
                    Predicate p = generatePredicate(root, criteriaBuilder, so);
                    if (p == null) {
                        continue;
                    }
                    if (entry.getKey().startsWith("AND")) {
                        predicate = (predicate != null ? criteriaBuilder.and(predicate, p) : p);
                    } else if (entry.getKey().startsWith("OR")) {
                        predicate = (predicate != null ? criteriaBuilder.or(predicate, p) : p);
                    }
                }
                resultPre = resultPre != null ? criteriaBuilder.and(resultPre, predicate) : predicate;
            }
        }
        return resultPre;
    }

    private Predicate generatePredicate(Root<T> root, CriteriaBuilder criteriaBuilder, SpecificationOperator so) {
        if (so != null && so.getOperator() != null) {
            switch (so.getOperator()) {
                case EQ:
                    return criteriaBuilder.equal(root.get(so.getKey()), so.getValue());
                case NOTEQ:
                    return criteriaBuilder.notEqual(root.get(so.getKey()), so.getValue());
                case EQANDNOTNULL:
                    return criteriaBuilder.and(
                            criteriaBuilder.equal(root.get(so.getKey()), so.getValue()),
                            criteriaBuilder.isNotNull(root.get(so.getKey()))
                    );
                case EQORISNULL:
                    return criteriaBuilder.or(
                            criteriaBuilder.equal(root.get(so.getKey()), so.getValue()),
                            criteriaBuilder.isNull(root.get(so.getKey()))
                    );
                case NOTEQANDNOTNULL:
                    return criteriaBuilder.and(
                            criteriaBuilder.notEqual(root.get(so.getKey()), so.getValue()),
                            criteriaBuilder.isNotNull(root.get(so.getKey()))
                    );
                case NOTEQORISNULL:
                    return criteriaBuilder.or(
                            criteriaBuilder.notEqual(root.get(so.getKey()), so.getValue()),
                            criteriaBuilder.isNull(root.get(so.getKey()))
                    );
                case LIKE:
                    return criteriaBuilder.like(root.get(so.getKey()).as(String.class), "%" + so.getValue() + "%");
                case NOTLIKE:
                    return criteriaBuilder.notLike(root.get(so.getKey()).as(String.class), "%" + so.getValue() + "%");
                case LEFTLIKE:
                    return criteriaBuilder.like(root.get(so.getKey()).as(String.class), "%" + so.getValue());
                case RIGHTLIKE:
                    return criteriaBuilder.like(root.get(so.getKey()).as(String.class), so.getValue() + "%");
                case ISNULL:
                    return criteriaBuilder.isNull(root.get(so.getKey()));
                case ISNOTNULL:
                    return criteriaBuilder.isNotNull(root.get(so.getKey()));
                case GT:
                    return criteriaBuilder.gt(root.get(so.getKey()).as(Number.class), (Number) so.getValue());
                case GE:
                    return criteriaBuilder.ge(root.get(so.getKey()).as(Number.class), (Number) so.getValue());
                case LT:
                    return criteriaBuilder.lt(root.get(so.getKey()).as(Number.class), (Number) so.getValue());
                case LE:
                    return criteriaBuilder.le(root.get(so.getKey()).as(Number.class), (Number) so.getValue());
                case BETWEEN:
                    String values = so.getValue().toString();
                    if (values != null && values.split("~").length == 2) {
                        return criteriaBuilder.and(
                                criteriaBuilder.greaterThanOrEqualTo(root.get(so.getKey()).as(String.class), values.split("~")[0].trim()),
                                criteriaBuilder.lessThanOrEqualTo(root.get(so.getKey()).as(String.class), values.split("~")[1].trim())
                        );
                    }
                    break;
                case LEFTJOIN:
                    String leftJoinValues = so.getKey();
                    if (leftJoinValues != null && leftJoinValues.split("-").length == 2) {
                        return criteriaBuilder.equal(root.join(leftJoinValues.split("-")[0], JoinType.LEFT).get(leftJoinValues.split("-")[1]), so.getValue());
                    }
                    return null;
                case RIGHTJOIN:
                    String rightJoinValues = so.getKey();
                    if (rightJoinValues != null && rightJoinValues.split("-").length == 2) {
                        return criteriaBuilder.equal(root.join(rightJoinValues.split("-")[0], JoinType.RIGHT).get(rightJoinValues.split("-")[1]), so.getValue());
                    }
                    return null;
                case INNERJOIN:
                    String innerJoinValues = so.getKey();
                    if (innerJoinValues != null && innerJoinValues.split("-").length == 2) {
                        return criteriaBuilder.equal(root.join(innerJoinValues.split("-")[0], JoinType.INNER).get(innerJoinValues.split("-")[1]), so.getValue());
                    }
                    return null;
                case IN:
                    String inValues = so.getValue().toString();
                    if (inValues != null) {
                        CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get(so.getKey()).as(String.class));
                        for (String str : inValues.split(":")) {
                            in.value(str);
                        }
                        return in;
                    }
                    return null;
                default:
                    break;
            }
        }
        return null;
    }
}
