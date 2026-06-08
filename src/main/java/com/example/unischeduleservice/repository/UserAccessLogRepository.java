package com.example.unischeduleservice.repository;

import com.example.unischeduleservice.models.UserAccessLogs;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccessLogRepository extends MongoRepository<UserAccessLogs, String> {
}
