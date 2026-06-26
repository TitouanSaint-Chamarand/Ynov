package com.example.mediacity.repository;

import com.example.mediacity.model.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanRepository {

    Loan save(Loan loan);

    Optional<Loan> findById(long id);

    Optional<Loan> findActiveByWorkId(String workId);

    List<Loan> findByMemberId(String memberId);
}
