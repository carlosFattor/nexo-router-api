exports.name = "findAndModify";

exports.command = function(data){
  return {
     findAndModify : "account",
     query: { accountId : data.accountId },
     update: data,
     fields : {
        name: 1,
        accountId: 1,
        _id: 1
     },
     upsert : true,
     new : true
  };
 }