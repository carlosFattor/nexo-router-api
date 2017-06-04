package com.nexo.nexorouter.microservice.mail.flow;

import com.nexo.nexorouter.microservice.common.Flow;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;


/**
 * Created by carlos on 21/05/17.
 */
public class EmailCarrier extends Flow {
    private EventBus eb;

    @Override
    protected void process(Message<JsonObject> message) {
        eb = vertx.eventBus();
        JsonObject body = message.body().getJsonObject("data");
        eb.send("mail@make-email", body, ar -> {
            if(ar.succeeded()){
                eb.send("mail@send-email", ar.result().body(), _ar -> {
                    message.reply(new JsonObject().put("email", "sent"));
                });
            } else {

                message.reply(new JsonObject().put("email", "failed"));
            }
        });
    }
}
