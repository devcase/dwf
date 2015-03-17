/**
 */

$(document).on('click', '[dwf-toggle="paginator"]', function (evt) {
    var $this   = $(this);
    var href    = $this.attr('href');
    var $target = $this.closest('table'); 
    evt.preventDefault();
    
    $target.css({'position': 'relative'});
	$target.append('<div style="position:absolute; top: 45%; left: 20%; right: 20%; z-index: 1000;"><div class="panel panel-default"><div class="panel-body"><div class="progress no-margin"><div class="progress-bar progress-bar-striped active"  role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%">Carregando</div></div></div></div></div>');
    $.ajax({
    	url: href,
    	success: function(data) {
    		$target.fadeOut({
				always: function() {
    	    		$target.html(data);
    	    		$target.trigger('dwf-postupdate');
    	    		$target.fadeIn();
    	    		var top = $target.scrollTop(); //Getting Y of target element
    	    	    window.scrollTo(0, top);
				}
    		});
    	}
    });

});