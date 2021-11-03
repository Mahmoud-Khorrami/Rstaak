var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var ObjectId = Schema.ObjectId;

var productSchema = new Schema({
	productId:{type : String, required : true},
	title : {type : String, required : true},
	imageUrl : {type : String, required : true},
	create_date : { type: Number, required: true, default : new Date().getTime()},
	update_date : { type: Number, required: true, default : new Date().getTime()},
});

productSchema.pre('save', function(next) {
	this.create_date = new Date().getTime();
	this.update_date = new Date().getTime();
	next();
});

var Product = mongoose.model('product', productSchema);

module.exports = Product;
