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
        var query_str = {
            sql: 'SELECT username, password, customer_id FROM customer WHERE username = ?;',
            values: [username],
            timeout: 2000
        };

        pool.getConnection(function (err, connection) {
            if (err) {
                console.log(err);
                res.status(503).json({error: new Error("Service Unavailable").message});
            } else {
                connection.query(query_str, function (err, rows, fields) {
                    connection.release();
                    if (err) {
                        console.log(err);
                        res.status(500).json({error: new Error("Internal Server Error").message});
                    }

                    if (rows[0]) {
                        if (rows[0].hasOwnProperty('username') && rows[0].hasOwnProperty('password')) {
                            var hash = rows[0].password;
                            var customer_id = rows[0].customer_id;
                            if (bcrypt.compareSync(password, hash)) {
                                res.status(200).json({
                                    "username": username,
                                    "customer_id": customer_id,
                                    "token": encodeToken(username)
                                });
                            } else {
                                res.status(400).json({error: new Error("Invalid username and/or password").message});
                            }
                        } else {
                            res.status(400).json({error: new Error("Invalid username and/or password").message});
                        }
                    } else {
                        res.status(400).json({error: new Error("Invalid username and/or password").message});
                    }

                });
            }
        });
    } else {
        res.status(400).json({error: new Error("Invalid username and/or password").message});
    }

});

//Register a new customer
router.post('/register', function (req, res) {
    var body = req.body;

    if (body.username !== "" && body.password !== "") {

        var createDate = moment().format('YYYY-MM-DD HH:mm:ss');
        var hash = bcrypt.hashSync(body.password, 10);

        var query_str = {
            sql: 'INSERT INTO `customer`(store_id, first_name, last_name, email, address_id, create_date, username, password) VALUES (?,?,?,?,?,?,?,?)',
            values: [body.store_id, body.first_name, body.last_name, body.email, body.address_id, createDate, body.username, hash],
            timeout: 2000
        };

        pool.getConnection(function (err, connection) {
            if (err) {
                console.log(err);
                res.status(503).json({error: new Error("Service Unavailable").message});
            } else {
                connection.query(query_str, function (err, rows, fields) {
                    connection.release();
                    if (err) {
                        console.log(err);
                        res.status(500).json({error: new Error("Internal Server Error - username may already be in use").message});
                    }
                    res.status(200).json(rows);
                });
            }
        });

    } else {
        res.status(400).json({error: new Error("Invalid username and/or password").message});
    }

});

//Returns customer_id of a specific customer
router.get('/customers/:username', function (req, res) {
    var username = req.params.username;

    var query_str = {
        sql: 'SELECT username, customer_id FROM customer WHERE username = ?;',
        values: [username],
        timeout: 2000
    };

    pool.getConnection(function (err, connection) {
        if (err) {
            console.log(err);
            res.status(503).json({error: new Error("Service Unavailable").message});
        }
        connection.query(query_str, function (err, rows, fields) {
            connection.release();
            if (err) {
                console.log(err);
                res.status(500).json({error: new Error("Internal Server Error").message});
            }
            res.status(200).json(rows[0]);
        });
    });
});

//Returns film info of multiple films by film_id
router.get('/films', function (req, res) {
    var offset = req.query.offset;
    var count = req.query.count;

    var query_str;
    if (offset && count) {
        query_str = {
            sql: 'SELECT * FROM film ORDER BY film_id LIMIT ? OFFSET ?;',
            values: [count, offset],
            timeout: 2000
        };
    } else {
        query_str = {
            sql: 'SELECT * FROM film ORDER BY film_id;',
            timeout: 2000
        };
    }

    pool.getConnection(function (err, connection) {
        if (err) {
            console.log(err);
            res.status(503).json({error: new Error("Service Unavailable").message});
        }
        connection.query(query_str, function (err, rows, fields) {
            connection.release();
            if (err) {
                console.log(err);
                res.status(500).json({error: new Error("Internal Server Error").message});
            }
            res.status(200).json(rows);
        });
    });
});

//Returns info about a specific film
router.get('/films/:film_id', function (req, res) {
    var id = req.params.film_id;

    if (!isNaN(id)) {
        var query_str = {
            sql: 'SELECT * FROM film WHERE film_id = ?;',
            values: [id],
            timeout: 2000
        };

        pool.getConnection(function (err, connection) {
            if (err) {
                console.log(err);
                res.status(503).json({error: new Error("Service Unavailable").message});
            }
            connection.query(query_str, function (err, rows, fields) {
                connection.release();
                if (err) {
                    console.log(err);
                    res.status(500).json({error: new Error("Internal Server Error").message});
                }
                res.status(200).json(rows);
            });
        });
    } else {
        res.status(400).json({error: new Error("film_id must be a number").message});
    }
});

