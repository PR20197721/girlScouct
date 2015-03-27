#!/usr/bin/env node
// Dependency: node.js. See https://nodejs.org/ for how to install.
// Mike Zou <mzou@northpointdigital.com>

console.log("VTK Scaffolding Data Migration Tool.");

var request = require('urllib-sync').request;

function getJSON(path) {
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

var fieldsMeeting = {
    "sling:resourceType": "girlscouts-vtk/components/vtk-data",
    "vtkDataType": "meeting"
};

var fieldsYearPlan = {
    "sling:resourceType": "girlscouts-vtk/components/vtk-data",
    "vtkDataType": "year-plan"
};

function addProperties(entityPath, fields) {
    console.log('Adding properties: ' + entityPath);
    request('http://localhost:4502' + entityPath, {
        method: 'POST',
        auth: 'admin:admin',
        data: fields
    });
}

function addAllProperties(rootPath, fields) {
    var root = getJSON(rootPath);
    for (var levelKey in root) {
        var level = getJSON(rootPath + '/' + levelKey);
        for (var entityKey in level) {
            addProperties(rootPath + '/' + levelKey + '/' + entityKey, fields);
        }
    }
}

addAllProperties('/content/girlscouts-vtk/meetings/myyearplan', fieldsMeeting);
