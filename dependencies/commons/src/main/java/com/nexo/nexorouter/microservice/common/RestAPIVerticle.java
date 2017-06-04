package com.nexo.nexorouter.microservice.common;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.core.json.JsonArray;
import com.nexo.nexorouter.microservice.common.enums.Role;
import java.util.Optional;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by carlos on 17/04/17.
 */
public class RestAPIVerticle extends BaseMicroserviceVerticle {

    protected Future<Void> createHttpServer(Router router, String host, int port) {
        Future<HttpServer> httpServerFuture = Future.future();
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(port, host, httpServerFuture.completer());
        return httpServerFuture.map(r -> null);
    }

    protected void enableCorsSupport(Router router) {
        router.route().handler(CorsHandler.create("*")
                .allowedHeader("Content-Type")
                .allowedHeader("Authorization")
                .allowedHeader("Access-Control-Request-Method")
                .allowedHeader("Access-Control-Allow-Credentials")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Headers")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.PUT)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedMethod(HttpMethod.DELETE));
    }

    // helper result handler within a request context

    /**
     * This method generates handler for async methods in REST APIs.
     */
    protected <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context, Handler<T> handler) {
        return res -> {
            if (res.succeeded()) {
                handler.handle(res.result());
            } else {
                internalError(context, res.cause());
                res.cause().printStackTrace();
            }
        };
    }

    /**
     * This method generates handler for async methods in REST APIs.
     * Use the result directly and invoke `toString` as the response. The content type is JSON.
     */
    protected <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context) {
        return ar -> {
            if (ar.succeeded()) {
                T res = ar.result();
                context.response()
                        .putHeader("content-type", "application/json")
                        .end(res == null ? "{}" : res.toString());
            } else {
                internalError(context, ar.cause());
                ar.cause().printStackTrace();
            }
        };
    }

    /**
     * This method generates handler for async methods in REST APIs.
     * Use the result directly and use given {@code converter} to convert result to string
     * as the response. The content type is JSON.
     *
     * @param context   routing context instance
     * @param converter a converter that converts result to a string
     * @param <T>       result type
     * @return generated handler
     */
    protected <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context, Function<T, String> converter) {
        return ar -> {
            if (ar.succeeded()) {
                T res = ar.result();
                if (res == null) {
                    serviceUnavailable(context, "invalid_result");
                } else {
                    context.response()
                            .putHeader("content-type", "application/json")
                            .end(converter.apply(res));
                }
            } else {
                internalError(context, ar.cause());
                ar.cause().printStackTrace();
            }
        };
    }

    /**
     * This method generates handler for async methods in REST APIs.
     * The result requires non-empty. If empty, return <em>404 Not Found</em> status.
     * The content type is JSON.
     *
     * @param context routing context instance
     * @param <T>     result type
     * @return generated handler
     */
    protected <T> Handler<AsyncResult<Message<T>>> resultHandlerNonEmpty(RoutingContext context) {
        return ar -> {
            if (ar.succeeded()) {
                T res = ar.result().body();
                if (res == null) {
                    notFound(context);
                } else {
                    context.response()
                            .putHeader("content-type", "application/json")
                            .end(res.toString());
                }
            } else {
                badRequest(context, ar.cause());
                ar.cause().printStackTrace();
            }
        };
    }

    /**
     * This method generates handler for async methods in REST APIs.
     * The content type is originally raw text.
     *
     * @param context routing context instance
     * @param <T>     result type
     * @return generated handler
     */
    protected <T> Handler<AsyncResult<Message<T>>> rawResultHandler(RoutingContext context) {
        return ar -> {
            if (ar.succeeded()) {
                T res = ar.result().body();
                context.response()
                        .end(res == null ? "" : res.toString());
            } else {
                internalError(context, ar.cause());
                ar.cause().printStackTrace();
            }
        };
    }

