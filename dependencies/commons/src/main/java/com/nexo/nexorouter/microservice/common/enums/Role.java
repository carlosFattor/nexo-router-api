package com.nexo.nexorouter.microservice.common.enums;

/**
 * Created by carlos on 21/04/17.
 */
public enum Role {
    ADMIN("admin"),
    DEFAULT("default"),
    DRIVER("driver"),
    OPERATOR("operator");

    public String typeUser;

    private Role(String type){
        this.typeUser = type;
    }

    public String getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(String role) {
        this.typeUser = role;
    }
}
