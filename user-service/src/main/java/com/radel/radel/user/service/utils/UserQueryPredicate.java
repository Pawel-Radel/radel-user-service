package com.radel.radel.user.service.utils;

import static com.radel.radel.user.service.domain.model.QUserEntity.userEntity;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.radel.services.user.api.UserSearchRequest;

public class UserQueryPredicate {

    public static Predicate of(UserSearchRequest request) {
        PredicateBuilder predicateBuilder = PredicateBuilder.create()
                .in(userEntity.userId, request.getUserIds())
                .equals(userEntity.enabled, request.getEnabled())
                .equals(userEntity.emailVerified, request.getEmailVerified())
                .contains(userEntity.name, request.getName())
                .contains(userEntity.surname, request.getSurname())
                .contains(userEntity.email, request.getEmail())
                .contains(asList(userEntity.name, userEntity.surname, userEntity.email), request.getSearchPhrase());

        addRolePredicate(predicateBuilder, request);
        addGroupPredicate(predicateBuilder, request);
        addAttributesPredicate(predicateBuilder, request);

        return predicateBuilder.build();
    }

    private static void addAttributesPredicate(PredicateBuilder predicateBuilder, UserSearchRequest request) {
        Map<String, String> attributes = request.getAttributes();

        if (isNull(attributes)) {
            return;
        }

        predicateBuilder.predicate(() -> {
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            List<Predicate> predicates = new ArrayList<>();

            attributes.forEach((key, value) -> predicates.add(userEntity.attributes.contains(key, value)));

            if (request.getHasAnyAttribute()) {
                predicates.forEach(booleanBuilder::or);
            } else {
                predicates.forEach(booleanBuilder::and);
            }

            return booleanBuilder;
        }, () -> !isEmpty(attributes));
    }

    private static void addGroupPredicate(PredicateBuilder predicateBuilder, UserSearchRequest request) {
        if (request.getBelongsToAnyGroup()) {
            predicateBuilder.in(userEntity.groups.any(), request.getGroups());
        } else {
            predicateBuilder.predicate(() -> {
                BooleanBuilder booleanBuilder = new BooleanBuilder();
                List<Predicate> predicates = new ArrayList<>();

                request.getGroups().forEach(groupId -> predicates.add(userEntity.groups.contains(groupId)));
                predicates.forEach(booleanBuilder::and);

                return booleanBuilder;
            }, () -> !isEmpty(request.getGroups()));
        }
    }

    private static void addRolePredicate(PredicateBuilder predicateBuilder, UserSearchRequest request) {
        if (request.getHasAnyRole()) {
            predicateBuilder.in(userEntity.roles.any(), request.getRoles());
        } else {
            predicateBuilder.predicate(() -> {
                BooleanBuilder booleanBuilder = new BooleanBuilder();
                List<Predicate> predicates = new ArrayList<>();

                request.getRoles().forEach(role -> predicates.add(userEntity.roles.contains(role)));
                predicates.forEach(booleanBuilder::and);

                return booleanBuilder;
            }, () -> !isEmpty(request.getRoles()));
        }
    }
}
