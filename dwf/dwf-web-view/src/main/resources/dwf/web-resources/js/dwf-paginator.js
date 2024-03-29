/**
 */

$(document).on('click', '[dwf-toggle~="paginator"]', function (evt) {
    var $this   = $(this);
    var href    = $this.attr('href');
    var $target = $($this.attr('dwf-target'));
    if(!$target || $target.size() == 0) {
    	$target = $this.closest('table');
    }
    if(!$target  || $target.size() == 0) return;
    var $wrapper   = $target.wrap('<div class="autoreload-wrapper"></div>').parent();
    evt.preventDefault();
    
	var top = $wrapper.offset().top; //Getting Y of target element
	if($('body').scrollTop() > top) {
		$('body').animate({scrollTop:top}, 200);
	}

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
    	    	    	    $wrapper.children().unwrap();
    	    			}
    	    		});
				}
    		});
    	}
    });

});