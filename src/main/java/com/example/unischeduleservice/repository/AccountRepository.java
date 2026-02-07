package com.example.unischeduleservice.repository;

import com.example.unischeduleservice.dto.VisitByMonthDTO;
import com.example.unischeduleservice.dto.VisitsInDayDTO;
import com.example.unischeduleservice.models.Account;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author vuvanhoang
 * @created 22/10/25 07:01
 * @project unischedule_service
 */
@Repository
public interface AccountRepository extends MongoRepository<Account, String> {
    Account findByUsername(String username);
    List<Account> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Aggregation(pipeline = {
            "{ $group: { _id: { " +
                    "year: { $year: '$createdAt' }, " +
                    "month: { $month: '$createdAt' }, " +
                    "day: { $dayOfMonth: '$createdAt' }}, totalVisit: { $sum: 1 } } }",
            "{ $sort: { '_id.year': -1, '_id.month': -1, '_id.day': -1 } }",
            "{ $project: { year: '$_id.year', month: '$_id.month', day: '$_id.day', totalVisit: 1, _id: 0 } }"
    })
    List<VisitsInDayDTO> countVisitByDay();

    @Aggregation(pipeline = {
            "{ $group: { _id: { year: { $year: '$createdAt' }, month: { $month: '$createdAt' } }, totalVisit: { $sum: 1 } } }",
            "{ $sort: { '_id.year': -1, '_id.month': -1 } }",
            "{ $project: { year: '$_id.year', month: '$_id.month', totalVisit: 1, _id: 0 } }"
    })
    List<VisitByMonthDTO> countVisitByMonth();
}
