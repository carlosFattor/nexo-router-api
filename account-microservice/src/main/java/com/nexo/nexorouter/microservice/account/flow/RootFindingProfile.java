package com.nexo.nexorouter.microservice.account.flow;

import com.nexo.nexorouter.microservice.account.models.User;
import com.nexo.nexorouter.microservice.common.Flow;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Created by carlos on 22/04/17.
 */
public class RootFindingProfile extends Flow{

    private EventBus eb;

    @Override
    protected void process(Message<JsonObject> message) {
        eb = vertx.eventBus();
        JsonObject body = message.body();
        existUser(body.getJsonObject("params")).compose(jsonUser -> {
            if(jsonUser.isEmpty()){
                message.fail(404, "User not found");
                return;
            }

            User user = new User(jsonUser);
            message.reply(user.getProfile().toJson());

        }, Future.future().setHandler( fail -> {
            message.reply(new JsonObject().put("error", fail.cause()));
        }));
    }

    private Future<JsonObject> existUser(JsonObject params) {
        Future<JsonObject> future = Future.future();
        if(params.containsKey("email")){
            eb.send("account@user-exist", params, ar -> {
                future.complete((JsonObject) ar.result().body());
            });
        } else {
            future.complete(new JsonObject());
        }
        return future;
    }
}
