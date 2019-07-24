package org.girlscouts.vtk.impl.servlets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.ejb.SessionFactory;
import org.girlscouts.vtk.ejb.YearPlanUtil;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Service
@SlingServlet(paths = {"/bin/vtk/v1/meetingFilter"})
public class MeetingFilter extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 1L;
    private static Logger log = LoggerFactory.getLogger(MeetingFilter.class);
    @Reference
    private SessionFactory sessionFactory;
    @Reference
    private YearPlanUtil yearPlanUtil;

    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        //do nothing
    }

    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        response.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        HttpSession session = request.getSession();
        ApiConfig apiConfig = null;
        try {
            if (session.getAttribute(ApiConfig.class.getName()) != null) {
                apiConfig = ((ApiConfig) session.getAttribute(ApiConfig.class.getName()));
                if (apiConfig != null && apiConfig.getFilters() != null) {
                    TreeMap<String, FilterOption> filters = apiConfig.getFilters();
                    log.debug("Loaded meetings meeting filter from session ");
                    try {
                        response.getWriter().write(mapper.writeValueAsString(filters));
                    } catch (Exception e) {
                        log.error("Exception occured:", e);
                    }
                } else {
                    List<Meeting> meetings = new ArrayList<>();
                    User user = (User) session.getAttribute(User.class.getName());
                    Troop troop = (Troop) session.getAttribute("VTK_troop");
                    try {
                        meetings = yearPlanUtil.getAllMeetings(user, troop);
                    } catch (Exception e) {
                        log.error("Exception occured:", e);
                    }
                    TreeMap<String, FilterOption> filters = new TreeMap<String, FilterOption>(new Comparator<String>() {
                        @Override
                        public int compare(String key1, String key2) {
                            int result = getWeight(key1).compareTo(getWeight(key2));
                            return result;
                        }
                        private Integer getWeight(String key) {
                            if (key != null) {
                                switch (key.toLowerCase()) {
                                    case "daisy":
                                        return 1;
                                    case "brownie":
                                        return 2;
                                    case "junior":
                                        return 3;
                                    case "cadette":
                                        return 4;
                                    case "senior":
                                        return 5;
                                    case "ambassador":
                                        return 6;
                                    case "multi-level":
                                        return 7;
                                }
                            }
                            return 0;
                        }
                    });
                    if (meetings != null) {
                        for (Meeting meeting : meetings) {
                            try {
                                FilterOption levelOption = null;
                                String meetingLevel = meeting.getLevel().replace("-", "");
                                if (filters.containsKey(meetingLevel)) {
                                    levelOption = filters.get(meetingLevel);
                                    log.debug("Updating filter /" + levelOption.getValue());
                                } else {
                                    levelOption = new FilterOption("ML_" + new Date().getTime() + "_" + Math.random(), meeting.getLevel(), meeting.getLevel().replace("_", " "));
                                    log.debug("Created new filter /" + levelOption.getValue());
                                }
                                if (levelOption != null) {
                                    TreeMap<String, FilterOption> typeFilters = levelOption.getSubFilterOptions();
                                    if (typeFilters == null) {
                                        typeFilters = new TreeMap<String, FilterOption>();
                                    }
                                    FilterOption typeOption = null;
                                    if (typeFilters.containsKey(meeting.getMeetingPlanType())) {
                                        typeOption = typeFilters.get(meeting.getMeetingPlanType());
                                        log.debug("Updating filter /" + levelOption.getValue() + "/" + typeOption.getValue());
                                    } else {
                                        typeOption = new FilterOption("MT_" + new Date().getTime() + "_" + Math.random(), meeting.getMeetingPlanType(), meeting.getMeetingPlanType().replace("_", "-"));
                                        log.debug("Created new filter /" + levelOption.getValue() + "/" + meeting.getMeetingPlanType());
                                    }
                                    if (typeOption != null) {
                                        TreeMap<String, FilterOption> categoryFilters = typeOption.getSubFilterOptions();
                                        if (categoryFilters == null) {
                                            categoryFilters = new TreeMap<String, FilterOption>();
                                        }
                                        String categories = meeting.getCatTags();
                                        if (categories != null) {
                                            StringTokenizer t = new StringTokenizer(categories, ",");
                                            while (t.hasMoreElements()) {
                                                String category = (String) t.nextToken();
                                                if (!categoryFilters.containsKey(category)) {
                                                    FilterOption categoryOption = new FilterOption("MC_" + new Date().getTime() + "_" + Math.random(), category, category.replace("_", "-"));
                                                    log.debug("Created new filter /" + levelOption.getValue() + "/" + typeOption.getValue() + "/" + category);
                                                    log.debug("Adding filter /" + levelOption.getValue() + "/" + typeOption.getValue() + "/" + categoryOption.getValue() + " to filter");
                                                    categoryFilters.put(category, categoryOption);
                                                }
                                            }
                                        }
                                        typeOption.setSubFilterOptions(categoryFilters);
                                    }
                                    log.debug("Adding filter /" + levelOption.getValue() + "/" + typeOption.getValue() + " to filter");
                                    typeFilters.put(meeting.getMeetingPlanType(), typeOption);
                                    levelOption.setSubFilterOptions(typeFilters);
                                }
                                log.debug("Adding filter /" + levelOption.getValue() + " to filter");
                                filters.put(meetingLevel, levelOption);
                            } catch (Exception e) {
                                log.error("Exception occured:", e);
                            }
                        }
                    }
                    if (filters != null && filters.size() > 0) {
                        try {
                            apiConfig.setFilters(filters);
                            session.setAttribute(ApiConfig.class.getName(), apiConfig);
                            response.getWriter().write(mapper.writeValueAsString(filters));
                        } catch (Exception e) {
                            log.error("Exception occured:", e);
                        }
                    }
                }
            } else {
                log.error("ApiConfig is not in session, probably user is not logged in. Responding with UNAUTHORIZED response");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("Exception occured:", e);
        }

    }

    public class FilterOption {
        private String id;
        private String value;
        private String label;
        private TreeMap<String, FilterOption> subFilterOptions;

        FilterOption(String id, String value, String label) {
            this.id = id;
            this.value = value;
            this.label = label;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public TreeMap<String, FilterOption> getSubFilterOptions() {
            return subFilterOptions;
        }

        public void setSubFilterOptions(TreeMap<String, FilterOption> subFilterOptions) {
            this.subFilterOptions = subFilterOptions;
        }

        public void addSubFilter(FilterOption filter) {
            String key = filter.getValue();
            if (!this.subFilterOptions.containsKey(key)) {
                this.subFilterOptions.put(key, filter);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            FilterOption that = (FilterOption) o;
            return id.equals(that.id) && value.equals(that.value) && label.equals(that.label) && subFilterOptions.equals(that.subFilterOptions);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, value, label, subFilterOptions);
        }
    }
}
