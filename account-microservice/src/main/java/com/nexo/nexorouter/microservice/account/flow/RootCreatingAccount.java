package com.nexo.nexorouter.microservice.account.flow;

import com.nexo.nexorouter.microservice.account.models.Account;
import com.nexo.nexorouter.microservice.common.Flow;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Created by carlos on 19/04/17.
 */
public class RootCreatingAccount extends Flow {
    @Override
    protected void process(Message<JsonObject> message) {

        JsonObject body = message.body().getJsonObject("data");
        body.put("accountId", java.util.UUID.randomUUID().toString());
        body.put("createdAt", System.currentTimeMillis());
        Account account = new Account(body);

        vertx.eventBus().send("account-repository@creating-account", account.toJson(), ar -> {

            if(ar.failed()){
                message.<Message<JsonObject>>reply(new JsonObject().put("error", ar.cause().getCause()));
            } else {
                message.<Message<JsonObject>>reply(ar.result().body());
            }
        });
    }
}
