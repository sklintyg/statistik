$(function() {
	$('#statistics-form')[0].callback = {
		url : '/statistics/monthwise', 
		func: function(data) {
				columnchart(data, 'Månad');
				table(data, 'Månad');
		}
	};
});