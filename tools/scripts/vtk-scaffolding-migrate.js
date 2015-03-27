#!/usr/bin/env node
// Dependency: node.js. See https://nodejs.org/ for how to install.
// Mike Zou <mzou@northpointdigital.com>

console.log("VTK Scaffolding Data Migration Tool.");

var http = require('http');
function httpCombine(callback) {
    return function(response) {
        var str = '';
        response.on('data', function (chunk) {
            str += chunk; 
        });
        response.on('end', function () {
            callback.call(this, str, response);
        });
    }
};

function LocalRequest(path, callback, method) {
    var request = http.request({
        host: 'localhost',
        port: '4502',
        auth: 'admin:admin',
        path: path,
        method: method ? method : 'GET'
    }, httpCombine(callback));

    this.do = function(){ request.end(); };
}

new LocalRequest('/content/gateway/en.1.json', function(str){ console.log(str); }).do();
