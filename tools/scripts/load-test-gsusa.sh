# defaults to 100 but you can adjust by invoking this script with flag -h 100
hits=100
# defaults to 10 but you can adjust by invoking this script with flag -u 10
users=10
server='https://uat.girlscouts.org'
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
    ${server}/content/gsusa/en/about-girl-scouts/who-we-are.html
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
