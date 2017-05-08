exports.name = "aggregate";

exports.command = function(data){
    return {
        aggregate : "user",
        pipeline : [
            {
                $match : {
                    email : data.email
                }
            },
            {
                $project : {
                    userId: 1,
                    createdAt: 1,
                    email: 1,
                    password: 1,
                    userStatus: 1,
                    profile: 1,
                    tokens: 1,
                    updateAt: 1
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