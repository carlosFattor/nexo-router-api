package com.nexo.nexorouter.microservice.account.action;

import com.nexo.nexorouter.microservice.common.Action;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Created by carlos on 21/04/17.
 */
public class EncryptingUserPassword extends Action {
    @Override
    protected void process(Message<JsonObject> message) {
        try{
            message.body().put("password", BCrypt.hashpw(message.body().getString("password"), BCrypt.gensalt()));
            message.reply(message.body());
        }catch(Exception e){
            message.fail(500, e.getMessage());
        }
    }
}
