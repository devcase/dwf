/**
 * Loads by ajax a remote content and replace the contents
 *  <div dwf-url="url" dwf-toggle="remotecontents" ></div>
 *  
 *  <div dwf-url="url" dwf-toggle="remotecontents" dwf-replace="true"></div>
 */

$(document).on('dwf-postupdate', function (evt) {
	$(evt.target).find('[dwf-toggle~="remotecontents"][dwf-url][dwf-remotecontents-loaded!="true"]').each(function() {
		var href    = $(this).attr('dwf-url');
	    var $target = $(this); //strip for ie7
	    var replace = $(this).attr("dwf-replace") == "true";
	    evt.preventDefault();
	    
	    $target.css({'position': 'relative'});
		//$target.append('<div style="position:absolute; top: 45%; left: 20%; right: 20%; z-index: 1000;"><div class="panel panel-default"><div class="panel-body"><div class="progress no-margin"><div class="progress-bar progress-bar-striped active"  role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%">Carregando</div></div></div></div></div>');
		$target.html('<div style="margin: 40px;"><div class=""><div class="progress no-margin"><div class="progress-bar progress-bar-striped active"  role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%">Carregando</div></div></div></div>');
	    $.ajax({
	    	url: href,
	    	success: function(data) {
				if(replace) {
					$target.replaceWith(data);
				} else {
		    		$target.fadeOut({
						always: function() {
				    		$target.html(data);
				    	    $target.fadeIn();
				    		$target.attr('dwf-remotecontents-loaded', 'true');
				    		$target.trigger('dwf-postupdate');
						}
		    		});
				}
	    	},
	    });
	});
});