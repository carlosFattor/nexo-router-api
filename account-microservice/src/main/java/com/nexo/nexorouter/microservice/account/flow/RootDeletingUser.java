package com.nexo.nexorouter.microservice.account.flow;

import com.nexo.nexorouter.microservice.account.models.User;
import com.nexo.nexorouter.microservice.common.Flow;
import com.nexo.nexorouter.microservice.common.enums.UserStatus;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Created by carlos on 27/04/17.
 */
public class RootDeletingUser extends Flow {

    EventBus eb;

    @Override
    protected void process(Message<JsonObject> message) {
        eb = vertx.eventBus();

        JsonObject header = new JsonObject(message.body().getString("header"));
        System.out.println(header.encodePrettily());
        JsonObject body = message.body();

        existUser(body).setHandler(userJson -> {
            User user = new User(userJson.result());
            String accountId = header.getJsonObject("SUBJECT").getString("accountId");
            if(isSameAccount(user, accountId)){
                user.setUserStatus(UserStatus.INACTIVE);
                System.out.println(user.toJson().encodePrettily());
                eb.send("account-repository@root-updating-user", user.toJson(), ar -> {
                     message.reply(new JsonObject().put("200", "resource deleted successfully"));
                });
            } else {
                message.fail(403, "not authorized");
            }
        });
    }

    private boolean isSameAccount(User user, String accountId) {
        System.out.println(accountId);
        return user.getProfile().getAccountId().equals(accountId);
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
