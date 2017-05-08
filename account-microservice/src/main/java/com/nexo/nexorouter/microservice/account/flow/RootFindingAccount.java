package com.nexo.nexorouter.microservice.account.flow;

import com.nexo.nexorouter.microservice.common.Flow;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Created by carlos on 20/04/17.
 */
public class RootFindingAccount extends Flow {
    @Override
    protected void process(Message<JsonObject> message) {
        JsonObject query = new JsonObject().put("accountId", message.body().getString("accountId"));

        vertx.eventBus().send("account-repository@finding-account", query, ar -> {
            if(ar.failed()){
                message.<Message<JsonObject>>reply(new JsonObject().put("error", ar.cause().getCause()));
            } else {
                message.<Message<JsonObject>>reply(ar.result().body());
            }
        });
    }
}
