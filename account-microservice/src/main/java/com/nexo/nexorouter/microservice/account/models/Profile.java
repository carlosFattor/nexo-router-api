package com.nexo.nexorouter.microservice.account.models;

import com.nexo.nexorouter.microservice.common.enums.Role;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by carlos on 21/04/17.
 */
@DataObject(generateConverter = true)
public class Profile {
    private String firstName;
    private String lastName;
    private String gender;
    private String location;
    private String avatar;
    private String accountId;
    private List<Role> roles;
    private Boolean confirmed;

    public Profile() {
    }

    public Profile(Profile profile) {
        this.firstName = profile.firstName;
        this.lastName = profile.lastName;
        this.gender = profile.gender;
        this.location = profile.location;
        this.avatar = profile.avatar;
        this.accountId = profile.accountId;
        this.roles = profile.roles;
        this.confirmed = profile.confirmed;
    }

    public Profile(JsonObject json) {
        ProfileConverter.fromJson(json, this);
    }

    public JsonObject toJson(){
        JsonObject json = new JsonObject();
        ProfileConverter.toJson(this, json);
        return json;
    }

    public Profile(String firstName, String lastName, String gender, String location, String avatar, String accountId, List<Role> roles, Boolean confirmed) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.location = location;
        this.avatar = avatar;
        this.accountId = accountId;
        this.roles = roles;
        this.confirmed = confirmed;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
