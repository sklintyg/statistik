$(function() {
	$('#statistics-form')[0].callback = {
		url : '/statistics/careunit', 
		func: function(data) {
				columnchart(data, 'Enhet');
				table(data, 'Enhet');
		}
	};
});
