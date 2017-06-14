/**
 * Created by Koen Kamman on 14-6-2017.
 */

var chai = require('chai');
var chaiHttp = require('chai-http');
var server = require('../index.js');
var should = chai.should();

chai.use(chaiHttp);

describe('Login Test', function () {
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

    it('Correct Credentials POST /api/v1/login', function (done) {
        chai.request(server)
            .post('/api/v1/login')
            .send({"username": "test", "password": "testing"})
            .end(function (err, res) {
                res.should.have.status(200);
                res.body.should.have.property('username');
                res.body.should.have.property('token');
                done();
            });
    });
});

describe('Register Test', function () {
    it('Empty Values POST /api/v1/login', function (done) {
        chai.request(server)
            .post('/api/v1/login')
            .send({"username": "", "password": ""})
            .end(function (err, res) {
                res.should.have.status(400);
                done();
            });
    });
});
