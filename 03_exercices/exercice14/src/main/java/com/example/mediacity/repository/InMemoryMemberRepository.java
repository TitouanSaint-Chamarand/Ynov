package com.example.mediacity.repository;

import com.example.mediacity.model.Member;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryMemberRepository implements MemberRepository {

    private final Map<String, Member> members = new HashMap<>();

    @Override
    public Optional<Member> findById(String id) {
        return Optional.ofNullable(members.get(id));
    }

    @Override
    public Member save(Member member) {
        members.put(member.id(), member);
        return member;
    }
}
