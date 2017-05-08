package com.nexo.nexorouter.microservice;

import com.nexo.nexorouter.microservice.account.AccountVerticle;
import com.nexo.nexorouter.microservice.account.action.CreatingJwt;
import com.nexo.nexorouter.microservice.account.action.EncryptingUserPassword;
import com.nexo.nexorouter.microservice.account.action.UserExist;
import com.nexo.nexorouter.microservice.account.action.ValidatingUserPassword;
import com.nexo.nexorouter.microservice.account.flow.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;

/**
 * Created by carlos on 18/04/17.
 */
public class starter extends AbstractVerticle{

    DeploymentOptions options = new DeploymentOptions().setInstances(1).setWorker(true);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);
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

        vertx.deployVerticle(AccountVerticle.class.getName(), options);
    }
}
