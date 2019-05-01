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
});
$(window).resize(function() {
    if( $(this).width() >= 961.008){
        $(".off-canvas-wrap").removeClass("move-right");
    }
});