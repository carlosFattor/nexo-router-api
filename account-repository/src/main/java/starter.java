import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;

/**
 * Created by carlos on 10/05/17.
 */
public class starter extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        super.start();
        DeploymentOptions options = new DeploymentOptions().setInstances(config().getInteger("instances")).setWorker(true);

        vertx.deployVerticle("js/util/mongo@executor.js", options);
    }
}
