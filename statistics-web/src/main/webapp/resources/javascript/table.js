function table(data, horizontalHeader) {
    var rows = data.data.matches;
    var y1_total = 0;
    var y2_total = 0;

    var body = $('#resultTable tbody');
    body.empty();
    for (var i = 0; i < rows.length; i++) {
    	y1_total = y1_total + rows[i].yValue1;
    	y2_total = y2_total + rows[i].yValue2;
    	body.append('<tr> ' +
    			'<td span="15">' + rows[i].xValue + '</td>' +
    			'<td span="15">' + rows[i].yValue1 + ' </td>' +
    			'<td span="15">' + rows[i].yValue2 + ' </td>' +
    			'<td span="15">' + (rows[i].yValue1 + rows[i].yValue2) + ' </td>' +
    			'</tr>');
    }
    var foot = $('#resultTable tfoot');
    foot.empty();
    foot.append('<tr> ' +
			'<th span="15">Totalt</th>' +
			'<td span="15">' + y1_total + ' </td>' +
			'<td span="15">' + y2_total + ' </td>' +
			'<td span="15">' + (y1_total + y2_total) + ' </td>' +
			'</tr>');
	
};