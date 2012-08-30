$(function() {	
	var ajax = new INERA.Ajax();
	
	$('#statistics-form').submit(function(e) {
		e.preventDefault();
		
		var criterias = new Object();
        
		criterias.startDate = $('input[name="fromDate"]').val();
		criterias.endDate = $('input[name="toDate"]').val();
		
		criterias.basedOnExamination = $('input[name="basedOnExamination"]:checked').val() == 1 ? true : false;
		criterias.basedOnTelephoneContact = $('input[name="basedOnTelephoneContact"]:checked').val() == 1 ? true : false;
		
		ajax.post('/statistics/sicknessgroups', criterias, function(data) {
			columnchart(data, 'Sjukdomsgrupper');
			table(data, 'Sjukdomsgrupper');
			
			INERA.log("Success");
		});
	});
});