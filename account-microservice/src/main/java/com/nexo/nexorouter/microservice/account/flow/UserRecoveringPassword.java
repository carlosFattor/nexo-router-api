package com.nexo.nexorouter.microservice.account.flow;

import com.nexo.nexorouter.microservice.account.models.Token;
import com.nexo.nexorouter.microservice.account.models.User;
import com.nexo.nexorouter.microservice.account.utils.UserHelper;
import com.nexo.nexorouter.microservice.common.Flow;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.LoggerFactory;

/**
 * Created by carlos on 09/05/17.
 */
public class UserRecoveringPassword extends Flow {
    private EventBus eb;
    private static final io.vertx.core.logging.Logger logger = LoggerFactory.getLogger(UserRecoveringPassword.class);

    @Override
    protected void process(Message<JsonObject> message) {
        eb = vertx.eventBus();
        JsonObject body = message.body().getJsonObject("data");
        existUser(body).setHandler(ar -> {
            if (ar.result().isEmpty()) {
                message.fail(404, "User not found");
                return;
            }

            Token token = UserHelper.generateToken();
            User user = new User(ar.result());
            user.getTokens().add(token);
            JsonObject params = new JsonObject()
                    .put("email", user.getEmail())
                    .put("token", token.toJson());

            addingToken(params).setHandler(_ar -> {
                if(_ar.result()){
                    sendEmailToRecoverPassword(user, token);
                    message.reply(ar.result());
                } else {
                    message.fail(404, "Error trying recover password");
                    return;
                }
            });

        });
    }

    private void sendEmailToRecoverPassword(User user, Token token) {
        JsonObject address = new JsonObject()
                .put("from", "carlos.fattor@gmail.com")
                .put("to", user.getEmail())
                .put("subject", "Email de recuperação de senha");
        JsonObject body = new JsonObject()
                .put("name", user.getProfile().getFirstName())
                .put("email", user.getEmail())
                .put("link", "http://localhost:8080/recover-password/validate/" + token.getToken());
        JsonObject email = new JsonObject()
                .put("template", "user_recover_pass")
                .put("address", address)
                .put("body", body);
        JsonObject data = new JsonObject().put("data", email);

        eb.send("mail@email-carrier", data, event -> {
            if(event.succeeded()){
                logger.info("sent email to recover password: " + email);
            } else {
                logger.warn(event.cause());
            }
        });

    }

    private Future<Boolean> addingToken(JsonObject param) {
        Future<Boolean> future = Future.future();
        eb.send("account-repository@user-adding-token", param, ar-> {
            if(ar.succeeded()){
                future.complete(true);
            } else {
                future.failed();
            }
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
}
