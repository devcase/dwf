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
    	    $.ajax({
    	    	url: href,
    	    	success: function(data) {
    	    		$target.html(data);
    	    		$target.find('[dwf-toggle="remoteload"]').not('[dwf-target]').attr('dwf-target', '#' + $target.attr('id'))
    	    		$target.trigger('dwf-postupdate');
    	    	    $target.fadeIn();
    	    	}
    	    });
    	}
    });

});