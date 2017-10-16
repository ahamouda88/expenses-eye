'use strict';

(function() {
	var application = angular.module('application');
	// Uploader Directive
	application.directive("uploader", [ function() {
		return {
			$scope : {
				uploader : "="
			},
			link : function($scope, element) {
				element.bind("change", function(changeEvent) {
					$scope.imageFile = changeEvent.target.files[0];
					$scope.imageUrl = URL.createObjectURL($scope.imageFile);
					$scope.$apply();
				});
			}
		}
	} ]);
	
	// Line Chart Directive
	application.directive('lineChart', ['$window', function($window) {
	    return {
	        restrict : 'A',
	        scope : {
	        		data: '=chartData',
	        		time: '=chartTime'
	        	},
	        link : function (scope, element, attrs) {
	            var chart = $window.d3.select(element[0]);
	            setTimeout(function () {
		            c3.generate({
		                bindto: chart,
		                data: {
		                		x: 'x',
		                		columns: [
		                			scope.time,
		                			scope.data
		                		]
		                },
		                axis : {
		                    x : {
		                    	    label: {
		                            text: 'Dates',
		                            position: 'outer-center'
		                        },
		                        type: 'timeseries',
		                        tick: {
		                            format: '%m-%d-%Y'
		                        }
		                    },
		                    y : {
		                    		label: {
		                            text: 'Amount',
		                            position: 'outer-middle'
		                        }
		                    }
		                }
		            }); 
	            }, 300);
	        } 
	    };
	}]);  
})();