//The following routes require a token, the previous ones do not
//Check for authentication token
router.all('*', function (req, res, next) {
    var token = (req.header('X-Access-Token')) || '';

    decodeToken(token, function (err, payload) {
        if (err) {
            console.log(err);
            res.status(401).json({error: new Error("Not authorised").message});
        } else {
            next();
        }
    });
});

//Returns rentals for a specific customer
router.get('/rentals/:customer_id', function (req, res) {
    var customerId = req.params.customer_id;

    var query_str = 'SELECT * FROM `rental` INNER JOIN inventory ON inventory.inventory_id = rental.inventory_id INNER JOIN film ON film.film_id = inventory.film_id WHERE rental.customer_id = ' + customerId;

    pool.getConnection(function (err, connection) {
        if (err) {
            console.log(err);
            res.status(503).json({error: new Error("Service Unavailable").message});
        }
        connection.query(query_str, function (err, rows, fields) {
            connection.release();
            if (err) {
                console.log(err);
                res.status(503).json({error: new Error("Internal Server Error").message});
            }
            res.status(200).json(rows);
        });
    });
});

//Creates a new rental entry for a specific customer
router.post('/rentals/:customer_id/:inventory_id', function (req, res) {
    var customerId = req.params.customer_id;
    var inventoryId = req.params.inventory_id;

    var staffId = req.body.staff_id || 0;

    var rentalDate = moment().format('YYYY-MM-DD HH:mm:ss');
    console.log(rentalDate);
    var returnDate = moment().add(1, 'week').format('YYYY-MM-DD HH:mm:ss');

    var query_str = {
        sql: 'INSERT INTO `rental`(rental_date, inventory_id, customer_id, return_date, staff_id) VALUES (?,?,?,?,?);',
        values: [rentalDate, inventoryId, customerId, returnDate, staffId],
        timeout: 2000
    };

    pool.getConnection(function (err, connection) {
        if (err) {
            console.log(err);
            res.status(503).json({error: new Error("Service Unavailable").message});
        }
        connection.query(query_str, function (err, rows, fields) {
            connection.release();
            if (err) {
                console.log(err);
                res.status(500).json({error: new Error("Internal Server Error").message});
            }
            res.status(200).json(rows);
        });
    });

});

//Updates rental entry for a specific customer
router.put('/rentals/:customer_id/:inventory_id', function (req, res) {
    var customerId = req.params.customer_id;
    var inventoryId = req.params.inventory_id;

    var staffId = req.body.staff_id;
    var rentalDate = req.body.rental_date;
    var returnDate = req.body.return_date;

    var query_str = {
        sql: 'UPDATE `rental` SET rental_date = ?, return_date = ?, staff_id = ? WHERE customer_id = ? AND inventory_id = ?;',
        values: [rentalDate, returnDate, staffId, customerId, inventoryId],
        timeout: 2000
    };

    pool.getConnection(function (err, connection) {
        if (err) {
            console.log(err);
            res.status(503).json({error: new Error("Service Unavailable").message});
        }
        connection.query(query_str, function (err, rows, fields) {
            connection.release();
            if (err) {
                console.log(err);
                res.status(500).json({error: new Error("Internal Server Error").message});
            }
            res.status(200).json(rows);
        });
    });
});

//Deletes a rental entry for a specific customer
router.delete('/rentals/:customer_id/:inventory_id', function (req, res) {
    var customerId = req.params.customer_id;
    var inventoryId = req.params.inventory_id;

    var query_str = {
        sql: 'DELETE FROM rental WHERE customer_id = ? AND inventory_id = ?;',
        values: [customerId, inventoryId],
        timeout: 2000
    };

    pool.getConnection(function (err, connection) {
        if (err) {
            console.log(err);
            res.status(503).json({error: new Error("Service Unavailable").message});
        }
        connection.query(query_str, function (err, rows, fields) {
            connection.release();
            if (err) {
                console.log(err);
                res.status(500).json({error: new Error("Internal Server Error").message});
            }
            res.status(200).json(rows);
        });
    });
});

module.exports = router;
