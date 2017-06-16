/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
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

    describe('selectAll', function () {
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

    describe('deselectAll', function () {
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

        it('hideCodeLevel', function() {
            var expected = [{
                id: '1',
                typ: 'kapitel',
                name: '1 Kapitel',
                subs: [{
                    id: '2',
                    typ: 'avsnitt',
                    name: '2 Avsnitt',
                    subs: [{
                        id: '3',
                        name: '3 Kategori',
                        subItems: [{
                            id: '4',
                            name: 'code'
                        }]
                    }],
                    subItems: [{
                        id: '3',
                        name: '3 Kategori',
                        subItems: [{
                            id: '4',
                            name: 'code'
                        }]
                    }]
                }],
                subItems: [{
                    id: '2',
                    typ: 'avsnitt',
                    name: '2 Avsnitt',
                    subs: [{
                        id: '3',
                        name: '3 Kategori',
                        subItems: [{
                            id: '4',
                            name: 'code'
                        }]
                    }],
                    subItems: [{
                        id: '3',
                        name: '3 Kategori',
                        subItems: [{
                            id: '4',
                            name: 'code'
                        }]
                    }]
                }]
            }];

            diagnosisTreeFilter.setupDiagnosisTreeForSelectionModal(diagnosis, false);

            expect(diagnosis).toEqual(expected);
        });

        it('showCodeLevel', function() {
            var expected = [{
                id: '1',
                typ: 'kapitel',
                name: '1 Kapitel',
                subs: [{
                    id: '2',
                    typ: 'avsnitt',
                    name: '2 Avsnitt',
                    subs: [{
                        id: '3',
                        typ: 'kategori',
                        name: '3 Kategori',
                        subs: [{
                            id: '4',
                            name: '4 code',
                        }],
                        subItems: [{
                            id: '4',
                            name: '4 code',
                        }]
                    }],
                    subItems: [{
                        id: '3',
                        typ: 'kategori',
                        name: '3 Kategori',
                        subs: [{
                            id: '4',
                            name: '4 code',
                        }],
                        subItems: [{
                            id: '4',
                            name: '4 code',
                        }]
                    }]
                }],
                subItems: [{
                    id: '2',
                    typ: 'avsnitt',
                    name: '2 Avsnitt',
                    subs: [{
                        id: '3',
                        typ: 'kategori',
                        name: '3 Kategori',
                        subs: [{
                            id: '4',
                            name: '4 code',
                        }],
                        subItems: [{
                            id: '4',
                            name: '4 code',
                        }]
                    }],
                    subItems: [{
                        id: '3',
                        typ: 'kategori',
                        name: '3 Kategori',
                        subs: [{
                            id: '4',
                            name: '4 code',
                        }],
                        subItems: [{
                            id: '4',
                            name: '4 code',
                        }]
                    }]
                }]
            }];

            diagnosisTreeFilter.setupDiagnosisTreeForSelectionModal(diagnosis, true);

            expect(diagnosis).toEqual(expected);
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
                },{
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

        it('selected tow levels', function() {

            var node = {
                allSelected: true,
                someSelected: false,
                name: 'level1',

                subs: [{
                    allSelected: true,
                    someSelected: false,
                    name: 'level2'
                },{
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





    // selectDiagnoses
    // getSelectedDiagnosis
});
