exports.name = "aggregate";

exports.command = function(data){
    return {
        aggregate : "user",
        pipeline : [
            {
                $match : {
                    tokens : {$elemMatch: {"token": data.token}}
                }
            },
            {
                $project : {
                    email: 1,
                    "profile.firstName" : 1,
                    "profile.lastName" : 1,
                    tokens: 1,
                    userStatus: 1,
                    confirmed: 1
                }
            },
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