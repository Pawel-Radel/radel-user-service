package com.radel.radel.user.service.domain.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.radel.radel.user.service.domain.model.UserEntity;

public interface MongoUserRepository extends MongoRepository<UserEntity, String> ,  QuerydslPredicateExecutor<UserEntity>, UserRepositoryCustom {

    Optional<UserEntity> findByUserId(String userId);
}
