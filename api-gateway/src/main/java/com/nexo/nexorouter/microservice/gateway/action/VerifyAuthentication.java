package com.nexo.nexorouter.microservice.gateway.action;

import co.paralleluniverse.fibers.Suspendable;
import com.nexo.nexorouter.microservice.common.Flow;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import javax.xml.bind.DatatypeConverter;

/**
 * Created by carlos on 18/04/17.
 */
public class VerifyAuthentication extends Flow{

    @Override
    @Suspendable
    protected void process(Message<JsonObject> message) {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("N3X0@R0UT3R");
        String authorization = message.body().getString("Authorization").split(" ")[1];
        JsonObject jsonResponse = new JsonObject();

        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(apiKeySecretBytes)
                    .parseClaimsJws(authorization).getBody();
            jsonResponse
                    .put("ID", claims.getId())
                    .put("SUBJECT", new JsonObject(claims.getSubject()))
                    .put("ISSUER", claims.getIssuer());

            message.reply(jsonResponse);
        }catch (JwtException e){
            System.out.println("GEN-TOKEN-EXPIRED");
            message.fail(401, "GEN-TOKEN-EXPIRED");
        }
    }
}
