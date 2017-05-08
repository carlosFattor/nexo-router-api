package com.nexo.nexorouter.microservice.account.action;

import com.nexo.nexorouter.microservice.account.models.User;
import com.nexo.nexorouter.microservice.common.Action;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by carlos on 22/04/17.
 */
public class CreatingJwt extends Action {

    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

    @Override
    protected void process(Message<JsonObject> message) {
        User user = new User(message.body());
        JsonObject jsonJwt = new JsonObject();


        Date now = new Date(System.currentTimeMillis());
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.add(Calendar.DATE, 1);
        Date expired = c.getTime();

        System.out.println(now);
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("N3X0@R0UT3R");
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        final JsonArray roles = new JsonArray();
        final JsonObject subject = new JsonObject();
        subject.put("accountId", user.getProfile().getAccountId());
        user.getProfile().getRoles().forEach(role -> {
            roles.add(role.getTypeUser());
        });

        subject.put("roles", roles);
        JwtBuilder builder = Jwts.builder().setId(user.getEmail())
                .setIssuedAt(now)
                .setSubject(subject.toString())
                //.setIssuer(issuer)
                .setExpiration(expired)
                .signWith(signatureAlgorithm, signingKey);
        jsonJwt.put("token", builder.compact());

        message.reply(jsonJwt);
    }
}
