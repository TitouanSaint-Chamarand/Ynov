package com.example.mediacity.repository;

import com.example.mediacity.model.Loan;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryLoanRepository implements LoanRepository {

    private final AtomicLong sequence = new AtomicLong(1);
    private final List<Loan> loans = new ArrayList<>();

    @Override
    public Loan save(Loan loan) {
        if (loan.id() == 0) {
            Loan created = new Loan(
                    sequence.getAndIncrement(),
                    loan.memberId(),
                    loan.workId(),
                    loan.loanDate(),
                    loan.dueDate(),
                    loan.returnDate(),
                    loan.penaltyAmount()
            );
            loans.add(created);
            return created;
        }

        loans.removeIf(existing -> existing.id() == loan.id());
        loans.add(loan);
        return loan;
    }

    @Override
    public Optional<Loan> findById(long id) {
        return loans.stream().filter(loan -> loan.id() == id).findFirst();
    }

    @Override
    public Optional<Loan> findActiveByWorkId(String workId) {
        return loans.stream()
                .filter(loan -> loan.workId().equals(workId) && loan.isActive())
                .findFirst();
    }

    @Override
    public List<Loan> findByMemberId(String memberId) {
        return loans.stream().filter(loan -> loan.memberId().equals(memberId)).toList();
    }
}
