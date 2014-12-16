

<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
<script src="http://fb.me/react-0.12.1.js"></script>
<script src="http://fb.me/JSXTransformer-0.12.1.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>

 
  
<script type="text/jsx">




var Parent = React.createClass({

    getInitialState: function() {
        return {
            value: 'foo'
        }
    },

    alex: function(value) {
        //this.setState({value: value });
console.log("alex"); 
    },

    render: function() {
        return (
            <div>
                <Child value={this.state.value} onChange={this.alex} />
                <span>{this.state.value}</span>
            </div>
        );
    }
});

var Child = React.createClass({
    propTypes: {
        value:      React.PropTypes.string,
        onChange:   React.PropTypes.func
    },
    
    mike: function(e) {
console.log(111);
console.log(this.props);
        if (typeof this.props.onChange === 'function') {
            this.props.onChange("test:"+e.target.value);
        }
    },
    render: function() {
        return (
            <input type="text" value={this.props.value} onChange={this.mike} />
        );
    }
});




React.render( <Parent />,  document.getElementById('container') );

</script>

<div id="container"></div>