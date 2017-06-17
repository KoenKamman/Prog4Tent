/**
 * Created by Koen Kamman on 14-6-2017.
 */

var chai = require('chai');
var chaiHttp = require('chai-http');
var server = require('../index.js');
var should = chai.should();

chai.use(chaiHttp);

var username = "mocha";
var password = "mocha";


describe('Login Tests', function () {

    before(function (done) {
        chai.request(server)
            .post('/api/v1/login')
            .send({
                "username": username, "password": password
            })
            .end(function (err, res) {
                if (res.body.hasOwnProperty("token")) {
                    done();
                } else {
                    chai.request(server)
                        .post('/api/v1/register')
                        .send({
                            "username": username,
                            "password": password,
                            "store_id": "1",
                            "first_name": "mocha",
                            "last_name": "mocha",
                            "email": "mocha",
                            "address_id": "0"
                        })
                        .end(function (err, res) {
                            done();
                        });
                }
            });
    });

    it('Empty Values POST /api/v1/login', function (done) {
        chai.request(server)
            .post('/api/v1/login')
            .send({"username": "", "password": ""})
            .end(function (err, res) {
                res.should.have.status(400);
                res.body.should.have.property('error');
                done();
            });
    });

    it('Empty Body POST /api/v1/login', function (done) {
        chai.request(server)
            .post('/api/v1/login')
            .end(function (err, res) {
                res.should.have.status(400);
                res.body.should.have.property('error');
                done();
            });
    });

    it('Valid Credentials POST /api/v1/login', function (done) {
        chai.request(server)
            .post('/api/v1/login')
            .send({"username": username, "password": password})
            .end(function (err, res) {
                res.should.have.status(200);
                res.body.should.have.property('username');
                res.body.should.have.property('token');
                res.body.should.have.property('customer_id');
                done();
            });
    });
});

describe('Register Tests', function () {

    before(function (done) {
        chai.request(server)
            .post('/api/v1/login')
            .send({
                "username": username, "password": password
            })
            .end(function (err, res) {
                if (res.body.hasOwnProperty("token")) {
                    done();
                } else {
                    chai.request(server)
                        .post('/api/v1/register')
                        .send({
                            "username": username,
                            "password": password,
                            "store_id": "1",
                            "first_name": "mocha",
                            "last_name": "mocha",
                            "email": "mocha",
                            "address_id": "0"
                        })
                        .end(function (err, res) {
                            done();
                        });
                }
            });
    });

    it('Duplicate Credentials POST /api/v1/register', function (done) {
        chai.request(server)
            .post('/api/v1/register')
            .send({
                "username": username,
                "password": password,
                "store_id": "1",
                "first_name": "mocha",
                "last_name": "mocha",
                "email": "mocha",
                "address_id": "0"
            })
            .end(function (err, res) {
                res.should.have.status(500);
                res.body.should.have.property('error');
                done();
            });
    });
});

describe('Film Tests', function () {

    it('GET /api/v1/films', function (done) {
        chai.request(server)
            .get('/api/v1/films')
            .end(function (err, res) {
                res.should.have.status(200);
                res.body.should.be.a('array');
                done();
            });
    });

    it('GET /api/v1/films/:filmid', function (done) {
        chai.request(server)
            .get('/api/v1/films/1')
            .end(function (err, res) {
                res.should.have.status(200);
                res.body.should.be.a('array');
                done();
            });
    });

    it('GET /api/v1/films/:filmid', function (done) {
        chai.request(server)
            .get('/api/v1/films/invalid_id')
            .end(function (err, res) {
                res.should.have.status(400);
                res.body.should.have.property('error');
                done();
            });
    });
});

