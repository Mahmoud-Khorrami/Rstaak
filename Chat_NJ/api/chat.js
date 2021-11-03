var Message = require('../schema/message');
var Chat = require('../schema/chat');

exports.chatStatus = function(req, res)
{
	var users = req.body.users;
	var productId = req.body.productId;

	var query = {$and: [{users: {$all: users}}, {productId: productId}]};
	Chat.findOne(query, function (err, chat)
	{
		if (chat)
		{
			res.send({
				"status": 200,
				"message": {"id": chat._id}
			});
		}

		else
		{
			res.send({
				"status": 201,
				//"message": "chat not exist"
			});
		}
	});

};

exports.createChat = function(req,mysqlConnection, io, hashMap,res)
{
	var senderId = req.body.senderId;
	var receiverId = req.body.receiverId;
	var productId = req.body.productId;

	var users = [senderId,receiverId];

	var query = {$and: [{users: {$all: users}}, {productId: productId}]};
	Chat.findOne(query, function (err, chat)
	{
		if (chat)
		{
			mysqlConnection.query("SELECT * FROM product WHERE productId = ?", productId, function (err, result)
			{
				if (err)
					console.log(err)

				else
				{
					var data = {
						chat: chat,
						productTitle: result[0].title,
						imageList: result[0].imageList
					};

					var socketId = hashMap.get(receiverId);
					if (socketId)
					{
						io.to(socketId).emit("new chat", data);
						console.log("new chat sent to receiver");
					}

					res.send({
						"status": 200,
						"message": data
					});
				}
			});

		}

		else
		{
			chat = new Chat({
				users: users,
				productId: productId
			});

			chat.save(function (err, chat1)
			{
				if (err)
					console.log(err);

				else
				{
					mysqlConnection.query("SELECT * FROM product WHERE productId = ?", productId, function (err, result)
					{
						if (err)
							console.log(err)

						else
						{
							var data = {
								chat: chat1,
								productTitle: result[0].title,
								imageList: result[0].imageList
							};

							var socketId = hashMap.get(receiverId);
							if (socketId)
							{
								io.to(socketId).emit("new chat", data);
								console.log("new chat sent to receiver");
							}

							res.send({
								"status": 200,
								"message": data
							});
						}
					});
				}

			});
		}
	});

};

exports.chatAndMessageList = function(req, mysqlConnection, res)
{
	var userId = req.body.userId;
	var chatUpdatedAt = req.body.chatUpdatedAt || 0;
	var messageUpdatedAt = req.body.messageUpdatedAt || 0;

	userId = [userId];
	var query = {$and: [{users: {$in: userId}}, {updatedAt: {$gt: parseInt(chatUpdatedAt)}}]};

	var chatData = [];
	var messageData = [];

	Chat.find(query).sort({updatedAt: +1}).exec(function (err, chats)
	{
		if (err)
			console.log(err);

		else
		{

			let promise = new Promise(function (resolve, reject)
			{
				if (chats.length > 0)
				{
					for (let i = 0; i < chats.length; i++)
					{
						mysqlConnection.query("SELECT * FROM product WHERE productId = ?", chats[i].productId, function (err, result)
						{
							if (err)
								console.log(err);

							else
							{
								var data = {
									chat: chats[i],
									productTitle: result[0].title,
									imageList: result[0].imageList
								};
								chatData.push(data);

								if (i === chats.length - 1)
									resolve();
							}
						});

					}

				}

				else
					resolve();
			});

			promise.then(
				function ()
				{
					Chat.find({users: {$in: userId}}).exec(function (err, chats)
					{
						if (err)
							console.log(err);

						else
						{

							let promise1 = new Promise(function (resolve, reject)
							{
								for (let x = 0; x < chats.length; x++)
								{
									var query = {$and: [{chatId: chats[x]._id}, {updatedAt: {$gt: parseInt(messageUpdatedAt)}}]};

									Message.find(query).sort({updatedAt: +1}).exec(function (err, messages)
									{
										if (err)
											console.log(err);

										else if (messages.length > 0)
										{
											for (let i = 0; i < messages.length; i++)
											{
												var data = {
													messageId: messages[i]._id,
													chatId: messages[i].chatId,
													senderId: messages[i].senderId,
													message: messages[i].message,
													sentDateTime: messages[i].sentDateTime.toString(),
													deliveredDateTime: messages[i].deliveredDateTime.toString(),
													viewedDateTime: messages[i].viewedDateTime.toString(),
													updatedAt: messages[i].updatedAt.toString()
												};
												messageData.push(data);
											}

											if (x === chats.length - 1)
												resolve();
										}
										else
										{
											if (x === chats.length - 1)
												resolve();
										}
									});
								}
							});

							promise1.then(
								function ()
								{
									res.send({
										status: 200,
										message: {messageData: messageData, chatData: chatData}
									});

									console.log({messageData: messageData, chatData: chatData});
								},
								function (error)
								{
									console.log(error);
								}
							);

						}
					});
				},

				function (error)
				{
					console.log(error)
				}
			)
		}

	});
};

