/**
 * Loads by ajax a remote content and replace the contents somewhere in the page
 *  Inspired by bootstrap's remote modal:
 *  <a href="url" dwf-toggle="remoteload"  dwf-target="#divid">Click here</a>
 */

$(document).on('click', '[dwf-toggle="remoteload"][dwf-target][href]', function (evt) {
    var $this   = $(this);
    var href    = $this.attr('href');
    var $target = $($this.attr('dwf-target')); //strip for ie7
    evt.preventDefault();

    $target.css({'position': 'relative'});
	//$target.append('<div style="position:absolute; top: 45%; left: 20%; right: 20%; z-index: 1000;"><div class="panel panel-default"><div class="panel-body"><div class="progress no-margin"><div class="progress-bar progress-bar-striped active"  role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%">Carregando</div></div></div></div></div>');
	$target.html('<div style="margin: 40px;"><div class=""><div class="progress no-margin"><div class="progress-bar progress-bar-striped active"  role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%">Carregando</div></div></div></div>');
    $.ajax({
    	url: href,
    	statusCode: {
    		302: function (x) {
    			return false;
    		}
    	},
    	success: function(data) {
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
});