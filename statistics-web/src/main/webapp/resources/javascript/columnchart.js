function columnchart(data, horizontalHeader) {
	 var drawdata = new google.visualization.DataTable();
     drawdata.addColumn('string', horizontalHeader);
     drawdata.addColumn('number', 'Kvinnor');
     drawdata.addColumn('number', 'Män');
     
     if (data == null){
     	alert("null data");
     }
     if (data.data == null){
     	alert("null data.data");
     }
     if (data.data.matches == null){
     	alert("null data.data.matches");
     }
     
     var rows = data.data.matches;
     for (var i = 0; i < rows.length; i++) {
     	drawdata.addRow(new Array(rows[i].xValue, rows[i].yValue1, rows[i].yValue2));
     }
     var options = {
             hAxis: {title: horizontalHeader, titleTextStyle: {color: 'green'},
         		textStyle: {fontSize: 9.5}},
             legend: {position: 'top'},
             colors: ['green', 'orange'],
             vAxis: {minValue: 0, baseline: 0}
           };
     var columnchart = new google.visualization.ColumnChart(document.getElementById('diagram'));
     columnchart.draw(drawdata, options);
};