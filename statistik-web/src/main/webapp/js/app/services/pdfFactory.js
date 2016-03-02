/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
'use strict';
angular.module('StatisticsApp')
    .factory('pdfFactory', ['$window', function($window) {

        function _create(header, subHeader, table, images) {
            var content = [];

            content.push({ text: header, style: 'header' });
            content.push({ text: subHeader, style: 'subheader' });

            content.push(_getImages(images));

            content.push(_getTable(table.header, table.data));

            var docDefinition = { content: content,
                styles: {
                    header: {
                        fontSize: 18,
                        bold: true,
                        margin: [0, 0, 0, 10]
                    },
                    subheader: {
                        fontSize: 14,
                        bold: true,
                        margin: [0, 10, 0, 5]
                    },
                    table: {
                        margin: [0, 5, 0, 15]
                    }
                }
            };

            pdfMake.createPdf(docDefinition).getBase64(function(result) {

                var inputs = '<input type="hidden" name="pdf" value="'+result+'">';
                inputs += '<input type="hidden" name="name" value="'+header+'.pdf">';

                $window.jQuery('<form action="/api/pdf/create" target="_blank" method="post">'+inputs+'</form>')
                    .appendTo('body').submit().remove();
            });
        }

        function _getImages(images) {
            var content = [];
            var canvasElement = $window.jQuery('<canvas id="canvas"></canvas>').appendTo('body');
            canvasElement.width(800);

            angular.forEach(images, function(chart) {
                chart.options.legend.enabled = true;
                chart.redraw();
                var canvas = document.getElementById("canvas");
                canvg(canvas, chart.getSVG());

                var image = canvas.toDataURL("image/png");;

                content.push({
                    image: image,
                    width: 450
                });
            });

            canvasElement.remove();

            return content;
        }

        function _getTable(headerRows, data) {
            var body = [];
            var numberOfHeaderRows = headerRows.length;

            angular.forEach(headerRows, function(headerRow) {
                var header = [];

                angular.forEach(headerRow, function(item) {
                    header.push({
                        text: item.text,
                        style: 'tableHeader'
                    });
                });

                body.push(header);
            });

            angular.forEach(data, function(row) {
                var rowData = [row.name];

                angular.forEach(row.data, function(item) {
                    rowData.push(item+'');
                });

                body.push(rowData);
            });

            return [{
                    table: {
                        headerRows: numberOfHeaderRows,
                        body: body
                    },
                    layout: 'lightHorizontalLines',
                    style: 'table'
                }];
        }

        //The public api of this factory
        return {
            create: _create
        };
    }]);
