/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.nexo.nexorouter.microservice.account.models;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link com.nexo.nexorouter.microservice.account.models.User}.
 *
 * NOTE: This class has been automatically generated from the {@link com.nexo.nexorouter.microservice.account.models.User} original class using Vert.x codegen.
 */
public class UserConverter {

  public static void fromJson(JsonObject json, User obj) {
    if (json.getValue("createdAt") instanceof Number) {
      obj.setCreatedAt(((Number)json.getValue("createdAt")).longValue());
    }
    if (json.getValue("email") instanceof String) {
      obj.setEmail((String)json.getValue("email"));
    }
    if (json.getValue("password") instanceof String) {
      obj.setPassword((String)json.getValue("password"));
    }
    if (json.getValue("profile") instanceof JsonObject) {
      obj.setProfile(new com.nexo.nexorouter.microservice.account.models.Profile((JsonObject)json.getValue("profile")));
    }
    if (json.getValue("tokens") instanceof JsonArray) {
      java.util.ArrayList<com.nexo.nexorouter.microservice.account.models.Token> list = new java.util.ArrayList<>();
      json.getJsonArray("tokens").forEach( item -> {
        if (item instanceof JsonObject)
          list.add(new com.nexo.nexorouter.microservice.account.models.Token((JsonObject)item));
      });
      obj.setTokens(list);
    }
    if (json.getValue("updateAt") instanceof Number) {
      obj.setUpdateAt(((Number)json.getValue("updateAt")).longValue());
    }
    if (json.getValue("userId") instanceof String) {
      obj.setUserId((String)json.getValue("userId"));
    }
    if (json.getValue("userStatus") instanceof String) {
      obj.setUserStatus(com.nexo.nexorouter.microservice.common.enums.UserStatus.valueOf((String)json.getValue("userStatus")));
    }
  }

  public static void toJson(User obj, JsonObject json) {
    if (obj.isActive() != null) {
      json.put("active", obj.isActive());
    }
    json.put("createdAt", obj.getCreatedAt());
    if (obj.getEmail() != null) {
      json.put("email", obj.getEmail());
    }
    if (obj.getPassword() != null) {
      json.put("password", obj.getPassword());
    }
    if (obj.getProfile() != null) {
      json.put("profile", obj.getProfile().toJson());
    }
    if (obj.getTokens() != null) {
      JsonArray array = new JsonArray();
      obj.getTokens().forEach(item -> array.add(item.toJson()));
      json.put("tokens", array);
    }
    json.put("updateAt", obj.getUpdateAt());
    if (obj.getUserId() != null) {
      json.put("userId", obj.getUserId());
    }
    if (obj.getUserStatus() != null) {
      json.put("userStatus", obj.getUserStatus().name());
    }
  }
}