'use strict';

var ControllerCommons = new function(){

 this.addColor = function(rawData){
     var color = ["#fbb10c", "#2ca2c6", "#B0B0B0", "#12BC3A", "#9c734d", "#D35ABB", "#4A4A4A"];
     for (var i = 0; i < rawData.length; i++) {
         rawData[i].color = color[i];
     }
     return rawData;
 };
 
 this.showHideDataTableDefault = "Dölj datatabell";
 this.toggleTableVisibilityGeneric = function(event, $scope){
     var elem = $(event.target);
     var accordionGroup = $(elem.parents('.accordion-group')[0]);
     var accordionBody = $(accordionGroup.children('.accordion-body'));
     var wasTableVisible = accordionBody.hasClass("in");
     $scope.showHideDataTable = wasTableVisible ? "Visa datatabell" : "Dölj datatabell"; 
 };
 
 this.exportTableDataGeneric = function() {
     var dt = $('#datatable');
     var csvData = table2CSV(dt);
     $.generateFile({
         filename : 'export.csv',
         content : csvData,
         script : 'fileDownload.jsp'
     });
 };
 
 this.getChartCategories = function(ajaxResult) {
     return ajaxResult.rows.map(function(e) {
         return e.name;
     });
 };

 this.getChartSeries = function(ajaxResult) {
     var dataSeries = [];
     var length = ajaxResult.headers[0].length;
     for ( var i = 0; i < length; i++) {
         var ds = {};
         ds.name = ajaxResult.headers[0][i].text;
         ds.data = [];
         dataSeries.push(ds);
     }

     var length = ajaxResult.rows.length;
     for ( var i = 0; i < length; i++) {
         var rowdata = ajaxResult.rows[i].data;
         var rowdatalength = rowdata.length;
         for ( var c = 0; c < rowdatalength; c++) {
             dataSeries[c].data.push(rowdata[c]);
         }
     }
     return dataSeries;
 };

 this.htmlsafe = function(string) {
     return string.replace(/&/g,'&amp;').replace(/</g,'&lt;');
 }

};
