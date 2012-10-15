$(function() {
	$('#statistics-form')[0].callback = {
		url : '/statistics/sicknessgroups', 
		func: function(data) {
				columnchart(data, 'Diagnosgrupper');
				table(data, 'Diagnosgrupper');
		}
	};
});
