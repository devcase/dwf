/**
 * Loads by ajax a remote content and replace the contents somewhere in the page
 *  Inspired by bootstrap's remote modal:
 *  <a href="url" dwf-toggle="remoteload"  dwf-target="#divid">Click here</a>
 */

$(document).on('click', '[dwf-toggle="remoteload"][dwf-target]', function (evt) {
    var $this   = $(this);
    var href    = $this.attr('href');
    var $target = $($this.attr('dwf-target')); //strip for ie7
    evt.preventDefault();

    $target.fadeOut({
    	always: function() {
    		$target.html('<div style="padding: 10px;"><div class="progress no-margin"><div class="progress-bar progress-bar-striped active"  role="progressbar" aria-valuenow="45" aria-valuemin="0" aria-valuemax="100" style="width: 10%"><span class="sr-only">10% Complete</span></div></div></div>');
    		$target.show();
    	    $.ajax({
    	    	url: href,
    	    	success: function(data) {
    	    		$target.find('.progress-bar').css('width', '100%');
    	    		$target.fadeOut({
	    				always: function() {
	        	    		$target.html(data);
	        	    		$target.find('[dwf-toggle="remoteload"]').not('[dwf-target]').attr('dwf-target', '#' + $target.attr('id'))
	        	    	    $target.fadeIn();
	        	    		$target.trigger('dwf-postupdate');
	    				}
    	    		});
    	    	},
    	    });
    	}
    });

});