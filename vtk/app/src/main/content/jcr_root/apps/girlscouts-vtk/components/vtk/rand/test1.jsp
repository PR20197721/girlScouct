

<!--
/*!
 * Ext JS Library 3.0.0
 * Copyright(c) 2006-2009 Ext JS, LLC
 * licensing@extjs.com
 * http://www.extjs.com/license
 */
-->
<html>
<head>
<title>Hello World Window</title>
<link rel="stylesheet" type="text/css" href="ext-3.0.0/resources/css/ext-all.css" />
<script type="text/javascript" src="http://extjs.cachefly.net/ext-3.0.0/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="http://extjs.cachefly.net/ext-3.0.0/ext-all.js"></script>
</head>

<!-- Revised from demo code from ext3.0.0 -->
<body>
<script type="text/javascript">
Ext.onReady(function() {

    var myData = {
    records : [
      { name : "Record 0", column1 : "11", column2 : "0" },
      { name : "Record 1", column1 : "1", column2 : "1" },
      { name : "Record 2", column1 : "2", column2 : "2" },
      { name : "Record 3", column1 : "3", column2 : "3" },
      { name : "Record 4", column1 : "4", column2 : "4" },
      { name : "Record 5", column1 : "5", column2 : "5" },
      { name : "Record 6", column1 : "6", column2 : "6" },
      { name : "Record 7", column1 : "7", column2 : "7" },
      { name : "Record 8", column1 : "8", column2 : "8" },
      { name : "Record 9", column1 : "9", column2 : "9" }
    ]
  };


  // Generic fields array to use in both store defs.
  var fields = [
     {name: 'name', mapping : 'name'},
     {name: 'column1', mapping : 'column1'},
     {name: 'column2', mapping : 'column2'}
  ];

    // create the data store
    var gridStore = new Ext.data.JsonStore({
        fields : fields,
    data   : myData,
    root   : 'records'
    });


  // Column Model shortcut array
  var cols = [
    { id : 'name', header: "Record Name", width: 160, sortable: true, dataIndex: 'name'},
    {header: "column1", width: 50, sortable: true, dataIndex: 'column1'},
    {header: "column2", width: 50, sortable: true, dataIndex: 'column2'}
  ];

  // declare the source Grid
    var grid = new Ext.grid.GridPanel({
        ddGroup          : 'gridDDGroup',
        store            : gridStore,
        columns          : cols,
   		enableDragDrop   : true,
        stripeRows       : true,
        autoExpandColumn : 'name',
        width            : 650,
        height           : 325,
        region           : 'west',
        title            : 'Data Grid',
        selModel         : new Ext.grid.RowSelectionModel({singleSelect : true})
    });

  //Simple 'border layout' panel to house both grids
  var displayPanel = new Ext.Panel({
    width    : 650,
    height   : 300,
    layout: 'fit',
    renderTo : 'panel',
    items    : [
      grid
    ],
    bbar    : [
      '->', // Fill
      {
        text    : 'Reset Example',
        handler : function() {
          //refresh source grid
          gridStore.loadData(myData);
        }
      }
    ]
  });

});
</script> 

<div id="panel"></div>

</body>
</html>

   