var Message = require('../schema/message');
var Chat = require('../schema/chat');

exports.sendMessage = function(data, io, hashMap)
{
    Chat.findOne({_id: data.chatId}, function (err, chat)
    {
        if (err)
            console.error(err);

        else
        {
            try
            {
                var message = new Message({
                    _id: data.messageId,
                    chatId: data.chatId,
                    senderId: data.senderId,
                    message: data.message,
                    sentDateTime: new Date().getTime()
                });

                message.save(function (err,message1)
                {
                    if (err)
                        console.error(err);

                    else
                    {
                        console.log("Message saved!!");

                        var socketId1 = hashMap.get(data.senderId);

                        if (socketId1)
                        {
                            var data1 = {
                                chatId: message1.chatId,
                                messageId: message1._id,
                                sentDateTime: message1.sentDateTime.toString(),
                                updatedAt: message1.updatedAt.toString()
                            };
                            io.to(socketId1).emit("sent", data1);
                            console.log("sentDateTime sent to sender")
                        }

                        var receiver = "";

                        if (chat.users[0] !== data.senderId)
                            receiver = chat.users[0];
                        else
                            receiver = chat.users[1];

                        var socketId2 = hashMap.get(receiver);

                        if (socketId2)
                        {
                            var data2 = {
                                chatId: message1.chatId,
                                messageId: message1._id,
                                senderId: message1.senderId,
                                message: message1.message,
                                sentDateTime: message1.sentDateTime.toString(),
                                updatedAt: message1.updatedAt.toString()
                            };

                            io.to(socketId2).emit("message", data2);
                            console.log("message sent to receiver")
                        }
                    }

                });
            } catch (err)
            {
                console.error(err);
            }

        }
    });
};

exports.deliveredMessage = function(data, io, hashMap)
{
    Message.findOneAndUpdate({_id: data.messageId},{deliveredDateTime: data.deliveredDateTime},{}
        ,(err)=>{

            if (err)
                console.log(err);

            else
            {
                console.log("message delivered");

                Message.findOne({_id: data.messageId}, function (err, message)
                {

                    var socketId1 = hashMap.get(data.user);
                    if (socketId1)
                    {
                        io.to(socketId1).emit("updatedAt", {updatedAt: message.updatedAt.toString()});
                        console.log("updatedAt sent to user")
                    }

                    var socketId2 = hashMap.get(data.senderId);
                    if (socketId2)
                    {
                        var data2 = {
                            chatId: data.chatId,
                            messageId: data.messageId,
                            deliveredDateTime: data.deliveredDateTime,
                            updatedAt: message.updatedAt.toString()
                        };

                        io.to(socketId2).emit("delivered", data2);
                        console.log("deliveredDateTime sent to sender")
                    }
                });
            }
        });
};

exports.viewedMessage = function(data, io, hashMap)
{
    var viewedDateTime = new Date().getTime();

    Message.findOneAndUpdate({_id: data.messageId},{viewedDateTime: data.viewedDateTime},{}
        ,(err)=>{

            if (err)
                console.log(err);

            else
            {
                console.log("message viewed");

                Message.findOne({_id: data.messageId}, function (err, message)
                {
                    var socketId1 = hashMap.get(data.user);
                    if (socketId1)
                    {
                        io.to(socketId1).emit("updatedAt", {updatedAt: message.updatedAt.toString()});
                        console.log("viewedDateTime sent to user")
                    }

                    var data1 = {
                        chatId: data.chatId,
                        messageId: data.messageId,
                        viewedDateTime: data.viewedDateTime,
                        updatedAt: message.updatedAt.toString()
                    };

                    var socketId2 = hashMap.get(data.senderId);
                    if (socketId2)
                    {
                        io.to(socketId2).emit("viewed", data1);
                        console.log("viewedDateTime sent to sender")
                    }
                });
            }
        });
};

exports.typing = function(data, io, hashMap)
{
    Chat.findOne({_id: data.chatId}, function (err, chat)
    {
        if (err)
            console.error(err);

        else
        {
            try
            {
                for (let i = 0; i < chat.users.length; i++)
                {
                    if (chat.users[i] !== data.senderId)
                    {
                        var socketId = hashMap.get(chat.users[i]);

                        if (socketId)
                        {
                            io.to(socketId).emit("typing", data);
                            console.log("typing sent");
                        }
                    }
                }

            } catch (err)
            {
                console.error(err);
            }

        }
    });
};
