package com.example.unischeduleservice.service;

import com.example.unischeduleservice.dto.AccountReportDTO;
import com.example.unischeduleservice.dto.CategoryCountDTO;
import com.example.unischeduleservice.dto.ReportDashboardDTO;
import com.example.unischeduleservice.models.Device;
import com.example.unischeduleservice.models.Review;
import com.example.unischeduleservice.models.UserAccessLogs;
import com.example.unischeduleservice.repository.DeviceRepository;
import com.example.unischeduleservice.repository.ReviewRepository;
import com.example.unischeduleservice.repository.UserAccessLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private static final DateTimeFormatter DATE_LABEL = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final AccountService accountService;
    private final UserAccessLogRepository userAccessLogRepository;
    private final DeviceRepository deviceRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public ReportDashboardDTO getDashboard() {
        List<AccountReportDTO> allAccounts = accountService.getAllAccountsForReport();
        List<AccountReportDTO> recentAccounts = accountService.getRecentAccounts(10);
        List<UserAccessLogs> accessLogs = userAccessLogRepository.findAll();
        List<Device> devices = deviceRepository.findAll();
        List<Review> reviews = reviewRepository.findAll();

        return ReportDashboardDTO.builder()
                .generatedAt(LocalDateTime.now())
                .accountStatistics(accountService.getAccountStatistics())
                .accountBatchSummary(accountService.getAccountBatchSummary())
                .accountBatchStatsSummary(accountService.getAccountBatchStatsSummary())
                .totalReviews(reviews.size())
                .totalDevices(devices.size())
                .totalAccessLogs(accessLogs.size())
                .recentAccounts(recentAccounts)
                .allAccounts(allAccounts)
                .accountBatches(accountService.getAllAccountBatches())
                .accountBatchStats(accountService.getAllAccountBatchStats())
                .accountStatusChart(countByCategory(allAccounts, AccountReportDTO::getAccountStatus))
                .accountCreationChart(countByDate(allAccounts, AccountReportDTO::getCreatedAt))
                .accessLogOsChart(countByCategory(accessLogs, UserAccessLogs::getOsOfDevice))
                .deviceOsChart(countByCategory(devices, Device::getOs))
                .reviewTimelineChart(countByDate(reviews, Review::getCreatedAt))
                .build();
    }

    private <T> List<CategoryCountDTO> countByCategory(List<T> items, Function<T, String> extractor) {
        return items.stream()
                .map(extractor)
                .filter(StringUtils::hasText)
                .collect(Collectors.groupingBy(
                        value -> value.trim(),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .map(entry -> CategoryCountDTO.builder()
                        .label(entry.getKey())
                        .value(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(CategoryCountDTO::getLabel, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private <T> List<CategoryCountDTO> countByDate(List<T> items, Function<T, LocalDateTime> extractor) {
        Map<LocalDate, Long> counts = items.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        dateTime -> dateTime.toLocalDate(),
                        Collectors.counting()
                ));

        return counts.entrySet().stream()
                .map(entry -> CategoryCountDTO.builder()
                        .label(entry.getKey().format(DATE_LABEL))
                        .value(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(CategoryCountDTO::getLabel))
                .toList();
    }
}
