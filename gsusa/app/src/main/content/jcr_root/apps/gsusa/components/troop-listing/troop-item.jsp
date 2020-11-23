<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false"%>

<div class="row details">
    <div class="detail clearfix">
        <section>
            <p>{{{Location}}}</p>
            <p>{{Address1}}</p>
            <p>{{Address2}}</p>
            <p>{{City}}, {{State}} {{ZipCode}}</p>
        </section>
        <section>
            <p>{{DateEnd}}</p>
        </section>
        <section>
            <p>{{Distance}} Miles</p>
        </section>
    </div>
    <div class="clearfix right">
        <a class="viewmap button" data='{{{json .}}}'>{{{detailsText}}}</a>
    </div>
</div>