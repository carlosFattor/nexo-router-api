package com.nexo.nexorouter.microservice.account.models;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Created by carlos on 18/04/17.
 */
@DataObject(generateConverter = true)
public class Account {

    private String name;
    private String accountId;

    private long createdAt;

    public Account() {
    }

    public Account(Account account) {
        this.name = account.name;
        this.accountId = account.accountId;
        this.createdAt = account.createdAt;
    }

    public Account(JsonObject json) {
        AccountConverter.fromJson(json, this);
    }

    public JsonObject toJson(){
        JsonObject json = new JsonObject();
        AccountConverter.toJson(this, json);
        return json;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return this.toJson().encodePrettily();
    }
}
