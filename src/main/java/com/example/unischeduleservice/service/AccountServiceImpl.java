package com.example.unischeduleservice.service;

import com.example.unischeduleservice.dto.*;
import com.example.unischeduleservice.models.Account;
import com.example.unischeduleservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @author vuvanhoang
 * @created 25/10/25 07:36
 * @project unischedule_service
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Account getRandom() {
        Random random = new Random();
        List<Account> accounts = accountRepository.findAll();
        return accounts.get(random.nextInt(accounts.size()));
    }

    @Override
    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    @Override
    public void saveNewAccount(Account account) {
        accountRepository.save(account);
    }

    @Override
    public List<Account> getAccountsByNumRecord(int num) {
        return accountRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, num));
    }

    @Override
    public FullInfoReportOverviewDTO fullInfoReportOverview() {
        FullInfoReportOverviewDTO fullInfoReportOverviewDTO;
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<TotalVisitsDTO> totalVisitsDTOFuture = executor.submit(this::processGetDataTotalVisit);
            Future<VisitsDayInfoDTO> totalVisitsInDayDTOFuture = executor.submit(this::processGetDataTotalVisitByDay);
            TotalVisitsDTO totalVisitsDTO = totalVisitsDTOFuture.get();
            VisitsDayInfoDTO visitsDayInfoDTO = totalVisitsInDayDTOFuture.get();
            fullInfoReportOverviewDTO = FullInfoReportOverviewDTO.builder()
                    .totalVisits(totalVisitsDTO)
                    .visitsDayInfoDTO(visitsDayInfoDTO)
                    .build();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return fullInfoReportOverviewDTO;
    }

    private TotalVisitsDTO processGetDataTotalVisit() {
        return TotalVisitsDTO.builder()
                .total(accountRepository.count())
                .visitByMonth(accountRepository.countVisitByMonth())
                .build();
    }

    private VisitsDayInfoDTO processGetDataTotalVisitByDay() {
        List<VisitsInDayDTO> visitsInDayDTOList = accountRepository.countVisitByDay();
        return VisitsDayInfoDTO.builder()
                .visitsInDays(visitsInDayDTOList)
                .total(visitsInDayDTOList.isEmpty() ? 0 : visitsInDayDTOList.getLast().getTotalVisit())
                .build();
    }
}
