package com.nexo.nexorouter.microservice.gateway;

import com.hazelcast.config.Config;
import com.nexo.nexorouter.microservice.gateway.action.VerifyAuthentication;
import com.nexo.nexorouter.microservice.gateway.api.APIGatewayVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

/**
 * Created by carlos on 18/04/17.
 */
public class starter extends AbstractVerticle{

    @Override
    public void start() throws Exception {
        super.start();
        Config hazelcastConfig = new Config();

        ClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);
        VertxOptions _options = new VertxOptions().setClusterManager(mgr);

        Vertx.clusteredVertx(_options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                DeploymentOptions options = new DeploymentOptions().setConfig(config());

                vertx.deployVerticle(APIGatewayVerticle.class.getName(), options);
                vertx.deployVerticle(VerifyAuthentication.class.getName(), options);
            } else {
                // failed!
                System.out.println("Error cluster");
            }
        });

    }
}
