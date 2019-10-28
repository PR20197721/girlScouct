(function(document, $) {
    "use strict";
    //download action
    $(document).on('foundation-selections-change', '.foundation-collection', function() {
        var collection = $(this);
        var items = collection.find('.foundation-selections-item');
        allowDelete(items);
    });

    function allowDelete(items) {
        var requestPath = "/etc/renderconditions/features/content/assets/delete-dam-folder";
        $.ajax({
            type: "GET",
            items: items,
            url: requestPath + ".json" + '?_ck=' + Date.now(),
            contentType: "application/json",
            success: function (data) {
                console.log(data);
                return true
            },
            error: function (error) {
                console.log(error);
                checkSelections(this.items);
            }
        });
    }

    function checkSelections(items){
        if (items.length) {
            items.each(function(i) {
                var item = $(this);
                if(isFolder(item)){
                    hideDelete();
                    return;
                }
            });
        }
    }

    function hideDelete(){
        console.log("hiding delete button");
        $("button.cq-damadmin-admin-actions-delete-activator").parent().css("display:none");
    }

    function isFolder(item){
        var regex = new RegExp("^.*\\..{2,4}$");
        return (regex.test(item.data("foundation-collection-item-id")));
    }
})(document, Granite.$);
        