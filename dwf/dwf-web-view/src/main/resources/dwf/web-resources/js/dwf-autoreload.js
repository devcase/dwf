/**
 */
$(document).on('dwf-postupdate', function(evt) {
	var autoreloadelements;
	if($(evt.target).is('[dwf-toggle~="autoreload"][autoreload-href]') > 0) {
		autoreloadelements = $(evt.target);
	} else {
		autoreloadelements =$(evt.target).find('[dwf-toggle~="autoreload"][autoreload-href]');
	}
	
	autoreloadelements.each(function() {
	    var $this   = $(this);
	    var href    = $this.attr('autoreload-href');
	    var timeout = $this.attr('autoreload-timeout');
	    if(timeout) {
	    	timeout = parseInt(timeout);
	    } else {
	    	timeout = 5000;
	    }
	    var fx = $this.attr('autoreload-fx');
	    if(fx) {
	    	fx = "true" == fx;
	    } else {
	    	fx = true;
	    }

		window.setTimeout(function() {
			if(jQuery.contains(document.documentElement, $this[0])) {
			    var $wrapper   = $this.wrap('<div class="autoreload-wrapper"></div>').parent();
			    if(fx) {
					$wrapper.css({'position': 'relative'});
					$wrapper.append('<div style="position:absolute; top: 45%; left: 20%; right: 20%; z-index: 1000;"><div class="panel panel-default"><div class="panel-body"><div class="progress no-margin"><div class="progress-bar progress-bar-striped active"  role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%">Carregando</div></div></div></div></div>');
			    }
			    $.ajax({
			    	url: href,
			    	success: function(data) {
			    		$wrapper.fadeOut({
			    			duration: fx ? 400 : 0,
							always: function() {
								$wrapper.empty();
								$wrapper.html(data);
			    	    		$wrapper.fadeIn({
			    	    			duration: fx ? 400 : 0,
			    	    			always: function() {
					    	    		var top = $wrapper.scrollTop(); //Getting Y of target element
					    	    		if(fx) {
						    	    	    window.scrollTo(0, top);
					    	    		}
					    	    	    newcontent = $wrapper.children().unwrap();
					    	    	    $(newcontent).trigger('dwf-postupdate');
			    	    			}
			    	    		});
							}
			    		});
			    	}
			    });
			}
		}, timeout);
	});
});