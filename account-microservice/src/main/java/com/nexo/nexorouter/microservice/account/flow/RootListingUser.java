package com.nexo.nexorouter.microservice.account.flow;

import com.nexo.nexorouter.microservice.common.Flow;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Optional;

/**
 * Created by carlos on 30/04/17.
 */
public class RootListingUser extends Flow {
    private EventBus eb;

    @Override
    protected void process(Message<JsonObject> message) {
        eb = vertx.eventBus();

        JsonObject body = message.body();
        JsonObject _params = body.getJsonObject("params");
        JsonObject header = new JsonObject(body.getString("header"));
        Integer offset = (Integer.parseInt(_params.getString("page")) - 1) * Integer.parseInt(_params.getString("limit"));

        JsonObject params = new JsonObject()
                .put("accountId", header.getJsonObject("SUBJECT").getString("accountId"))
                .put("query", _params.getString("query"))
                .put("limit", Integer.parseInt(_params.getString("limit")))
                .put("orderBy", _params.getString("orderBy"))
                .put("ascending", Integer.parseInt(_params.getString("ascending")))
                .put("page", _params.getString("page"))
                .put("byColumn", _params.getString("byColumn"))
                .put("offset", offset);

        getUsersList(params).setHandler(users -> {
            getTotalUserByAccount(params).setHandler(count -> {
                JsonObject result = new JsonObject()
                        .put("data", users.result())
                        .put("count", count.result());
                message.reply(result);
            });
        });
    }

    private Future<JsonArray> getUsersList(JsonObject params) {
        Future<JsonArray> future = Future.future();

        eb.send("account-repository@root-listing-users", params, ar -> {
            future.complete((JsonArray) ar.result().body());
        });

        return future;
    }

    private Future<Integer> getTotalUserByAccount(JsonObject params) {

        Future<Integer> future = Future.future();
        eb.send("account-repository@root-counting-account", params, ar-> {
            Integer count = ((JsonObject) ar.result().body()).getInteger("count");
            future.complete(count);
        });

        return future;
    }
}
