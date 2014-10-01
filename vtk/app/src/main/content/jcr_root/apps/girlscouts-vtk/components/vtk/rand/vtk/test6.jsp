<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>jQuery UI Tooltip - Custom content</title>
  <link rel="stylesheet" href="//code.jquery.com/ui/1.11.0/themes/smoothness/jquery-ui.css">
  <script src="//code.jquery.com/jquery-1.10.2.js"></script>
  <script src="//code.jquery.com/ui/1.11.0/jquery-ui.js"></script>
  <link rel="stylesheet" href="/resources/demos/style.css">
  <style>
  .photo {
    width: 300px;
    text-align: center;
  }
  .photo .ui-widget-header {
    margin: 1em 0;
  }
  .map {
    width: 350px;
    height: 350px;
  }
  .ui-tooltip {
    max-width: 350px;
  }
  </style>
  <script>
  $(function() {
    $( document ).tooltip({
    	
      items: "img, [data-geo], [title]",
      content: function() {
    	  
        var element = $( this );
        if ( element.is( "[data-geo]" ) ) {
        	
          var text = element.attr('href');
          text= "<iframe src='"+text+"'></iframe>";
          return text;
         
           
              
        }
        
        
        if ( element.is( "[title]" ) ) {
          return element.attr( "title" );
        }
        if ( element.is( "img" ) ) {
          return element.attr( "alt" );
        }
        
      }
  
    });
  });
  </script>
</head>
<body>
 
<h3><a href="/content/dam/girlscouts-vtk/global/resource/A-World-of-Girls-Journey-Awards.pdf" data-geo="">A-World-of-Girls-Journey-Award</a></h3>

asdfasdfasdf
<h3><a href="/content/dam/girlscouts-vtk/global/resource/Brownie-Kaper-Chart.pdf" data-geo="">Brownie-Kaper-Chart</a></h3>


</body>
</html>