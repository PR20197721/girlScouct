## copy the cookie from one of your requests in the Network tab after logging into the site
## note that the Cookie: header name is NOT included

cookies='girl-scout-name=Ana; troopDataToken=uQzUIAU_79fa9b57ff728bf66b0c7352d7c5e1eb; AWSELB=E99F618B1E969B6294D706184CF2532CFE337AF000F1AB79CCB003165D41DEA56BE1F29A4C1B27C33F2AB79A81C7CF32FB616ACE7D3D19D1A0FC22997DF0BB1AD66458F1AE; vtk_referer_council=""; JSESSIONID=056b5374-051a-4fe9-82ba-58423e2f39e9'


# defaults to 100 but you can adjust by invoking this script with flag -h 100
hits=100
# defaults to 10 but you can adjust by invoking this script with flag -u 10
users=10
server='https://girlscouts-dev2.adobecqms.net'
timestamp=$( date "+%Y_%b_%d at %r")
branch=$(git symbolic-ref --short -q HEAD)


while getopts ":h:u:e:c:" opt; do
  case $opt in
    s)
      server="$OPTARG"
      ;;
    h)
      hits="$OPTARG"
      ;;
    u)
      users="$OPTARG"
      ;;
    c)
      cookies="$OPTARG"
      ;;
    \?)
      echo "Invalid option: -$OPTARG. " >&2
      exit 1
      ;;
    :)
      echo "Option -$OPTARG requires an argument." >&2
      exit 1
      ;;
  esac
done

#clean out header name in case it accidentally got included in cookies string
cookies=${cookies#Cookie:}
#strip leading and trailing spaces
read  -rd '' cookies <<< "$cookies"


# set remaining params into global array
# shift $(($OPTIND - 1))
# CMD_ARGS=("$@")


#https://gsuat-gsmembers.cs17.force.com/members/Community_Home
read -r -d '' GETURLS <<EOF
    ${server}/content/girlscouts-vtk/en/vtk.myTroop.html
    ${server}/content/girlscouts-vtk/en/vtk.html
    ${server}/content/girlscouts-vtk/en/vtk.resource.html
    ${server}/content/girlscouts-vtk/en/vtk.finances.html
    ${server}/content/girlscouts-vtk/en/vtk.details.html?elem=1455397200000
    ${server}/content/dam/girlscouts-vtk/local/aid/meetings/D14TC12/Back-in-the-Garden.pdf
EOF


## verify and count urls, and verify that the given cookie is OK
echo "verifying urls and cookie..."
urlcount=0
while read url; do
    echo "testing $url"
    urlcount=$((urlcount+1))
    response=$(curl -k  -o /dev/null --silent --head --write-out '%{http_code}\n' -H'Cookie:'"$cookies" $url)
    if [ "$response" -gt "299" ]; then
        echo "PROBLEM: got a $response response from $url"
        exit 1
    fi
done <<< "$GETURLS"

echo "urls and cookie verified, proceeding with tests"

totalhits=$(($hits * $urlcount))
totalusers=$(($users * $urlcount))


resultsdir=/var/tmp/benchmark-results/$branch/"$totalhits-hits_$totalusers-users"
if [ -e $resultsdir ]; then
    rm -rf $resultsdir
fi
mkdir -p $resultsdir
summaryfile=$resultsdir/AB-benchmark-summary.txt

# -c is number of concurrent users
# -n is total number of requests to make
while read url; do
   ab -n $hits -c $users -C "$cookies" $url > $resultsdir/$(basename $url) &
done <<< "$GETURLS"


echo "TOTAL LOAD TEST: $urlcount x $hits = $totalhits hits distrubuted among $urlcount x $users = $totalusers users" | tee -a $summaryfile 
echo "($urlcount urls, each with $hits hits distributed among $users users, all at once)" | tee -a $summaryfile
echo "run on $timestamp from branch $branch" >> $summaryfile

echo "testing..."
echo "urls tested:" >> $summaryfile
echo "$GETURLS" >> $summaryfile

wait

echo "Done! Please find your test results in" 
echo "$resultsdir"
