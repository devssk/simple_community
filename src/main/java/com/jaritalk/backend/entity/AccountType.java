package com.jaritalk.backend.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AccountType {
    LESSOR("임대인"),
    REALTOR("공인중개사"),
    LESSEE("임차인");

    private String value;

    public String getName() {
        return name();
    }

    public String getValue() {
        return this.value;
    }

}
