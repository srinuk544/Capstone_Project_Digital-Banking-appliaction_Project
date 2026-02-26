package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Account;
import com.example.demo.entity.AccountStatus;
import com.example.demo.repo.AccountRepository;

import jakarta.transaction.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    // Constructor Injection (Best Practice)
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account createAccount(Account account) {
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);
        return accountRepository.save(account);
    }

    public Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

 
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Transactional
    public Account updateBalance(Long id, BigDecimal amount) {
        Account account = getAccount(id);

        if (account.getStatus() == AccountStatus.FROZEN) {
            throw new RuntimeException("Account is frozen");
        }

        account.setBalance(amount);
        return accountRepository.save(account);
    }

    @Transactional
    public Account freezeAccount(Long id) {
        Account account = getAccount(id);
        account.setStatus(AccountStatus.FROZEN);
        return accountRepository.save(account);
    }

    @Transactional
    public Account unfreezeAccount(Long id) {
        Account account = getAccount(id);
        account.setStatus(AccountStatus.ACTIVE);
        return accountRepository.save(account);
    }
}