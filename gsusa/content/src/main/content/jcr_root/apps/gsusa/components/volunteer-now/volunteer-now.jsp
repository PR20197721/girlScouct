<%@include file="/libs/foundation/global.jsp" %>
<div class="standalone-volunteer">
    <div class="wrapper">
        <a href="#" title="Volunteer">Volunteer Today</a>
        <section>
            <form action="" method="post" id="findSomethingByZip" onsubmit="return handleFindCouncilByZip(this.zipcode.value)">
                <span>FIND YOUR LOCAL Something</span>
                <input type="text" pattern="[0-9]*" name="zipcode" placeholder="enter ZIP code" />
                <button class="button btn" type="submit" form="findCouncilByZip">GO</button>
            </form>
        </section>
    </div>
</div>