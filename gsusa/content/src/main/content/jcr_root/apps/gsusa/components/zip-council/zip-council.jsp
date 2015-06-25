<%@include file="/libs/foundation/global.jsp" %>
<script>
    // http://www.girlscouts.org/includes/join/join_ajax_GetCouncilInfo.asp?actiontype=homepage&source=homepage-outdoor&zipcode=10001
    // 161,INSERTED,http://www.girlscoutsnyc.org?utm_medium=homepage-outdoor&utm_source=homepage&utm_campaign=GSRecruitmentCampaign
    function handleFindCouncilByZip(zipCode) {
        console.log("Zip code redirect : " + zipCode);
		var result = "";
        $.ajax({
            method: "GET",
            url: "/zipCouncil/join_ajax_GetCouncilInfo.asp?actiontype=homepage&source=homepage-outdoor&zipcode=" + zipCode,
            data: {zip: zipCode}
        }).fail(function() {
			alert("Sorry, unable to connect to the internet.");
        }).done(function(msg) {
            result = msg;
        }).always(function() {
            if (result != "") {
                var resultArray = result.split(",");
                if (resultArray && resultArray.length > 2) {
                    window.location = resultArray[2];
                }
            }
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
