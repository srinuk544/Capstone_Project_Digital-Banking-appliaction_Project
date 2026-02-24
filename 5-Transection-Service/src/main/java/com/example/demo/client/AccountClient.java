package com.example.demo.client;

import com.example.demo.dto.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "ACCOUNT-SERVICE")
public interface AccountClient {

    @GetMapping("/accounts/{id}")
    AccountResponse getAccount(@PathVariable Long id);

    @PutMapping("/accounts/{id}/balance")
    AccountResponse updateBalance(
            @PathVariable Long id,
            @RequestParam BigDecimal amount
    );
}