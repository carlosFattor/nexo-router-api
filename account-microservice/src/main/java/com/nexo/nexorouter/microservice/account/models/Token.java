package com.nexo.nexorouter.microservice.account.models;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Created by carlos on 28/05/17.
 */
@DataObject(generateConverter = true)
public class Token {

    private String token;
    private long validate;

    public Token() {
    }

    public Token(JsonObject json) {
        TokenConverter.fromJson(json, this);
    }

    public Token(Token token) {
        this.token = token.getToken();
        this.validate = token.getValidate();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        TokenConverter.toJson(this, json);
        return json;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getValidate() {
        return validate;
    }

    public void setValidate(long validate) {
        this.validate = validate;
    }

    @Override
    public String toString() {
        return this.toJson().encodePrettily();
    }
}
