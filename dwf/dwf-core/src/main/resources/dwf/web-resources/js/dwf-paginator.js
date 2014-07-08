/**
 */

$(document).on('click', '[dwf-toggle="paginator"]', function (evt) {
    var $this   = $(this);
    var href    = $this.attr('href');
    var $target = $this.closest('table'); 

    evt.preventDefault();

    $.ajax({
    	url: href,
    	success: function(data) {
    		$target.html(data);
    		$target.trigger('dwf-postupdate');    		
    	}
    });
});