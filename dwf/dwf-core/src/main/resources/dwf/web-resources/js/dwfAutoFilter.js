$(function() {
	if($('.autoFilter').length == 1) {
		var $filter = $('.autoFilter'); 
		$filter.children().on("change", function(e) {
			$filter.submit();
		});
		if(!$(this).hasClass('dontAutoLoad')) {
			$filter.submit();
		}
	}
});		
