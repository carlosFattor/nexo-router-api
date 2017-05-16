package com.nexo.nexorouter.microservice.common;

import com.nexo.nexorouter.microservice.common.util.Util;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;


/**
 * Created by carlos on 18/04/17.
 */
public abstract class Flow extends AbstractVerticle {

    @Override
    public void start() throws Exception{
        EventBus eb = vertx.eventBus();
        System.out.println(Util.event(getClass()));
        MessageConsumer<JsonObject> consumer = eb.consumer(Util.event(getClass()));
        consumer.handler(this::process);
    }

    abstract protected void process(Message<JsonObject> message);

}
