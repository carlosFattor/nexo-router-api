package com.nexo.nexorouter.microservice.account.flow;

import com.nexo.nexorouter.microservice.account.models.Token;
import com.nexo.nexorouter.microservice.account.models.User;
import com.nexo.nexorouter.microservice.common.Flow;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by carlos on 22/04/17.
 */
public class UserFindingProfileByToken extends Flow {

    private EventBus eb;

    @Override
    protected void process(Message<JsonObject> message) {
        eb = vertx.eventBus();
        JsonObject params = message.body();
        LocalDate now = LocalDate.now();
        findUserByToken(params).setHandler(ar -> {
            if(!ar.succeeded()){
                message.fail(404, "User not found");
                return;
            }

            User user = new User(ar.result());

            Optional<Token> userToken = user.getTokens().stream()
                    .filter(_token -> _token.getToken().equals(params.getString("token")))
                    .findFirst();
            if(userToken.isPresent()){

                LocalDate validate = Instant.ofEpochMilli(userToken.get().getValidate()).atZone(ZoneId.systemDefault()).toLocalDate();

                if(isValid(now, validate)){
                    message.reply(user.getSimpleUser());
                } else {
                    message.fail(400, "Token invalid");
                }
            } else {
                message.fail(400, "Token invalid");
            }
        });

    }

    private Boolean isValid(LocalDate now, LocalDate validate) {
        return validate.isBefore(now);
    }

    private Future<JsonObject> findUserByToken(JsonObject params) {
        Future<JsonObject> future = Future.future();

        this.eb.send("account-repository@finding-user-by-token", params, ar -> {
            if(ar.succeeded()){
                future.complete((JsonObject) ar.result().body());
            } else {
                future.failed();
            }
        });

        return future;
    }
}
