var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var chatSchema = new Schema({
        productId: String,
        users: [String],
        createdAt: Number,
        updatedAt: Number
    },
    {
        timestamps: {currentTime: () => Date.now()}
    });

var Chat = mongoose.model('chat', chatSchema);

module.exports = Chat;
