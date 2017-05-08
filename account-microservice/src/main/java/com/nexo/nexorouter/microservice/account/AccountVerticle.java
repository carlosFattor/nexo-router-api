package com.nexo.nexorouter.microservice.account;

import com.nexo.nexorouter.microservice.account.api.RestAccountAPIVerticle;
import com.nexo.nexorouter.microservice.common.BaseMicroserviceVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;

/**
 * Created by carlos on 18/04/17.
 */
public class AccountVerticle extends BaseMicroserviceVerticle {
    DeploymentOptions options = new DeploymentOptions().setInstances(1).setWorker(true);
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start();

        vertx.deployVerticle(RestAccountAPIVerticle.class.getName(), options);
    }
}
