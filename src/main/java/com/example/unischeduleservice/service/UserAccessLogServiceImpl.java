package com.example.unischeduleservice.service;

import com.example.unischeduleservice.dto.UserAccessLogInfoDTO;
import com.example.unischeduleservice.dto.base.PageResponseDTO;
import com.example.unischeduleservice.models.UserAccessLogs;
import com.example.unischeduleservice.repository.UserAccessLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserAccessLogServiceImpl implements UserAccessLogService {

    private static final List<String> SORTABLE_FIELDS = List.of("id", "username", "osOfDevice", "createdAt");

    private final UserAccessLogRepository userAccessLogRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public UserAccessLogs findById(String id) {
        return userAccessLogRepository.findById(id).orElse(null);
    }

    @Override
    public UserAccessLogs findByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        return userAccessLogRepository.findFirstByUsernameIgnoreCaseOrderByCreatedAtDesc(username);
    }

    @Override
    public PageResponseDTO<UserAccessLogInfoDTO> queryUserAccessLogs(
            String username,
            String osOfDevice,
            LocalDateTime createdFrom,
            LocalDateTime createdTo,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        int pageIndex = Math.max(page, 0);
        int pageSize = Math.max(size, 1);
        String resolvedSortBy = SORTABLE_FIELDS.contains(sortBy) ? sortBy : "createdAt";
        Sort.Direction direction = resolveDirection(sortDirection);

        Criteria criteria = buildCriteria(username, osOfDevice, createdFrom, createdTo);
        Query countQuery = new Query();
        if (criteria != null) {
            countQuery.addCriteria(criteria);
        }

        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        query.with(PageRequest.of(pageIndex, pageSize, Sort.by(direction, resolvedSortBy)));

        long totalElements = mongoTemplate.count(countQuery, UserAccessLogs.class);
        List<UserAccessLogs> logs = mongoTemplate.find(query, UserAccessLogs.class);

        List<UserAccessLogInfoDTO> items = logs.stream()
                .map(this::toDTO)
                .toList();

        int totalPages = pageSize == 0 ? 0 : (int) Math.ceil((double) totalElements / pageSize);

        return PageResponseDTO.<UserAccessLogInfoDTO>builder()
                .items(items)
                .page(pageIndex)
                .size(pageSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(pageIndex == 0)
                .last(pageIndex >= Math.max(totalPages - 1, 0))
                .build();
    }

    private Criteria buildCriteria(
            String username,
            String osOfDevice,
            LocalDateTime createdFrom,
            LocalDateTime createdTo
    ) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (StringUtils.hasText(username)) {
            criteriaList.add(Criteria.where("username")
                    .regex(Pattern.compile(Pattern.quote(username.trim()), Pattern.CASE_INSENSITIVE)));
        }
        if (StringUtils.hasText(osOfDevice)) {
            criteriaList.add(Criteria.where("osOfDevice")
                    .regex(Pattern.compile(Pattern.quote(osOfDevice.trim()), Pattern.CASE_INSENSITIVE)));
        }
        if (createdFrom != null || createdTo != null) {
            Criteria createdAtCriteria = Criteria.where("createdAt");
            if (createdFrom != null) {
                createdAtCriteria = createdAtCriteria.gte(createdFrom);
            }
            if (createdTo != null) {
                createdAtCriteria = createdAtCriteria.lte(createdTo);
            }
            criteriaList.add(createdAtCriteria);
        }

        if (criteriaList.isEmpty()) {
            return null;
        }
        return new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
    }

    private Sort.Direction resolveDirection(String sortDirection) {
        try {
            if (StringUtils.hasText(sortDirection)) {
                return Sort.Direction.fromString(sortDirection);
            }
        } catch (IllegalArgumentException ignored) {
            // Fall back to DESC below.
        }
        return Sort.Direction.DESC;
    }

    private UserAccessLogInfoDTO toDTO(UserAccessLogs logs) {
        return UserAccessLogInfoDTO.builder()
                .id(logs.getId())
                .username(logs.getUsername())
                .osOfDevice(logs.getOsOfDevice())
                .createdAt(logs.getCreatedAt())
                .build();
    }
}
