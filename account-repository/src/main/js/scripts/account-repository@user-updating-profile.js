exports.name = "findAndModify";

exports.command = function(data){
  return {
     findAndModify : "user",
     query: {
        email: data.email
     },
     update: {
        $set: {
            profile: data.profile
        }
     },
     new: false
  };
 }