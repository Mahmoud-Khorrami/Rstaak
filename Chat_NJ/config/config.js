const mysql = require('mysql');
const mongo = require('mongodb');
const mongoHost = 'localhost';
const mongoPort = 27017;
const mongoUsername = encodeURIComponent("rstaakir_mongo");
const mongoPassword = encodeURIComponent("McKC(o!4hP3;");
const mongoDatabase = 'rstaakir_chat';
const authMechanism = "SCRAM-SHA-256";
const uri =
    `mongodb://${mongoUsername}:${mongoPassword}@${mongoHost}:${mongoPort}/${mongoDatabase}`;
const MongoClient = mongo.MongoClient;


var mysqlAuth = {host: "localhost", user: "rstaakir_mysql", password: "Vi8Um-i(2*v;", database: "rstaakir_rostaak"};
var mysqlConnection = mysql.createConnection(mysqlAuth);
mysqlConnection.connect(function(err) { if (err) throw err; });


// module.exports = MongoClient
// module.exports = mysqlAuth

module.exports = {
    MongoClient: MongoClient,
    mysqlConnection: mysqlConnection,
    uri: uri,
    mongoDatabase: mongoDatabase
};