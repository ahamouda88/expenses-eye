(function() {
	var application = angular.module("application");
	
	// Pager Service, and Pagination Logic
	function PagerService(){		
		
		this.tablePagination = function(items, numPerPage){			
			var Math = window.Math,
				paginate,
				setPrev,
				setNext,
				currentPage = 1,
				maxPage = Math.ceil(items.length / numPerPage);
			
			paginate = function(value) {
				var begin, end, index;
				begin = (currentPage - 1) * numPerPage;
				end = begin + numPerPage;
				index = items.indexOf(value);
				return (begin <= index && index < end);
			};

			setPrev = function() {
				if(currentPage > 1) {
					currentPage = currentPage - 1;
				}
				return currentPage;
			};

			setNext = function() {
				if(currentPage < maxPage){
					currentPage = currentPage + 1;
				}
				return currentPage;
			};
			
			return {
				paginate : paginate,
				setPrev : setPrev,
				setNext : setNext,
				currentPage : currentPage,
				maxPage : maxPage
			};
		};
	}
	
	application.service("PagerService", PagerService);
})();