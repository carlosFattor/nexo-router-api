exports.name = "findAndModify";

exports.command = function(data){
    return {
        findAndModify : "user",
        query : {
            tokens : {$elemMatch: {"token": data.token}}
        },
        update: {
           $set: {
               tokens: []
           }
        },
      new: false
    };
 }


exports.transform = function(result){
  if(result && result.length){
    return result[0];
  }else
    return null;
}