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
    playlist : [{type:Schema.Types.ObjectId, ref:'Video'}]
});
var User = mongoose.model('User', userSchema, 'User');

var groupSchema = new Schema({
    groupName : {type:String, required:true},
    creatorID : {type:String, required:true},
    creatorName : {type:String, required:true},
    
    currentPlayingIndex : {type:Number, required:true, default:0},
    currentPlyingVideo : [{type:Schema.Types.ObjectId, ref:'Video'}],
    currentPlayer : [{type:String}],
    startedAt : {type:Date, default:0},

    like : {type:Number, default:0}
});
var Group = mongoose.model('Groups', groupSchema, 'Groups');

var videoSchema = new Schema({
    url : {type:String, required:true},
    title : {type:String, required:true},
    uploader : {type:String},
    thumbnail : {type:String}
});
var Video = mongoose.model('Video', videoSchema, 'Video');



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


// POST request for user enrollment
app.post('/user/enroll', function(req, res) {
    console.log("[user/enroll] Got request");

    User.findOneAndUpdate({userID:req.body['userID']}, req.body, {upsert:true}, function(err, doc) {
        if (err) return res.send(500, {error: err});

        console.log("DONE ENROLL NEW USER " + JSON.stringify(req.body));
        res.writeHead(200, {'Content-Type':'application/json'});
        res.write(JSON.stringify({result: 'OK'}));
        res.end();
    });
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

// GET request for playlist for user with 'userID'
app.get('/playlist/:userID', function(req, res) {
    console.log("[/playlist/:userID] Got request");
    User.findOne({userID : req.params.userID})
        .populate('playlist')
        .exec(function(err, result) {
            if (err) return res.send(500, {error: err});

            res.writeHead(200, {'Content-Type' : 'application/json'});
            if(result.hasOwnProperty('playlist')) {
                res.write(JSON.stringify(result['playlist']));
            } else {
                res.write(JSON.stringify([]));
            }
            res.end();
    });
});

//POST request for new playlist
app.post('/playlist/:userID', function(req, res) {
    console.log("[/playlist/:userID] Got request");
    
    var newVideoSchema = new videoSchema(req.body);
    newVideoSchema.save(function(err1) {
	if (err1) return res.send(500, {error: err1});
	
	res.writeHead(200, {'Content-Type' : 'application/json'});
	res.write(JSON.stringify({Result : "OK}));
	res.end();
    });
});

//DELETE request for playlist
app.delete('playlist/:userID', function(req, res) {
    



});








app.listen(8080, function() {console.log("Listening on port #8080")});
