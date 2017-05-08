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
public class UserLogging extends Flow {

    private EventBus eb;

    @Override
    protected void process(Message<JsonObject> message) {
        eb = vertx.eventBus();
        JsonObject body = message.body().getJsonObject("data");

        existUser(body).compose(jsonUser -> {
            if (jsonUser.isEmpty()) {
                message.reply(null);
                return;
            }
            User user = new User(jsonUser);

            JsonObject params = new JsonObject()
                    .put("password", body.getString("password"))
                    .put("hash_password", user.getPassword());

            if(user.isActive()){

                validatingPassword(params).setHandler(pass -> {

                    if (pass.result()) {
                        createJwt(user).setHandler(token -> {
                            JsonObject response = token.result();
                            response.put("user", user.getSimpleUser())
                                    .put("redirect", "dashboard");
                            isAdmin(user, response);
                            JsonObject data = new JsonObject().put("data", response);
                            message.reply(data);
                        });

                    } else {
                        message.reply(null);
                    }
                });
            } else {
                message.fail(403, "not authorized");
            }

        }, Future.future().setHandler(fail -> {
            message.reply(new JsonObject().put("error", fail.cause()));
        }));
    }

    private void isAdmin(User user, JsonObject response) {
        if(user.isAdmin()){
            response.put("isAdmin", true);
        }
    }

    private Future<JsonObject> createJwt(User user){
        Future<JsonObject> future = Future.future();

        eb.send("account@creating-jwt", user.toJson(), ar -> {
            future.complete((JsonObject) ar.result().body());
        });

        return future;
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

    private Future<Boolean> validatingPassword(JsonObject params) {
        Future<Boolean> future = Future.future();

        eb.send("account@validating-user-password", params, ar -> {
            future.complete((Boolean) ar.result().body());
        });

        return future;
    }
}