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
 * Converter for {@link com.nexo.nexorouter.microservice.account.models.Account}.
 *
 * NOTE: This class has been automatically generated from the {@link com.nexo.nexorouter.microservice.account.models.Account} original class using Vert.x codegen.
 */
public class AccountConverter {

  public static void fromJson(JsonObject json, Account obj) {
    if (json.getValue("accountId") instanceof String) {
      obj.setAccountId((String)json.getValue("accountId"));
    }
    if (json.getValue("createdAt") instanceof Number) {
      obj.setCreatedAt(((Number)json.getValue("createdAt")).longValue());
    }
    if (json.getValue("name") instanceof String) {
      obj.setName((String)json.getValue("name"));
    }
  }

  public static void toJson(Account obj, JsonObject json) {
    if (obj.getAccountId() != null) {
      json.put("accountId", obj.getAccountId());
    }
    json.put("createdAt", obj.getCreatedAt());
    if (obj.getName() != null) {
      json.put("name", obj.getName());
    }
  }
}