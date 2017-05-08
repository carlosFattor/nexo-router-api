package com.nexo.nexorouter.microservice.common.enums;

/**
 * Created by carlos on 24/04/17.
 */
public enum UserStatus {
    ACTIVE("active"), INACTIVE("inactive"), BLOCKED("blocked");

    private String userStatus;

    UserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserStatus(){
        return this.userStatus;
    }
}
