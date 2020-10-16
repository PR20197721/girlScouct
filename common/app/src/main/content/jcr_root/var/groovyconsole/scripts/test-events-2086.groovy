limit = 100
page = 7
srcPath = '/content/girlscoutseasternmass/en/events-repository/2020'
testPath = '/content/girlscoutseasternmass/en/events-repository/2021'

test = getResource(testPath)
if (test != null) {
    test = getNode(testPath)
    test.remove()
    save()
}

copy srcPath to testPath
test = getNode(testPath)
max = limit * page
min = limit * (page - 1)
total = test.nodes.size() - 1 // -1 for jcr:content
println "Printing event nodes ${min + 1} through ${Math.min(max, total)} out of ${total}"
test.nodes.eachWithIndex { event, i ->
    if (i != 0 && (i > max || i <= min)) { // ignore i=0 because it is jcr:content
        event.remove()
    } else {
        println event.path
    }
}
save()
