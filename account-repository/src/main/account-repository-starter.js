var config = vertx.getOrCreateContext().config();
config.scripts_prefix = "account-repository";

var options = {
  instances : config.instances || 8,
  config : config
};
vertx.deployVerticle("js/util/mongo@executor.js", options);