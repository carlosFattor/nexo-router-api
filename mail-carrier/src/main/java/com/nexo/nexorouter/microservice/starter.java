package com.nexo.nexorouter.microservice;

import com.hazelcast.config.Config;
import com.nexo.nexorouter.microservice.mail.MailCarrierVerticle;
import com.nexo.nexorouter.microservice.mail.action.MakeEmail;
import com.nexo.nexorouter.microservice.mail.action.SendEmail;
import com.nexo.nexorouter.microservice.mail.flow.EmailCarrier;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

/**
 * Created by carlos on 18/05/17.
 */
public class starter extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        super.start();

        Config hazelcastConfig = new Config();

        ClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);
        VertxOptions _options = new VertxOptions().setClusterManager(mgr);

        Vertx.clusteredVertx(_options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                DeploymentOptions options = new DeploymentOptions();
                options.setConfig(config());

                vertx.deployVerticle(MakeEmail.class.getName(), options);
                vertx.deployVerticle(SendEmail.class.getName(), options);
                vertx.deployVerticle(EmailCarrier.class.getName(), options);

                vertx.deployVerticle(MailCarrierVerticle.class.getName(), options);
            } else {
                // failed!
                System.out.println("Error cluster");
            }
        });
    }
}
