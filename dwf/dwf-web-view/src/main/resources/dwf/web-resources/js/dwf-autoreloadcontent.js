/**
 */
$(document).on('dwf-postupdate', function() {
	$(this).find('[dwf-toggle="autoreloadcontent"][autoreload-href]').each(function() {
		dwfAutoReloadContentQueue(this);
	});
});
function dwfAutoReloadContentQueue(dom) {
    var $this   = $(dom);
    var href    = $this.attr('autoreload-href');
	window.setTimeout(function() {
		if(jQuery.contains(document.documentElement, dom)) {
		    $.ajax({
		    	url: href,
		    	success: function(data) {
					$this.empty();
					$this.html(data);
					dwfAutoReloadContentQueue(dom);
		    	}
		    });
		}
	}, 5000);
}