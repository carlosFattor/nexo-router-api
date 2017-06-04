exports.name = "findAndModify";

exports.command = function(data){
  return {
     findAndModify : "user",
     query: {
        email: data.email
     },
     update: {
        $push: {
            tokens: data.token
        }
     },
     new: false
  };
 }