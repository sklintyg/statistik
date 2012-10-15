$(function() {
	$('#statistics-form')[0].callback = {
		url : '/statistics/age', 
		func: function(data) {
				columnchart(data, 'Ålder');
				table(data, 'Ålder');
		}
	};
});
