(function() {
  'use strict';

  angular
  .module('StatisticsApp')
  .factory('ObjectHelper', objectHelper);

  /** @ngInject */
  function objectHelper() {

    return {
      isEmpty: _isEmpty,
      isNotEmpty: _isNotEmpty
    };

    function _isEmpty(val) {
      return !(angular.isDefined(val) && val !== null);
    }

    function _isNotEmpty(val) {
      return !_isEmpty(val);
    }
  }
})();
