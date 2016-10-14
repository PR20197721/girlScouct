<%-- display aids --%>


        <%      
                
                Page categoryPage = manager.getPage(categoryParam);

                if (categoryPage != null) {
            	
                    if ( categoryPage.getProperties().get("type", "").equals(TYPE_MEETING_AIDS)) {
        %><table width="90%" align="center" class="browseMeetingAids">
            <tr>
                <th colspan="3">Meeting Aids</th>
            </tr>
            <%
                //LOCAL AIDS
                            try {
                                Iterator<Resource> iter = levelMeetingsRoot.listChildren();
                                while (iter.hasNext()) {                 	
                                    Resource meetingResource = iter.next();
                     //System.err.println("TESTERX: "+ meetingResource.getPath());             
                                    String meetingId= meetingResource.getPath().substring( meetingResource.getPath().lastIndexOf("/"));
                     //System.err.println("TESTERX: "+ meetingId);              
                                    meetingId= meetingId.replace("/","");
                     //System.err.println("TESTERXY: "+ meetingId);             
                                    java.util.List<org.girlscouts.vtk.models.Asset> lresources = yearPlanUtil.getAllResources(user, troop, LOCAL_MEETING_AID_PATH+"/"+meetingId);//meeting.getId());                            
                                    for(int i=0;i<lresources.size();i++){      
                                        org.girlscouts.vtk.models.Asset la = lresources.get(i);
                                        String lAssetImage = org.girlscouts.vtk.utils.GSUtils.getDocTypeImageFromString(la.getDocType());

            %>
            <tr>

                <td width="40">
                    <%
                        if (lAssetImage != null) {
                    %> <img src="<%=lAssetImage%>" width="40" height="40" border="0" />
                    <%
                        }
                    %>
                </td>
                <td><a class="previewItem" href="<%=la.getRefId()%>"
                    target="_blank"><%=la.getTitle()%></a></td>
                <td width="40"> 
                    <%
                        if( VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID ) ){
                    %>
                    <input type="button" value="Add to Meeting"
                    onclick="applyAids('<%=la.getRefId()%>', '<%=la.getTitle()%>', '<%=AssetComponentType.AID%>' )"
                    class="button linkButton" /> <%
    }
 %>
                </td>

            </tr>
            <%
                }
                                }
                            } catch (Exception e) {e.printStackTrace();}
                          
          java.util.List<org.girlscouts.vtk.models.Asset> gresources = yearPlanUtil.getAllResources(user, troop, GLOBAL_MEETING_AID_PATH+"/"); 
          for(int i=0;i<gresources.size();i++){
          org.girlscouts.vtk.models.Asset a = gresources.get(i);
          String assetImage = org.girlscouts.vtk.utils.GSUtils.getDocTypeImageFromString(a.getDocType());
            %>
            <tr>
                <td width="40">
                    <%
                        if (assetImage != null) {
                    %> <img src="<%=assetImage%>" width="40" height="40" border="0" />
                    <%
                        }
                    %>
                </td>
                <td><a class="previewItem" href="<%=a.getRefId()%>"
                    target="_blank"><%=a.getTitle()%></a></td>
                <td width="40"> 
                    <%
                        if( VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID ) ){
                    %>
                    <input type="button" value="Add to Meeting"
                    onclick="applyAids('<%=a.getRefId()%>', '<%=a.getTitle()%>', '<%=AssetComponentType.AID%>' )"
                    class="button linkButton" /> <%
    }
 %>
                </td>

            </tr>
            <%
            
                }
            %>
        </table>
        <%
            } else if (categoryPage.getProperties().get("type", "").equals(TYPE_MEETING_OVERVIEWS)) {
            	
            	
        %><%=displayMeetingOverviews(user, troop, resourceResolver, yearPlanUtil, levelMeetingsRoot)%>
        <%
            } else {
        %><div><%=categoryPage.getTitle()%></div>
        <%
            
        %><ul>
            <%
                StringBuilder builder = new StringBuilder();
                            Iterator<Page> resIter = categoryPage.listChildren();
                            while (resIter.hasNext()) {
                                Page resPage = resIter.next();
                                displayAllChildren(resPage, builder, xssAPI);
                            }
            %><%=builder.toString()%>
            <%
                
            %>
        </ul>
        <%
            }
                }
        %>