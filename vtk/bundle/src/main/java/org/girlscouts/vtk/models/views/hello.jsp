<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>
<html>
<body>
<h1>
    Add me
</h1>

<c:url var="addAction" value="/product/add" ></c:url>

<form:form action="${addAction}" commandName="product">
<table>
    <c:if test="${!empty product.id}">
    <tr>
        <td>
            <form:label path="id">
                <spring:message text="ID"/>
            </form:label>
        </td>
        <td>
            <form:input path="id" readonly="true" size="8"  disabled="true" />
            <form:hidden path="id" />
        </td> 
    </tr>
    </c:if>
    
    <tr>
        <td colspan="2">
            
                <input type="submit"
                    value="<spring:message text="Add "/>" />
           
        </td>
    </tr>
</table>    
</form:form>

</body>
</html>