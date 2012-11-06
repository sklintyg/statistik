/*
 * Copyright (C) 2012 Inera AB (http://www.inera.se)
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
function table(data, horizontalHeader) {
    var rows = data.data.matches;
    var y1_total = 0;
    var y2_total = 0;

    var body = $('#resultTable tbody');
    body.empty();
    for (var i = 0; i < rows.length; i++) {
    	y1_total = y1_total + rows[i].yValue1;
    	y2_total = y2_total + rows[i].yValue2;
    	body.append('<tr> ' +
    			'<td span="15">' + rows[i].xValue + '</td>' +
    			'<td span="15">' + rows[i].yValue1 + ' </td>' +
    			'<td span="15">' + rows[i].yValue2 + ' </td>' +
    			'<td span="15">' + (rows[i].yValue1 + rows[i].yValue2) + ' </td>' +
    			'</tr>');
    }
    var foot = $('#resultTable tfoot');
    foot.empty();
    foot.append('<tr> ' +
			'<th span="15">Totalt</th>' +
			'<td span="15">' + y1_total + ' </td>' +
			'<td span="15">' + y2_total + ' </td>' +
			'<td span="15">' + (y1_total + y2_total) + ' </td>' +
			'</tr>');
	
};