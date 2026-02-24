package com.example.demo.controller;

import com.example.demo.dto.TransactionRequest;
import com.example.demo.entity.Transaction;
import com.example.demo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService service;

    @PostMapping("/deposit")
    public Transaction deposit(@RequestBody TransactionRequest request) {
        return service.deposit(request);
    }

    @PostMapping("/withdraw")
    public Transaction withdraw(@RequestBody TransactionRequest request) {
        return service.withdraw(request);
    }

    @GetMapping("/{accountId}")
    public List<Transaction> history(@PathVariable Long accountId) {
        return service.history(accountId);
    }
}


