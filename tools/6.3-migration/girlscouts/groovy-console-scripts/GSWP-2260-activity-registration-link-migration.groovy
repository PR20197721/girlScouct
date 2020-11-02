import javax.jcr.Node
import java.util.Iterator
import java.text.SimpleDateFormat
import java.sql.Timestamp;

//println("resourceResolver "+resourceResolver)


String[] sfCouncilArray = ["/content/citrus-gs/en/sf-events-repository", "/content/girlscoutcsa/en/sf-events-repository", "/content/girlscoutsaz/en/sf-events-repository", "/content/girlscoutshcc/en/sf-events-repository", "/content/girlscoutshh/en/sf-events-repository", "/content/girlscoutsnccp/en/sf-events-repository", "/content/girlscoutsni/en/sf-events-repository", "/content/girlscoutsosw/en/sf-events-repository", "/content/girlscoutstoday/en/sf-events-repository", "/content/girlscoutsww/en/sf-events-repository", "/content/gsbadgerland/en/sf-events-repository", "/content/gseok/en/sf-events-repository", "/content/gskentuckiana/en/sf-events-repository", "/content/gsnorcal/en/sf-events-repository", "/content/gssef/en/sf-events-repository", "/content/gssjc/en/sf-events-repository", "/content/gssn/en/sf-events-repository", "/content/gsutah/en/sf-events-repository", "/content/gswcf/en/sf-events-repository", "/content/gswise/en/sf-events-repository", "/content/gswo/en/sf-events-repository", "/content/gswpa/en/sf-events-repository", "/content/sdgirlscouts/en/sf-events-repository", "/content/vtkcontent/en/sf-events-repository"];
for (int i = 0; i < sfCouncilArray.length; i++) {
    Resource content = resourceResolver.resolve(sfCouncilArray[i])
    Iterator<Resource> sites = content.listChildren()
    while (sites.hasNext()) {
        try {
            Resource res = sites.next();
            if (res.isResourceType("cq:Page")) {
                //println("LatesnodeTillSfRepo >>"+res.path);
                findAllEvents(res);
            }
        } catch (Exception exception) {
            println("Exception >>" + exception)
        }
    }
}

def void findAllEvents(Resource res) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HHmmSS");
    Iterator<Resource> nodeTillSfRepoEvents = res.listChildren()
    while (nodeTillSfRepoEvents.hasNext()) {
        try {
            Resource sfRepoYearNode = nodeTillSfRepoEvents.next();
            if (sfRepoYearNode.getChild("jcr:content/data")) {
                Resource jcrContent = sfRepoYearNode.getChild("jcr:content/data");
                ModifiableValueMap dataNode = jcrContent.adaptTo(ModifiableValueMap.class);
                //println("caluemap >>"+dataNode)
                if (dataNode.containsKey("end")) {
                    Date endDate = dataNode.get("end", Date.class)
                    def eventEndDateTimestamp = new Timestamp(endDate.getTime()).getTime();
                    //println("TimeStamp "+ eventEndDateTimestamp);
                    //println("Event Date Before Conversion "+ endDate);
                    //println("Event Date Aefore Conversion "+ sdf.format(eventEndDateTimestamp));
                    Timestamp currentDate = new Timestamp(System.currentTimeMillis());
                    def currentDateTimestamp = currentDate.getTime();
                    //println("currentDateTimestamp "+ currentDateTimestamp);
                    //println("Present Date "+ currentDate);

                    //logic for requirment
                    if (eventEndDateTimestamp >= currentDateTimestamp) {
                        //println("found " +jcrContent.path);
                        //println("Event Date Before Conversion "+ endDate);
                        //println("Event Date Aefore Conversion "+ sdf.format(eventEndDateTimestamp));
                        //println("Present Date "+ currentDate);
                        if (dataNode.containsKey("register")) {
                            def registerUrl = dataNode.get("register", String.class);
                            //println("URL "+ registerUrl);
                            if (registerUrl.contains("https://gsmembers.force.com/members/Event_join?EventId=")
                                    && !registerUrl.contains("https://www.girlscouts.org/vs2tempeventreg?")) {
                                println("CRX Path " + jcrContent.path);
                                println("Old URL " + registerUrl);
                                registerUrl = registerUrl.replace("https://gsmembers.force.com/members/Event_join?EventId=",
                                        "https://www.girlscouts.org/vs2tempeventreg?url=https://gsmembers.force.com/members/Event_join?EventId=");
                                println("New URL " + registerUrl);

                                dataNode.put("register", registerUrl);
                                jcrContent.getResourceResolver().commit();
                            }

                        }
                    }
                }
            }

        } catch (Exception exception) {
            println("Exception >>" + exception)
        }
    }
}