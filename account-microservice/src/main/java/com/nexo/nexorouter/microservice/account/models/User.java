package com.nexo.nexorouter.microservice.account.models;

import com.nexo.nexorouter.microservice.common.enums.Role;
import com.nexo.nexorouter.microservice.common.enums.UserStatus;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by carlos on 21/04/17.
 */
@DataObject(generateConverter = true)
public class User {

    private String userId;
    private long createdAt;
    private long updateAt;
    private String email;
    private String password;
    private UserStatus userStatus;
    private Profile profile;
    private List<Token> tokens;

    public User() {
    }

    public User(User user) {
        this.userId = user.userId;
        this.createdAt = user.createdAt;
        this.updateAt = user.updateAt;
        this.email = user.email;
        this.password = user.password;
        this.userStatus = user.userStatus;
        this.profile = user.profile;
        this.tokens = user.tokens;
    }

    public User(JsonObject json) {
        UserConverter.fromJson(json, this);
    }

    public JsonObject toJson(){
        JsonObject json = new JsonObject();
        UserConverter.toJson(this, json);
        return json;
    }

    @GenIgnore
    public JsonObject getSimpleUser(){
        Profile profile = this.profile;
        JsonObject simpleUser = new JsonObject()
                .put("userId", this.userId)
                .put("firstName", profile.getFirstName())
                .put("lastName", profile.getLastName())
                .put("email", this.email)
                .put("location", profile.getLocation())
                .put("confirmed", profile.getConfirmed());

        return simpleUser;
    }

    @GenIgnore
    public Boolean isAdmin() {
        return this.profile.getRoles().contains(Role.ADMIN);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Boolean isActive(){
        return this.userStatus.equals(UserStatus.ACTIVE);
    }

    @Override
    public String toString() {
        return this.toJson().encodePrettily();
    }
}
