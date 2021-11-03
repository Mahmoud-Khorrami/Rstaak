const {MongoClient, mysqlConnection, uri, mongoDatabase} = require('./config/config.js');
const express = require('express');
const bodyParser = require('body-parser');
const http = require('http');
const path = require('path');
const mongoose = require('mongoose');
const HashMap = require('hashmap');
mongoose.set('useFindAndModify', false);

var userApi = require('./api/user');
var chatApi = require('./api/chat');
var messageApi = require('./api/message');

//---------------------------------------

const app = express();
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

//---------------------------------------

var hashMap = new HashMap();

//---------------------------------------

const server = http.createServer(app);
const { Server } = require("socket.io");
const io = new Server(server);

//---------------------------------------


mongoose.Promise = global.Promise;
var promise = mongoose.connect(uri,{
    useNewUrlParser:true,
    useUnifiedTopology:true
});

promise.then(function(db){

    server.listen(5000, () => {
        console.log('listening on localhost:5000');
    });

}).catch(function(reason) {

    console.error(reason);
});

//---------------------------------------

app.get('/', (req, res) => {
    res.sendFile(__dirname + '/index.html');
});

//---------------------------------------

app.post("/api/chat-status",chatApi.chatStatus);

app.post("/api/create-chat",function(req,res){
    chatApi.createChat(req,mysqlConnection,io,hashMap,res);
});

app.post("/api/chat-and-message-list",function (req,res)
{
    chatApi.chatAndMessageList(req,mysqlConnection,res);
});

app.post("/online",function(req,res){

    var v = hashMap.search(req.body.userId + "-online");
    if (v)
        res.send({"status": 200});
    else
        res.send({"status": 201});
});
//---------------------------------------

io.sockets.on('connection', function(socket)
{

    socket.on('join', function (data)
    {
        console.log("join", data);
        socket.join(data.userId);
        hashMap.set(data.userId, socket.id);

        //---------------------------------------------

        console.log(data.userId + '-online');
        hashMap.set(data.userId + '-online', data.userId + '-online');
        io.emit('online', {"userId": data.userId});
    });

    socket.on('send-message', function (data)
    {
        messageApi.sendMessage(data, io, hashMap);
    });

    socket.on('delivered-message', function (data)
    {
        messageApi.deliveredMessage(data, io, hashMap);
    });

    socket.on('viewed-message', function (data)
    {
        messageApi.viewedMessage(data, io, hashMap);
    });

    socket.on('disconnect', function ()
    {
        var key1 = hashMap.search(socket.id);
        var key2 = hashMap.search(key1 + '-online');

        if (key2)
        {
            hashMap.delete(key2);
            io.emit('offline', {"userId": key1});
        }

        if (key1)
        {
            socket.leave(key1, function (err)
            {
                console.log("user disconnected", err);
            });
            hashMap.delete(key1);
        }
        console.log("clients", hashMap);
    });

    socket.on('online', function (userId)
    {
//        console.log(userId + '-online');
//        hashMap.set(userId + '-online', userId + '-online');
//        io.emit('online', {"userId": userId});
    });

    socket.on('offline', function (userId)
    {
        console.log(userId + '-offline');

        var key = hashMap.search(userId + '-online');
        if (key)
        {
            hashMap.delete(key);
            io.emit('offline', {"userId": userId});
        }

    });

    socket.on('typing', function (data)
    {
        messageApi.typing(data, io, hashMap);
    });
});