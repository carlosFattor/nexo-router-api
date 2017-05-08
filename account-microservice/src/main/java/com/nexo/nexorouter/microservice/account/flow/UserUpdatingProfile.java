package com.nexo.nexorouter.microservice.account.flow;

import com.nexo.nexorouter.microservice.account.models.Profile;
import com.nexo.nexorouter.microservice.common.Flow;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.Optional;

/**
 * Created by carlos on 26/04/17.
 */
public class UserUpdatingProfile extends Flow {
    EventBus eb;
    @Override
    protected void process(Message<JsonObject> message) {
        eb = vertx.eventBus();

        JsonObject body = message.body();

        JsonObject header = new JsonObject(body.getString("header"));
        body.getJsonObject("data").put("email", header.getString("ID"));
        JsonObject data = body.getJsonObject("data");

        existUser(data).setHandler(user -> {
            if(isSameUser(header, user)){
                Profile profile = updateProfile(data, user);
                data.put("profile", profile.toJson());

                eb.send("account-repository@user-updating-profile", data, ar -> {
                    checkPassword(data).setHandler(newPsss -> {
                        data.put("password", newPsss.result().getString("newPassword"));
                        this.updatePassword(data).setHandler(updatingPass -> {
                            System.out.println("Updated password=> " + updatingPass.result());
                        });
                    });
                    message.reply(new JsonObject().put("update", "profile updated"));
                });
            } else {
                message.fail(403, "not authorized");
            }
        });
    }

    private Future<JsonObject> checkPassword(JsonObject data) {
        Future<JsonObject> future = Future.future();

        Optional<String> password = Optional.ofNullable(data.getString("password"));
        final JsonObject newPassword = new JsonObject();
        password.ifPresent(pass -> {
            this.encryptPassword(pass).setHandler(newPass -> {
                future.complete(newPassword.put("newPassword", newPass.result()));
            });
        });

        return future;
    }

    private Future<JsonObject> updatePassword(JsonObject data) {
        Future<JsonObject> future = Future.future();
        eb.send("account-repository@user-updating-password", data, ar-> {
            future.complete((JsonObject) ar.result().body());
        });
        return future;
    }

    private Profile updateProfile(JsonObject body, AsyncResult<JsonObject> user) {
        Profile profile = new Profile(user.result().getJsonObject("profile"));

        profile.setFirstName(body.getString("firstName"));
        profile.setLastName(body.getString("lastName"));
        profile.setLocation(body.getString("location"));
        profile.setGender(body.getString("gender"));

        return profile;
    }

    private boolean isSameUser(JsonObject header, AsyncResult<JsonObject> user) {
        return header.getString("ID").equalsIgnoreCase(user.result().getString("email"));
    }

    private Future<JsonObject> existUser(JsonObject params) {
        Future<JsonObject> future = Future.future();
        eb.send("account@user-exist", params, ar -> {
            future.complete((JsonObject) ar.result().body());
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
