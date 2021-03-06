var express = require('express');
var bodyParser = require('body-parser');
var path = require('path');
var fs = require('fs');
var isoduration = require('iso8601-duration');
var http = require('http');
var https = require('https');

var FCM = require('fcm-push');
var serverKey = 'AAAAzPr7jRE:APA91bEvQUWMB2zkRlGLkLH98eFRGuxT8faGyZucAgopCj2nnRjIAILb79zJKjPILB-84x1vkGSkxeyK4dNIXkcZJEMqDx4AF-RYLuMMr55wp_eAZ358K2X5OzmnQVwoz8uysvKpC3OK'
var fcm = new FCM(serverKey);

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
    userToken : {type:String},
    picture : {type:String, default:"http://www.ogubin.com/images/empty_profile2.png"},
    current : {type:Schema.Types.ObjectId, ref:'Group'},
    playlist : [{url : {type:String},
                 title: {type:String},
                 uploader: {type:String},
                 thumbnail: {type:String}}]
});
var User = mongoose.model('User', userSchema, 'User');

var defaultDate = 10000000000000
var groupSchema = new Schema({
    groupName : {type:String, required:true},
    creatorID : {type:String, required:true},
    creatorName : {type:String, required:true},
    
    //currentPlayingIndex : {type:Number, required:true, default:0},
    videoLineup : [{url : {type:String},
                    title: {type:String},
                    uploader: {type:String},
                    thumbnail: {type:String},
                    playerID: {type:String},
                    playerName: {type:String},
                    startedAt: {type:Number, default:defaultDate},
                    duration: {type:Number},
                    like: {type:Number, default:0}}]
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

    if(!req.body.hasOwnProperty("current")) {
        req.body['current'] = null;
    }

    User.findOneAndUpdate({userID:req.body['userID']}, req.body, {upsert:true, new:true}, function(err, doc) {
        if (err) return res.send(500, {error: err});

        console.log("DONE ENROLL NEW USER " + JSON.stringify(req.body));
        res.writeHead(200, {'Content-Type':'application/json'});
        res.write(JSON.stringify(doc));
        res.end();
    });
});

// GET request for user remove token
app.get('/user/graduate/:userID', function(req, res) {
    console.log("[/user/graduate/:userID] Got request");

    User.findOneAndUpdate({userID:req.params.userID}, {current: null}, function(err, doc) {
        console.log("DONE UNGROUP USER " + JSON.stringify(req.body));
        res.writeHead(200, {'Content-Type':'application/json'});
        res.write(JSON.stringify(doc));
        res.end();
    });
});

// POST request from user
app.post('/user/myplaylist/:userID', function(req, res){
    console.log("[user/:userID] Got post request");
    
    User.findOneAndUpdate({userID:req.params.userID}, 
            {$push: {"playlist":req.body}},
            {safe: true, upsert: true, new: true}, 
            function(err, model) {
                if(err) return res.send(500, {error:err});
                res.writeHead(200, {'Content-Type' : 'application/json'});
                res.write(JSON.stringify({Result: "OK"}));
                res.end();
            });
});

// DELETE request from use
app.post('/user/myplaylist/delete/:userID', function(req,res){
    console.log("[user/:userID] got delete request");
    User.findOneAndUpdate({userID:req.params.userID},
            {$pull: {"playlist":{url: req.body['url']}}},
            function(err, model){
                if(err) return res.send(500, {error:err});
                res.writeHead(200, {'Content-Type' : 'application/json'});
                res.write(JSON.stringify({Result:"OK"}));
                res.end();
            });
})


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

    req.body['duration'] = duration(req.body['duration']);

    Group.findByIdAndUpdate(req.params.groupID,
            {$push: {"videoLineup": req.body}},
            {safe: true, upsert: true, new: true},
            function(err, model) {
                if (err) throw err;
                notifyLineupChanged(req.params.groupID);

                res.writeHead(200, {'Content-Type' : 'application/json'});
                res.write(JSON.stringify({Result : "OK"}));
                res.end();
            });
});

