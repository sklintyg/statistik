$(document).ready(function() {
	options = {
		    pattern: 'mmm yyyy', // Default is 'mm/yyyy' and separator char is not mandatory
		    selectedYear: 2012,
		    startYear: 2010,
		    finalYear: 2014,
		    monthNames: ['Jan', 'Feb', 'Mar', 'Apr', 'Maj', 'Jun', 'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Dec']
		};

	$('#fromDate').monthpicker(options);
	$('#toDate').monthpicker(options);
});

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
	$('#statistics-form').submit(function(e) {
		var ajax = new INERA.Ajax();
		$('.loader').show();
		e.preventDefault();
		var form = $('#statistics-form');
		var criterias = form.serializeObject();
		ajax.post(form[0].callback.url, criterias, function(data) {
			form[0].callback.func(data);
			INERA.log("Success");
			$('.loader').hide();
		});
	});
});
