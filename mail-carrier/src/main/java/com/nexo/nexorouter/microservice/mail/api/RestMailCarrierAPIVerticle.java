package com.nexo.nexorouter.microservice.mail.api;

import com.nexo.nexorouter.microservice.common.RestAPIVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.Optional;

/**
 * Created by carlos on 18/05/17.
 */
public class RestMailCarrierAPIVerticle extends RestAPIVerticle {

    private EventBus eb;

    //Service name
    private static final String SERVICE_NAME = "mail-rest-api";

    //API's mail
    private static final String API_EMAIL_CARRIER = "/email-carrier";

    @Override
    public void start(Future<Void> future) throws Exception {
        super.start();
        this.eb = vertx.eventBus();
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.post(API_EMAIL_CARRIER).handler(this::apiEmailCarrier);

        creatingHttpServer(future, router);
    }

    /**
     * Method to send email for new users
     * @param context
     */
    private void apiEmailCarrier(RoutingContext context) {
        JsonObject json = getBodyParamsHeader(context);
        if(havePermission(json)){
            this.eb.send("mail@email-carrier", json, resultHandlerNonEmpty(context));
        } else {
            unauthorized(context);
        }
    }

    private void creatingHttpServer(Future<Void> future, Router router) {
        String host = config().getString("mail.http.address", "0.0.0.0");
        int port = config().getInteger("mail.http.port", 8087);
        System.out.println(SERVICE_NAME);
        System.out.println(port);
        System.out.println(host);
        createHttpServer(router, host, port)
                .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port))
                .setHandler(future.completer());
    }


}
