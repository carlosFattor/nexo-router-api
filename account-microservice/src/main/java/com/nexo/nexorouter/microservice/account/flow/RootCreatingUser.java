package com.nexo.nexorouter.microservice.account.flow;

import com.nexo.nexorouter.microservice.account.models.User;
import com.nexo.nexorouter.microservice.account.utils.UserHelper;
import com.nexo.nexorouter.microservice.common.Flow;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.LoggerFactory;

import java.util.Optional;

/**
 * Created by carlos on 21/04/17.
 */
public class RootCreatingUser extends Flow {

    private EventBus eb;
    private static final io.vertx.core.logging.Logger logger = LoggerFactory.getLogger(RootCreatingUser.class);

    @Override
    protected void process(Message<JsonObject> message) {
        eb = vertx.eventBus();

        JsonObject data = message.body().getJsonObject("data");
        Future<Boolean> existUser = existUser(data.getString("email"));
        Future<String> passwordEncrypt = encryptPassword(data.getString("password"));
        JsonObject header = new JsonObject(message.body().getString("header"));
        existUser.compose(exist -> {
            if(exist.booleanValue()){
                message.fail(400, "Create user impossible.");
                logger.warn("Attempt to create a new user with: " + data.getString("email"));
                return;
            }

            passwordEncrypt.setHandler(pass -> {
                User user = createUser(data, header, pass.result());
                user.setUserId(java.util.UUID.randomUUID().toString());
                eb.send("account-repository@creating-user", user.toJson(), ar -> {
                    if(ar.succeeded()){
                        sendEmailToNewUser(user);
                        message.reply(new JsonObject().put("status", "created"));
                    } else {
                        logger.warn("Error trying create a new user: " + ar.cause());
                        message.reply(ar.cause());
                    }
                });
            });
        }, Future.future().setHandler( fail -> {
            logger.error("error trying find a user: " + fail.cause());
            message.reply(new JsonObject().put("error", fail.cause()));
        }));

    }

    private void sendEmailToNewUser(User user) {
        JsonObject address = new JsonObject()
                .put("from", "carlos.fattor@gmail.com")
                .put("to", user.getEmail())
                .put("subject", "Email de boas vindas");
        JsonObject body = new JsonObject()
                .put("name", user.getProfile().getFirstName())
                .put("email", user.getEmail())
                .put("link", "http://localhost:8080/active-user/" + user.getTokens().get(0).getToken());
        JsonObject email = new JsonObject()
                .put("template", "user_created")
                .put("address", address)
                .put("body", body);
        JsonObject data = new JsonObject().put("data", email);

        eb.send("mail@email-carrier", data, event -> {
            if(event.succeeded()){
                logger.info("sent a new email: " + email);
            } else {
                logger.warn(event.cause());
            }
        });

    }

    private User createUser(JsonObject body, JsonObject header, String pass) {
        User user = UserHelper.createUser(body, header, pass);
        return user;
    }

    private Future<Boolean> existUser(String email) {
        Future<Boolean> future = Future.future();
        JsonObject param = new JsonObject().put("email", email);

        eb.send("account-repository@load-user-by-email", param, ar-> {
            boolean present = Optional.ofNullable(ar.result().body()).isPresent();
            if(present){
                future.complete(true);
            } else {
                future.complete(false);
            }
        });

        return future;
    }

    private Future<String> encryptPassword(String password){
        Future<String> future = Future.future();
        JsonObject param = new JsonObject().put("password", password);
        eb.send("account@encrypting-user-password", param, ar -> {
            if(ar.succeeded()){
                future.complete(((JsonObject)ar.result().body()).getString("password"));
            } else {
                future.fail(ar.cause().getMessage());
            }
        });

        return future;
    }
}
