package com.nexo.nexorouter.microservice.mail.action;

import com.nexo.nexorouter.microservice.common.Action;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.*;

/**
 * Created by carlos on 18/05/17.
 */
public class SendEmail extends Action {

    private MailClient mailClient;

    @Override
    public void start() throws Exception{
        super.start();
        MailConfig mailConfig = new MailConfig();
        mailConfig.setHostname("smtp.sendgrid.net");
        mailConfig.setPort(587);
        mailConfig.setLogin(LoginOption.REQUIRED);
        mailConfig.setStarttls(StartTLSOptions.REQUIRED);
        mailConfig.setAuthMethods("PLAIN");
        mailConfig.setUsername(System.getenv("USER"));
        mailConfig.setPassword(System.getenv("PASSWORD"));
        try {
            mailClient = MailClient.createNonShared(vertx, mailConfig);
        }catch(Exception e){
            System.out.println(e.getCause());
        }
    }

    @Override
    protected void process(Message<JsonObject> message) {
        mailClient.sendMail(makeEmail(message), result -> {
            if (result.succeeded()) {
                message.reply(new JsonObject().put("sent email", true));
            } else {
                result.cause().printStackTrace();
                message.reply(new JsonObject().put("sent email", false));
            }
        });
    }

    private MailMessage makeEmail(Message<JsonObject> message){
        JsonObject address = message.body().getJsonObject("address");
        MailMessage email = new MailMessage()
                .setFrom(address.getString("from"))
                .setTo(address.getString("to"))
                .setSubject(address.getString("subject"))
                .setHtml(message.body().getString("html"));

        return email;
    }
}
