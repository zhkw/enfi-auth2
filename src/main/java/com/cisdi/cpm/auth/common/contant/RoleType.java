package com.cisdi.cpm.auth.common.contant;

public enum RoleType {
    SYSTEM("M"),
    PROJECT("S"),
    ADMIN("A");

    private String roleType;
    private RoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getRoleType() {
        return this.roleType;
    }
}
