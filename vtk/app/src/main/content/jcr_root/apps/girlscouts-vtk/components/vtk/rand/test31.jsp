<html>
 
  <title>Hello React</title>
  <script src="http://fb.me/react-0.12.1.js"></script>
  <script src="http://fb.me/JSXTransformer-0.12.1.js"></script>
  <script src="http://code.jquery.com/jquery-1.10.0.min.js"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/showdown/0.3.1/showdown.min.js"></script>
  
  <script src="http://cdnjs.cloudflare.com/ajax/libs/react/0.12.1/react-with-addons.js"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/react/0.12.1/JSXTransformer.js"></script>
  
</head>
    
    <script>
   var Hello = React.createClass({
    render: function() {
        return React.DOM.div({}, 'Hello ' + this.props.name);
    }
});
Hello = React.createFactory(Hello);

React.render(Hello({name: 'Bar'}), $("#foo"));
</script>
  </body>
</html>

