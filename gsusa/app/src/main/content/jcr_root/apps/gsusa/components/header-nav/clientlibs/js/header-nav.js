$(document).ready(function(){
    $("#searchIcon").on("click",function(){
        if($("#searchIcon").attr("searchShown") === "false"){
            $("#mobileSearch").slideDown(250);
            $("#searchIcon").attr("searchShown", "true");
        }else{
             $("#mobileSearch").slideUp(250);
            $("#searchIcon").attr("searchShown", "false");
        }
    });
    $(".side-nav-toggle").on("click", function(){
        if($(".left-off-canvas-menu").hasClass("closed")){
            $(".left-off-canvas-menu").removeClass("closed");
            $(".left-off-canvas-menu").addClass("open");
        }else{
            $(".left-off-canvas-menu").removeClass("open");
            $(".left-off-canvas-menu").addClass("closed");
        }
    });
    $('.left-off-canvas-menu').on('transitionend webkitTransitionEnd oTransitionEnd', function () {
        if(!$(".left-off-canvas-menu").hasClass("closed"))
            $(".left-off-canvas-menu").addClass("complete");
    });
    $(".off-canvas-wrap").click(function() {
        if($(".left-off-canvas-menu").hasClass("complete")){
            $(".left-off-canvas-menu").removeClass("open");
            $(".left-off-canvas-menu").removeClass("complete");
            $(".left-off-canvas-menu").addClass("closed");
        }
    });
    $('.left-off-canvas-menu').click(function(event){
        event.stopPropagation();
    });
})
