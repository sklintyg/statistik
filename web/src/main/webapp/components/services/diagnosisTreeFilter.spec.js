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

describe('Test of diagnosisTreeFilter', function() {
  'use strict';

  var diagnosisTreeFilter;

  beforeEach(module('StatisticsApp'));

  beforeEach(inject(function(_diagnosisTreeFilter_) {
    diagnosisTreeFilter = _diagnosisTreeFilter_;
  }));

  describe('selectAll', function() {
    it('one level', function() {
      var items = {
        allSelected: false,
        someSelected: false
      };

      var expectedItems = {
        allSelected: true,
        someSelected: false
      };

      diagnosisTreeFilter.selectAll(items);

      expect(items).toEqual(expectedItems);
    });

    it('one level some selected', function() {
      var items = {
        allSelected: false,
        someSelected: true
      };

      var expectedItems = {
        allSelected: true,
        someSelected: false
      };

      diagnosisTreeFilter.selectAll(items);

      expect(items).toEqual(expectedItems);
    });

    it('two levels', function() {
      var items = {
        allSelected: false,
        someSelected: false,
        subs: [{
          allSelected: false,
          someSelected: false
        }]
      };

      var expectedItems = {
        allSelected: true,
        someSelected: false,
        subs: [{
          allSelected: true,
          someSelected: false
        }]
      };

      diagnosisTreeFilter.selectAll(items);

      expect(items).toEqual(expectedItems);
    });

    it('two levels some selected', function() {
      var items = {
        allSelected: false,
        someSelected: true,
        subs: [{
          allSelected: true,
          someSelected: false
        }]
      };

      var expectedItems = {
        allSelected: true,
        someSelected: false,
        subs: [{
          allSelected: true,
          someSelected: false
        }]
      };

      diagnosisTreeFilter.selectAll(items);

      expect(items).toEqual(expectedItems);
    });
  });

  describe('deselectAll', function() {
    it('one level', function() {
      var items = {
        allSelected: true,
        someSelected: false
      };

      var expectedItems = {
        allSelected: false,
        someSelected: false
      };

      diagnosisTreeFilter.deselectAll(items);

      expect(items).toEqual(expectedItems);
    });

    it('one level some selected', function() {
      var items = {
        allSelected: false,
        someSelected: true
      };

      var expectedItems = {
        allSelected: false,
        someSelected: false
      };

      diagnosisTreeFilter.deselectAll(items);

      expect(items).toEqual(expectedItems);
    });

    it('two levels', function() {
      var items = {
        allSelected: true,
        someSelected: false,
        subs: [{
          allSelected: true,
          someSelected: false
        }]
      };

      var expectedItems = {
        allSelected: false,
        someSelected: false,
        subs: [{
          allSelected: false,
          someSelected: false
        }]
      };

      diagnosisTreeFilter.deselectAll(items);

      expect(items).toEqual(expectedItems);
    });

    it('two levels some selected', function() {
      var items = {
        allSelected: false,
        someSelected: true,
        subs: [{
          allSelected: true,
          someSelected: false
        }]
      };

      var expectedItems = {
        allSelected: false,
        someSelected: false,
        subs: [{
          allSelected: false,
          someSelected: false
        }]
      };

      diagnosisTreeFilter.deselectAll(items);

      expect(items).toEqual(expectedItems);
    });
  });

  it('resetSelections', function() {
    var items = {
      allSelected: false,
      someSelected: true,
      subs: [{
        allSelected: true,
        someSelected: false
      }]
    };

    var expectedItems = {
      allSelected: false,
      someSelected: false,
      subs: [{
        allSelected: false,
        someSelected: false
      }]
    };

    diagnosisTreeFilter.diagnosisOptionsTree = items;

    diagnosisTreeFilter.resetSelections();

    expect(items).toEqual(expectedItems);
  });

  describe('selectByAttribute', function() {

    it('one attribute', function() {
      var expectedItems = {
        allSelected: false,
        someSelected: false,
        subs: [{
          allSelected: true,
          someSelected: false,
          attribute: '1'
        }, {
          allSelected: false,
          someSelected: false,
          attribute: '2'
        }, {
          allSelected: false,
          someSelected: false,
          attribute: '2'
        }]
      };

      var items = {
        allSelected: false,
        someSelected: true,
        subs: [{
          allSelected: false,
          someSelected: true,
          attribute: '1'
        }, {
          allSelected: false,
          someSelected: false,
          attribute: '2'
        }, {
          allSelected: true,
          someSelected: false,
          attribute: '2'
        }]
      };
      var ids = ['1'];
      var attribute = 'attribute';

      diagnosisTreeFilter.selectByAttribute(items, ids, attribute);

      expect(items).toEqual(expectedItems);
    });
  });

  describe('setupDiagnosisTreeForSelectionModal', function() {

    var diagnosis = {};

    beforeEach(function() {
      diagnosis = [{
        id: '1',
        name: 'Kapitel',
        subItems: [{
          id: '2',
          name: 'Avsnitt',
          subItems: [{
            id: '3',
            name: 'Kategori',
            subItems: [{
              id: '4',
              name: 'code'
            }]
          }]
        }]
      }];
    });

  });

  describe('getSelectedLeaves', function() {

    it('none selected one level', function() {
      var node = {
        allSelected: false,
        someSelected: false
      };

      var selected = diagnosisTreeFilter.getSelectedLeaves(node);

      expect(selected).toEqual([]);
    });

    it('none selected tow levels', function() {
      var node = {
        allSelected: false,
        someSelected: false,

        subs: [{
          allSelected: false,
          someSelected: false
        }, {
          allSelected: false,
          someSelected: false
        }]
      };

      var selected = diagnosisTreeFilter.getSelectedLeaves(node);

      expect(selected).toEqual([]);
    });

    it('selected one level', function() {
      var node = {
        allSelected: true,
        someSelected: false
      };

      var selected = diagnosisTreeFilter.getSelectedLeaves(node);

      expect(selected).toEqual([node]);
    });

    it('selected two levels', function() {

      var node = {
        allSelected: true,
        someSelected: false,
        name: 'level1',

        subs: [{
          allSelected: true,
          someSelected: false,
          name: 'level2'
        }, {
          allSelected: false,
          someSelected: false,
          name: 'level2.1'
        }]
      };

      var expected = [{
        allSelected: true,
        someSelected: false,
        name: 'level2'
      }];

      var selected = diagnosisTreeFilter.getSelectedLeaves(node);

      expect(selected).toEqual(expected);
    });
  });

  it('convertBetweenLevels', function() {
    expect(diagnosisTreeFilter.urlLevelToCorrectLevel(diagnosisTreeFilter.levelToUrlLevel(1))).toBe(1);
    expect(diagnosisTreeFilter.urlLevelToCorrectLevel(diagnosisTreeFilter.levelToUrlLevel(2))).toBe(2);
    expect(diagnosisTreeFilter.urlLevelToCorrectLevel(diagnosisTreeFilter.levelToUrlLevel(3))).toBe(3);
    expect(diagnosisTreeFilter.urlLevelToCorrectLevel(diagnosisTreeFilter.levelToUrlLevel(4))).toBe(4);
  });

  describe('getSelectedDiagnosis', function() {

    it('Null tree', function() {
      diagnosisTreeFilter.diagnosisOptionsTree = null;

      var result = diagnosisTreeFilter.getSelectedDiagnosis();

      expect(result).toBeNull();
    });

    it('One sublevel selected', function() {
      var node = {
        allSelected: false,
        someSelected: true,
        name: 'level1',
        numericalId: 1,

        subs: [{
          allSelected: true,
          someSelected: false,
          name: 'level2',
          numericalId: 2
        }, {
          allSelected: false,
          someSelected: false,
          name: 'level2.1',
          numericalId: 3
        }]
      };

      diagnosisTreeFilter.diagnosisOptionsTree = node;

      var result = diagnosisTreeFilter.getSelectedDiagnosis();

      expect(result).toEqual([2]);
    });
  });

  it('setPreselectedFilter', function() {
    var node = {
      allSelected: false,
      someSelected: true,
      name: 'level1',
      numericalId: 1,

      subs: [{
        allSelected: true,
        someSelected: false,
        name: 'level2',
        numericalId: 2
      }]
    };

    var expected = {
      allSelected: false,
      someSelected: false,
      name: 'level1',
      numericalId: 1,

      subs: [{
        allSelected: false,
        someSelected: false,
        name: 'level2',
        numericalId: 2
      }]
    };

    diagnosisTreeFilter.diagnosisOptionsTree = node;

    diagnosisTreeFilter.setPreselectedFilter();

    expect(node).toEqual(expected);
  });

  describe('byLevel', function() {

    var node4 = {
      allSelected: true,
      someSelected: true,
      name: 'level4'
    };

    var node3 = {
      allSelected: true,
      someSelected: true,
      name: 'level3',

      subs: [node4]
    };

    var node2 = {
      allSelected: true,
      someSelected: true,
      name: 'level2',

      subs: [node3]
    };

    var node1 = {
      allSelected: true,
      someSelected: true,
      name: 'level1',

      subs: [node2]
    };

    var node = {
      allSelected: true,
      someSelected: true,
      name: 'level0',

      subs: [node1]
    };

    it('level 1', function() {
      diagnosisTreeFilter.level = 1;

      var result = diagnosisTreeFilter.getSelectedLeaves(node, 1);

      expect(result).toEqual([node1]);
    });

    it('level 2', function() {
      diagnosisTreeFilter.level = 2;

      var result = diagnosisTreeFilter.getSelectedLeaves(node, 1);

      expect(result).toEqual([node2]);
    });

    it('level 3', function() {
      diagnosisTreeFilter.level = 3;

      var result = diagnosisTreeFilter.getSelectedLeaves(node, 1);

      expect(result).toEqual([node3]);
    });

    it('level 4', function() {
      diagnosisTreeFilter.level = 4;

      var result = diagnosisTreeFilter.getSelectedLeaves(node, 1);

      expect(result).toEqual([node4]);
    });
  });

  describe('setup', function() {
    var tree = {
      allSelected: false,
      someSelected: true,
      name: 'level0',
      numericalId: 1,

      subs: [{
        allSelected: true,
        someSelected: false,
        name: 'level1',
        numericalId: 2
      }]
    };

    it('Missing diagnos', function() {
      var diagnosHash = '-';
      diagnosisTreeFilter.diagnosisOptionsTree = tree;

      diagnosisTreeFilter.setup(diagnosHash, 1);
    });

    it('With diagnos', function() {
      var diagnosHash = '123';
      diagnosisTreeFilter.diagnosisOptionsTree = tree;

      diagnosisTreeFilter.setup(diagnosHash, 1);
    });

  });
});
