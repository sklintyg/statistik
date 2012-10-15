$(function() {
	$('#statistics-form')[0].callback = {
		url : '/statistics/duration', 
		func: function(data) {
				columnchart(data, 'Dagar');
				table(data, 'Dagar');
		}
	};
});
