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
 * Converter for {@link com.nexo.nexorouter.microservice.account.models.Token}.
 *
 * NOTE: This class has been automatically generated from the {@link com.nexo.nexorouter.microservice.account.models.Token} original class using Vert.x codegen.
 */
public class TokenConverter {

  public static void fromJson(JsonObject json, Token obj) {
    if (json.getValue("token") instanceof String) {
      obj.setToken((String)json.getValue("token"));
    }
    if (json.getValue("validate") instanceof Number) {
      obj.setValidate(((Number)json.getValue("validate")).longValue());
    }
  }

  public static void toJson(Token obj, JsonObject json) {
    if (obj.getToken() != null) {
      json.put("token", obj.getToken());
    }
    json.put("validate", obj.getValidate());
  }
}