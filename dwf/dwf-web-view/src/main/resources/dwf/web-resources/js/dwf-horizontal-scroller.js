/**
 * 
 */

$(document).on('dwf-postupdate', function(evt) {
	$(evt.target).find("[dwf-toggle='horizontal-scroller']").each(function() {

		var scrollTarget = $(this).find('ul');
		scrollTarget.css({'position' : 'absolute'});
		var domJQ = $(this);
		var startPosition = scrollTarget.position().left;
		 
		
		
		var getElementWidthFunction = function() {
			var visibleItems = scrollTarget.find('li:visible');
			if(visibleItems.length > 2) {
				return $(visibleItems[2]).position().left - $(visibleItems[1]).position().left;
			}
			else if(visibleItems.length == 2) {
				return $(visibleItems[1]).position().left - $(visibleItems[0]).position().left;
			}
			else if((visibleItems.length ==1 )) {
				return $(visibleItems[0]).width();
			} else {
				return 0;
			}
			
		};
		
		//check when the controllers should be displayed
		var checkControlVisibilityFunction = function() {
			var itemCount = scrollTarget.find('li:visible').length;
			if(itemCount <= 1) {
				domJQ.find('.horizontal-scroller-control').hide();
			} else if(scrollTarget) {
				var contentWidth = getElementWidthFunction() * itemCount;
				if(contentWidth > domJQ.width()) {
					domJQ.find('.horizontal-scroller-control').show();
				} else {
					domJQ.find('.horizontal-scroller-control').hide();
				}
			}
		};
		
		checkControlVisibilityFunction();
		$(window).on('resize', checkControlVisibilityFunction);
		
		
		//handler for clicking on a control
		domJQ.on('click', '.horizontal-scroller-control', function(evt) {
			evt.preventDefault();
			
			var dir = $(this).hasClass('right') ? -1 : 1;
			
			var deltaXStep = getElementWidthFunction();
			
			var deltaX = Math.max(Math.floor(domJQ.width() / deltaXStep), 1) * deltaXStep * dir;
			var startLeft = scrollTarget.position().left;
			var finalLeft = startLeft + deltaX; 
			var minLeft = startPosition -  (deltaXStep * scrollTarget.find('li').length - domJQ.width() ) ; 
			
			if(finalLeft > startPosition) {
				finalLeft = startPosition;
			} else if(finalLeft < minLeft) {
				finalLeft = minLeft;
			}
			if(startLeft == startPosition && startLeft == finalLeft) {
				finalLeft = minLeft;
			} else if (startLeft == minLeft && startLeft == finalLeft) {
				finalLeft = startPosition;
			}
			
			domJQ.find('.horizontal-scroller-control').hide();
			scrollTarget.animate({
				"left": finalLeft
			}, {
				done: function() {
					domJQ.find('.horizontal-scroller-control').show();
				}
			});
		});
	});
});