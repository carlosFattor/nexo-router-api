exports.name = "aggregate";

exports.command = function(data){
  console.log(data)
  return {
     aggregate : "user",
     pipeline : [
       { $match : {email : data.email}},
       { $limit : 1}
     ]
  };
 }


exports.transform = function(result){
  var result = result && result.length ? result[0] : null;
  console.log(JSON.stringify(result));
  return result;
}