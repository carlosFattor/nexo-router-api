exports.name = "aggregate";

exports.command = function(data){
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
            $group: {
                _id: null,
                count: { $sum: 1 },
            }
        }
     ]
  };
 }