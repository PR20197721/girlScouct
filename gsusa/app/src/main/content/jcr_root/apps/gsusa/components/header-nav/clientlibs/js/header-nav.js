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
})