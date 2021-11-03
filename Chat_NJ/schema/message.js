var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var ObjectId = Schema.ObjectId;

var messageSchema = new Schema({
	_id: String,
	chatId: String,
	senderId: String,
	message: String,
	sentDateTime : { type: Number, default : new Date().getTime()},
	deliveredDateTime: { type: Number, default : 0},
	viewedDateTime: { type: Number, default : 0},
	createdAt: Number,
	updatedAt: Number
},
	{
		timestamps: {currentTime: () => Date.now()}
	});

var Message = mongoose.model('Message', messageSchema);

module.exports = Message;