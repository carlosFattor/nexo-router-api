exports.name = "aggregate";

exports.command = function(data){
  var sort_order = {};
  sort_order[data.orderBy] = (data.ascending == 1)? 1 : -1;
  return {
     aggregate : "user",
     pipeline: [
        { $unwind: "$profile" },
        {
            $match: {
                "profile.accountId": data.accountId,
                email : { $regex: ".*"+data.query+".*", $options: "i" }
            }
        },
        {
            $project: {
                "_id": 0,
                "email": 1,
                "firstName": "$profile.firstName",
                "confirmed": "$profile.confirmed",
                "avatar": "$profile.avatar",
                "userStatus": 1,
                "createdAt": 1
            }
        },
        {
            $sort: sort_order,
        },
        {
            $skip: data.offset || 0,
        },
        {
            $limit: data.limit || 20
        }
     ]
  };
 }