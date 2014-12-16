
   <script type='text/javascript' src='https://code.jquery.com/jquery-2.1.1.min.js'></script>
    <script type='text/javascript' src='https://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.7.0/underscore-min.js'></script>


<script src="http://fb.me/react-0.12.1.js"></script>
<script src="http://fb.me/JSXTransformer-0.12.1.js"></script>

 <script src="http://fb.me/react-with-addons-0.12.1.js"></script>
 <script src="/etc/designs/girlscouts-vtk/clientlibs/js/require.js"></script>
  
  <script src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.5.2/underscore.js"></script>
  
  
<script type="text/jsx">





var App = React.createClass({
    getInitialState: function () {
        return {
            "a": 10,
            "b": {
                "foo": {
                    "bar": 42,
                    "baz": ['red', 'green']
                }
            }
        };
    },
shouldComponentUpdate: function (nextProps, nextState) {
    return this.props.cursor !== nextProps.cursor;
},
    render: function () {
        return <pre>{JSON.stringify(this.state, undefined, 2)}</pre>;
    }
});

var myClass = React.createClass({
    mixins: [ImmutableOptimizations(['cursor'])],
});

React.render(
<App />,
  document.getElementById('test')
);
var Cursor = require(["../../../reactjs/react-cursor"], function (util){ 

	console.log("1");
}).Cursor;

var cursor = Cursor.build(this);
                

cursor.refine('a').value;
cursor.refine('a').set(11);
cursor.refine('b').refine('foo').value     ;
cursor.refine('b').refine('foo').set({ 'bar': 43, 'baz': ['red', 'green'] });
cursor.refine('b', 'foo', 'baz', 1).set('blue');




                                </script>
                                
                                
                                <div id="test"></div>