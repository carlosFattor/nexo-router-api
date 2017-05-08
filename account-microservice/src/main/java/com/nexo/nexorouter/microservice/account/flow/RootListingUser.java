package com.nexo.nexorouter.microservice.account.flow;

import com.nexo.nexorouter.microservice.common.Flow;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by carlos on 30/04/17.
 */
public class RootListingUser extends Flow {
    private EventBus eb;

    @Override
    protected void process(Message<JsonObject> message) {
        eb = vertx.eventBus();

        JsonObject body = message.body();
        JsonObject header = new JsonObject(body.getString("header"));
        Integer offset = (Integer.parseInt(body.getString("page")) - 1) * Integer.parseInt(body.getString("limit"));

        JsonObject params = new JsonObject()
                .put("accountId", header.getJsonObject("SUBJECT").getString("accountId"))
                .put("query", body.getString("query"))
                .put("limit", Integer.parseInt(body.getString("limit")))
                .put("orderBy", body.getString("orderBy"))
                .put("ascending", Integer.parseInt(body.getString("ascending")))
                .put("page", body.getString("page"))
                .put("byColumn", body.getString("byColumn"))
                .put("offset", offset);

        eb.send("account-repository@root-listing-users", params, ar -> {
            getTotalUserByAccount(params).setHandler(count -> {

                JsonObject result = new JsonObject()
                        .put("data", ar.result().body())
                        .put("count", count.result());
                message.reply(result);
            });
        });
    }

    private Future<Integer> getTotalUserByAccount(JsonObject params) {
        Future<Integer> future = Future.future();
        eb.send("account-repository@root-counting-account", params, ar-> {
            future.complete(((JsonArray) ar.result().body()).getJsonObject(0).getInteger("count"));
        });

        return future;
    }
}
