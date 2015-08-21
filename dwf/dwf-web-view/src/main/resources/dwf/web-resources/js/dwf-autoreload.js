/**
 */
$(document).on('dwf-postupdate', '[dwf-toggle="autoreload"][autoreload-href]', function() {
    var $this   = $(this);
    var href    = $this.attr('autoreload-href');
    var $wrapper   = $(this).wrap('<div class="autoreload-wrapper"></div>').parent();
	window.setTimeout(function() {
		if(jQuery.contains(document.documentElement, $target[0])) {
			$wrapper.css({'position': 'relative'});
			$wrapper.append('<div style="position:absolute; top: 45%; left: 20%; right: 20%; z-index: 1000;"><div class="panel panel-default"><div class="panel-body"><div class="progress no-margin"><div class="progress-bar progress-bar-striped active"  role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%">Carregando</div></div></div></div></div>');
		    $.ajax({
		    	url: href,
		    	success: function(data) {
		    		$wrapper.fadeOut({
						always: function() {
							$wrapper.empty();
							$wrapper.html(data);
		    	    		$wrapper.fadeIn({
		    	    			always: function() {
				    	    		$wrapper.trigger('dwf-postupdate');
				    	    		var top = $wrapper.scrollTop(); //Getting Y of target element
				    	    	    window.scrollTo(0, top);
				    	    	    $wrapper.children().unwrap();
		    	    			}
		    	    		});
						}
		    		});
		    	}
		    });
		}
	}, 5000);
});