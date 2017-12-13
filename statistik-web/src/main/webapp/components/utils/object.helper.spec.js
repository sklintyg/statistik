(function() {
  'use strict';

  describe('helper ObjectHelper', function() {
    var ObjectHelper;

    beforeEach(module('StatisticsApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function(_ObjectHelper_) {
      ObjectHelper = _ObjectHelper_;
    }));

    describe('isNotEmpty', function() {
      it('is empty', function() {
        expect(ObjectHelper.isNotEmpty(null)).toBeFalsy();
        expect(ObjectHelper.isNotEmpty(undefined)).toBeFalsy();
      });

      it('is not empty', function() {
        expect(ObjectHelper.isNotEmpty('')).toBeTruthy();
        expect(ObjectHelper.isNotEmpty(true)).toBeTruthy();
      });
    });

    describe('isEmpty', function() {
      it('is empty', function() {
        expect(ObjectHelper.isEmpty(null)).toBeTruthy();
        expect(ObjectHelper.isEmpty(undefined)).toBeTruthy();
      });

      it('is not empty', function() {
        expect(ObjectHelper.isEmpty('')).toBeFalsy();
        expect(ObjectHelper.isEmpty(true)).toBeFalsy();
      });
    });
  });
})();
