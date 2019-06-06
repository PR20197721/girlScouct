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
            $(".exit-off-canvas").addClass("overlay");
            $(".left-off-canvas-menu").addClass("open");
            $(".main-section").css("height", $("#left-canvas-menu").css("height"));
            $(".main-section").css("overflow", "hidden");
        }else{
            $(".left-off-canvas-menu").removeClass("open");
            $(".left-off-canvas-menu").addClass("closed");
            $(".exit-off-canvas").removeClass("overlay");
            $(".main-section").css("height", "");
            $(".main-section").css("overflow", "");
        }
    });
    $('.left-off-canvas-menu').on('transitionend webkitTransitionEnd oTransitionEnd', function () {
        if(!$(".left-off-canvas-menu").hasClass("closed"))
            $(".left-off-canvas-menu").addClass("complete");
    });
    $(".off-canvas-wrap").on("click", function() {
        if($(".left-off-canvas-menu").hasClass("complete")){
            $(".left-off-canvas-menu").removeClass("open");
            $(".left-off-canvas-menu").removeClass("complete");
            $(".exit-off-canvas").removeClass("overlay");
            $(".left-off-canvas-menu").addClass("closed");
            $(".main-section").css("height", "");
            $(".main-section").css("overflow", "");
        }
    });
    $('.left-off-canvas-menu').on("click", function(event){
        event.stopPropagation();
    });
})
