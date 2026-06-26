package com.example.mediacity.model;

public record Work(String id, String title, String heldForMemberId) {

    public Work(String id, String title) {
        this(id, title, null);
    }

    public Work withHeldForMember(String memberId) {
        return new Work(id, title, memberId);
    }

    public Work withoutHold() {
        return new Work(id, title, null);
    }
}
