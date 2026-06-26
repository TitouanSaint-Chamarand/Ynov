package com.example.bank.repository;

import com.example.bank.model.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    Account save(String number, String holder, BigDecimal balance);

    Optional<Account> findByNumber(String number);

    List<Account> findAll();

    Account updateBalance(String number, BigDecimal balance);

    void deleteAll();
}
