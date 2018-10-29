girlscouts.components.hideCouncilWidget = function(comp) {
    if (window.location.href.indexOf('/content/vtkcontent/') == -1) {
        comp.hide();
    }
}