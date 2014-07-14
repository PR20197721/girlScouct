
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">

    <title>jQuery UI Touch Punch - Mobile Device Touch Event Support for jQuery UI</title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <link rel="shortcut icon" href="/ico/favicon.ico">
    <link rel="apple-touch-icon-precomposed" sizes="114x114" href="/ico/apple-touch-icon-114-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="72x72" href="/ico/apple-touch-icon-72-precomposed.png">
    <link rel="apple-touch-icon-precomposed" href="/ico/apple-touch-icon-57-precomposed.png">

    
    <style>body { background:#fff; font-family:"Helvetica Neue",Helvetica,Arial,sans-serif; }</style>

    <link href="//cdnjs.cloudflare.com/ajax/libs/jqueryui/1.10.3/css/base/jquery.ui.all.css" rel="stylesheet">
    <link href="//cdnjs.cloudflare.com/ajax/libs/jqueryui/1.10.2/css/lightness/jquery-ui-1.10.2.custom.min.css" rel="stylesheet">
    <script src="//cdnjs.cloudflare.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/jqueryui-touch-punch/0.2.2/jquery.ui.touch-punch.min.js"></script>
  </head>
  <body>

    <div class="container">
      

        <style>
        #sortable { list-style-type: none; margin: 0; padding: 0; width: 60%; }
        #sortable li { margin: 0 5px 5px 5px; padding: 5px; font-size: 1.2em; height: 1.5em; }
        html>body #sortable li { height: 1.5em; line-height: 1.2em; }
        .ui-state-highlight { height: 1.5em; line-height: 1.2em; }
        </style>
        <script>
        $(function() {
          $( "#sortable" ).sortable({
            placeholder: "ui-state-highlight"
          });
          $( "#sortable" ).disableSelection();
        });
        </script>


      <div class="demo">

      <ul id="sortable">
        <li class="ui-state-default">Item 1</li>
        <li class="ui-state-default">Item 2</li>
        <li class="ui-state-default">Item 3</li>
        <li class="ui-state-default">Item 4</li>
        <li class="ui-state-default">Item 5</li>
        <li class="ui-state-default">Item 6</li>
        <li class="ui-state-default">Item 7</li>
      </ul>

      </div><!-- End demo -->



      <div class="demo-description">
      <p>
        When dragging a sortable item to a new location, other items will make room
        for the that item by shifting to allow white space between them. Pass a
        class into the <code>placeholder</code> option to style that space to
        be visible.  Use the boolean <code>forcePlaceholderSize</code> option
        to set dimensions on the placeholder.
      </p>
      </div><!-- End demo-description -->


      
    </div>

   

    </body>
</html>
