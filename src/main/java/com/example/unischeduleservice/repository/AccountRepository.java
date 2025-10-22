package com.example.unischeduleservice.repository;

import com.example.unischeduleservice.models.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author vuvanhoang
 * @created 22/10/25 07:01
 * @project unischedule_service
 */
@Repository
public interface AccountRepository extends MongoRepository<Account, String> {}
