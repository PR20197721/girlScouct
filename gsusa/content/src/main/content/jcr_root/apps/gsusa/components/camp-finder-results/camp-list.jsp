<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%= properties.get("intro", "") %>
<form class="camp-finder-options sort-form clearfix">
    <div class="clearfix">
        <section class="radius">
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
        <section class="duration">
            <label>Duration:</label>
            <select name="duration" onchange="this.form.submit()">
                <!-- default -->
                <option value="all" selected>All</option>
                <option value="less">Less than 1 week</option>
                <option value="week">1 week</option>
                <option value="long">More than 1 week</option>
            </select>
        </section>
        <section  class="grade">
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
        <section  class="date">
            <label>Start Date:</label>
            <input type="text" class="dp-calendar form-control hide-for-touch" id="start-desktop" data-language="my-lang" placeholder="mm/dd/yyyy" data-date-format="yyyy-mm-dd" data-position="bottom center">
            <input type="date" class="show-for-touch" id="start-touch" data-language="my-lang" name="startDate" placeholder="mm/dd/yyyy" data-date-format="mm/dd/yyyy" data-position="bottom center">
        </section>
        <section  class="date">
            <label>End Date:</label>
            <input type="text" class="dp-calendar form-control hide-for-touch" id="end-desktop" data-language="my-lang" placeholder="mm/dd/yyyy" data-date-format="yyyy-mm-dd" data-position="bottom center">
            <input type="date" class="show-for-touch" id="end-touch" data-language="my-lang" name="endDate" placeholder="mm/dd/yyyy" data-date-format="mm/dd/yyyy" data-position="bottom center">
        </section>
        <section  class="sort">
            <label>Sort by:</label>
            <select name="sortBy" onchange="this.form.submit()">
                <!-- default -->
                <option value="distance" selected>Distance</option>
                <option value="date">Date</option>
            </select>
        </section>
    </div>
</form>

<cq:include script="camp-list-more.jsp" />
<div class="row show-more">
    <a id="more" title="show more results">LOAD MORE</a>
</div>

<script>
$(function() {
    $(".dp-calendar").datepicker({
      navTitles: {
        days: 'MM <i>yyyy</i>',
        months: 'yyyy',
        years: 'yyyy1 - yyyy2'
      },
      onSelect: function (fd, date) {
        var start = $('#start-desktop'),
            end = $('#end-desktop');
        start.data('datepicker').update('maxDate', date);
        end.data('datepicker').update('minDate', date);
        $('#start-touch').val(start.val());
        $('#end-touch').val(end.val());
        
        $('form.camp-finder-options').submit();
      }
    });
});
<{{!}}/script>