//    protected Handler<AsyncResult<Message<T>>> resultVoidHandler(RoutingContext context, JsonObject result) {
//        return resultVoidHandler(context, result, 200);
//    }

    /**
     * This method generates handler for async methods in REST APIs.
     * The result is not needed. Only the state of the async result is required.
     *
     * @param context routing context instance
     * @param result  result content
     * @param status  status code
     * @return generated handler
     */
    protected Handler<AsyncResult<Void>> resultVoidHandler(RoutingContext context, JsonObject result, int status) {
        return ar -> {
            if (ar.succeeded()) {
                context.response()
                        .setStatusCode(status == 0 ? 200 : status)
                        .putHeader("content-type", "application/json")
                        .end(result.encodePrettily());
            } else {
                internalError(context, ar.cause());
                ar.cause().printStackTrace();
            }
        };
    }

    protected Handler<AsyncResult<Void>> resultVoidHandler(RoutingContext context, int status) {
        return ar -> {
            if (ar.succeeded()) {
                context.response()
                        .setStatusCode(status == 0 ? 200 : status)
                        .putHeader("content-type", "application/json")
                        .end();
            } else {
                internalError(context, ar.cause());
                ar.cause().printStackTrace();
            }
        };
    }

    /**
     * This method generates handler for async methods in REST DELETE APIs.
     * Return format in JSON (successful status = 204):
     * <code>
     * {"message": "delete_success"}
     * </code>
     *
     * @param context routing context instance
     * @return generated handler
     */
    protected Handler<AsyncResult<Void>> deleteResultHandler(RoutingContext context) {
        return res -> {
            if (res.succeeded()) {
                context.response().setStatusCode(204)
                        .putHeader("content-type", "application/json")
                        .end(new JsonObject().put("message", "delete_success").encodePrettily());
            } else {
                internalError(context, res.cause());
                res.cause().printStackTrace();
            }
        };
    }

    // helper method dealing with failure

    protected void badRequest(RoutingContext context, Throwable ex) {
        context.response().setStatusCode(400)
                .putHeader("content-type", "application/json;charset=UTF-8")
                .end(new JsonObject().put("error", ex.getMessage()).encodePrettily());
    }

    protected void notFound(RoutingContext context) {
        context.response().setStatusCode(404)
                .putHeader("content-type", "application/json;charset=UTF-8")
                .end(new JsonObject().put("message", "not_found").encodePrettily());
    }

    protected void internalError(RoutingContext context, Throwable ex) {
        context.response().setStatusCode(500)
                .putHeader("content-type", "application/json;charset=UTF-8")
                .end(new JsonObject().put("error", ex.getMessage()).encodePrettily());
    }

    protected void notImplemented(RoutingContext context) {
        context.response().setStatusCode(501)
                .putHeader("content-type", "application/json;charset=UTF-8")
                .end(new JsonObject().put("message", "not_implemented").encodePrettily());
    }

    protected void badGateway(Throwable ex, RoutingContext context) {
        context.response()
                .setStatusCode(502)
                .putHeader("content-type", "application/json;charset=UTF-8")
                .end(new JsonObject().put("error", "bad_gateway")
                        //.put("message", ex.getMessage())
                        .encodePrettily());
    }

    protected void serviceUnavailable(RoutingContext context) {
        context.fail(503);
    }

    protected void serviceUnavailable(RoutingContext context, Throwable ex) {
        context.response().setStatusCode(503)
                .putHeader("content-type", "application/json;charset=UTF-8")
                .end(new JsonObject().put("error", ex.getMessage()).encodePrettily());
    }

    protected void serviceUnavailable(RoutingContext context, String cause) {
        context.response().setStatusCode(503)
                .putHeader("content-type", "application/json;charset=UTF-8")
                .end(new JsonObject().put("error", cause).encodePrettily());
    }

    protected void unauthorized(RoutingContext context){
        context.response().setStatusCode(401)
                .putHeader("content-type", "application/json")
                .end(new JsonObject().put("error", "unauthorized").encodePrettily());
    }

    protected void tokenException(RoutingContext context){
        context.response().setStatusCode(401)
                .putHeader("content-type", "application/json")
                .end(new JsonObject().put("tokenException", "GEN-TOKEN-EXPIRED").encodePrettily());
    }

    protected Boolean haveAdminPermission(JsonObject json){
        try {
            JsonArray roles = new JsonObject(json.getString("header")).getJsonObject("SUBJECT").getJsonArray("roles");
            return roles.contains(Role.ADMIN.getTypeUser());
        } catch (Exception e){
            return false;
        }
    }

    protected Boolean haveUserPermission(JsonObject json){
        try {
            JsonArray roles = new JsonObject(json.getString("header")).getJsonObject("SUBJECT").getJsonArray("roles");
            return roles.contains(Role.DEFAULT.getTypeUser());
        } catch (Exception e){
            return false;
        }
    }

    protected Boolean havePermission(JsonObject json){
        try {
            JsonArray roles = Optional.ofNullable(new JsonObject(json.getString("header"))
                    .getJsonObject("SUBJECT")
                    .getJsonArray("roles"))
                    .orElse(new JsonArray());
            return roles.size() > 0;
        } catch (Exception e){
            return false;
        }
    }

    protected JsonObject getBodyParamsHeader(RoutingContext context){
        final JsonObject json = new JsonObject();
        final JsonObject params = new JsonObject();
        try{
            json.put("header", context.request().getHeader("Authorization"));
            json.put("data", context.getBodyAsJson());
        } catch (Exception e){}
        context.request().params().forEach(param -> {
            params.put(param.getKey(), param.getValue());
        });
        json.put("params", params);
        return json;
    }
}
