$(function() {
	
	var ajax = new INERA.Ajax();
	
	$('#statistics-form').submit(function(e) {
		e.preventDefault();
		
		var criterias = new Object();
        
		criterias.startDate = $('input[name="fromDate"]').val();
		criterias.endDate = $('input[name="toDate"]').val();
		
		criterias.basedOnExamination = $('input[name="basedOnExamination"]:checked').val() == 1 ? true : false;
		criterias.basedOnTelephoneContact = $('input[name="basedOnTelephoneContact"]:checked').val() == 1 ? true : false;
		
		ajax.post('/statistics/careunit', criterias, function(data) {
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
	        var y1_total = 0;
	        var y2_total = 0;

	        var table = $('#resultTable');
	        table.empty();
	        table.append('<tr> ' +
    			'<th span="15">Enhet </th>' +
    			'<th span="15">Antal Intyg/Män </th>' +
    			'<th span="15">Antal Intyg/Kvinnor </th>' +
    			'<th span="15">Antal Intyg </th>' +
    			'</tr>');
	        for (var i = 0; i < rows.length; i++) {
	        	y1_total = y1_total + rows[i].yValue1;
	        	y2_total = y2_total + rows[i].yValue2;
	        	table.append('<tr> ' +
	        			'<td span="15">' + rows[i].xValue + '</td>' +
	        			'<td span="15">' + rows[i].yValue1 + ' </td>' +
	        			'<td span="15">' + rows[i].yValue2 + ' </td>' +
	        			'<td span="15">' + (rows[i].yValue1 + rows[i].yValue2) + ' </td>' +
	        			'</tr>');
	        }
	        table.append('<tr> ' +
        			'<th span="15">Alla </th>' +
        			'<td span="15">' + y1_total + ' </td>' +
        			'<td span="15">' + y2_total + ' </td>' +
        			'<td span="15">' + (y1_total + y2_total) + ' </td>' +
        			'</tr>');
			
			INERA.log("Success");
		});
	});
});
