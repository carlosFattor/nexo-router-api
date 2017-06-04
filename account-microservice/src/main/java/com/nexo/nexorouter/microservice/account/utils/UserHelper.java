package com.nexo.nexorouter.microservice.account.utils;

import com.nexo.nexorouter.microservice.account.models.Profile;
import com.nexo.nexorouter.microservice.account.models.Token;
import com.nexo.nexorouter.microservice.account.models.User;
import com.nexo.nexorouter.microservice.common.enums.Role;
import com.nexo.nexorouter.microservice.common.enums.UserStatus;
import io.vertx.core.json.JsonObject;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by carlos on 29/05/17.
 */
public class UserHelper {
    private static String AVATAR_URL = "https://api.adorable.io/avatars/";

    public static Token generateToken() {
        LocalDate date = LocalDate.now().plusDays(7);
        return new Token(new JsonObject().put("token", UUID.randomUUID().toString()).put("validate", date.toEpochDay()));
    }

    public static User createUser(final JsonObject body, final JsonObject header,final String pass) {
        User user = new User();

        Role role = Role.valueOf(body.getString("roles"));
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
        user.setTokens(Arrays.asList(UserHelper.generateToken()));

        return user;
    }
}
