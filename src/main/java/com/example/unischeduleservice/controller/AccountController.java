package com.example.unischeduleservice.controller;

import com.example.unischeduleservice.dto.AccountInfoDTO;
import com.example.unischeduleservice.dto.base.PageResponseDTO;
import com.example.unischeduleservice.dto.base.ResponseData;
import com.example.unischeduleservice.models.Account;
import com.example.unischeduleservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author vuvanhoang
 * @created 25/10/25 07:35
 * @project unischedule_service
 */
@RestController
@RequestMapping(path = "/account")
@RequiredArgsConstructor
@Tag(name = "Account", description = "API truy vấn thông tin account")
public class AccountController {
    private final AccountService accountService;

    @GetMapping(path = "/random")
    @Operation(summary = "Lấy random một account")
    public Account getRandom() {
        return accountService.getRandom();
    }

    @GetMapping(path = "/accounts")
    @Operation(summary = "Lấy N account gần nhất")
    public List<Account> getAccounts(@RequestParam(defaultValue = "10") int num) {
        return accountService.getAccountsByNumRecord(num);
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Lấy account theo ID")
    public ResponseData<AccountInfoDTO> getAccountById(
            @Parameter(description = "ID của account") @PathVariable String id) {
        AccountInfoDTO account = accountService.getAccountInfoById(id);
        return account == null ? ResponseData.error(404, "Không tìm thấy account") : ResponseData.success(account);
    }

    @GetMapping(path = "/by-username/{username}")
    @Operation(summary = "Lấy account theo username")
    public ResponseData<AccountInfoDTO> getAccountByUsername(
            @Parameter(description = "Tên đăng nhập") @PathVariable String username) {
        AccountInfoDTO account = accountService.getAccountInfoByUsername(username);
        return account == null ? ResponseData.error(404, "Không tìm thấy account") : ResponseData.success(account);
    }

    @GetMapping(path = "/query")
    @Operation(summary = "Truy vấn account có phân trang và lọc")
    public ResponseData<PageResponseDTO<AccountInfoDTO>> queryAccounts(
            @RequestParam(required = false) String username,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime createdFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime createdTo,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime updatedFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime updatedTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return ResponseData.success(accountService.queryAccounts(
                username,
                createdFrom,
                createdTo,
                updatedFrom,
                updatedTo,
                page,
                size,
                sortBy,
                sortDirection
        ));
    }
}
