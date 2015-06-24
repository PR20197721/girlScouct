<%@include file="/libs/foundation/global.jsp" %>
<script>
    // http://www.girlscouts.org/includes/join/join_ajax_GetCouncilInfo.asp?actiontype=homepage&source=homepage-outdoor&zipcode=10001
    function handleFindCouncilByZip(zipCode) {
        console.log("Zip code redirect : " + zipCode);
        $.ajax({
            method: "GET",
            url: "zipCouncil/join_ajax_GetCouncilInfo.asp?actiontype=homepage&source=homepage-outdoor&zipcode=10001",
            data: {zip: zipCode}
        }).done(function(msg) {
			alert(msg);
        });
        return false;
    }
</script>
	<div class="join">
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