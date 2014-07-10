/**
 */

$(document).on('click', '[dwf-toggle="paginator"]', function (evt) {
    var $this   = $(this);
    var href    = $this.attr('href');
    var $target = $this.closest('table'); 
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
		    	    		$target.trigger('dwf-postupdate');
		    	    		$target.fadeIn();
	        	    		$target.trigger('dwf-postupdate');
		    	    		var top = $target.scrollTop(); //Getting Y of target element
		    	    	    window.scrollTo(0, top);
	    				}
    	    		});
    	    	}
    	    });
    		
    	}
    });

});