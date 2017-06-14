 /**
 * Created by Koen on 13-6-2017.
 */

 var express = require('express');
 var app = express();
 var config = require('./config.json');
 var http = require('http');

 app.set('PORT', config.webPort);

 app.get('/', function (request, response) {
     response.send('Prog4Tent_Node')
 });

 app.all('*', function(request, response, next) {
     console.log(request.method + " " + request.url);
     next();
 });

 app.use('/api/v1', require('./routes/routes_api_v1'));

 var port = process.env.PORT || app.get('PORT');

 app.listen(port, function() {
     console.log('Server is listening on port ' + port)
 });

 module.exports = app;
