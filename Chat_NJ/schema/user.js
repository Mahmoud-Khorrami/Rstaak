var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var ObjectId = Schema.ObjectId;

var userSchema = new Schema({
	userId: { type:String,required:true},
	cellNumber:{type : String, required : true, unique : true},
	create_date : { type: Number, required: true, default : new Date().getTime()},
	update_date : { type: Number, required: true, default : new Date().getTime()},
});

userSchema.pre('save', function(next) {
	this.create_date = new Date().getTime();
	this.update_date = new Date().getTime();
	next();
});

var User = mongoose.model('User', userSchema);

module.exports = User;
