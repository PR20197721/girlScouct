<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>tooltip demo</title>
  <link rel="stylesheet" href="//code.jquery.com/ui/1.11.0/themes/smoothness/jquery-ui.css">
  <script src="//code.jquery.com/jquery-1.10.2.js"></script>
  <script src="//code.jquery.com/ui/1.11.0/jquery-ui.js"></script>
  
  
  <script>
  
 	 $( "#caca" ).tooltip({ content: "Awesome title!" });
  </script>
</head>
<body>
 
<p>
  <a href="#" title="Anchor description">Anchor text</a>
  <input id="caca">
</p>
<script>
  $( document ).tooltip();
</script>
 
</body>
</html>