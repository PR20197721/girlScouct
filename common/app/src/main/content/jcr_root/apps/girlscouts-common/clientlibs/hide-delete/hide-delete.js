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
            },
            error: function (error) {
                console.log(error);
                checkSelections(this.items);
            }
        });
    }

    function checkSelections(items){
        if (items.length) {
            var base = $.when({isShowDelete:true});
            items.each(function(i) {
                var url =  $(this).data("foundationCollectionItemId");
                base = base.then(checkResource(url));
            });
            base = base.done(function(isShowDelete) {
                if (isShowDelete) {
                    showDelete();
                } else {
                    hideDelete();
                }
            });
        }
    }

    function checkResource(url){
        return function(isShowDelete){
            var defer = $.Deferred();
            $.ajax({
                url: url + ".json" + '?_ck=' + Date.now(),
                contentType: "application/json",
                method: 'GET'})
                .complete(function(data){
                    if(data["responseJSON"]["jcr:primaryType"] == "sling:OrderedFolder" ||data["responseJSON"]["jcr:primaryType"] == "sling:Folder" || data["responseJSON"]["jcr:primaryType"] == "nt:Folder"){
                        isShowDelete = isShowDelete && false;
                    }else{
                        isShowDelete = isShowDelete && true;
                    }
                    defer.resolve(isShowDelete);
                });
            return defer.promise();
        };
    }

    function hideDelete(){
        console.log("hiding delete button");
        $("button.cq-damadmin-admin-actions-delete-activator").parent().css("display","none");
        $("button.cq-siteadmin-admin-actions-delete-activator").parent().css("display","none");
    }

    function showDelete(){
        console.log("showing delete button");
        $("button.cq-damadmin-admin-actions-delete-activator").parent().css("display","block");
        $("button.cq-siteadmin-admin-actions-delete-activator").parent().css("display","block");
    }
})(document, Granite.$);
