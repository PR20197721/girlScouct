COOKIES='JSESSIONID=b407f711-5037-4879-b9ef-0f99576cbaae'
./load-test-vtk.sh -u 1 -c "$COOKIES"
./load-test-vtk.sh -u 2 -c "$COOKIES"
./load-test-vtk.sh -u 5 -c "$COOKIES"
./load-test-vtk.sh -u 10 -c "$COOKIES"
./load-test-vtk.sh -u 20 -c "$COOKIES"
./load-test-vtk.sh -h 500 -u 50 -c "$COOKIES"
