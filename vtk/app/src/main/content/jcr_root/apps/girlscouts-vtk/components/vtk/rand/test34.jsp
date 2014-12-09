
<html>
  <head>
    <title>Test</title>
    <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
    <script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
    <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
    <script src="http://fb.me/react-0.5.1.js"></script>
    <script src="http://fb.me/JSXTransformer-0.5.1.js"></script>
     <script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
  </head>
  <body>
    <script type="text/jsx">
      /** @jsx React.DOM */

      function cloneWithProps(c) {
        // Coming in next version of React!
        var newInstance = new c.constructor();
        newInstance.construct(c.props);
        return newInstance;
      }

      var TestItem = React.createClass({
        getInitialState: function() {
          return {count: 0};
        },
        componentDidMount: function() {
          this.interval = setInterval(this.handleTick, 1000);
        },
        componentWillUnmount: function() {
          clearInterval(this.interval);
        },
        handleTick: function() {
          this.setState({count: this.state.count + 1});
        },
        render: function() {
          return <span>{this.props.children}{': '}{this.state.count}</span>;
        }
      });

      var SmartSortable = React.createClass({
        getDefaultProps: function() {
          return {component: React.DOM.ul, childComponent: React.DOM.li};
        },

        render: function() {
          var component = this.props.component;
          return this.transferPropsTo(<component />);
        },

        componentDidMount: function() {
          jQuery(this.getDOMNode()).sortable({stop: this.handleDrop});
          this.getChildren().forEach(function(child, i) {
            jQuery(this.getDOMNode()).append('<' + this.props.childComponent.componentConstructor.displayName + ' />');
            var node = jQuery(this.getDOMNode()).children().last()[0];
            node.dataset.reactSortablePos = i;
            React.renderComponent(cloneWithProps(child), node);
          }.bind(this));
        },

        componentDidUpdate: function() {
          var childIndex = 0;
          var nodeIndex = 0;
          var children = this.getChildren();
          var nodes = jQuery(this.getDOMNode()).children();
          var numChildren = children.length;
          var numNodes = nodes.length;

          while (childIndex < numChildren) {
            if (nodeIndex >= numNodes) {
              jQuery(this.getDOMNode()).append('<' + this.props.childComponent.componentConstructor.displayName + '/>');
              nodes.push(jQuery(this.getDOMNode()).children().last()[0]);
              nodes[numNodes].dataset.reactSortablePos = numNodes;
              numNodes++;
            }
            React.renderComponent(cloneWithProps(children[childIndex]), nodes[nodeIndex]);
            childIndex++;
            nodeIndex++;
          }

          while (nodeIndex < numNodes) {
            React.unmountComponentAtNode(nodes[nodeIndex]);
            jQuery(nodes[nodeIndex]).remove();
            nodeIndex++;
          }
        },

        componentWillUnmount: function() {
          jQuery(this.getDOMNode()).children().get().forEach(function(node) {
            React.unmountComponentAtNode(node);
          });
        },

        getChildren: function() {
          // TODO: use mapChildren()
          return this.props.children || [];
        },

        handleDrop: function() {
          var newOrder = jQuery(this.getDOMNode()).children().get().map(function(child, i) {
            var rv = child.dataset.reactSortablePos;
            child.dataset.reactSortablePos = i;
            return rv;
          });
          this.props.onSort(newOrder);
        }
      });

      var App = React.createClass({
        getInitialState: function() {
          return {items: ['test 0', 'test 1', 'test 2'], counter: 3};
        },
        handleSort: function(newOrder) {
          var newItems = newOrder.map(function(index) {
            return this.state.items[index];
          }.bind(this));
          this.setState({items: newItems});
        },
        handleAdd: function() {
          var newItems = this.state.items.concat(['test ' + this.state.counter]);
          this.setState({items: newItems, counter: this.state.counter + 1});
        },
        handleRemove: function() {
          this.setState({items: this.state.items.slice(0, this.state.items.length - 1)});
        },
        render: function() {
          var items = this.state.items.map(function(item) {
            return <li key={item}><TestItem>{item}</TestItem></li>;
          });
          return (
            <div>
              <h1>React + Sortable</h1>
              <SmartSortable onSort={this.handleSort}>
                {items}
              </SmartSortable>
              <p>Current order: {JSON.stringify(this.state.items)}</p>
              <p><button onClick={this.handleAdd}>Add an item</button></p>
              <p><button onClick={this.handleRemove}>Remove an item</button></p>
            </div>
          );
        }
      });

      React.renderComponent(<App />, document.body);
    </script>
  </body>
</html>