package com.radel.radel.user.service.domain.repository;

import static com.radel.radel.user.service.domain.enumeration.EntityFieldMapping.USER_ATTRIBUTES_FIELD;
import static com.radel.radel.user.service.domain.enumeration.EntityFieldMapping.USER_EMAIL_FIELD;
import static com.radel.radel.user.service.domain.enumeration.EntityFieldMapping.USER_EMAIL_VERIFIED_FIELD;
import static com.radel.radel.user.service.domain.enumeration.EntityFieldMapping.USER_ENABLED_FIELD;
import static com.radel.radel.user.service.domain.enumeration.EntityFieldMapping.USER_GROUPS_FIELD;
import static com.radel.radel.user.service.domain.enumeration.EntityFieldMapping.USER_ID_FIELD;
import static com.radel.radel.user.service.domain.enumeration.EntityFieldMapping.USER_NAME_FIELD;
import static com.radel.radel.user.service.domain.enumeration.EntityFieldMapping.USER_ROLES_FIELD;
import static com.radel.radel.user.service.domain.enumeration.EntityFieldMapping.USER_SURNAME_FIELD;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.UpdateResult;
import com.radel.radel.user.service.domain.model.UserEntity;
import com.radel.services.user.api.UserEditableFieldEnum;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    public UserRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void upsertAll(Iterable<UserEntity> users) {
        MongoCollection<Document> collection = mongoTemplate.getCollection("users");

        List<WriteModel<Document>> updates = new ArrayList<>();

        users.forEach(user -> {
            UpdateOneModel<Document> upsert = new UpdateOneModel<>(
                    new Document(USER_ID_FIELD, user.getUserId()),
                    new Document("$set", objectMapper.convertValue(user, Map.class)),
                    new UpdateOptions().upsert(true)
            );

            updates.add(upsert);
        });

        if (!updates.isEmpty()) {
            collection.bulkWrite(updates);
        }
    }

    @Override
    public UpdateResult upsert(UserEntity user) {
        Query query = new Query();
        query.addCriteria(Criteria.where(USER_ID_FIELD).is(user.getUserId()));

        Document document = new Document();
        mongoTemplate.getConverter().write(user, document);
        Update update = Update.fromDocument(document);

        return mongoTemplate.upsert(query, update, UserEntity.class);

    }

    @Override
    public void updatePartial(String userId, UserEntity userEntity, List<UserEditableFieldEnum> fields) {
        Query query = new Query();
        query.addCriteria(Criteria.where(USER_ID_FIELD).is(userId));

        Update update = new Update();

        fields.forEach(field -> {
            switch (field) {
                case NAME:
                    update.set(USER_NAME_FIELD, userEntity.getName());
                    break;
                case SURNAME:
                    update.set(USER_SURNAME_FIELD, userEntity.getSurname());
                    break;
                case EMAIL:
                    update.set(USER_EMAIL_FIELD, userEntity.getEmail());
                    break;
                case ENABLED:
                    update.set(USER_ENABLED_FIELD, userEntity.getEnabled());
                    break;
                case EMAIL_VERIFIED:
                    update.set(USER_EMAIL_VERIFIED_FIELD, userEntity.getEmailVerified());
                    break;
                case ATTRIBUTES:
                    update.set(USER_ATTRIBUTES_FIELD, userEntity.getAttributes());
                    break;
                case ROLES:
                    update.set(USER_ROLES_FIELD, userEntity.getRoles());
                    break;
                case GROUPS:
                    update.set(USER_GROUPS_FIELD, userEntity.getGroups());
                    break;
            }
        });

        mongoTemplate.updateFirst(query, update, UserEntity.class);
    }
}
