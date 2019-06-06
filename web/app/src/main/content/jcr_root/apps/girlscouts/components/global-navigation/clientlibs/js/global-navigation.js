$(document).ready(function(){
    $("#searchIcon").on("click",function(){
        if($("#searchIcon").attr("searchShown") === "false"){
            $("#mobileSearchBar").slideDown(250);
            $("#searchIcon").attr("searchShown", "true");
        }else{
             $("#mobileSearchBar").slideUp(250);
            $("#searchIcon").attr("searchShown", "false");
        }
    });
    $(".side-nav-toggle").on("click", function(){
        if($(".left-off-canvas-menu").hasClass("closed")){
            $(".left-off-canvas-menu").removeClass("closed");
            $(".exit-off-canvas-el").addClass("overlay");
            $(".left-off-canvas-menu").addClass("open");
            $(".inner-wrap").css("height",  $("#side-nav-top").height() + $("#right-canvas-menu-bottom").height() + 29 + "px");
            $(".inner-wrap").css("overflow", "hidden");
        }else{
            $(".left-off-canvas-menu").removeClass("open");
            $(".left-off-canvas-menu").addClass("closed");
            $(".exit-off-canvas-el").removeClass("overlay");
            $(".inner-wrap").css("height", "");
            $(".inner-wrap").css("overflow", "");
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
            $(".exit-off-canvas-el").removeClass("overlay");
            $(".left-off-canvas-menu").addClass("closed");
            $(".inner-wrap").css("height", "");
            $(".inner-wrap").css("overflow", "");
        }
    });
    $('.left-off-canvas-menu').on("click", function(event){
        event.stopPropagation();
    });
});
$(window).resize(function() {
    if( $(this).width() >= 961.008){
       console.log("test");
       $(".exit-off-canvas-el").click();
    }
});