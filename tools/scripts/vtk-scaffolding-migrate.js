#!/usr/bin/env node
// Dependency: node.js. See https://nodejs.org/ for how to install.
// Mike Zou <mzou@northpointdigital.com>

console.log("VTK Scaffolding Data Migration Tool.");

var request = require('urllib-sync').request;

function getJSON(path, method) {
    var method = method ? method : 'GET';
    var response = request('http://localhost:4502' + path + '.1.json', {
        auth: 'admin:admin',
        dataType: 'json'
    });

    var data = response.data;
    for (var key in data) {
        if (key.indexOf('jcr:') == 0 || key.indexOf('cq:') == 0) {
            delete data[key];
        }
    }
    return data;
}

function addProperties(rootPath, options) {
    var root = getJSON(rootPath);
    for (var level in root) {
        console.log(level);
    }
}

addProperties('/content/girlscouts-vtk/meetings/myyearplan');
