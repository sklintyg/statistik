'use strict';

var ControllerCommons = new function(){

 this.addColor = function(rawData){
     var color = ["#fbb10c", "#2ca2c6", "#B0B0B0", "#12BC3A", "#9c734d", "#D35ABB", "#4A4A4A"];
     var maleColor = ["#008391", "#00CDE3"]
     var femaleColor = ["#EA8025", "#FFC18C"]
     var colorSelector = 0;
     var maleColorSelector = 0;
     var femaleColorSelector = 0;
     for (var i = 0; i < rawData.length; i++) {
         if (rawData[i].sex === "Male") {
             rawData[i].color = maleColor[maleColorSelector++];
         } else if (rawData[i].sex === "Female") {
             rawData[i].color = femaleColor[femaleColorSelector++];
         } else {
             rawData[i].color = color[colorSelector++];
         }
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
 
 this.htmlsafe = function(string) {
     return string.replace(/&/g,'&amp;').replace(/</g,'&lt;');
 }

};