describe('Rental Tests', function () {
    var token;
    var customerId;

    before(function (done) {
        chai.request(server)
            .post('/api/v1/login')
            .send({
                "username": username, "password": password
            })
            .end(function (err, res) {
                token = res.body.token;
            });
        chai.request(server)
            .post('/api/v1/login')
            .send({
                "username": username, "password": password
            })
            .end(function (err, res) {
                if (!res.body.hasOwnProperty("token")) {
                    chai.request(server)
                        .post('/api/v1/register')
                        .send({
                            "username": username,
                            "password": password,
                            "store_id": "1",
                            "first_name": "mocha",
                            "last_name": "mocha",
                            "email": "mocha",
                            "address_id": "0"
                        })
                        .end(function (err, res) {
                        });
                }
            });
        chai.request(server)
            .get('/api/v1/customers/' + username)
            .end(function (err, res) {
                customerId = res.body.customer_id;
                done();
            });
    });

    it('Valid Token GET /api/v1/rentals/', function (done) {
        chai.request(server)
            .get('/api/v1/rentals/' + customerId)
            .set('X-Access-Token', token)
            .end(function (err, res) {
                res.should.have.status(200);
                res.body.should.be.a('array');
                done();
            });
    });

    it('No Token GET /api/v1/rentals/', function (done) {
        chai.request(server)
            .get('/api/v1/rentals/' + customerId)
            .end(function (err, res) {
                res.should.have.status(401);
                res.body.should.have.property('error');
                done();
            });
    });

    it('Valid Token POST /api/v1/rentals/', function (done) {
        chai.request(server)
            .post('/api/v1/rentals/' + customerId + '/10')
            .set('X-Access-Token', token)
            .end(function (err, res) {
                res.should.have.status(200);
                done();
            });
    });

    it('No Token POST /api/v1/rentals/', function (done) {
        chai.request(server)
            .post('/api/v1/rentals/' + customerId + '/10')
            .end(function (err, res) {
                res.should.have.status(401);
                res.body.should.have.property('error');
                done();
            });
    });

    it('Valid Token PUT /api/v1/rentals/', function (done) {
        chai.request(server)
            .put('/api/v1/rentals/' + customerId + '/10')
            .send({"staff_id":"86","rental_date":"2100-07-14 19:06:48", "return_date":"2021-07-14 19:06:41"})
            .set('X-Access-Token', token)
            .end(function (err, res) {
                res.should.have.status(200);
                done();
            });
    });

    it('No Token PUT /api/v1/rentals/', function (done) {
        chai.request(server)
            .put('/api/v1/rentals/' + customerId + '/10')
            .send({"staff_id":"86","rental_date":"2100-07-14 19:06:48", "return_date":"2021-07-14 19:06:41"})
            .end(function (err, res) {
                res.should.have.status(401);
                res.body.should.have.property('error');
                done();
            });
    });

    it('Valid Token DELETE /api/v1/rentals/', function (done) {
        chai.request(server)
            .delete('/api/v1/rentals/' + customerId + '/10')
            .set('X-Access-Token', token)
            .end(function (err, res) {
                res.should.have.status(200);
                done();
            });
    });

    it('No Token DELETE /api/v1/rentals/', function (done) {
        chai.request(server)
            .delete('/api/v1/rentals/' + customerId + '/10')
            .end(function (err, res) {
                res.should.have.status(401);
                done();
            });
    });

});

describe('Customer Tests', function () {
    var token;
    var customerId;

    before(function (done) {
        chai.request(server)
            .post('/api/v1/login')
            .send({
                "username": username, "password": password
            })
            .end(function (err, res) {
                if (res.body.hasOwnProperty("token")) {
                    done();
                } else {
                    chai.request(server)
                        .post('/api/v1/register')
                        .send({
                            "username": username,
                            "password": password,
                            "store_id": "1",
                            "first_name": "mocha",
                            "last_name": "mocha",
                            "email": "mocha",
                            "address_id": "0"
                        })
                        .end(function (err, res) {
                            done();
                        });
                }
            });
    });

    it('Valid Token GET /api/v1/customers/:username', function (done) {
        chai.request(server)
            .get('/api/v1/customers/' + username)
            .end(function (err, res) {
                res.should.have.status(200);
                res.body.should.have.property('customer_id');
                done();
            });
    });

});
