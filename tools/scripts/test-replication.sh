#!/bin/bash

for i in `seq 1 100`; do
    #curl -u admin:admin -F'test=test' http://localhost:4503/vtk/test/test-$i
    #curl -u admin:admin -F'test=test' http://localhost:4503/vtk/test/test-$i/child-$i
    #curl -u admin:admin -F":operation=delete" http://localhost:4503/vtk/test/test-$i
    #curl -u admin:admin -F":operation=copy" -F":dest=/content/dam/girlscouts-vtk/troop-data/603/test-$i" http://localhost:4503/content/dam/girlscouts-vtk/troop-data/603/test
    #curl -u admin:admin -F":operation=delete" http://localhost:4503/content/dam/girlscouts-vtk/troop-data/603/test-$i
done
