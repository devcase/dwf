/**
 */
$(document).on('dwf-postupdate', function(evt) {
	var autoreloadelements;
	if($(evt.target).attr('dwf-toggle') == 'autoreload') {
		autoreloadelements = $(evt.target);
	} else {
		autoreloadelements =$(evt.target).find('[dwf-toggle="autoreload"][autoreload-href]');
	}
	
	autoreloadelements.each(function() {
	    var $this   = $(this);
	    var href    = $this.attr('autoreload-href');

		window.setTimeout(function() {
			if(jQuery.contains(document.documentElement, $this[0])) {
			    var $wrapper   = $this.wrap('<div class="autoreload-wrapper"></div>').parent();
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
					    	    		var top = $wrapper.scrollTop(); //Getting Y of target element
					    	    	    window.scrollTo(0, top);
					    	    	    newcontent = $wrapper.children().unwrap();
					    	    	    $(newcontent).trigger('dwf-postupdate');
			    	    			}
			    	    		});
							}
			    		});
			    	}
			    });
			}
		}, 5000);
	});
});