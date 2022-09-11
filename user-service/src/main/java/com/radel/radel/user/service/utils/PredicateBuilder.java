package com.radel.radel.user.service.utils;

import static java.util.Objects.nonNull;
import static org.aspectj.util.LangUtil.isEmpty;
import static org.springframework.util.StringUtils.hasText;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.LiteralExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringPath;

public class PredicateBuilder {

    private final List<Predicate> predicates = new ArrayList<>();

    public static PredicateBuilder create() {
        return new PredicateBuilder();
    }

    public <U extends Comparable> PredicateBuilder in(LiteralExpression<U> path, Collection<? extends U> value) {
        return predicate(() -> path.in(value), () -> !isEmpty(value));
    }

    public <U extends Comparable> PredicateBuilder notIn(LiteralExpression<U> path, Collection<? extends U> value) {
        return predicate(() -> path.notIn(value), () -> !isEmpty(value));
    }

    public PredicateBuilder contains(StringPath path, String value) {
        return predicate(() -> path.containsIgnoreCase(value), () -> hasText(value));
    }

    public PredicateBuilder contains(List<StringPath> paths, String value) {
        return predicate(() -> {
            BooleanBuilder booleanBuilder = new BooleanBuilder(null);
            paths.forEach(path -> booleanBuilder.or(path.containsIgnoreCase(value)));
            return booleanBuilder;
        }, () -> hasText(value));
    }

    public <U extends Comparable> PredicateBuilder contains(ListPath<U, SimpleExpression<? super U>> paths, Collection<? extends U> values) {
        return predicate(() -> {
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            List<Predicate> predicates = new ArrayList<>();

            values.forEach(val -> predicates.add(paths.contains(val)));

            predicates.forEach(booleanBuilder::and);
            return booleanBuilder;
        }, () -> !isEmpty(values));
    }

    public PredicateBuilder notContains(StringPath path, String value) {
        return predicate(() -> path.containsIgnoreCase(value).not(), () -> hasText(value));
    }

    public PredicateBuilder endsWith(StringPath path, String value) {
        return predicate(() -> path.endsWithIgnoreCase(value), () -> hasText(value));
    }

    public PredicateBuilder notEndsWith(StringPath path, String value) {
        return predicate(() -> path.endsWithIgnoreCase(value).not(), () -> hasText(value));
    }

    public PredicateBuilder startsWith(StringPath path, String value) {
        return predicate(() -> path.startsWithIgnoreCase(value), () -> hasText(value));
    }

    public PredicateBuilder notStartsWith(StringPath path, String value) {
        return predicate(() -> path.endsWithIgnoreCase(value).not(), () -> hasText(value));
    }

    public PredicateBuilder like(StringPath path, String value) {
        return predicate(() -> path.like(value), () -> hasText(value));
    }

    public PredicateBuilder notLike(StringPath path, String value) {
        return predicate(() -> path.like(value).not(), () -> hasText(value));
    }

    public <U extends Comparable> PredicateBuilder equals(LiteralExpression<U> path, U value) {
        return predicate(() -> path.eq(value), () -> nonNull(value));
    }

    public <U extends Comparable> PredicateBuilder notEquals(LiteralExpression<U> path, U value) {
        return predicate(() -> path.eq(value).not(), () -> nonNull(value));
    }

    public <U extends Comparable> PredicateBuilder lessThan(LiteralExpression<U> path, U value) {
        return predicate(() -> path.lt(value), () -> nonNull(value));
    }

    public <U extends Comparable> PredicateBuilder lessThanOrEqualTo(LiteralExpression<U> path, U value) {
        return predicate(() -> path.loe(value), () -> nonNull(value));
    }

    public <U extends Comparable> PredicateBuilder greaterThan(LiteralExpression<U> path, U value) {
        return predicate(() -> path.gt(value), () -> nonNull(value));
    }

    public <U extends Comparable> PredicateBuilder greaterThanOrEqualTo(LiteralExpression<U> path, U value) {
        return predicate(() -> path.goe(value), () -> nonNull(value));
    }

    public <U extends Comparable> PredicateBuilder betweenOrEquals(LiteralExpression<U> path, U first, U second) {
        return predicate(() -> path.between(first, second), () -> nonNull(first) || nonNull(second));
    }

    public <U extends Comparable> PredicateBuilder isNull(LiteralExpression<U> path, Boolean isIncluded) {
        return predicate(path::isNull, () -> nonNull(isIncluded) && isIncluded);
    }

    public <U extends Comparable> PredicateBuilder isNotNull(LiteralExpression<U> path, Boolean isIncluded) {
        return predicate(path::isNotNull, () -> nonNull(isIncluded) && isIncluded);
    }

    public <U extends Comparable> PredicateBuilder predicate(Supplier<Predicate> expression, Supplier<Boolean> canApply) {
        if (canApply.get()) {
            predicates.add(expression.get());
        }

        return this;
    }

    public Predicate build() {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        predicates.forEach(booleanBuilder::and);
        return booleanBuilder;
    }
}
