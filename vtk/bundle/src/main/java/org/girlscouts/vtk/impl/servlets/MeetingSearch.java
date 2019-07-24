package org.girlscouts.vtk.impl.servlets;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.ejb.SessionFactory;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.utils.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@SlingServlet(paths = {"/bin/vtk/v1/meetingSearch"})
//EX: http://localhost:4503/bin/vtk/v1/meetingSearch?search={%22keywords%22:%22Award%22,%22year%22:2017,%22meetingPlanType%22:%22Journey%22,%22level%22:[%22daisy%22,%22Junior%22],%22categoryTags%22:[%22Its_Your_Story_-_Tell_It,%22]}
public class MeetingSearch extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 1L;
    private static Logger log = LoggerFactory.getLogger(MeetingSearch.class);
    @Reference
    private SessionFactory sessionFactory;

    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        Session session = null;
        ResourceResolver rr = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            mapper.setSerializationInclusion(Include.NON_NULL);
            mapper.setSerializationInclusion(Include.NON_EMPTY);
            org.girlscouts.vtk.models.MeetingSearch search = mapper.readValue(VtkUtil.getJsonFromRequest(request).toString(), org.girlscouts.vtk.models.MeetingSearch.class);
            rr = sessionFactory.getResourceResolver();
            session = rr.adaptTo(Session.class);
            QueryResult result = getQueryResult(session, search);
            LinkedHashSet<MeetingResult> results = new LinkedHashSet<>();
            if(result != null) {
                int index = 0;
                for (RowIterator it = result.getRows(); it.hasNext(); ) {
                    try {
                        index++;
                        Row row = it.nextRow();
                        MeetingResult meetingResult = new MeetingResult();
                        Resource meeting = rr.resolve(row.getPath());
                        ValueMap vm = meeting.getValueMap();
                        meetingResult.setPath(row.getPath());
                        meetingResult.setReq(vm.get("req",""));
                        meetingResult.setReqTitle(vm.get("reqTitle",""));
                        meetingResult.setResultIndex(index);
                        meetingResult.setMeetingType(vm.get("meetingPlanType", ""));
                        meetingResult.setCat(vm.get("cat", ""));
                        meetingResult.setBlurb(vm.get("blurb", ""));
                        meetingResult.setId(vm.get("id", ""));
                        meetingResult.setLevel(vm.get("level", ""));
                        meetingResult.setName(vm.get("name", ""));
                        Boolean hasOutdoor = Boolean.FALSE;
                        Boolean hasGlobal = Boolean.FALSE;
                        hasOutdoorOrGlobal(meeting, hasOutdoor, hasGlobal);
                        meetingResult.setHasGlobal(hasGlobal);
                        meetingResult.setHasOutdoor(hasOutdoor);
                        meetingResult.setHealthyLiving(vm.get("healthyliving", "false").equals("true"));
                        meetingResult.setLifeSkills(vm.get("lifeskills", "false").equals("true"));
                        meetingResult.setAidPaths(vm.get("aidPaths", new String[]{}));
                        meetingResult.setCatTags(vm.get("catTags", "").split(","));
                        results.add(meetingResult);
                    } catch (Exception e) {
                        log.error("Error parsing search result: ", e);
                    }
                }
            }
            response.getWriter().write(mapper.writeValueAsString(results));
        } catch (Exception e) {
            log.debug("Error occurred:",e);
        } finally {
            try {
                if (rr != null) {
                    sessionFactory.closeResourceResolver(rr);
                }
                if (session != null) {
                    session.logout();
                }
            } catch (Exception ex) {
                log.debug("Error occurred while closing resource resolver: ",ex);
            }
        }
    }

    private QueryResult getQueryResult(Session session, org.girlscouts.vtk.models.MeetingSearch search) throws RepositoryException {
        javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
        String sql = "select s.[*] from [nt:unstructured] as s where s.[ocm_classname]='org.girlscouts.vtk.models.Meeting' " + " and isdescendantnode('/content/girlscouts-vtk/meetings/myyearplan" + search.getYear() + "/')  ";
        if (search.getLevel() != null && search.getLevel().size() > 0) {
            sql += " and  contains(s.[level] ,  '" + fmtMultiContainsSql(search.getLevel()) + "') ";
        }
        if (search.getCategoryTags() != null && search.getCategoryTags().size() > 0) {
            String catTagsFmt = fmtMultiContainsSql(search.getCategoryTags()).replace("'", "''");
            sql += " and (contains(s.[catTags] ,  '" + catTagsFmt + "') ";
            sql += " or contains(s.[catTagsAlt] ,  '" + catTagsFmt + "') )";
        }
        if (search.getMeetingPlanType() != null && !"".equals(search.getMeetingPlanType())) {
            String meetingPlanTypeFmt = search.getMeetingPlanType();
            sql += " and (s.[meetingPlanType] = '" + meetingPlanTypeFmt + "' ";
            sql += " or  s.[meetingPlanTypeAlt] = '" + meetingPlanTypeFmt + "' ) ";
        }
        if (search.getKeywords() != null && !"".equals(search.getKeywords().trim())) {
            String keywords = search.getKeywords().replace(" ", " OR ").replace("'", "''");
            String keywordString = "";
            if (keywords.length() >= 5) {
                //regex to check for illegal sql2 characters
                Pattern p = Pattern.compile("[\\Q+-&|!(){}[]^\"~*?:\\/\\E]");
                Matcher m = p.matcher(keywords);
                //If illegal characters contained
                if (m.find()) {
                    //Replace illegal character with space, split keywords on the space.
                    keywords = keywords.replaceAll("[\\Q+-&|!(){}[]^\"~*?:\\/\\E]", " ");
                    keywordString = "and (";
                    int count = 0;
                    //Add an individual contains for each word separated by the illegal character
                    for (String searchWord : keywords.split(" ")) {
                        keywordString = keywordString + "contains( s.[*], '" + searchWord + "')";
                        if (count != keywords.split(" ").length - 1) {
                            keywordString = keywordString + " or ";
                        } else {
                            keywordString = keywordString + ")";
                        }
                        count++;
                    }
                    //No illegal characters
                } else {
                    keywordString = " and (contains( s.[*], '*" + keywords + "*') or contains(s.[*], '* " + keywords + " *'))";

                }
                sql += keywordString;
            } else {
                sql += " and contains(s.[*], '" + keywords + "')  ";
            }
        }
        javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.JCR_SQL2);
        return q.execute();
    }

    private String fmtMultiContainsSql(java.util.List<String> val) {
        return val.stream().map(entry -> entry).collect(Collectors.joining(" OR "));
    }
    private void hasOutdoorOrGlobal(Resource meeting, Boolean hasOutdoor, Boolean hasGlobal) {
        try {
            Resource activities = meeting.getChild("activities");
            if(activities != null) {
                Iterator<Resource> activityIterator = activities.getChildren().iterator();
                while(activityIterator.hasNext()) {
                    Resource activity = activityIterator.next();
                    Resource multiactivity = meeting.getChild("multiactivities");
                    if(multiactivity != null) {
                        Iterator<Resource> multiactivityIterator = multiactivity.getChildren().iterator();
                        while(multiactivityIterator.hasNext()) {
                            Resource agenda = multiactivityIterator.next();
                            ValueMap vm  = agenda.getValueMap();
                            if(vm.get("outdoor","false").equals("true")) {
                                hasOutdoor = Boolean.TRUE;
                            }
                            if(vm.get("global","false").equals("true")) {
                                hasGlobal = Boolean.TRUE;
                            }
                        }
                    }
                }
            }
        }catch(Exception e) {

        }
    }

    private class MeetingResult{
        private int resultIndex;
        private String meetingType;
        private String path;
        private String cat;
        private String blurb;
        private String id;
        private String req;
        private String reqTitle;
        private String level;
        private String name;
        private String[] catTags;
        private String[] aidPaths;
        private Boolean healthyLiving = Boolean.FALSE;
        private Boolean lifeSkills = Boolean.FALSE;
        private Boolean hasOutdoor = Boolean.FALSE;
        private Boolean hasGlobal = Boolean.FALSE;

        public int getResultIndex() {
            return resultIndex;
        }

        public void setResultIndex(int resultIndex) {
            this.resultIndex = resultIndex;
        }

        public String getMeetingType() {
            return meetingType;
        }

        public void setMeetingType(String meetingType) {
            this.meetingType = meetingType;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getCat() {
            return cat;
        }

        public void setCat(String cat) {
            this.cat = cat;
        }

        public String getBlurb() {
            return blurb;
        }

        public void setBlurb(String blurb) {
            this.blurb = blurb;
        }

        public Boolean getHealthyLiving() {
            return healthyLiving;
        }

        public void setHealthyLiving(Boolean healthyLiving) {
            this.healthyLiving = healthyLiving;
        }

        public Boolean getLifeSkills() {
            return lifeSkills;
        }

        public void setLifeSkills(Boolean lifeSkills) {
            this.lifeSkills = lifeSkills;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String[] getCatTags() {
            return catTags;
        }

        public void setCatTags(String[] catTags) {
            this.catTags = catTags;
        }

        public String[] getAidPaths() {
            return aidPaths;
        }

        public void setAidPaths(String[] aidPaths) {
            this.aidPaths = aidPaths;
        }

        public Boolean getHasOutdoor() {
            return hasOutdoor;
        }

        public void setHasOutdoor(Boolean hasOutdoor) {
            this.hasOutdoor = hasOutdoor;
        }

        public Boolean getHasGlobal() {
            return hasGlobal;
        }

        public void setHasGlobal(Boolean hasGlobal) {
            this.hasGlobal = hasGlobal;
        }

        public String getReq() {
            return req;
        }

        public void setReq(String req) {
            this.req = req;
        }

        public String getReqTitle() {
            return reqTitle;
        }

        public void setReqTitle(String reqTitle) {
            this.reqTitle = reqTitle;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MeetingResult that = (MeetingResult) o;
            return resultIndex == that.resultIndex &&
                    Objects.equals(meetingType, that.meetingType) &&
                    Objects.equals(path, that.path) &&
                    Objects.equals(cat, that.cat) &&
                    Objects.equals(blurb, that.blurb) &&
                    Objects.equals(id, that.id) &&
                    Objects.equals(req, that.req) &&
                    Objects.equals(reqTitle, that.reqTitle) &&
                    Objects.equals(level, that.level) &&
                    Objects.equals(name, that.name) &&
                    Arrays.equals(catTags, that.catTags) &&
                    Arrays.equals(aidPaths, that.aidPaths) &&
                    Objects.equals(healthyLiving, that.healthyLiving) &&
                    Objects.equals(lifeSkills, that.lifeSkills) &&
                    Objects.equals(hasOutdoor, that.hasOutdoor) &&
                    Objects.equals(hasGlobal, that.hasGlobal);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(resultIndex, meetingType, path, cat, blurb, id, req, reqTitle, level, name, healthyLiving, lifeSkills, hasOutdoor, hasGlobal);
            result = 31 * result + Arrays.hashCode(catTags);
            result = 31 * result + Arrays.hashCode(aidPaths);
            return result;
        }
    }
}
