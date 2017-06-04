package com.nexo.nexorouter.microservice.account.api;

import com.nexo.nexorouter.microservice.common.RestAPIVerticle;
import com.nexo.nexorouter.microservice.common.enums.Role;
import io.vertx.codegen.annotations.Nullable;
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

    //API user recover password
    private static final String API_USER_RECOVER = "/recover-password";
    private static final String API_USER_BY_TOKEN = "/recover-password/:token";

    //API to active user
    private static final String API_USER_ACTIVE = "/users/active/:token";

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

        //RECOVER PASSWORD
        router.post(API_USER_RECOVER).handler(this::apiRecoverPassword);
        router.get(API_USER_BY_TOKEN).handler(this::apiFindUserByToken);
        router.post(API_USER_BY_TOKEN).handler(this::apiUpdatePassword);

        //ACTIVE USER
        router.get(API_USER_ACTIVE).handler(this::apiUserByTokenToActive);
        router.post(API_USER_ACTIVE).handler(this::apiActiveUser);

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

    /**
     * find list of users
     * @param context
     */
    private void apiAccountListUser(RoutingContext context){
        JsonObject json = getBodyParamsHeader(context);
        if(haveAdminPermission(json)){
            Optional<String> email = Optional.ofNullable(json.getJsonObject("params").getString("email"));
            if(email.isPresent()){
                this.eb.send("account@root-finding-profile", json, resultHandlerNonEmpty(context));
            } else {
                this.eb.send("account@root-listing-user", json, resultHandlerNonEmpty(context));
            }
        } else {
            System.out.println("ERROR");
            unauthorized(context);
        }
    }

    /**
     * user recovering password
     *
     * @param context
     */
    private void apiRecoverPassword(RoutingContext context) {
        JsonObject json = getBodyParamsHeader(context);
        this.eb.send("account@user-recovering-password", json, resultHandlerNonEmpty(context));
    }

    /**
     * Find user by token to recover password
     * @param context
     */
    private void apiFindUserByToken(RoutingContext context){
        @Nullable String token = context.request().getParam("token");
        this.eb.send("account@user-finding-profile-by-token", new JsonObject().put("token", token), resultHandlerNonEmpty(context));
    }

    private void apiUpdatePassword(RoutingContext context){
        JsonObject json = getBodyParamsHeader(context);
        this.eb.send("account@user-updating-password", json, resultHandlerNonEmpty(context));
    }

    /**
     * find user by token to active the same
     * @param context
     */
    private void apiUserByTokenToActive(RoutingContext context){
        @Nullable String token = context.request().getParam("token");
        this.eb.send("account@user-finding-profile-by-token", new JsonObject().put("token", token), resultHandlerNonEmpty(context));
    }

    private void apiActiveUser(RoutingContext context) {
        JsonObject json = getBodyParamsHeader(context);
        this.eb.send("account@user-activating-user", json, resultHandlerNonEmpty(context));
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
        JsonObject json = this.getBodyParamsHeader(context);
        if(havePermission(json)){
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
        if(havePermission(json)){
            this.eb.send("account@user-updating-profile", json, resultHandlerNonEmpty(context));
        } else {
            unauthorized(context);
        }
    }

    private void creatingHttpServer(Future<Void> future, Router router) {
        String host = config().getString("account.http.address", "0.0.0.0");
        int port = config().getInteger("account.http.port", 8085);
        System.out.println(SERVICE_NAME);
        System.out.println(port);
        System.out.println(host);
        createHttpServer(router, host, port)
                .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port))
                .setHandler(future.completer());
    }


}
