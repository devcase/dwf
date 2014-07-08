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
    		var top = $target.scrollTop(); //Getting Y of target element
    	    window.scrollTo(0, top);
    	}
    });
});