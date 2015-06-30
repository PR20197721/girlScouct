<%@include file="/libs/foundation/global.jsp" %>
<div class="standalone-join">
    <div class="wrapper">
        <a href="#" title="join">Join now</a>
        <section>
            <form action="" method="post" id="findCouncilByZip" onsubmit="return handleFindCouncilByZip(this.zipcode.value)">
                <span>FIND YOUR LOCAL COUNCIL</span>
                <input type="text" pattern="[0-9]*" name="zipcode" placeholder="enter ZIP code" />
                <button class="button btn" type="submit" form="findCouncilByZip">GO</button>
            </form>
        </section>
    </div>
</div>