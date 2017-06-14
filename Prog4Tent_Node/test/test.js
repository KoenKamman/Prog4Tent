/**
 * Created by Koen Kamman on 14-6-2017.
 */

var chai = require('chai');
var chaiHttp = require('chai-http');
var server = require('../index.js');
var should = chai.should();

chai.use(chaiHttp);

describe('Login Test', function() {
    it('Test POST /api/v1/login SHOULD FAIL', function(done) {
        chai.request(server)
            .post('/api/v1/login')
            .send({"username":"asdasd","password":"asdsad"})
            .end(function(err, res) {
                res.should.have.status(400);
                done();
            });
    });
});
