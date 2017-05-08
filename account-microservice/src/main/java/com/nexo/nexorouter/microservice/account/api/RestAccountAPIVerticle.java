package com.nexo.nexorouter.microservice.account.api;

import com.nexo.nexorouter.microservice.common.RestAPIVerticle;

import com.nexo.nexorouter.microservice.common.enums.Role;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.Optional;

/**
 * Created by carlos on 19/04/17.
 */
public class RestAccountAPIVerticle extends RestAPIVerticle {

    private EventBus eb;

    //Service name
    private static final String SERVICE_NAME = "account-rest-api";

    //API's account
    private static final String API_ACCOUNT_USER = "/account/users";
    private static final String API_ACCOUNT = "/account";

    //API's user
    private static final String API_USER = "/users";

    //API's login
    private static final String API_LOGIN = "/login";

    //API refresh token
    private static final String API_REFRESH_TOKEN = "/refresh-token";

    @Override
    public void start(Future<Void> future) throws Exception {
        super.start();
        this.eb = vertx.eventBus();
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        //ROTES ACCOUNT
        router.get(API_ACCOUNT).handler(this::apiAccountRetrieve);
        router.post(API_ACCOUNT).handler(this::apiAccountSave);
        router.put(API_ACCOUNT_USER).handler(this::apiAccountUpdateProfile);
        router.post(API_ACCOUNT_USER).handler(this::apiAccountCreateUser);
        router.get(API_ACCOUNT_USER).handler(this::apiAccountListUser);


        //ROUTES USER
        router.put(API_USER).handler(this::apiUpdateUser);
        router.get(API_USER).handler(this::apiRetrieve);
        router.delete(API_USER).handler(this::apiDeleteUser);


        //ROUTES LOGIN
        router.post(API_LOGIN).handler(this::apiLogin);

        //REFRESH TOKEN
        router.post(API_REFRESH_TOKEN).handler(this::apiRefreshToken);

        creatingHttpServer(future, router);
    }

    /**
     * Admin user creating account
     * @param context
     */
    private void apiAccountSave(RoutingContext context) {
        JsonObject json = getBodyParamsHeader(context);
        if(haveAdminPermission(json)){
            this.eb.send("account@root-creating-account", json, resultHandlerNonEmpty(context));
        } else {
            unauthorized(context);
        }
    }

    /**
     * Admin user get account
     * @param context
     */
    private void apiAccountRetrieve(RoutingContext context){
        JsonObject json = getBodyParamsHeader(context);
        if(haveAdminPermission(json)){
            this.eb.send("account@root-finding-account", json, resultHandlerNonEmpty(context));
        } else {
            unauthorized(context);
        }
    }

    private void apiAccountUpdateProfile(RoutingContext context) {
        JsonObject json = getBodyParamsHeader(context);
        if(haveAdminPermission(json)) {
            this.eb.send("account@root-updating-user-profile", json, resultHandlerNonEmpty(context));
        }
    }

    /**
     * Admin user creating default user
     * @param context
     */
    private void apiAccountCreateUser(RoutingContext context){
        JsonObject json = getBodyParamsHeader(context);
        if(haveAdminPermission(json)){
            this.eb.send("account@root-creating-user", json, resultHandlerNonEmpty(context));
        } else {
            unauthorized(context);
        }
    }

    private void apiAccountListUser(RoutingContext context){
        JsonObject json = getBodyParamsHeader(context);
        if(haveAdminPermission(json)){
            Optional<String> email = Optional.ofNullable(json.getString("email"));
            if(email.isPresent()){
                this.eb.send("account@root-finding-profile", json, resultHandlerNonEmpty(context));
            } else {
                this.eb.send("account@root-listing-user", json, resultHandlerNonEmpty(context));
            }
        } else {
            unauthorized(context);
        }
    }

    /**
     * login user - getting jwt
     * @param context
     */
    private void apiLogin(RoutingContext context){
        JsonObject json = getBodyParamsHeader(context);
        this.eb.send("account@user-logging", json, resultHandlerNonEmpty(context));
    }

    private void apiRefreshToken(RoutingContext context){
        JsonObject json = getBodyParamsHeader(context);
        this.eb.send("account@user-refreshing-token", json, resultHandlerNonEmpty(context));
    }

    /**
     * User getting profile
     * @param context
     */
    private void apiRetrieve(RoutingContext context){
        JsonObject json = getBodyParamsHeader(context);
        if(haveUserPermission(json)){
            this.eb.send("account@user-finding-profile", json, resultHandlerNonEmpty(context));
        } else {
            unauthorized(context);
        }
    }

    /**
     * Admin deleting user
     * @param context
     */
    private void apiDeleteUser(RoutingContext context){
        JsonObject json = getBodyParamsHeader(context);
        if(haveAdminPermission(json)){
            this.eb.send("account@root-deleting-user", json, resultHandlerNonEmpty(context));
        } else {
            unauthorized(context);
        }
    }

    /**
     * user update own profile
     * @param context
     */
    private void apiUpdateUser(RoutingContext context) {
        JsonObject json = getBodyParamsHeader(context);
        if(haveUserPermission(json)){
            this.eb.send("account@user-updating-profile", json, resultHandlerNonEmpty(context));
        } else {
            unauthorized(context);
        }
    }

    private void creatingHttpServer(Future<Void> future, Router router) {
        String host = config().getString("account.http.address", "0.0.0.0");
        int port = config().getInteger("account.http.port", 8085);

        createHttpServer(router, host, port)
                .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port))
                .setHandler(future.completer());
    }

    private JsonObject getBodyParamsHeader(RoutingContext context){
        JsonObject json = new JsonObject();
        json.put("header", context.request().getHeader("Authorization"));
        try{
            json.put("data", context.getBodyAsJson());
        } catch (Exception e){}

        context.request().params().forEach(param -> {
            json.put(param.getKey(), param.getValue());
        });
        return json;
    }

    private Boolean haveAdminPermission(JsonObject json){
        JsonArray roles = new JsonObject(json.getString("header")).getJsonObject("SUBJECT").getJsonArray("roles");
        return roles.contains(Role.ADMIN.getTypeUser());
    }

    private Boolean haveUserPermission(JsonObject json){
        JsonArray roles = new JsonObject(json.getString("header")).getJsonObject("SUBJECT").getJsonArray("roles");
        return roles.contains(Role.DEFAULT.getTypeUser());
    }
}
