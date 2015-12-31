<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>

<form class="sort-form clearfix">
    <div class="clearfix">
        <section>
            <label>Radius:</label>
            <select name="radius" onchange="this.form.submit()">
                <option value="1">1 miles</option>
                <option value="5">5 miles</option>
                <option value="10">10 miles</option>
                <option value="15">15 miles</option>
                <option value="25">25 miles</option>
                <option value="50">50 miles</option>
                <option value="100">100 miles</option>
                <!-- default -->
                <option value="250" selected>250 miles</option>
                <option value="500">500 miles</option>
            </select>
        </section>
        <section>
            <label>Duration:</label>
            <select name="duration" onchange="this.form.submit()">
                <!-- default -->
                <option value="all" selected>All</option>
                <option value="less_than_1_week">Less than 1 week</option>
                <option value="1_week">1 week</option>
                <option value="more_than_1_week">More than 1 week</option>
            </select>
        </section>
        <section>
            <label>Grade:</label>
            <select name="grade" onchange="this.form.submit()">
                <!-- default -->
                <option value="all" selected>All</option>
                <option value="k">Grade K</option>
                <option value="1">Grade 1</option>
                <option value="2">Grade 2</option>
                <option value="3">Grade 3</option>
                <option value="4">Grade 4</option>
                <option value="5">Grade 5</option>
                <option value="6">Grade 6</option>
                <option value="7">Grade 7</option>
                <option value="8">Grade 8</option>
                <option value="9">Grade 9</option>
                <option value="10">Grade 10</option>
                <option value="11">Grade 11</option>
                <option value="12">Grade 12</option>
                <option value="adult">Adult</option>
            </select>
        </section>
    </div>
    <div class="clearfix">
        <section>
            <label>Start Date:</label>
        </section>
        <section>
            <label>End Date:</label>
        </section>
    </div>
    <div class="clearfix">
        <section>
            <label>Sort by:</label>
            <select name="sortBy" onchange="this.form.submit()">
                <!-- default -->
                <option value="distance" selected>distance</option>
                <option value="date">date</option>
            </select>
        </section>
    </div>
</form>

<cq:include script="camp-list-more.jsp" />
<div class="row show-more">
    <a id="more" title="show more results">LOAD MORE</a>
</div>