// POST request for remove videoLineup to the group
// TODO : Becareful! pull delete every records that match the condition!!!!
app.post('/group/:groupID/removeLineup', function(req, res) {
    console.log("[/group/:groupID/removeLineup] Got request");

    Group.findByIdAndUpdate(req.params.groupID,
            {$pull: {"videoLineup": {_id: req.body['_id'], url: req.body['url'], playerID: req.body['playerID']}}},
            function(err, model) {
                if (err) throw err;
                notifyLineupChanged(req.params.groupID);

                res.writeHead(200, {'Content-Type' : 'application/json'});
                res.write(JSON.stringify({Result : "OK"}));
                res.end();
            });
});

function notifyLineupChanged(groupID) {
    User.find({current:groupID}, function(err, result) {
        if (err) throw err;
        
        for (var i=0 ; i<result.length ; i++) {
            var message = {
                to: result[i]['userToken'],
                priority: "high",
                notification: {
                    title: "Lineup Changed",
                    body: "Lineup has been changed"
                }
            };

            fcm.send(message, function(err1, res) {
                if (err1) throw err1;
                console.log("Successfully sent with response: ", res);
            })
        }
    });
}

// GET request for next lineup
app.get('/group/:groupID/nextLineup', function(req, res) {
    console.log("[/group/:groupID/nextLineup] Got request");

    Group.findById(req.params.groupID, function(err, result) {
        if (err) throw err;
        
        var lineup = result['videoLineup'];
        for (var i=0 ; i<lineup.length ; i++) {
            // If the video has already been finished, continue to next one
            if (lineup[i].startedAt.valueOf() + lineup[i].duration < Date.now()) continue;

            // If somevideo is still playing, return that
            // If the video has not yet been played, return that & set startedAt to now
            if (lineup[i].startedAt.valueOf() == defaultDate) {
                console.log("\tnew video will be played " + lineup[i]._id);
                Group.update({'videoLineup._id' : lineup[i]._id},
                        {$set : {'videoLineup.$.startedAt' : Date.now()}}, function(err) {
                    if (err) throw err;

                    Group.findOne({_id:req.params.groupID, 'videoLineup._id':lineup[i]._id },
                            {'videoLineup.$':1}, function(err1, result) {
                        if (err1) throw err1;

                        res.writeHead(200, {'Content-Type' : 'application/json'});
                        res.write(JSON.stringify(result['videoLineup'][0]));
                        res.end();
                    });
                });
                return;
            } else {
                console.log("\tvideo has been played");
                res.writeHead(200, {'Content-Type' : 'application/json'});
                res.write(JSON.stringify(lineup[i]));
                res.end();
                return;
            }
        }
    
        // if there isn't such video, return nothing

        res.writeHead(200, {'Content-Type' : 'application/json'});
        res.write(JSON.stringify({}));
        res.end();
    });
});

// GET request for group information for list adapter
app.get('/group/info/:groupID', function(req, res) {
    console.log("[/group/info/:groupID] Got request");

    User.count({current:req.params.groupID}, function(errCount, count) {
        if (errCount) throw errCount;

        Group.findById(req.params.groupID, function(err, result) {
            if (err) throw err;

            var lineup = result['videoLineup'];
            for (var i=0 ; i<lineup.length ; i++) {
                // If the video has already been finished, continue to next one
                if (lineup[i].startedAt.valueOf() + lineup[i].duration < Date.now()) continue;

                // If somevideo is still playing, return that
                // If the video has not yet been played, return that & set startedAt to now
                res.writeHead(200, {'Content-Type' : 'application/json'});
                res.write(JSON.stringify({thumbnail : lineup[i]['thumbnail'],
                                          count : count}));
                res.end();
                return;
            }

            // if there isn't such video, return nothing

            res.writeHead(200, {'Content-Type' : 'application/json'});
            res.write(JSON.stringify({count : count}));
            res.end();
        });
    });
});

function duration(durationStr) {
    return isoduration.toSeconds(isoduration.parse(durationStr)) * 1000; 
}

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

// GET request for remove group
app.get('/group/remove/:groupID', function(req, res) {
    console.log("[/group/remove/:groupID] Got request");

    Group.remove({_id : req.params.groupID}, function(err) {
        if (err) throw err;

        res.writeHead(200, {'Content-Type':'application/json'});
        res.write(JSON.stringify({result:'OK'}));
        res.end();
    });
});

app.listen(8080, function() {console.log("Listening on port #8080")});

