package com.nexo.nexorouter.microservice.mail;

import com.nexo.nexorouter.microservice.common.BaseMicroserviceVerticle;
import com.nexo.nexorouter.microservice.mail.api.RestMailCarrierAPIVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;

/**
 * Created by carlos on 18/05/17.
 */
public class MailCarrierVerticle extends BaseMicroserviceVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start();
        DeploymentOptions options = new DeploymentOptions().setInstances(config().getInteger("instances")).setWorker(true);
        vertx.deployVerticle(RestMailCarrierAPIVerticle.class.getName(), options);
    }
}
