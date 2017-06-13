/**
 * Created by Koen on 13-6-2017.
 */

var express = require('express');
var router = express.Router();
var moment = require('moment');
var jwt = require('jwt-simple');
var bcrypt = require('bcrypt');

var bodyParser = require('body-parser');
router.use(bodyParser.json());
router.use(bodyParser.urlencoded({ extended: true }));

var path = require('path');
var pool = require('../db/db_connector');
var config = require('../config.json');

function encodeToken(username) {
    const payload = {
        exp: moment().add(10, 'days').unix(),
        iat: moment().unix(),
        sub: username
    };
    return jwt.encode(payload, config.secretkey);
}

function decodeToken(token, cb) {

    try {
        const payload = jwt.decode(token, config.secretkey);
        const now = moment().unix();

        if (now > payload.exp) {
            console.log('Token has expired.')
        }
        cb(null, payload);

    } catch (err) {
        cb(err, null);
    }
}

router.post('/login', function(req, res) {

    var username = req.body.username || '';
    var password = req.body.password || '';

    if (username && password) {
        var query_str = {
            sql: 'SELECT * FROM users WHERE username = "?";',
            values: [username],
            timeout: 2000
        };

        pool.getConnection(function (err, connection) {
            if (err) {
                throw err
            }
            connection.query(query_str, function (err, rows, fields) {
                connection.release();
                if (err) {
                    throw err
                }

                if (rows[0]) {
                    if (rows[0].hasOwnProperty('username') && rows[0].hasOwnProperty('password')) {
                        var hash = rows[0].password;
                        if (bcrypt.compareSync(password, hash)){
                            res.status(200).json(encodeToken(username));
                        } else {
                            res.json({error: "Invalid password"});
                        }
                    } else {
                        res.json({error: "Invalid username and/or password"});
                    }
                } else {
                    res.json({error: "Invalid username and/or password"});
                }

            });
        });
    } else {
        res.json({error: "Invalid username and/or password"});
    }

});

router.post('/register', function(req, res){
    var body = req.body;

    var createDate = moment().format('YYYY-MM-DD HH:MM:SS');
    var hash = bcrypt.hashSync(body.password, 10);

    var query_str = {
        sql: 'INSERT INTO `customer`(store_id, first_name, last_name, email, address_id, create_date, username, password) VALUES (?,?,?,?,?,?,?,?)',
        values: [body.storeId, body.firstName, body.lastName, body.email, body.addressId, createDate, body.username, hash],
        timeout: 2000
    };

    pool.getConnection(function (err, connection) {
        if (err) {
            throw err
        }
        connection.query(query_str, function (err, rows, fields) {
            connection.release();
            if (err) {
                console.log(err);
            }
            res.status(200).json(rows);
        });
    });

});

router.get('/films?offset=:start&count=:number', function(req, res){
});

router.get('/films/:filmid', function(req, res){
});

router.all('*', function(req, res, next){
    var token = (req.header('X-Access-Token')) || '';

    decodeToken(token, function (err, payload) {
        if (err) {
            console.log('Error handler: ' + err.message);
            res.status((err.status || 401 )).json({error: new Error("Not authorised").message});
        } else {
            next();
        }
    });
});

router.get('/rentals/:userid', function(req, res){
});

router.post('/rentals/:userid/:inventoryid', function(req, res){
});

router.put('/rentals/:userid/:inventoryid', function(req, res){
});

router.delete('/rentals/:userid/:inventoryid', function(req, res){
});

module.exports = router;
