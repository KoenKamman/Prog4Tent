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
router.use(bodyParser.urlencoded({extended: true}));

var path = require('path');
var pool = require('../db/db_connector');
var config = require('../config.json');

//Create a token
function encodeToken(username) {
    const payload = {
        exp: moment().add(10, 'days').unix(),
        iat: moment().unix(),
        sub: username
    };
    return jwt.encode(payload, config.secretkey);
}

//Check if token has expired
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

//Logging in with a valid username and password returns a token
router.post('/login', function (req, res) {

    var username = req.body.username || '';
    var password = req.body.password || '';

    if (username && password) {
        var query_str = 'SELECT username, password FROM customer WHERE username = "' + username + '";';

        pool.getConnection(function (err, connection) {
            if (err) {
                console.log(err);
                res.status((err.status || 503 )).json({error: new Error("Service Unavailable").message});
            } else {
                connection.query(query_str, function (err, rows, fields) {
                    connection.release();
                    if (err) {
                        console.log(err);
                        res.status((err.status || 500 )).json({error: new Error("Internal Server Error").message});
                    }

                    if (rows[0]) {
                        if (rows[0].hasOwnProperty('username') && rows[0].hasOwnProperty('password')) {
                            var hash = rows[0].password;
                            if (bcrypt.compareSync(password, hash)) {
                                res.status(200).json({"username": username, "token": encodeToken(username)});
                            } else {
                                res.status((err.status || 400 )).json({error: new Error("Invalid username and/or password").message});
                            }
                        } else {
                            res.status((err.status || 400 )).json({error: new Error("Invalid username and/or password").message});
                        }
                    } else {
                        res.status((err.status || 400 )).json({error: new Error("Invalid username and/or password").message});
                    }

                });
            }
        });
    } else {
        res.status((err.status || 400 )).json({error: new Error("Invalid username and/or password").message});
    }

});

//Register a new user
router.post('/register', function (req, res) {
    var body = req.body;

    if (body.username !== "" && body.password !== "") {

        var createDate = moment().format('YYYY-MM-DD HH:MM:SS');
        var hash = bcrypt.hashSync(body.password, 10);

        var query_str = {
            sql: 'INSERT INTO `customer`(store_id, first_name, last_name, email, address_id, create_date, username, password) VALUES (?,?,?,?,?,?,?,?)',
            values: [body.storeId, body.firstName, body.lastName, body.email, body.addressId, createDate, body.username, hash],
            timeout: 2000
        };

        pool.getConnection(function (err, connection) {
            if (err) {
                console.log(err);
                res.status((err.status || 503 )).json({error: new Error("Service Unavailable").message});
            } else {
                connection.query(query_str, function (err, rows, fields) {
                    connection.release();
                    if (err) {
                        console.log(err);
                        res.status((err.status || 500 )).json({error: new Error("Internal Server Error - username may already be in use").message});
                    }
                    res.status(200).json(rows);
                });
            }
        });

    } else {
        res.status(400).json({error: new Error("Invalid username and/or password").message});
    }

});

//Returns film info of multiple films by film_id
router.get('/films', function (req, res) {
    var offset = req.query.offset;
    var count = req.query.count;

    var query_str;
    query_str = 'SELECT * FROM film ORDER BY film_id LIMIT ' + count + ' OFFSET ' + offset + ';';
    pool.getConnection(function (err, connection) {
        if (err) {
            console.log(err);
            res.status((err.status || 503 )).json({error: new Error("Service Unavailable").message});
        }
        connection.query(query_str, function (err, rows, fields) {
            connection.release();
            if (err) {
                console.log(err);
                res.status((err.status || 500 )).json({error: new Error("Internal Server Error").message});
            }
            res.status(200).json(rows);
        });
    });
});

//Returns info about a specific film
router.get('/films/:filmid', function (req, res) {
    var id = req.params.filmid;

    var query_str;
    if (id) {
        query_str = 'SELECT * FROM film WHERE film_id = ' + id + ';';
    } else {
        query_str = 'SELECT * FROM film';
    }
    pool.getConnection(function (err, connection) {
        if (err) {
            console.log(err);
            res.status((err.status || 503 )).json({error: new Error("Service Unavailable").message});
        }
        connection.query(query_str, function (err, rows, fields) {
            connection.release();
            if (err) {
                console.log(err);
                res.status((err.status || 500 )).json({error: new Error("Internal Server Error").message});
            }
            res.status(200).json(rows);
        });
    });
});

//The following routes require a token, the previous ones do not
//Check for authentication token
router.all('*', function (req, res, next) {
    var token = (req.header('X-Access-Token')) || '';

    decodeToken(token, function (err, payload) {
        if (err) {
            console.log(err);
            res.status((err.status || 401 )).json({error: new Error("Not authorised").message});
        } else {
            next();
        }
    });
});

//Returns rentals for a specific user
router.get('/rentals/:userid', function (req, res) {
    var userId = req.params.userid;

    var query_str = 'SELECT * FROM rental WHERE customer_id =' + userId;

    pool.getConnection(function (err, connection) {
        if (err) {
            console.log(err);
            res.status((err.status || 503 )).json({error: new Error("Service Unavailable").message});
        }
        connection.query(query_str, function (err, rows, fields) {
            connection.release();
            if (err) {
                console.log(err);
                res.status((err.status || 500 )).json({error: new Error("Internal Server Error").message});
            }
            res.status(200).json(rows);
        });
    });
});

//Creates a new rental entry for a specific user
router.post('/rentals/:userid/:inventoryid', function (req, res) {
    var userId = req.params.userid;
    var inventoryId = req.params.inventoryid;

    var staffId = req.body.staffId;

    var rentalDate = moment().format('YYYY-MM-DD HH:MM:SS');
    var returnDate = moment().add(1, 'week').format('YYYY-MM-DD HH:MM:SS');

    var query_str;
    if (staffId) {
        query_str = {
            sql: 'INSERT INTO `rental`(rental_date, inventory_id, customer_id, return_date, staff_id) VALUES (?,?,?,?,?);',
            values: [rentalDate, inventoryId, userId, returnDate, staffId],
            timeout: 2000
        };
    } else {
        query_str = {
            sql: 'INSERT INTO `rental`(rental_date, inventory_id, customer_id, return_date) VALUES (?,?,?,?);',
            values: [rentalDate, inventoryId, userId, returnDate],
            timeout: 2000
        };
    }

    pool.getConnection(function (err, connection) {
        if (err) {
            console.log(err);
            res.status((err.status || 503 )).json({error: new Error("Service Unavailable").message});
        }
        connection.query(query_str, function (err, rows, fields) {
            connection.release();
            if (err) {
                console.log(err);
                res.status((err.status || 500 )).json({error: new Error("Internal Server Error").message});
            }
            res.status(200).json(rows);
        });
    });

});

//Updates rental entry for a specific user
router.put('/rentals/:userid/:inventoryid', function (req, res) {
});

//Deletes a rental entry for a specific user
router.delete('/rentals/:userid/:inventoryid', function (req, res) {
});

module.exports = router;
