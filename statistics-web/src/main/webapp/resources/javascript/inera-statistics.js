/*
 * Copyright (C) 2012 Callista Enterprise AB <info@callistaenterprise.se>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
