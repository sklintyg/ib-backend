describe('Directive: IbVardenhetSelector', function() {
    'use strict';

    // load the controller's module
    beforeEach(module('ibApp'));
    beforeEach(module('htmlTemplates'));

    var $compile;
    var $scope;
    var element;
    var elementScope;

    var userJson = {
        'currentlyLoggedInAt': {
            id: 'm21'
        },
        'authoritiesTree': [

        {
            'id': 'TSTNMT2321000156-105M22',
            'name': 'Rehabstöd Vårdgivare 2',
            'vardenheter': [ {
                'id': 'TSTNMT2321000156-105N22',
                'name': 'Rehabstöd Enhet 2.1',
                'mottagningar': [ {
                    'id': 'mottagning 1',
                    'name': 'mottagning 1',
                    'parentHsaId': 'linkoping'
                }, {
                    'id': 'mottagning 2',
                    'name': 'mottagning 2',
                    'parentHsaId': 'linkoping'
                } ]
            }, {
                'id': 'TSTNMT2321000156-105N222B',
                'name': 'Rehabstöd Enhet 2.2',
                'mottagningar': [ {
                    'id': 'm21',
                    'name': 'mottagning 1',
                    'parentHsaId': 'linkoping'
                } ]
            } ]
        } ]
    };

    // Store references to $scope and $compile
    // so they are available to all tests in this describe block
    beforeEach(inject(function(_$compile_, $rootScope) {
        // The injector unwraps the underscores (_) from around the parameter names when matching
        $compile = _$compile_;
        $scope = $rootScope.$new();
        //Setup common prerequsites for all tests
        $scope.userParameter = userJson;
        element = $compile('<ib-vardenhet-selector user="userParameter" expand-vardgivare="true"/>')($scope);
        $scope.$digest();

        elementScope = element.isolateScope() || element.scope();

    }));
/*
    it('should have correct nodes expanded for valdVardenhet', function() {
        // Assert
        expect(elementScope.model.vardgivare[0].expanded).toBeTruthy();
        expect(elementScope.model.vardgivare[0].vardenheter[0].expanded).toBeFalsy();
        expect(elementScope.model.vardgivare[0].vardenheter[1].expanded).toBeTruthy();
    });

    it('should have calculated correct number of units', function() {
        // Assert
        expect(elementScope.getTotalVECount(elementScope.model.vardgivare)).toBe(2);

    });
*/
});
