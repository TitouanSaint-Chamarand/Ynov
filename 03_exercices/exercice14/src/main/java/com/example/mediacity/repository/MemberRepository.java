package com.example.mediacity.repository;

import com.example.mediacity.model.Member;

import java.util.Optional;

public interface MemberRepository {

    Optional<Member> findById(String id);

    Member save(Member member);
}
