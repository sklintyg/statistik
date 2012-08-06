if (typeof console === "undefined") {
	console = {};
}

if (typeof console.log === "undefined") {
	console.log = function() {};
}

INERA = {
	getCtx : function() {
		return GLOB_CTX_PATH;
	}, 
	
	log : function(msg) {
		console.log(msg);
	}
};

INERA.Ajax = function() {
	
	var _contextPath = INERA.getCtx();
	var _basePath = '/api';
	
	var _dataType = 'json';
	var _contentType = 'application/json'
	
	//var _pm = new NC.PageMessages();
		
	var _defaultSuccess = function(data, show, callback) {
		/*
		 * Show results
		 */
		if (show) {
			//_pm.processServiceResult(data);
		}
		
		/*
		 * Execute callback 
		 */
		if (data.success && callback !== undefined) {
			callback(data);
		} else {
			INERA.log("Call was not successful. Data success: " + data.success + " Callback: " + callback);
		}
	};
	
	var _showMessages = function(show) {
		var showMessages;
		if (show === undefined || show == null || show == false) {
			showMessages = false;
		} else {
			showMessages = true;
		}
		
		return showMessages;
	};
	
	var _getDefaultOpts = function(url, callback, displayMessages) {
		INERA.log("==== AJAX GET " + url + " ====");
		return {
			url : url,
			cache : false,
			success : function(data) {
				INERA.log('....success');
				_defaultSuccess(data, _showMessages(displayMessages), callback);
			}
		}
	};
	
	var _getDefaultPostOpts = function(url, callback, displayMessages) {
		INERA.log("==== AJAX POST " + url + " ====");
		
		return {
			url : url,
			type : 'post',
			success : function(data) {
				_defaultSuccess(data, _showMessages(displayMessages), callback);
			}
		}
	}

	var _getDefaultDeleteOpts = function(url, callback, displayMessages) {
		INERA.log("==== AJAX POST " + url + " ====");
		
		return {
			url : url,
			type : 'delete',
			success : function(data) {
				_defaultSuccess(data, _showMessages(displayMessages), callback);
			}
		}
	}

	public = {
			/**
			 * Execute a GET request
			 */
			get : function(url, callback, displayMessages) {
				var call = _contextPath + _basePath + url;
				$.ajax(_getDefaultOpts(call, callback, displayMessages));
			},
			
			getWithParams : function(url, data, callback, displayMessages) {
				var opts = _getDefaultOpts(_contextPath + _basePath + url, callback, displayMessages);
				opts.data = data;
				
				$.ajax(opts);
			},
			
			getWithParamsSynchronous : function(url, data, callback, displayMessages) {
				var opts = _getDefaultOpts(_contextPath + _basePath + url, callback, displayMessages);
				opts.data = data;
				opts.async = false;
				
				$.ajax(opts);
			},
			
			post : function(url, data, callback, displayMessages) {
				var call = _contextPath + _basePath + url;
				var opts = _getDefaultPostOpts(call, callback, displayMessages);
				opts.contentType = _contentType;
				opts.dataType = _dataType;
				if (data != null) {
					opts.data = JSON.stringify(data);
				}
				
				$.ajax(opts);
			},
			
			postWithParams : function(url, data, callback, displayMessage) {
				var call = _contextPath + _basePath + url;
				var opts = _getDefaultPostOpts(call, callback, displayMessage);
				opts.dataType = _dataType;
				if (data != null) {
					opts.data = data;
				}
				
				$.ajax(opts);
			},
			
			postSynchronous : function(url, data, callback, displayMessages) {
				var call = _contextPath + _basePath + url;
				var opts = _getDefaultPostOpts(call, callback, displayMessages);
				opts.async = false;
				opts.contentType = _contentType;
				opts.dataType = _dataType;
				if (data != null) {
					opts.data = JSON.stringify(data);
				}
				
				$.ajax(opts);
			},
			
			deleteWithoutParams : function(url, callback, displayMessages) {
				var call = _contextPath + _basePath + url;
				var opts = _getDefaultDeleteOpts(call, callback, displayMessages);
				$.ajax(opts);
			}
	};
	
	return public;
};

INERA.Cert = function() {
	
	var public = {
			list : function(callback) {
				new INERA.Ajax().get('/certificate/list', callback)
			},
			
			remove : function(id, callback) {
				new INERA.Ajax().deleteWithoutParams('/certificate/' + id, callback)
			},

			get : function(id, callback) {
				new INERA.Ajax().get('/certificate/' + id, callback)
			},

			restore : function(id, callback) {
				new INERA.Ajax().post('/certificate/' + id + '/restore', null, callback)
			},
			
			send : function(id, destination, callback) {
				new INERA.Ajax().postSynchronous('/certificate/' + id + '/' + destination + '/send', null, callback);
			}
	};
	
	return public;
};

$(document).ready(function() {
	/*
	 * Default to swedish
	 */
//	$.datepicker.setDefaults( $.datepicker.regional[ 'sv' ] );

	/*
	 * Process all datefields, add datepicker 
	 * as well as date icon
	 */
	$('.monthField').each(function(i, v) {
//		$(v).datepicker($.datepicker.regional['sv']);
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
			initStatus: 'Välj månand',
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
		INERA.log('Binding datepicker...');
//		$(v).datepicker({
//			dateFormat : 'yy-mm-dd',
//			firstDay : 1
//		});
//		
//		$(v).siblings('span').css('cursor', 'pointer').click(function(e) {
//			$(v).datepicker('show');
//		});
	});
});
