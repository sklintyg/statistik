(function($) {

	// Creating a jQuery plugin:

	$.generateFile = function(options) {

		options = options || {};

		if (!options.script || !options.filename || !options.content) {
			throw new Error("Please enter all the required config options!");
		}

		// Creating a 1 by 1 px invisible iframe:

		var iframe = $('<iframe>', {
			width : 1,
			height : 1,
			frameborder : 0,
			css : {
				display : 'none'
			}
		}).appendTo('body');

		var formHTML = '<form action="" method="post">'
				+ '<input type="hidden" name="filename" />'
				+ '<input type="hidden" name="content" />' + '</form>';

		// Giving IE a chance to build the DOM in
		// the iframe with a short timeout:

		setTimeout(function() {

			// The body element of the iframe document:

			var body = (iframe.prop('contentDocument') !== undefined) ? iframe
					.prop('contentDocument').body
					: iframe.prop('document').body; // IE

			body = $(body);

			// Adding the form to the body:
			body.html(formHTML);

			var form = body.find('form');

			form.attr('action', options.script);
			form.find('input[name=filename]').val(options.filename);
			form.find('input[name=content]').val(options.content);

			// Submitting the form to download.php. This will
			// cause the file download dialog box to appear.

			form.submit();
		}, 50);
	};

})(jQuery);

jQuery.fn.table2CSV = function(options) {
	var options = jQuery.extend({
		separator : ',',
		header : [],
		delivery : 'popup' // popup, value
	}, options);

	var csvData = [];
	var headerArr = [];
	var el = this;

	// header
	var numCols = options.header.length;
	var tmpRow = []; // construct header avalible array

	if (numCols > 0) {
		for ( var i = 0; i < numCols; i++) {
			tmpRow[tmpRow.length] = formatData(options.header[i]);
		}
	} else {
		$(el).find('th').each(function() {
			tmpRow[tmpRow.length] = formatData($(this).html());
		});
	}

	row2CSV(tmpRow);

	// actual data
	$(el).find('tr').each(function() {
		var tmpRow = [];
		$(this).find('td').each(function() {
			tmpRow[tmpRow.length] = formatData($(this).html());
		});
		row2CSV(tmpRow);
	});
	if (options.delivery == 'popup') {
		var mydata = csvData.join('\n');
		return popup(mydata);
	} else {
		var mydata = csvData.join('\n');
		return mydata;
	}

	function row2CSV(tmpRow) {
		var tmp = tmpRow.join('') // to remove any blank rows
		// alert(tmp);
		if (tmpRow.length > 0 && tmp != '') {
			var mystr = tmpRow.join(options.separator);
			csvData[csvData.length] = mystr;
		}
	}
	function formatData(input) {
		// return input;
		// replace " with “
		var regexp = new RegExp(/["]/g);
		var output = input.replace(regexp, "'");
		// HTML
		var regexp = new RegExp(/\<[^\<]+\>/g);
		var output = output.replace(regexp, "");
		if (output == "")
			return '';
		return '"' + output + '"';
	}
	function popup(data) {

		$.generateFile({
			filename : 'export.csv',
			content : data,
			script : 'fileDownload.jsp'
		});

		e.preventDefault();

		return true;
	}
};