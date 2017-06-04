package com.nexo.nexorouter.microservice.account.action;

import com.nexo.nexorouter.microservice.common.Action;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Created by carlos on 30/05/17.
 */
public class CleaningUpTokens extends Action {
    @Override
    protected void process(Message<JsonObject> message) {
        JsonObject params = message.body();

        vertx.eventBus().send("account-repository@cleaning-up-tokens", params, ar -> {
            if(ar.succeeded()){
                message.reply(ar.result().body());
            } else {
                message.fail(404, "impossible cleaning up tokens");
            }
        });
    }
}
