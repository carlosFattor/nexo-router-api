//com.nexo.nexorouter.microservice.common.util.Util
//var util = require("../com/nexo/nexorouter/microservice/common/util/util.js");
var MongoClient = require("vertx-mongo-js/mongo_client");

module.exports = {

  vertxStart : function() {
    var self = this;
    var scripts = {};
    var config = vertx.getOrCreateContext().config();

    this.mongoClient = MongoClient.createShared(vertx, config.mongo);

    var files = vertx.fileSystem().readDir("js/scripts", function(files){

      console.log(config.scripts_prefix);
      console.log(files.length);

      for(var i in files){
        var filename = files[i];
        var path = files[i].split("\\").join("/");

        var key = path.substring(path.lastIndexOf("/")+1, path.indexOf(".js"));

        if(key.indexOf(config.scripts_prefix) !== 0)
           continue;

        scripts[key] = require("../scripts/"+key+".js");
        console.log(key);
      }

      for(var key in scripts){
        var event = key;
        registerEvent(event, load(key));
      }

    });

    function load(scriptName){
      return scripts[scriptName];
    }

    function registerEvent(event, script){
      var eb = vertx.eventBus();

      eb.consumer(event, function (message) {

        var data = message.body();
        var command = script.command(data);

        console.log(JSON.stringify(command));

        self.mongoClient.runCommand(script.name, command, function (res, res_err) {

          if (res_err == null) {
            if(res && res.ok){
              var r = res.result || res.value || null;

              message.reply(script.transform ? script.transform(r) : r);
            }else
              message.fail(500, "Erro executing mongo script");
          } else {
            res_err.printStackTrace();
            message.fail(500, res_err.getMessage() || "Erro executing mongo script");
          }

        });

      });
    }
  },

  vertxStop : function() {
    if(this.mongoClient)
      this.mongoClient.close();
  }
}

