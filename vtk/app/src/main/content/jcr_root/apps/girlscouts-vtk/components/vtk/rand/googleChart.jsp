<script type="text/javascript" src="https://www.google.com/jsapi?autoload={'modules':[{'name':'visualization','version':'1','packages':['corechart']}]}"></script>
       <div id="chart_div" style="width: 900px; height: 500px;"></div>
   <script>
   google.setOnLoadCallback(drawChart);
function drawChart() {

  var data = google.visualization.arrayToDataTable([
   
    
    
    
    ['Troop','597', '367', '320', '388', '313', '111', '603'],
    
[ 'Brownie', 84,
105,
68,
18,
2,
1,
0,
],

['Daisy',27,
53,
53,
1,
1,
1,
1,
],
[ 'Junior', 
68,
80,
55,
8,
2,
1,
0,
]
    
  ]);

  var options = {
    title: 'Girscouts VTK performance',
    hAxis: {title: 'Council Activity', titleTextStyle: {color: 'red'}}
  };

  var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));

  chart.draw(data, options);

}
</script>