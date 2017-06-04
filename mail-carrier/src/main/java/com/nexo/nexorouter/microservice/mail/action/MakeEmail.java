package com.nexo.nexorouter.microservice.mail.action;

import com.nexo.nexorouter.microservice.common.Action;
import com.nexo.nexorouter.microservice.mail.utils.HtmlParser;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Created by carlos on 18/05/17.
 */
public class MakeEmail extends Action{

    JsonObject emailMapper = new JsonObject();

    @Override
    public void start() throws Exception {
        super.start();

        vertx.fileSystem().readDir("resources/email/", listAsyncResult -> {

            listAsyncResult.result().forEach(email -> {
                vertx.fileSystem().readFile(email , bufferAsyncResult -> {
                    String html = bufferAsyncResult.result().toString();
                    emailMapper.put(email.replaceAll(".*\\/", "").replace(".html", ""), html);
                });
            });
        });
    }

    @Override
    protected void process(Message<JsonObject> message) {
        JsonObject body = message.body();
        JsonObject result = makeEmail(body);
        message.reply(result);
    }

    private JsonObject makeEmail(JsonObject body) {
        String emailHtml = emailMapper.getString(body.getString("template"));
        String html = HtmlParser.parserHtml(emailHtml, body.getJsonObject("body"));
        body.put("html", html);
        return body;
    }
}
