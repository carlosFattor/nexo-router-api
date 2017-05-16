package com.nexo.nexorouter.microservice.account.action;

import com.nexo.nexorouter.microservice.common.Action;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Created by carlos on 21/04/17.
 */
public class UserExist extends Action{
    @Override
    protected void process(Message<JsonObject> message) {
        vertx.eventBus().send("account-repository@finding-user", message.body(), ar -> {
            if (ar.succeeded() && ar.result().body() != null) {
                message.reply(ar.result().body());
            } else {
                message.reply(new JsonObject());
            }
        });
    }
}
