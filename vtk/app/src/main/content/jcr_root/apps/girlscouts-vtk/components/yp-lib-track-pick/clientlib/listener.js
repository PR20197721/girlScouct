(function (document, $, ns) {
    "use strict";
    $(document).on("dialog-ready", function () {
        var $dialog = $("coral-dialog-content");
        var $resourceType = $dialog.find("[name='./sling:resourceType']").val();
        if ("girlscouts-vtk/components/yp-lib-track-pick" == $resourceType) {
            var $levelSelect = new Coral.Select().set({
                name: "./level",
                placeholder: "Choose a grade level"
            });
            var $trackSelect = new Coral.Select().set({
                name: "./track",
                placeholder: "Choose a track"
            });
            var $levelHidden = $dialog.find("input[name='./level'][type='hidden']");
            var selectedLevel = $levelHidden.val();

            var $trackHidden = $dialog.find("input[name='./track'][type='hidden']");
            var selectedTrack = $trackHidden.val();
            if (selectedLevel=="") {
                try {
                    selectedLevel = selectedTrack.replace(/.*yearplan[^d][^d][^d][^d]\//, "").replace(/\/yearPlan.*/, "");
                } catch (err) {
                }
            }
            var levels = ["daisy","brownie","junior","cadette","senior","ambassador","multi-level"];
            console.log("levels="+levels);
            for (var i = 0; i < levels.length + 1; i++) {
                var selected = selectedLevel == levels[i] ? true : false;
                $levelSelect.items.add({
                    value: levels[i],
                    selected: selected,
                    content: {
                        textContent: levels[i]
                    }
                });
            }
            $levelHidden.after($("<div class='coral-Form-fieldwrapper'>").append($levelSelect));
            $trackHidden.after($("<div class='coral-Form-fieldwrapper'>").append($trackSelect));
            loadYearPlan($levelSelect, $trackSelect, selectedTrack);
            $levelSelect.on("change", function () {
                loadYearPlan($levelSelect, $trackSelect, selectedTrack)
            });
        }
    });

    function loadYearPlan($levelSelect, $trackSelect, selectedTrack) {
        try {
            if ($levelSelect.selectedItem) {
                $trackSelect.items.clear();
                $.getJSON("/bin/vtk/v1/scaffoldingdata.json?level=" + $levelSelect.selectedItem.value).done(function (data) {
                    $.each(data.yearplan, function (key, val) {
                        var selected = selectedTrack == val.data ? true : false;
                        try {
                            $trackSelect.items.add({
                                value: val.data,
                                selected: selected,
                                content: {
                                    textContent: val.title
                                }
                            });
                        } catch (err) {
                        }
                    });
                });
            }
        } catch (err) {
        }
    }
})(document, Granite.$, Granite.author);
