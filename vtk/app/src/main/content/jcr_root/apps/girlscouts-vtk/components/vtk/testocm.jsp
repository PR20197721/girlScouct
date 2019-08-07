<%@page import="org.slf4j.Logger,
                org.slf4j.LoggerFactory,
                org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository,
                org.girlscouts.vtk.ocm.AchievementNode" %>
<%
    Logger sessionlog = LoggerFactory.getLogger(this.getClass().getName());
    try {
        final GirlScoutsOCMRepository repoTest = sling.getService(GirlScoutsOCMRepository.class);
        AchievementNode testNode = new AchievementNode();
        testNode.setPath("/vtk2018/test");
        repoTest.create(testNode);
        AchievementNode testNode2 = repoTest.read("/vtk2018/test2");
        testNode2.setTotal(2);
        repoTest.update(testNode2);
    }catch(Exception ex){
        sessionlog.error("session.jsp",ex);
    }
%>                  