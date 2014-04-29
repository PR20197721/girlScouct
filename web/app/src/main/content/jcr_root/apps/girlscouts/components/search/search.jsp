<%@ page import="com.day.cq.wcm.foundation.Search,
                 com.day.cq.tagging.TagManager,
                 java.util.Locale,
                 java.util.ResourceBundle,
                 com.day.cq.i18n.I18n" %>
<%@include file="/libs/foundation/global.jsp" %>

<cq:setContentBundle source="page" />

<%
Search search = new Search(slingRequest);

final Locale pageLocale = currentPage.getLanguage(true);
final ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);

String searchIn = (String) properties.get("searchIn");
if (searchIn != null) {
	System.out.println(searchIn);
        search.setSearchIn(searchIn);
    }
 else {
	  System.out.println(currentPage.getAbsoluteParent(2).getPath());
      search.setSearchIn(currentPage.getAbsoluteParent(2).getPath());
}

final String escapedQuery = xssAPI.encodeForHTML(search.getQuery());
final String escapedQueryForAttr = xssAPI.encodeForHTMLAttr(search.getQuery());

pageContext.setAttribute("escapedQuery", escapedQuery);
pageContext.setAttribute("escapedQueryForAttr", escapedQueryForAttr);

pageContext.setAttribute("search", search);
TagManager tm = resourceResolver.adaptTo(TagManager.class);
%>
<c:set var="trends" value="${search.trends}"/><c:set var="result" value="${search.result}"/>
<center>
     <form action="${currentPage.path}.html">
        <input type="text" name="q" value="${escapedQueryForAttr}" class="searchField" />
     </form> 
</center>
<br/>
<c:choose>
  <c:when test="${empty result && empty escapedQuery}">
  </c:when>
  <c:when test="${empty result.hits}">
    ${result.trackerScript}
    <c:if test="${result.spellcheck != null}">
      <p><fmt:message key="spellcheckText"/> <a href="<c:url value="${currentPage.path}.html"><c:param name="q" value="${result.spellcheck}"/></c:url>"><b><c:out value="${result.spellcheck}"/></b></a></p>
    </c:if>
    <fmt:message key="noResultsText">
      <fmt:param value="${escapedQuery}"/>
    </fmt:message>
    <span record="'noresults', {'keyword': '<c:out value="${escapedQuery}"/>', 'results':'zero', 'executionTime':'${result.executionTime}'}"></span>
  </c:when>
  <c:otherwise>
    <span record="'search', {'keyword': '<c:out value="${escapedQuery}"/>', 'results':'${result.totalMatches}', 'executionTime':'${result.executionTime}'}"></span>
    ${result.trackerScript}
    <fmt:message key="statisticsText">
      <fmt:param value="${result.startIndex + 1}"/>
      <fmt:param value="${result.startIndex + fn:length(result.hits)}"/>
      <fmt:param value="${result.totalMatches}"/>
      <fmt:param value="${escapedQuery}"/>
      <fmt:param value="${result.executionTime}"/>
    </fmt:message>
   <br/>
      <c:forEach var="hit" items="${result.hits}" varStatus="status">
        <br/>
        <c:if test="${hit.extension != \"\" && hit.extension != \"html\"}">
            <span class="icon type_${hit.extension}"><img src="/etc/designs/default/0.gif" alt="*"></span>
        </c:if>
        <a href="${hit.URL}" onclick="trackSelectedResult(this, ${status.index + 1})">${hit.title}</a>
        <div>${hit.excerpt}</div>
       
        <br/>
      </c:forEach>
      <br/>
      <c:if test="${fn:length(result.resultPages) > 1}">
       
        <c:if test="${result.previousPage != null}">
          <a href="${result.previousPage.URL}"><fmt:message key="previousText"/></a>
        </c:if>
        <c:forEach var="page" items="${result.resultPages}">
          <c:choose>
            <c:when test="${page.currentPage}">${page.index + 1}</c:when>
            <c:otherwise>
              <a href="${page.URL}">${page.index + 1}</a>
            </c:otherwise>
          </c:choose>
        </c:forEach>
        <c:if test="${result.nextPage != null}">
          <a href="${result.nextPage.URL}"><fmt:message key="nextText"/></a>
        </c:if>
      </c:if>
  </c:otherwise>
</c:choose>