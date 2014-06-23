/**
 * 
 */

$(document).on('dwf-postupdate', function() {
	$(this).find("[dwf-toggle='horizontal-scroller']").each(function() {

		var scrollTarget = $(this).find('ul');
		var domJQ = $(this);
		var startPosition = scrollTarget.position().left;
		
		domJQ.on('click', '.horizontal-scroller-control', function(evt) {
			evt.preventDefault();
			
			var dir = $(this).hasClass('right') ? -1 : 1;
			var deltaXStep = $(scrollTarget.find('li')[1]).position().left - $(scrollTarget.find('li')[0]).position().left;
			var deltaX = Math.max(Math.floor(domJQ.width() / deltaXStep), 1) * deltaXStep * dir;
			var startLeft = scrollTarget.position().left;
			var finalLeft = startLeft + deltaX; 
			var minLeft = startPosition - deltaXStep * (scrollTarget.find('li').length - Math.max(Math.floor(domJQ.width() / deltaXStep), 1)) ; 
			
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