package com.nexo.nexorouter.microservice.account.action;

import com.nexo.nexorouter.microservice.common.Action;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Created by carlos on 21/04/17.
 */
public class ValidatingUserPassword extends Action{
    @Override
    protected void process(Message<JsonObject> message) {
        if (BCrypt.checkpw(message.body().getString("password"),message.body().getString("hash_password")))
            message.reply(true);
        else
            message.reply(false);
    }
}
