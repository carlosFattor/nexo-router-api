package com.nexo.nexorouter.microservice.account.flow;

import com.nexo.nexorouter.microservice.account.models.User;
import com.nexo.nexorouter.microservice.common.Action;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Created by carlos on 30/05/17.
 */
public class UserActivatingUser extends Action{
    private EventBus eb;

    @Override
    protected void process(Message<JsonObject> message) {
        eb = vertx.eventBus();
        JsonObject body = message.body();
        JsonObject data = body.getJsonObject("data");
        System.out.println(body.encodePrettily());
        JsonObject params = body.getJsonObject("params");
        findUserByToken(params).setHandler(ar -> {
            if (!ar.succeeded()) {
                message.fail(404, "User not found");
                return;
            }

            encryptPassword(data.getString("password")).setHandler( newPass -> {
                JsonObject query = new JsonObject()
                        .put("email", data.getString("email"))
                        .put("confirmed", true)
                        .put("password", newPass.result().getString("password"));

                updateUserToConfirmed(query).setHandler(updated -> {
                    if (updated.result()) {
                        message.reply(new JsonObject().put("202", "User activated"));
                    } else {
                        message.fail(404, "Impossible activated user");
                    }
                });
            });

        });
    }

    private Future<JsonObject> encryptPassword(String password){
        Future<JsonObject> future = Future.future();
        JsonObject param = new JsonObject().put("password", password);
        eb.send("account@encrypting-user-password", param, ar -> {
            if(ar.succeeded()){
                future.complete((JsonObject) ar.result().body());
            } else {
                future.fail(ar.cause().getMessage());
            }
        });

        return future;
    }

    private Future<Boolean> updateUserToConfirmed(JsonObject params) {
        Future<Boolean> future = Future.future();
        this.eb.send("account-repository@user-activating-user", params, ar -> {
            if (ar.succeeded()) {
                future.complete(true);
            } else {
                future.failed();
            }
        });

        return future;
    }

    private Future<JsonObject> findUserByToken(JsonObject params) {
        Future<JsonObject> future = Future.future();

        this.eb.send("account-repository@finding-user-by-token", params, ar -> {
            if (ar.succeeded()) {
                future.complete((JsonObject) ar.result().body());
            } else {
                future.failed();
            }
        });

        return future;
    }
}
