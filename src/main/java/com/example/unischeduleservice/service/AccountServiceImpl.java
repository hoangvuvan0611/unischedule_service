package com.example.unischeduleservice.service;

import com.example.unischeduleservice.dto.*;
import com.example.unischeduleservice.dto.base.PageResponseDTO;
import com.example.unischeduleservice.models.Account;
import com.example.unischeduleservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author vuvanhoang
 * @created 25/10/25 07:36
 * @project unischedule_service
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final List<String> SORTABLE_FIELDS = List.of("id", "username", "createdAt", "updatedAt");

    private final AccountRepository accountRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Account getRandom() {
        List<Account> accounts = accountRepository.findAll();
        if (accounts.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return accounts.get(random.nextInt(accounts.size()));
    }

    @Override
    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    @Override
    public Account findById(String id) {
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public void saveNewAccount(Account account) {
        accountRepository.save(account);
    }

    @Override
    public List<Account> getAccountsByNumRecord(int num) {
        int limit = Math.max(num, 1);
        return accountRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit));
    }

    @Override
    public PageResponseDTO<AccountInfoDTO> queryAccounts(
            String username,
            LocalDateTime createdFrom,
            LocalDateTime createdTo,
            LocalDateTime updatedFrom,
            LocalDateTime updatedTo,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        int pageIndex = Math.max(page, 0);
        int pageSize = Math.max(size, 1);
        String resolvedSortBy = SORTABLE_FIELDS.contains(sortBy) ? sortBy : "createdAt";
        Sort.Direction direction = resolveDirection(sortDirection);

        Criteria criteria = buildCriteria(username, createdFrom, createdTo, updatedFrom, updatedTo);
        Query countQuery = new Query();
        if (criteria != null) {
            countQuery.addCriteria(criteria);
        }

        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        query.with(PageRequest.of(pageIndex, pageSize, Sort.by(direction, resolvedSortBy)));

        long totalElements = mongoTemplate.count(countQuery, Account.class);
        List<Account> accounts = mongoTemplate.find(query, Account.class);
        List<AccountInfoDTO> items = accounts.stream()
                .map(this::toAccountInfoDTO)
                .toList();

        int totalPages = pageSize == 0 ? 0 : (int) Math.ceil((double) totalElements / pageSize);

        return PageResponseDTO.<AccountInfoDTO>builder()
                .items(items)
                .page(pageIndex)
                .size(pageSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(pageIndex == 0)
                .last(pageIndex >= Math.max(totalPages - 1, 0))
                .build();
    }

    @Override
    public AccountInfoDTO getAccountInfoById(String id) {
        Account account = findById(id);
        return account == null ? null : toAccountInfoDTO(account);
    }

    @Override
    public AccountInfoDTO getAccountInfoByUsername(String username) {
        Account account = findByUsername(username);
        return account == null ? null : toAccountInfoDTO(account);
    }

    @Override
    public AccountStatisticsDTO getAccountStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekStart = now.minusWeeks(1);
        LocalDateTime monthStart = now.minusMonths(1);
        LocalDateTime yearStart = now.minusYears(1);

        long totalAccounts = accountRepository.count();
        long accountsCreatedToday = accountRepository.countByCreatedAtAfter(todayStart);
        long accountsCreatedThisWeek = accountRepository.countByCreatedAtAfter(weekStart);
        long accountsCreatedThisMonth = accountRepository.countByCreatedAtAfter(monthStart);
        long accountsCreatedThisYear = accountRepository.countByCreatedAtAfter(yearStart);

        List<Account> lastAccounts = accountRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 1));
        List<Account> oldestAccounts = accountRepository.findAllByOrderByCreatedAtAsc(PageRequest.of(0, 1));
        
        Account lastAccount = lastAccounts.isEmpty() ? null : lastAccounts.get(0);
        Account oldestAccount = oldestAccounts.isEmpty() ? null : oldestAccounts.get(0);

        return AccountStatisticsDTO.builder()
                .totalAccounts(totalAccounts)
                .accountsCreatedToday(accountsCreatedToday)
                .accountsCreatedThisWeek(accountsCreatedThisWeek)
                .accountsCreatedThisMonth(accountsCreatedThisMonth)
                .accountsCreatedThisYear(accountsCreatedThisYear)
                .lastAccountCreated(lastAccount != null ? lastAccount.getCreatedAt() : null)
                .oldestAccountCreated(oldestAccount != null ? oldestAccount.getCreatedAt() : null)
                .build();
    }

    @Override
    public List<AccountReportDTO> getAccountsCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        List<Account> accounts = accountRepository.findByCreatedAtBetween(startDate, endDate);
        return accounts.stream()
                .map(this::convertToReportDTO)
                .toList();
    }

    @Override
    public List<AccountReportDTO> getRecentAccounts(int limit) {
        List<Account> accounts = accountRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit));
        return accounts.stream()
                .map(this::convertToReportDTO)
                .toList();
    }

    @Override
    public List<AccountReportDTO> getAllAccountsForReport() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(this::convertToReportDTO)
                .toList();
    }

    @Override
    public AccountBatchSummaryDTO getAccountBatchSummary() {
        List<AccountBatchReportDTO> batchReports = getAllAccountBatches();
        
        String largestBatch = batchReports.stream()
                .max(Comparator.comparing(AccountBatchReportDTO::getTotalAccounts))
                .map(AccountBatchReportDTO::getBatchCode)
                .orElse("N/A");
                
        String smallestBatch = batchReports.stream()
                .min(Comparator.comparing(AccountBatchReportDTO::getTotalAccounts))
                .map(AccountBatchReportDTO::getBatchCode)
                .orElse("N/A");

        long totalAccounts = batchReports.stream()
                .mapToLong(AccountBatchReportDTO::getTotalAccounts)
                .sum();

        return AccountBatchSummaryDTO.builder()
                .totalAccounts(totalAccounts)
                .totalBatches(batchReports.size())
                .largestBatch(largestBatch)
                .smallestBatch(smallestBatch)
                .batchReports(batchReports)
                .build();
    }

    @Override
    public AccountBatchReportDTO getAccountBatchByCode(String batchCode) {
        List<Account> accounts = accountRepository.findByUsernameStartingWith(batchCode);
        return createBatchReport(batchCode, accounts);
    }

    @Override
    public List<AccountBatchReportDTO> getAllAccountBatches() {
        List<String> batchCodes = getAllBatchCodes();
        return batchCodes.stream()
                .map(this::getAccountBatchByCode)
                .sorted(Comparator.comparing(AccountBatchReportDTO::getBatchCode))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllBatchCodes() {
        List<Account> allAccounts = accountRepository.findAll();
        return allAccounts.stream()
                .map(account -> extractBatchCode(account.getUsername()))
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public AccountBatchStatsSummaryDTO getAccountBatchStatsSummary() {
        List<AccountBatchStatsDTO> batchStats = getAllAccountBatchStats();
        
        String largestBatch = batchStats.stream()
                .max(Comparator.comparing(AccountBatchStatsDTO::getTotalAccounts))
                .map(AccountBatchStatsDTO::getBatchCode)
                .orElse("N/A");
                
        String smallestBatch = batchStats.stream()
                .min(Comparator.comparing(AccountBatchStatsDTO::getTotalAccounts))
                .map(AccountBatchStatsDTO::getBatchCode)
                .orElse("N/A");

        long totalAccounts = batchStats.stream()
                .mapToLong(AccountBatchStatsDTO::getTotalAccounts)
                .sum();
                
        long totalActiveAccounts = batchStats.stream()
                .mapToLong(AccountBatchStatsDTO::getActiveAccounts)
                .sum();
                
        long totalInactiveAccounts = batchStats.stream()
                .mapToLong(AccountBatchStatsDTO::getInactiveAccounts)
                .sum();
                
        long totalDormantAccounts = batchStats.stream()
                .mapToLong(AccountBatchStatsDTO::getDormantAccounts)
                .sum();
                
        long totalUnknownAccounts = batchStats.stream()
                .mapToLong(AccountBatchStatsDTO::getUnknownAccounts)
                .sum();

        return AccountBatchStatsSummaryDTO.builder()
                .totalAccounts(totalAccounts)
                .totalBatches(batchStats.size())
                .largestBatch(largestBatch)
                .smallestBatch(smallestBatch)
                .totalActiveAccounts(totalActiveAccounts)
                .totalInactiveAccounts(totalInactiveAccounts)
                .totalDormantAccounts(totalDormantAccounts)
                .totalUnknownAccounts(totalUnknownAccounts)
                .batchStats(batchStats)
                .build();
    }

    @Override
    public List<AccountBatchStatsDTO> getAllAccountBatchStats() {
        List<String> batchCodes = getAllBatchCodes();
        return batchCodes.stream()
                .map(this::getAccountBatchStatsByCode)
                .sorted(Comparator.comparing(AccountBatchStatsDTO::getBatchCode))
                .collect(Collectors.toList());
    }

    @Override
    public AccountBatchStatsDTO getAccountBatchStatsByCode(String batchCode) {
        List<Account> accounts = accountRepository.findByUsernameStartingWith(batchCode);
        return createBatchStats(batchCode, accounts);
    }

    private String extractBatchCode(String username) {
        if (username == null || username.length() < 2) {
            return null;
        }
        return username.substring(0, 2).toUpperCase();
    }

    private AccountBatchReportDTO createBatchReport(String batchCode, List<Account> accounts) {
        List<AccountReportDTO> accountReports = accounts.stream()
                .map(this::convertToReportDTO)
                .collect(Collectors.toList());

        long activeAccounts = accountReports.stream()
                .mapToLong(report -> "Active".equals(report.getAccountStatus()) ? 1 : 0)
                .sum();
                
        long inactiveAccounts = accountReports.stream()
                .mapToLong(report -> "Inactive".equals(report.getAccountStatus()) ? 1 : 0)
                .sum();
                
        long dormantAccounts = accountReports.stream()
                .mapToLong(report -> "Dormant".equals(report.getAccountStatus()) ? 1 : 0)
                .sum();
                
        long unknownAccounts = accountReports.stream()
                .mapToLong(report -> "Unknown".equals(report.getAccountStatus()) ? 1 : 0)
                .sum();

        LocalDateTime oldestAccount = accounts.stream()
                .map(Account::getCreatedAt)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
                
        LocalDateTime newestAccount = accounts.stream()
                .map(Account::getCreatedAt)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return AccountBatchReportDTO.builder()
                .batchCode(batchCode)
                .batchName(generateBatchName(batchCode))
                .totalAccounts(accounts.size())
                .activeAccounts(activeAccounts)
                .inactiveAccounts(inactiveAccounts)
                .dormantAccounts(dormantAccounts)
                .unknownAccounts(unknownAccounts)
                .oldestAccountInBatch(oldestAccount)
                .newestAccountInBatch(newestAccount)
                .accounts(accountReports)
                .build();
    }

    private AccountBatchStatsDTO createBatchStats(String batchCode, List<Account> accounts) {
        // Đếm các trạng thái mà không cần convert sang DTO
        long activeAccounts = 0;
        long inactiveAccounts = 0;
        long dormantAccounts = 0;
        long unknownAccounts = 0;
        
        for (Account account : accounts) {
            String status = getAccountStatus(account);
            switch (status) {
                case "Active" -> activeAccounts++;
                case "Inactive" -> inactiveAccounts++;
                case "Dormant" -> dormantAccounts++;
                default -> unknownAccounts++;
            }
        }

        LocalDateTime oldestAccount = accounts.stream()
                .map(Account::getCreatedAt)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
                
        LocalDateTime newestAccount = accounts.stream()
                .map(Account::getCreatedAt)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return AccountBatchStatsDTO.builder()
                .batchCode(batchCode)
                .batchName(generateBatchName(batchCode))
                .totalAccounts(accounts.size())
                .activeAccounts(activeAccounts)
                .inactiveAccounts(inactiveAccounts)
                .dormantAccounts(dormantAccounts)
                .unknownAccounts(unknownAccounts)
                .oldestAccountInBatch(oldestAccount)
                .newestAccountInBatch(newestAccount)
                .build();
    }

    private String generateBatchName(String batchCode) {
        try {
            // Kiểm tra xem batch code có phải là số không
            Integer.parseInt(batchCode);
            // Nếu là số, hiển thị trực tiếp: "66" -> "Khoá 66", "68" -> "Khoá 68"
            return "Khoá " + batchCode;
        } catch (NumberFormatException e) {
            // Nếu không phải số, hiển thị nguyên bản
            return "Khoá " + batchCode;
        }
    }

    private AccountReportDTO convertToReportDTO(Account account) {
        long daysSinceCreated = 0;
        String accountStatus = "Unknown";
        
        if (account.getCreatedAt() != null) {
            daysSinceCreated = ChronoUnit.DAYS.between(account.getCreatedAt(), LocalDateTime.now());
        }
        
        if (account.getUpdatedAt() != null) {
            accountStatus = getAccountStatus(account);
        }

        return AccountReportDTO.builder()
                .id(account.getId())
                .username(account.getUsername())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .daysSinceCreated(daysSinceCreated)
                .accountStatus(accountStatus)
                .build();
    }

    private String getAccountStatus(Account account) {
        if (account.getUpdatedAt() == null) {
            return "Unknown";
        }
        
        LocalDateTime now = LocalDateTime.now();
        long daysSinceLastUpdate = ChronoUnit.DAYS.between(account.getUpdatedAt(), now);
        
        if (daysSinceLastUpdate <= 7) {
            return "Active";
        } else if (daysSinceLastUpdate <= 30) {
            return "Inactive";
        } else {
            return "Dormant";
        }
    }

    private String getAccountStatus(AccountReportDTO account) {
        return account.getAccountStatus();
    }

    private AccountInfoDTO toAccountInfoDTO(Account account) {
        return AccountInfoDTO.builder()
                .id(account.getId())
                .username(account.getUsername())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    private Criteria buildCriteria(
            String username,
            LocalDateTime createdFrom,
            LocalDateTime createdTo,
            LocalDateTime updatedFrom,
            LocalDateTime updatedTo
    ) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (StringUtils.hasText(username)) {
            criteriaList.add(Criteria.where("username")
                    .regex(Pattern.compile(Pattern.quote(username.trim()), Pattern.CASE_INSENSITIVE)));
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
        if (updatedFrom != null || updatedTo != null) {
            Criteria updatedAtCriteria = Criteria.where("updatedAt");
            if (updatedFrom != null) {
                updatedAtCriteria = updatedAtCriteria.gte(updatedFrom);
            }
            if (updatedTo != null) {
                updatedAtCriteria = updatedAtCriteria.lte(updatedTo);
            }
            criteriaList.add(updatedAtCriteria);
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
}
