var express = require('express');
var bodyParser = require('body-parser');
var path = require('path');
var fs = require('fs');

var multer = require('multer');
var storage = multer.diskStorage({
    destination: function(req, file, cb) {
        cb(null, 'upload/'); 
    },
    filename: function(req, file, cb) {
        cb(null, Date.now() + path.extname(file.originalname));
    }
});
var upload = multer({ storage: storage });

var app = new express();
app.use(bodyParser.json());


////////*    MongoDB    *////////
var mongoose = require('mongoose');
mongoose.connect('localhost', 'shareplaylist');

// Defining Schemas and Models
var Schema = mongoose.Schema;

var userSchema = new Schema({
    userID : {type:String, required:true},
    userName : {type:String, required:true},
    picture : {type:String, default:"http://www.ogubin.com/images/empty_profile2.png"},
    current : {type:Schema.Types.ObjectId, ref:'Group'},
    playlist : [{url : {type:String},
                 title: {type:String},
                 uploader: {type:String},
                 thumbnail: {type:String}}]
});
var User = mongoose.model('User', userSchema, 'User');

var groupSchema = new Schema({
    groupName : {type:String, required:true},
    creatorID : {type:String, required:true},
    creatorName : {type:String, required:true},
    
    currentPlayingIndex : {type:Number, required:true, default:0},
    videoLineup : [{url : {type:String},
                    title: {type:String},
                    uploader: {type:String},
                    thumbnail: {type:String},
                    player: {type:String},
                    like: {type:Number, default:0}}],
    startedAt : {type:Date, default:0},
});
var Group = mongoose.model('Groups', groupSchema, 'Groups');


////////*    Server Implementation    *////////

// GET request for all users
app.get('/user/all', function(req, res) {
    console.log("[/user/all] Got request");

    User.find({}).exec(function(err, result) {
        if (err) return res.send(500, {error: err});
    
        res.writeHead(200, {'Content-Type':'application/json'});
        res.write(JSON.stringify(result));
        res.end();
    });
});

// GET request for specific user status
app.get('/user/:userID', function(req, res) {
    console.log("[/user/all] Got request");

    User.findOne({userID:req.params.userID})
        .exec(function(err, result) {
        if (err) return res.send(500, {error: err});
    
        res.writeHead(200, {'Content-Type':'application/json'});
        res.write(JSON.stringify(result));
        res.end();
    });
});


// POST request for user enrollment
app.post('/user/enroll', function(req, res) {
    console.log("[user/enroll] Got request");

    User.findOneAndUpdate({userID:req.body['userID']}, req.body, {upsert:true}, function(err, doc) {
        if (err) return res.send(500, {error: err});

        console.log("DONE ENROLL NEW USER " + JSON.stringify(req.body));
        res.writeHead(200, {'Content-Type':'application/json'});
        res.write(JSON.stringify(doc));
        res.end();
    });
});

// GET request for playlist
app.get('/user/myplaylist/:userID', function(req,res){
    console.log("[user/:userID Got get request");

    User.findOne({}, req.body, function(err, result){
        if(err) return res.send(500, {error:err});

        res.writeHead(200, {'Content_Type' : 'application/json'});
        res.write(JSON.stringify(result.playlist));
        res.end();
    });
});

// POST request from user
app.post('/user/myplaylist/:userID', function(req, res){
    console.log("[user/:userID Got post request");
    
    User.findOne({}, req.body, function(err, result){
        if(err) return res.send(500, {error:err});

        result.playlist.push(res.body);
        res.writeHead(200, {'Content_Type' : 'application/json'});
        res.write(JSON.stringify{Result : "OK"});
        res.end();
    })
});

// GET request for all group information
app.get('/group/all', function(req, res) {
    console.log("[group/all] Got request");

    Group.find({}, function(err, result) {
        if (err) return res.send(500, {error: err});

        res.writeHead(200, {'Content-Type' : 'application/json'});
        res.write(JSON.stringify(result));
        res.end();
    });
});

// GET request for specific group
app.get('/group/:groupID', function(req, res) {
    console.log("[group/:groupID] Got request");

    Group.findOne({_id:req.params.groupID}, function(err, result) {
        if (err) return res.send(500, {error: err});

        res.writeHead(200, {'Content-Type' : 'application/json'});
        res.write(JSON.stringify(result));
        res.end();
    });
});

// POST request for add videoLineup to the group
app.post('/group/:groupID/addLineup', function(req, res) {
    console.log("[/group/:groupID/addLineup] Got request");

    Group.findByIdAndUpdate(req.params.groupID,
            {$push: {"videoLineup": req.body}},
            {safe: true, upsert: true, new: true},
            function(err, model) {
                if (err) throw err;

                res.writeHead(200, {'Content-Type' : 'application/json'});
                res.write(JSON.stringify({Result : "OK"}));
                res.end();
            });
});

// POST request for new group
app.post('/group/new/', function(req, res) {
    console.log("[group/new] Got request");

    var newGroup = new Group(req.body);
    newGroup.save(function(err1) {
        if (err1) return res.send(500, {error: err1});

        res.writeHead(200, {'Content-Type' : 'application/json'});
        res.write(JSON.stringify({Result : "OK"}));
        res.end();
    });
});

//POST request for new playlist
app.post('/playlist/:userID', function(req, res) {
    console.log("[/playlist/:userID] Got request");

    User.findOne({userID : req.paramsUserID}, function(err, result){
        result.playlist.save(function(err){
            if(err1) return res.send(500, {error:err1});

            res.writeHead(200, {'Content-Type':'application/json'});
            res.write(JSON.stringify({Result : "OK"}));
            res.end();
        }); 
    });
});


app.listen(8080, function() {console.log("Listening on port #8080")});
