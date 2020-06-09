package com.cisdi.cpm.auth.common.contant;

public enum ModuleType {
    SYSTEM("M"),
    PROJECT("S");

    private String moduleType;
    private ModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public String getModuleType() {
        return this.moduleType;
    }
}
