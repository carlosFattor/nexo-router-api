exports.name = "findAndModify";

exports.command = function(data){

  return {
     findAndModify : "user",
     query: {
        email: data.email
     },
     update: {
        $set: {
            password: data.password
        }
     },
     new: false
  };
 }