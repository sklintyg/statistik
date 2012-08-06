// $(function() {
//	 $('.dateField').each(function(i, v) {
//		$(v).datepicker( {
//			changeMonth: true,
//	        changeYear: true,
//	        showButtonPanel: true,
//	        dateFormat: 'MM yy',
//	        onClose: function(dateText, inst) { 
//	            var month = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
//	            var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
//	            $(this).datepicker('setDate', new Date(year, month, 1));
//	        },
//	        beforeShow : function(input, inst) {
//	            if ((datestr = $(this).val()).length > 0) {
//	                year = datestr.substring(datestr.length-4, datestr.length);
//	                month = jQuery.inArray(datestr.substring(0, datestr.length-5), $(this).datepicker('option', 'monthNames'));
//	                $(this).datepicker('option', 'defaultDate', new Date(year, month, 1));
//	                $(this).datepicker('setDate', new Date(year, month, 1));
//	            }
//	        }
//		});
//    });
//})​;