exports.name = "aggregate";

exports.command = function(data){
  return {
     aggregate : "account",
     pipeline : [
       { $match : {
         accountId : data.accountId
       }},
       { $limit : 1}
     ]
  };
 }


exports.transform = function(result){
  if(result && result.length){
    return result[0];
  }else
    return null;
}