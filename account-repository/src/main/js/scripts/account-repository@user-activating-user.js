exports.name = "findAndModify";

exports.command = function(data){
  return {
     findAndModify : "user",
     query: {
        email: data.email
     },
     update: {
        $set: {
            "profile.confirmed": data.confirmed,
            "password": data.password,
            tokens: []
        }
     },
     new: false
  };
 }