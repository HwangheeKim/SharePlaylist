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
    current : {type:Schema.ObjectId, ref:'Group'}
});
var User = mongoose.model('user', userSchema, 'user');

var groupSchema = new Schema({
    groupName : {type:String, required:true},
    creator : {type:Schema.ObjectId, ref:'User'}
});
var Group = mongoose.model('group', groupSchema, 'group');

var playlistSchema = new Schema({
    userID : {type:Schema.ObjectId, ref:'User', required:true},
    playlistName : {type:String, required:true},
    videos : [{type:Schema.Types.ObjectId, ref:'Video'}],
});
var Playlist = mongoose.model('playlist', playlistSchema, 'playlist');

var playingSchema = new Schema({
    groupID : {type:Schema.Types.ObjectId, ref:'Group'},
    currentPlaying : {type:Number, required:true, default:0},
    player : [{type:Schema.Types.ObjectId, ref:'User'}],
    startedAt : {type:Date, default:0},
    like : {type:Number, default:0}
});
var Playing = mongoose.model('playing', playingSchema, 'playing');

var videoSchema = new Schema({
    url : {type:String, required:true},
    title : {type:String, required:true},
    uploader : {type:String},
    thumbnail : {type:String}
});
var Video = mongoose.model('video', videoSchema, 'video');



////////*    Server Implementation    *////////

app.listen(8080, function() {console.log("Listening on port #8080")});
