$(document).ready(function() {
	/*
	 * Process all datefields, add datepicker 
	 * as well as date icon
	 */
	$('.monthField').each(function(i, v) {
		$(v).datepicker( {
			closeText: 'Stäng',
	        prevText: '&laquo;Förra',
			nextText: 'Nästa&raquo;',
			currentText: 'Idag',
	        monthNames: ['Januari','Februari','Mars','April','Maj','Juni',
	        'Juli','Augusti','September','Oktober','November','December'],
	        monthNamesShort: ['Jan','Feb','Mar','Apr','Maj','Jun',
	        'Jul','Aug','Sep','Okt','Nov','Dec'],
			changeMonth: true,
	        changeYear: true,
	        showButtonPanel: true,
	        dateFormat: 'MM yy',
			initStatus: 'Välj månad',
	        onClose: function(dateText, inst) { 
	            var month = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
	            var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
	            $(this).datepicker('setDate', new Date(year, month, 1));
	        },
	        beforeShow : function(input, inst) {
	            if ((datestr = $(this).val()).length > 0) {
	                year = datestr.substring(datestr.length-4, datestr.length);
	                month = jQuery.inArray(datestr.substring(0, datestr.length-5), $(this).datepicker('option', 'monthNames'));
	                $(this).datepicker('option', 'defaultDate', new Date(year, month, 1));
	                $(this).datepicker('setDate', new Date(year, month, 1));
	            }
	        }
	    });
		var now = new Date();
		$(v).datepicker('setDate', new Date(now.getFullYear(), now.getMonth(), 1));
		INERA.log('Binding datepicker...');
	});
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
