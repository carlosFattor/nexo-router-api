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
 * Converter for {@link com.nexo.nexorouter.microservice.account.models.Profile}.
 *
 * NOTE: This class has been automatically generated from the {@link com.nexo.nexorouter.microservice.account.models.Profile} original class using Vert.x codegen.
 */
public class ProfileConverter {

  public static void fromJson(JsonObject json, Profile obj) {
    if (json.getValue("accountId") instanceof String) {
      obj.setAccountId((String)json.getValue("accountId"));
    }
    if (json.getValue("avatar") instanceof String) {
      obj.setAvatar((String)json.getValue("avatar"));
    }
    if (json.getValue("confirmed") instanceof Boolean) {
      obj.setConfirmed((Boolean)json.getValue("confirmed"));
    }
    if (json.getValue("firstName") instanceof String) {
      obj.setFirstName((String)json.getValue("firstName"));
    }
    if (json.getValue("gender") instanceof String) {
      obj.setGender((String)json.getValue("gender"));
    }
    if (json.getValue("lastName") instanceof String) {
      obj.setLastName((String)json.getValue("lastName"));
    }
    if (json.getValue("location") instanceof String) {
      obj.setLocation((String)json.getValue("location"));
    }
    if (json.getValue("roles") instanceof JsonArray) {
      java.util.ArrayList<com.nexo.nexorouter.microservice.common.enums.Role> list = new java.util.ArrayList<>();
      json.getJsonArray("roles").forEach( item -> {
        if (item instanceof String)
          list.add(com.nexo.nexorouter.microservice.common.enums.Role.valueOf((String)item));
      });
      obj.setRoles(list);
    }
  }

  public static void toJson(Profile obj, JsonObject json) {
    if (obj.getAccountId() != null) {
      json.put("accountId", obj.getAccountId());
    }
    if (obj.getAvatar() != null) {
      json.put("avatar", obj.getAvatar());
    }
    if (obj.getConfirmed() != null) {
      json.put("confirmed", obj.getConfirmed());
    }
    if (obj.getFirstName() != null) {
      json.put("firstName", obj.getFirstName());
    }
    if (obj.getGender() != null) {
      json.put("gender", obj.getGender());
    }
    if (obj.getLastName() != null) {
      json.put("lastName", obj.getLastName());
    }
    if (obj.getLocation() != null) {
      json.put("location", obj.getLocation());
    }
    if (obj.getRoles() != null) {
      JsonArray array = new JsonArray();
      obj.getRoles().forEach(item -> array.add(item.name()));
      json.put("roles", array);
    }
  }
}