package com.nexo.nexorouter.microservice.common.enums;

/**
 * Created by carlos on 21/04/17.
 */
public enum Role {
    ADMIN("admin"), DEFAULT("default");

    private String typeUser;

    Role(String type){
        this.typeUser = type;
    }

    public String getTypeUser() {
        return typeUser;
    }
}
