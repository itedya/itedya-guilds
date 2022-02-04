package com.itedya.guilds.enums;

public enum MemberRole {
    OWNER, MEMBER;

    public static MemberRole fromString(String val) {
        if ("OWNER".equalsIgnoreCase(val)) {
            return MemberRole.OWNER;
        } else if ("MEMBER".equalsIgnoreCase(val)) {
            return MemberRole.MEMBER;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    }
}
