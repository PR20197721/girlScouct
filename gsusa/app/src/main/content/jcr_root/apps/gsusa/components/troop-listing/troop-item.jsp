<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false"%>

<div class="row details">
    <div class="detail clearfix">
        <section>
            <p class="troopRegisterLink">{{{Location}}}</p>
            <p>{{City}}, {{State}} {{ZipCode}}</p>
        </section>
        <section>
            <p>{{Distance}} Miles </p>
        </section>
    </div>
    <div class="clearfix right">
        <a class="viewmap troopListingCustomButton button troopRegisterButton" href="{{visitBoothUrl}}" target="_blank" data='{{{json .}}}'>{{{detailsText}}}</a>
    </div>
</div>