package com.example.demo.service;

import com.example.demo.entity.Account;
import com.example.demo.entity.AccountStatus;
import com.example.demo.repo.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository repository;

    @InjectMocks
    private AccountService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAccount_Success() {

        Account account = new Account();
        account.setAccountId(1L);
        account.setBalance(BigDecimal.valueOf(1000));
        account.setStatus(AccountStatus.ACTIVE);

        when(repository.findById(1L))
                .thenReturn(Optional.of(account));

        Account result = service.getAccount(1L);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1000), result.getBalance());
        assertEquals(AccountStatus.ACTIVE, result.getStatus());
    }

    @Test
    void testGetAccount_NotFound() {

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.getAccount(1L));
    }

    @Test
    void testFreezeAccount() {

        Account account = new Account();
        account.setAccountId(1L);
        account.setStatus(AccountStatus.ACTIVE);

        when(repository.findById(1L))
                .thenReturn(Optional.of(account));

        // Mock save()
        when(repository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Account result = service.freezeAccount(1L);

        assertNotNull(result);
        assertEquals(AccountStatus.FROZEN, result.getStatus());
    }

    @Test
    void testUpdateBalance() {

        Account account = new Account();
        account.setAccountId(1L);
        account.setBalance(BigDecimal.valueOf(500));
        account.setStatus(AccountStatus.ACTIVE);

        when(repository.findById(1L))
                .thenReturn(Optional.of(account));

        // Mock save()
        when(repository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Account result = service.updateBalance(1L, BigDecimal.valueOf(2000));

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(2000), result.getBalance());
    }
}