//expand elements for current page
function init(){
    $("#mobileSearchSection .search.search-box .search-form").css("width", $(window).width() - 150);
    $(".side-nav-el.active").children("ul").css("display", "block");
    $(".side-nav-el.active").children("div").children("span").css({'transform' : 'rotate(90deg)'});
    $("#sub-active").children("ul").css("display", "block");
    $("#sub-active").find(".side-nav-expand").css({'transform' : 'rotate(90deg)'});
    $(".side-nav-expand-child.active-el").css({'transform' : 'rotate(90deg)'});
}
$(document).ready(function(){
    init();
    $(".side-nav-expand").on("click", function(){
        var el = $(this).parent().children("ul");
        if(el.css("display") === "none"){
            $(this).parent().attr("id", "selected");
            $(".side-nav-el.parentEl").each(function(){
                if($(this).attr("id") !== "selected"){
                    $(this).find(".side-nav-expand").css({'transform' : 'rotate(0deg)'});
                    $(this).children("ul").slideUp(400, function(){
                        $(".main-section").css("height", $("#left-canvas-menu").css("height"));
                        $(".main-section").css("overflow", "hidden");
                    });
                }
            });
            $([document.documentElement, document.body]).animate({
                scrollTop: 0
            }, 400);
            $(this).parent().removeAttr("id");
            $(this).find(".side-nav-expand").css({'transform' : 'rotate(90deg)'});
            $(this).parent().children("ul").slideDown(400, function(){
               $(".main-section").css("height", $("#left-canvas-menu").css("height"));
               $(".main-section").css("overflow", "hidden");
           });
        }else{
            $(this).find(".side-nav-expand").css({'transform' : 'rotate(0deg)'});
            $(this).parent().children("ul").slideUp(400, function(){
               $(".main-section").css("height", $("#left-canvas-menu").css("height"));
               $(".main-section").css("overflow", "hidden");
            });
        }
    });
    $("span.side-nav-expand-child").on("click", function(){
        var el = $(this).closest(".side-nav-el").children("ul");
        if(el.css("display") === "none"){
            $(this).closest(".side-nav-el").attr("id", "selected");
            $(this).closest("ul").children().each(function(){
                if($(this).attr("id") !== "selected"){
                    $(this).children(".menu-wrapper-el").children(".side-nav-expand-child").css({'transform' : 'rotate(0deg)'});
                    $(this).children("ul").slideUp(400, function(){
                        $(".main-section").css("height", $("#left-canvas-menu").css("height"));
                        $(".main-section").css("overflow", "hidden");
                    });
                }
            });
            $(this).closest(".side-nav-el").removeAttr("id");
            $(this).css({'transform' : 'rotate(90deg)'});
            $(this).closest(".side-nav-el").children("ul").slideDown(400, function(){
                $(".main-section").css("height", $("#left-canvas-menu").css("height"));
                $(".main-section").css("overflow", "hidden");
            });
        }else{
            $(this).css({'transform' : 'rotate(0deg)'});
            $(this).closest(".side-nav-el").children("ul").slideUp(400, function(){
                $(".main-section").css("height", $("#left-canvas-menu").css("height"));
                $(".main-section").css("overflow", "hidden");
            });
        }
    });
});
$(window).on('resize', function() {
    $("#mobileSearchSection .search.search-box .search-form").css("width", $(window).width() - 150);
});