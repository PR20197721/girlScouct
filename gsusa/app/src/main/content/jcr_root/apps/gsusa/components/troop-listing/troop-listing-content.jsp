<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>

<div><%=properties.get("troopLinkDescription","")%></div>

<cq:include script="troop-list-more.jsp" />

    <div style="text-align: center;" class="row supportAnotherTroopSection hide">
    <a id="supportAnotherTroopButton" class="button" title="Want to support another troop?">Want to support another troop?</a>
</div>
<div class="row show-more">
    <a id="more" title="show more results">MORE</a>
</div>