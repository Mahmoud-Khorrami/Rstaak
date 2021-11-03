
// exports.chatList = function(req, res)
// {
//     var userId = req.body.userId;
//     var updatedAt = req.body.updatedAt || 0;
//
//     userId = [userId];
//     var query = {$and: [{users: {$in: userId}}, {updatedAt: {$gt: parseInt(updatedAt)}}]};
//
//     Chat.find(query).sort({updatedAt: +1}).exec(function (err, chats)
//     {
//         if (err)
//             console.log(err);
//
//         else if (chats.length > 0)
//         {
//             var chatData = [];
//             for (let i=0; i<chats.length; i++)
//             {
//                 var data = {
//                     id: chats[i]._id,
//                     productId: chats[i].productId,
//                     users: chats[i].users,
//                     createdAt: chats[i].createdAt.toString(),
//                     updatedAt: chats[i].updatedAt.toString()
//                 };
//
//                 chatData.push(data);
//             }
//             res.send({
//                 "status": 200,
//                 "message": chatData
//             });
//         }
//
//         else
//         {
//             res.send({
//                 "status": 201,
//                 "message": "Not Exist New Data"
//             });
//         }
//     });
// };

//
// exports.messageList = function(req, res)
// {
//     var userId = req.body.userId;
//     var updatedAt = req.body.updatedAt || 0;
//
//     userId = [userId];
//     var query = {$and: [{users: {$in: userId}}, {updatedAt: {$gt: parseInt(updatedAt)}}]};
//
//     var chatData = [];
//     var messageData = [];
//
//     Chat.find(query).sort({updatedAt: +1}).exec(function (err, chats)
//     {
//         if (err)
//             console.log(err);
//
//         else
//         {
//             if (chats.length > 0)
//             {
//                 for (let i = 0; i < chats.length; i++)
//                 {
//                     var data = {
//                         id: chats[i]._id,
//                         productId: chats[i].productId,
//                         users: chats[i].users,
//                         createdAt: chats[i].createdAt.toString(),
//                         updatedAt: chats[i].updatedAt.toString()
//                     };
//
//                     chatData.push(data);
//                 }
//             }
//
//             Chat.find({users: {$in: userId}}).exec(function (err, chats)
//             {
//                 if (err)
//                     console.log(err);
//
//                 else
//                 {
//
//                     let promise = new Promise(function(resolve,reject)
//                     {
//                         for (let x = 0; x < chats.length; x++)
//                         {
//                             var query = {$and: [{chatId: chats[x]._id}, {updatedAt: {$gt: parseInt(updatedAt)}}]};
//
//                             Message.find(query).sort({updatedAt: +1}).exec(function (err, messages)
//                             {
//                                 if (err)
//                                     console.log(err);
//
//                                 else if (messages.length > 0)
//                                 {
//                                     for (let i = 0; i < messages.length; i++)
//                                     {
//                                         var data = {
//                                             messageId: messages[i]._id,
//                                             chatId: messages[i].chatId,
//                                             senderId: messages[i].senderId,
//                                             message: messages[i].message,
//                                             sentDateTime: messages[i].sentDateTime.toString(),
//                                             deliveredDateTim: messages[i].deliveredDateTim.toString(),
//                                             viewedDateTim: messages[i].viewedDateTim.toString(),
//                                             updatedAt: messages[i].updatedAt.toString()
//                                         };
//                                         messageData.push(data);
//                                     }
//
//                                     if (x === chats.length-1)
//                                         resolve({messageData:messageData, chatData:chatData});
//                                 }
//                                 else
//                                 {
//                                     if (x === chats.length-1)
//                                         resolve({messageData:messageData, chatData:chatData});
//                                 }
//                             });
//                         }
//                     });
//
//                     promise.then(
//                         function (value)
//                         {
//                             res.send({
//                                 status: 200,
//                                 message: value
//                             });
//
//                             console.log(value);
//                         },
//                         function (error)
//                         {
//                             console.log(error);
//                         }
//                     );
//
//                 }
//             });
//         }
//
//     });
//
//
//
// };

// exports.messageList = function(req, res)
// {
//     var chatId = req.body.chatId;
//     var updatedAt = req.body.updatedAt || 0;
//
//     var query = {$and: [{chatId: chatId}, {updatedAt: {$gt: parseInt(updatedAt)}}]};
//
//     Message.find(query).sort({updatedAt: +1}).exec(function (err, messages)
//     {
//         if (err)
//             console.log(err);
//
//         else if (messages.length > 0)
//         {
//             var messageData = [];
//             for (let i=0; i<messages.length; i++)
//             {
//                 var data = {
//                     messageId: messages[i]._id,
//                     chatId: messages[i].chatId,
//                     senderId: messages[i].senderId,
//                     message: messages[i].message,
//                     sentDateTime: messages[i].sentDateTime.toString(),
//                     deliveredDateTim: messages[i].deliveredDateTim.toString(),
//                     viewedDateTim: messages[i].viewedDateTim.toString(),
//                     updatedAt: messages[i].updatedAt.toString()
//                 };
//                 messageData.push(data);
//             }
//             res.send({
//                 status: 200,
//                 message: messageData
//             });
//         }
//
//         else
//         {
//             res.send({
//                 status: 201,
//                 message: "Not Exist New Data"
//             });
//         }
//     });
// };

// mysqlConnection.query("SELECT * FROM product WHERE productId = ?", productId, function (err, result) {
// 	if (err)
// 		console.log(err)
//
// 	else
// 	{
// 		var data = {
// 			chat: chat._id,
// 			productTitle: result[0].title,
// 			imageList: result[0].imageList
// 		};
//
//
//
// 		console.log(data);
// 	}
// });