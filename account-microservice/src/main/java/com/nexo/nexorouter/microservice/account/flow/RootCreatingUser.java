package com.nexo.nexorouter.microservice.account.flow;

import com.nexo.nexorouter.microservice.account.models.Profile;
import com.nexo.nexorouter.microservice.account.models.User;
import com.nexo.nexorouter.microservice.common.Flow;
import com.nexo.nexorouter.microservice.common.enums.Role;
import com.nexo.nexorouter.microservice.common.enums.UserStatus;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by carlos on 21/04/17.
 */
public class RootCreatingUser extends Flow {

    private static final String PERMISSION = "admin";
    private EventBus eb;
    private static String AVATAR_URL = "https://api.adorable.io/avatars/";

    @Override
    protected void process(Message<JsonObject> message) {
        eb = vertx.eventBus();

        JsonObject data = message.body().getJsonObject("data");
        Future<Boolean> existUser = existUser(data.getString("email"));
        Future<String> passwordEncrypt = encryptPassword(data.getString("password"));
        JsonObject header = new JsonObject(message.body().getString("header"));

        existUser.compose(exist -> {
            if(exist.booleanValue()){
                message.fail(400, "Create user impossible.");
                return;
            }

            passwordEncrypt.setHandler(pass -> {
                User user = createUser(data, header, pass.result());
                user.setUserId(java.util.UUID.randomUUID().toString());

                eb.send("account-repository@creating-user", user.toJson(), ar -> {
                    if(ar.succeeded()){

                        message.reply(new JsonObject().put("status", "created"));
                    } else {
                        message.reply(ar.cause());
                    }
                });
            });
        }, Future.future().setHandler( fail -> {
            message.reply(new JsonObject().put("error", fail.cause()));
        }));

    }

    private User createUser(JsonObject body, JsonObject header, String pass) {
        User user = new User();

        Role role = Role.valueOf(body.getString("role"));
        List<Role> roles = Arrays.asList(role);
        String accountId = header.getJsonObject("SUBJECT").getString("accountId");

        Profile profile = new Profile();
        profile.setAccountId(Optional.ofNullable(body.getString("accountId")).orElse(accountId));
        profile.setAvatar(Optional.ofNullable(body.getString("avatar")).orElse(AVATAR_URL+System.currentTimeMillis()));
        profile.setConfirmed(false);
        profile.setFirstName(body.getString("firstName"));
        profile.setLastName(Optional.ofNullable(body.getString("lastName")).orElse(""));
        profile.setLocation(Optional.ofNullable(body.getString("location")).orElse(""));
        profile.setGender(Optional.ofNullable(body.getString("gender")).orElse(""));
        profile.setRoles(roles);

        user.setCreatedAt(System.currentTimeMillis());
        user.setUpdateAt(System.currentTimeMillis());
        user.setEmail(body.getString("email"));
        user.setPassword(pass);
        user.setUserStatus(UserStatus.ACTIVE);
        user.setProfile(profile);
        user.setTokens(Arrays.asList(java.util.UUID.randomUUID().toString()));
        return user;
    }

    private Future<Boolean> existUser(String email) {
        Future<Boolean> future = Future.future();
        JsonObject param = new JsonObject().put("email", email);

        eb.send("account-repository@load-user-by-email", param, ar-> {
            boolean present = Optional.ofNullable(ar.result().body()).isPresent();
            if(present){
                future.complete(true);
            } else {
                future.complete(false);
            }
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
