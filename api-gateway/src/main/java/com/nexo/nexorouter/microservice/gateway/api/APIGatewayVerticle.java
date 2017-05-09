package com.nexo.nexorouter.microservice.gateway.api;

import com.nexo.nexorouter.microservice.common.RestAPIVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.HttpEndpoint;

import java.util.*;

/**
 * Created by carlos on 17/04/17.
 */
public class APIGatewayVerticle extends RestAPIVerticle {

    private static final int DEFAULT_PORT = 3000;
    private static final Logger logger = LoggerFactory.getLogger(APIGatewayVerticle.class);
    private static final List<String> pathsToIgnore = Arrays.asList("login", "refresh-token", "recover-password");

    @Override
    public void start(Future<Void> future) throws Exception {
        super.start();

        JsonObject circuit_break = new JsonObject()
                .put("timeout", 10000)
                .put("max-failures", 5);

        config().put("api.gateway.http.port", DEFAULT_PORT)
                .put("api.gateway.http.address", "localhost")
                .put("circuit-breaker", circuit_break);


        String host = config().getString("api.gateway.http.address", "localhost");
        int port = config().getInteger("api.gateway.http.port", DEFAULT_PORT);

        Router router = Router.router(vertx);

        enableCorsSupport(router);

        router.route().handler(BodyHandler.create());

        router.get("/api/v").handler(this::apiVersion);

        // api dispatcher
        router.route("/api/*").handler(this::dispatchRequests);

        // create http server
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(port, host, ar -> {
                    if (ar.succeeded()) {
                        publishApiGateway(host, port);
                        future.complete();
                        logger.info("API Gateway is running on port " + port);
                        // publish log
                        publishGatewayLog("api_gateway_init_success:" + port);
                    } else {
                        future.fail(ar.cause());
                    }
                });
    }

    private void dispatchRequests(RoutingContext context){
        int initialOffset = 8;
        System.out.println(context.request().absoluteURI());
        Future<JsonObject> futAuth = checkAuthentication(context);

        futAuth.setHandler(authorized -> {
            if (authorized.succeeded()) {
                circuitBreaker.execute(future -> getAllEndpoints().setHandler(ar -> {
                    if(ar.succeeded()){
                        List<Record> recordList = ar.result();

                        String path = context.request().uri();

                        if(path.length() <= initialOffset){
                            notFound(context);
                            future.complete();
                            return;
                        }
                        String prefix = (path.substring(initialOffset).split("/"))[0];

                        // generate new relative path
                        String newPath = path.substring(initialOffset + prefix.length());

                        // get one relevant HTTP client, may not exist
                        Optional<Record> client = recordList.stream()
                                .filter(record -> record.getMetadata().getString("api.name") != null)
                                .filter(record -> record.getName().equalsIgnoreCase(prefix))
                                .findAny();
                        if(client.isPresent()){
                            context.request().headers().add("AUTHORIZATION", authorized.result().toString());
                            doDispatch(context, newPath, discovery.getReference(client.get()).get(), future);
                        } else {
                            notFound(context);
                            future.complete();
                        }
                    } else {
                        future.fail(ar.cause());
                    }
                })).setHandler(ar -> {
                    if (ar.failed()) {
                        badGateway(ar.cause(), context);
                    }
                });
            } else {
                tokenException(context);
            }
        });

    }

    private Future<JsonObject> checkAuthentication(RoutingContext context) {

        Future<JsonObject> future = Future.future();
        String[] peaceOfPath = context.request().uri().split("/");
        System.out.println(peaceOfPath[4]);
        if(peaceOfPath.length >=4 && this.pathsToIgnore.contains(peaceOfPath[4])){
            future.complete(new JsonObject().put("AUTHORIZATION", ""));
            return future;
        }

        JsonObject param = new JsonObject();
        getAuthorization(context, param);

        if(!param.isEmpty()){
            vertx.eventBus().send("gateway@verify-authentication", param, ar -> {

                if(ar.succeeded()){
                    future.complete((JsonObject) ar.result().body());
                } else {
                    future.fail(ar.cause());
                }
            });
        }else {
            future.failed();
        }

        return future;
    }

    private void getAuthorization(RoutingContext context, JsonObject param) {
        context.request().headers().entries().forEach(header -> {
            if(header.getKey().equalsIgnoreCase("Authorization")){
                param.put("Authorization", header.getValue());
            }
        });
    }

    private void doDispatch(RoutingContext context, String path, HttpClient client, Future<Object> cbFuture) {

        HttpClientRequest toReq = client
                .request(context.request().method(), path, response -> {
                    response.bodyHandler(body -> {
                        if (response.statusCode() >= 500) { // api endpoint server error, circuit breaker should fail
                            cbFuture.fail(response.statusCode() + ": " + body.toString());
                        } else {
                            HttpServerResponse toRsp = context.response()
                                    .setStatusCode(response.statusCode());
                            response.headers().forEach(header -> {
                                toRsp.putHeader(header.getKey(), header.getValue());
                            });
                            // send response
                            toRsp.end(body);
                            cbFuture.complete();
                        }
                        ServiceDiscovery.releaseServiceObject(discovery, client);
                    });
                });
        // set headers
        context.request().headers().forEach(header -> {
            toReq.putHeader(header.getKey(), header.getValue());
        });

        // send request
        if (context.getBody() == null) {
            toReq.end();
        } else {
            toReq.end(context.getBody());
        }
    }

    private Future<List<Record>> getAllEndpoints() {
        Future<List<Record>> future = Future.future();
        discovery.getRecords(record -> record.getType().equals(HttpEndpoint.TYPE),
                future.completer());
        return future;
    }

    private void apiVersion(RoutingContext context) {
        context.response()
                .end(new JsonObject().put("version", "v1").encodePrettily());
    }

    private void publishGatewayLog(String info) {
        JsonObject message = new JsonObject()
                .put("info", info)
                .put("time", System.currentTimeMillis());
        publishLogEvent("gateway", message);
    }

    private void publishGatewayLog(JsonObject msg) {
        JsonObject message = msg.copy()
                .put("time", System.currentTimeMillis());
        publishLogEvent("gateway", message);
    }
}
