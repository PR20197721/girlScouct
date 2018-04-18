$(document).ready(function () {
	$('.dropdown').show();
	
    var badges = $(".badge-block"),
        filterSets = {},
        filter,
        id,
        group,
        activeFilters = {},
        hideClass = "hide",
        groupClass = "group-",
        submenu = $(".submenu");

    $.fn.showBadge = function () {
        return this.removeClass(hideClass);
    };
    $.fn.hideBadge = function () {
        return this.addClass(hideClass);
    };
    $.fn.removeClassAll = function (name) {
        return this.removeClass(function (index, css) {
            return (css.match(new RegExp("(^|\\s)" + name + "\\S+", "g")) || []).join(' ');
        });
    };

    function hasActiveFilters() {
        return Object.keys(activeFilters).length;
    }

    $(".submenu label").each(function () {
        //filter = $(this).html();
        id = $(this).attr("for");
        filter = id.split("-")[3];
        group = Number(id.split("-")[1]);
        filterSets[filter] = {
            badges: $(".badge-block[filter~='" + filter.replace(/\s+/g, '').toLowerCase() + "']"), // Remove all spaces, ~ is for whitespace separated selector
            tag: $(".tags label[for='" + id + "']"),
            dropdown: $("#dropdown-" + group),
            group: group
        };
    });

    // Close dropdown if open on click
    $(document).on("click", function() {
        submenu.each(function () {
            if ($(this).height() > 1) {
                $(this).siblings("input[type='checkbox']").prop("checked", false);
            }
        });
    });

    $(".submenu input[type='checkbox']").on("change", function () {
    	var id = $(this).siblings("label").attr("for");
        var filter = id.split("-")[3],
            active = this.checked, // Check current active state of filter
            f,
            g,
            intersect = "";

        // Show/Hide badges
        if (active) {
            activeFilters[filter] = true;
            filterSets[filter].tag.showBadge();
        } else {
            delete activeFilters[filter];
            filterSets[filter].tag.hideBadge();
        }

        if (hasActiveFilters()) { // If there are other active filters
            for (f in activeFilters) {
                g = groupClass + filterSets[f].group;
                filterSets[f].badges.addClass(g);
                if (intersect.indexOf(g) < 0) { // If the filter group isn't already in the selector string
                    intersect += "." + g; // No space before period for AND relationship, add comma with space after g for OR
                }
            }
            badges.hideBadge();
            $(intersect).showBadge();
            badges.removeClassAll(groupClass);
        } else { // If there are no active filters
            badges.showBadge();
        }

    });
}());