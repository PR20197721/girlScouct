<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false"%>

<div class="row details">
    <div class="detail clearfix">
        <section>
            <p class="location" data='{{{json .}}}'>{{{Location}}}</p>
            <p class="address1">{{Address1}}</p>
            <p class="address2">{{Address2}}</p>
            <p>{{City}}, {{State}} {{ZipCode}}</p>
        </section>
        <section>
            <p>{{DateStart}}</p>
            <p>{{Distance}} Miles</p>
        </section>
    </div>
    <div class="clearfix right">
        <a class="viewmap button" data='{{{json .}}}'>{{{detailsText}}}</a>
    </div>
</div>