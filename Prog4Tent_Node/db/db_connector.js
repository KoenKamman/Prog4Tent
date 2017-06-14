/**
 * Created by Koen Kamman on 13-6-2017.
 */

var mysql = require('mysql');
var config = require('../config');

var pool = mysql.createPool({
    connectionLimit : 25,
    host            : config.dbServer,
    user            : config.dbUsername,
    password        : config.dbPassword,
    database        : config.dbSchema
});

module.exports = pool;
