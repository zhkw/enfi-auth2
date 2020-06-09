package com.cisdi.cpm.auth.common.contant;

public enum DataStatus {
    DELETE("DEL"),
    ADD("ADD"),
    SYNC("SYNC");

    private String dataStatus;

    private DataStatus(String dataStatus) {
        this.dataStatus = dataStatus;
    }

    public String getDataStatus() {
        return this.dataStatus;
    }
}
