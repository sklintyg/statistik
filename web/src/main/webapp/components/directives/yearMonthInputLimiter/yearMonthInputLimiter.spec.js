/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

describe('Tests for directive yearMonthInputLimiter', function() {
  'use strict';

  var scope, element, ngModel;
  var ORIGINAL_VALUE = '2016-01';

  beforeEach(module('StatisticsApp'));
  beforeEach(module('htmlTemplates'));

  beforeEach(inject(function($rootScope, $compile) {

    scope = $rootScope.$new();
    scope.model = {
      dateValue: ORIGINAL_VALUE
    };

    element = $compile('<input type="text" ng-model="model.dateValue" year-month-input-limiter />')(scope);

    ngModel = element.controller('ngModel');

    scope.$digest();

  }));

  describe('verify reverting behaviour', function() {

    it('should revert to original value for invalid values', function() {
      element.val('feb').trigger('input');
      scope.$digest();
      expect(ngModel.$viewValue).toEqual(ORIGINAL_VALUE);

      element.val('2016-15').trigger('input');
      scope.$digest();
      expect(ngModel.$viewValue).toEqual(ORIGINAL_VALUE);

      element.val('2016-125').trigger('input');
      scope.$digest();
      expect(ngModel.$viewValue).toEqual(ORIGINAL_VALUE);
    });

  });

  describe('verify accepting behaviour', function() {

    it('should keep new value when inputting new valid partial value', function() {
      element.val('20').trigger('input');
      scope.$digest();
      expect(ngModel.$viewValue).toEqual('20');

    });

    it('should keep new value when inputting new valid full value', function() {
      element.val('2013-09').trigger('input');
      scope.$digest();
      expect(ngModel.$viewValue).toEqual('2013-09');

    });

  });

  describe('verify expand yyyyd behaviour', function() {

    it('should insert - when entering yyyyd', function() {
      element.val('20130').trigger('input');
      scope.$digest();
      expect(ngModel.$viewValue).toEqual('2013-0');

    });

    it('should insert - when entering yyyydd', function() {
      element.val('201309').trigger('input');
      scope.$digest();
      expect(ngModel.$viewValue).toEqual('2013-09');

    });

  });

});
