package com.example.bank.controller;

import com.example.bank.model.AccountResponse;
import com.example.bank.model.AmountRequest;
import com.example.bank.model.CreateAccountRequest;
import com.example.bank.model.TransferRequest;
import com.example.bank.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody CreateAccountRequest request) {
        var createdAccount = service.create(request.number(), request.holder());
        var response = AccountResponse.from(createdAccount);

        return ResponseEntity
                .created(URI.create("/api/accounts/" + response.number()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> findAll() {
        var responses = service.findAll()
                .stream()
                .map(AccountResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{number}")
    public ResponseEntity<AccountResponse> findByNumber(@PathVariable String number) {
        return ResponseEntity.ok(AccountResponse.from(service.getByNumber(number)));
    }

    @PostMapping("/{number}/deposit")
    public ResponseEntity<AccountResponse> deposit(
            @PathVariable String number,
            @Valid @RequestBody AmountRequest request
    ) {
        return ResponseEntity.ok(AccountResponse.from(service.deposit(number, request.amount())));
    }

    @PostMapping("/{number}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(
            @PathVariable String number,
            @Valid @RequestBody AmountRequest request
    ) {
        return ResponseEntity.ok(AccountResponse.from(service.withdraw(number, request.amount())));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequest request) {
        service.transfer(request.fromNumber(), request.toNumber(), request.amount());
        return ResponseEntity.noContent().build();
    }
}
