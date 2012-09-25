$.fn.serializeObject = function()
{
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

$(function() {
	
	var ajax = new INERA.Ajax();

	$('#statistics-form').submit(function(e) {
		e.preventDefault();
		
		var criterias = $('#statistics-form').serializeObject();
		ajax.post('/statistics/age', criterias, function(data) {
			columnchart(data, 'Ålder');
			table(data, 'Ålder');
			INERA.log("Success");
		});
	});
});
