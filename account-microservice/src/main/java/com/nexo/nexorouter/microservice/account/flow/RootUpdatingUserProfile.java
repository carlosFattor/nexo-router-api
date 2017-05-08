package com.nexo.nexorouter.microservice.account.flow;

import com.nexo.nexorouter.microservice.account.models.Profile;
import com.nexo.nexorouter.microservice.common.Flow;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Created by carlos on 26/04/17.
 */
public class RootUpdatingUserProfile extends Flow {
    EventBus eb;
    @Override
    protected void process(Message<JsonObject> message) {
        eb = vertx.eventBus();

        JsonObject body = message.body();

        JsonObject data = body.getJsonObject("data");

        existUser(data).setHandler(user -> {
            Profile profile = updateProfile(data, user);
            data.put("profile", profile.toJson());

            eb.send("account-repository@user-updating-profile", data, ar -> {
                message.reply(new JsonObject().put("update", "profile updated"));
            });
        });
    }

    private Profile updateProfile(JsonObject body, AsyncResult<JsonObject> user) {
        Profile profile = new Profile(user.result().getJsonObject("profile"));

        profile.setFirstName(body.getString("firstName"));
        profile.setLastName(body.getString("lastName"));
        profile.setLocation(body.getString("location"));
        profile.setGender(body.getString("gender"));
        return profile;
    }

    private Future<JsonObject> existUser(JsonObject params) {
        Future<JsonObject> future = Future.future();
        eb.send("account@user-exist", params, ar -> {
            future.complete((JsonObject) ar.result().body());
        });
        return future;
    }
}
