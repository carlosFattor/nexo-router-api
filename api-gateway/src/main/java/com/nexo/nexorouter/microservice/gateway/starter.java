package com.nexo.nexorouter.microservice.gateway;

import com.nexo.nexorouter.microservice.gateway.api.APIGatewayVerticle;
import com.nexo.nexorouter.microservice.gateway.action.VerifyAuthentication;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;

/**
 * Created by carlos on 18/04/17.
 */
public class starter extends AbstractVerticle{

    DeploymentOptions options = new DeploymentOptions().setInstances(config().getInteger("instances")).setWorker(true);

    @Override
    public void start() throws Exception {
        super.start();
        vertx.deployVerticle(APIGatewayVerticle.class.getName(), options);

        vertx.deployVerticle(VerifyAuthentication.class.getName(), options);
    }
}
