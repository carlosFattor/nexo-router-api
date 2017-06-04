package com.nexo.nexorouter.microservice.account.flow;

import com.nexo.nexorouter.microservice.account.models.Token;
import com.nexo.nexorouter.microservice.account.models.User;
import com.nexo.nexorouter.microservice.account.utils.UserHelper;
import com.nexo.nexorouter.microservice.common.Flow;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.LoggerFactory;

import java.util.Optional;

/**
 * Created by carlos on 09/05/17.
 */
public class UserUpdatingPassword extends Flow {
    private EventBus eb;
    private static final io.vertx.core.logging.Logger logger = LoggerFactory.getLogger(UserUpdatingPassword.class);

    @Override
    protected void process(Message<JsonObject> message) {
        eb = vertx.eventBus();
        JsonObject body = message.body().getJsonObject("data");
        JsonObject params = message.body().getJsonObject("params");

        findUserByToken(params).setHandler(ar -> {
            if (!ar.succeeded()) {
                message.fail(404, "User not found");
                return;
            }

            User user = new User(ar.result());

            encryptPassword(body.getString("password")).setHandler(newPass -> {
                body.put("password", newPass.result().getString("password"));
                this.updatePassword(body).setHandler(updatingPass -> {
                    eb.send("account@cleaning-up-tokens", params, updated -> {
                        if(updated.succeeded()){
                            sendEmailToInformChangedPassword(user);
                            message.reply(new JsonObject().put("202", "Updated password OK"));
                        } else {
                            message.fail(404, "impossible update password");
                        }
                    });
                });
            });
        });
    }

    private Future<JsonObject> updatePassword(JsonObject data) {
        System.out.println(data.encodePrettily());
        Future<JsonObject> future = Future.future();
        eb.send("account-repository@user-updating-password", data, ar-> {
            future.complete((JsonObject) ar.result().body());
        });
        return future;
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

    private void sendEmailToInformChangedPassword(User user) {
        JsonObject address = new JsonObject()
                .put("from", "carlos.fattor@gmail.com")
                .put("to", user.getEmail())
                .put("subject", "Email from updated password");
        JsonObject body = new JsonObject()
                .put("name", user.getProfile().getFirstName())
                .put("email", user.getEmail());
        JsonObject email = new JsonObject()
                .put("template", "user_updated_pass")
                .put("address", address)
                .put("body", body);
        JsonObject data = new JsonObject().put("data", email);

        eb.send("mail@email-carrier", data, event -> {
            if (event.succeeded()) {
                logger.info("sent email to inform updated password: " + email);
            } else {
                logger.warn(event.cause());
            }
        });
    }
}
