const User = require('../schema/user');
const AppUtil = require('../utils/AppUtil');

exports.add = function(req, res) {

	var cellNumber = req.body.cellNumber;
	var name = req.body.name;

	console.log(req.body);
	if (cellNumber) {
		User.findOne({cellNumber: cellNumber}, function (err, result) {

			if (result) {

				res.send({
					"code": 201,
					"message": "User already exists."
				});
			}

			else {
				var user = new User({
					name: name,
					cellNumber: cellNumber,
				});

				user.save(function (err) {

					if (err) {
						console.error(err);
						res.send({
							"code": 201,
							"message": "Failed to save user!"
						});

					}
					else {
						res.send({
							"code": 200,
							"message": "User saved!!"
						});
					}
				});
			}
		});

	}

	else {
		res.send({
			"code": 201,
			"message": "Misssing Parameter!"
		});
	}
};

exports.login = function(req, res) {
	var cellNumber = req.body.cellNumber;

	if (cellNumber) {

		User.findOne({cellNumber: cellNumber}, function (err, user) {

			if (user) {
				res.send({
					"code": 200,
					"message": {
						id: user._id,
						name: user.name,
						cellNumber: user.cellNumber,
					}
				});
			} else {
				res.send({
					"code": 201,
					"message": "Invalid User!"
				});
			}
		});

	} else {
		res.send({
			"code": 201,
			"message": "Misssing Parameter!"
		});
	}
};