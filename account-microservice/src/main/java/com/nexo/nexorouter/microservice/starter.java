package com.nexo.nexorouter.microservice;

import com.hazelcast.config.Config;
import com.nexo.nexorouter.microservice.account.AccountVerticle;
import com.nexo.nexorouter.microservice.account.action.CreatingJwt;
import com.nexo.nexorouter.microservice.account.action.EncryptingUserPassword;
import com.nexo.nexorouter.microservice.account.action.UserExist;
import com.nexo.nexorouter.microservice.account.action.ValidatingUserPassword;
import com.nexo.nexorouter.microservice.account.flow.*;
import io.vertx.core.*;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

/**
 * Created by carlos on 18/04/17.
 */
public class starter extends AbstractVerticle{

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);
        Config hazelcastConfig = new Config();


        ClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);

        VertxOptions _options = new VertxOptions().setClusterManager(mgr);

        Vertx.clusteredVertx(_options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                DeploymentOptions options = new DeploymentOptions().setInstances(config().getInteger("instances")).setWorker(true);
                vertx.deployVerticle(RootCreatingAccount.class.getName(), options);
                vertx.deployVerticle(RootFindingAccount.class.getName(), options);
                vertx.deployVerticle(RootCreatingUser.class.getName(), options);
                vertx.deployVerticle(RootListingUser.class.getName(), options);
                vertx.deployVerticle(EncryptingUserPassword.class.getName(), options);
                vertx.deployVerticle(UserFindingProfile.class.getName(), options);
                vertx.deployVerticle(ValidatingUserPassword.class.getName(), options);
                vertx.deployVerticle(UserLogging.class.getName(), options);
                vertx.deployVerticle(CreatingJwt.class.getName(), options);
                vertx.deployVerticle(UserUpdatingProfile.class.getName(), options);
                vertx.deployVerticle(RootDeletingUser.class.getName(), options);
                vertx.deployVerticle(UserRefreshingToken.class.getName(), options);
                vertx.deployVerticle(UserExist.class.getName(), options);
                vertx.deployVerticle(RootFindingProfile.class.getName(), options);
                vertx.deployVerticle(RootUpdatingUserProfile.class.getName(), options);
                vertx.deployVerticle(UserRecoveringPassword.class.getName(), options);

                vertx.deployVerticle(AccountVerticle.class.getName(), options);
            } else {
                // failed!
                System.out.println("Error cluster");
            }
        });


    }
}
