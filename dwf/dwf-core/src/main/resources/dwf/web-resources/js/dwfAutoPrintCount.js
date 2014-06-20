/**
 * Placeholders com a classe autoPrintCount e com o atributo countUrl são preenchidos automaticamente via ajax. Ex:
 * <div class="autoPrintCount" countUrl="${baseApp}/perfilUsuario/countPerfil/123" pattern="Ver usuários ({0})"></div>
 * 
 * No document.ready, será feita uma chamada ajax a ${baseApp}/perfilUsuario/countPerfil/123, que deve retornar um ajax no formato: {"long": 3732}
 * 
 * O conteúdo da div é esvaziado e preenchido com o valor do ajax recebido (3732)
 */

$(function () {
	$('.autoPrintCount').each(function() {
		var gridJQ = $(this);
		//searches for a filterForm
		if($('.autoFilter').length == 1) {
			var $filter = $('.autoFilter'); 
			$(document).on("submit", '.autoFilter', function (e) {
				if(!$(this).hasClass('ignoreJS')) {				
					e.preventDefault();
					autoPrintCount(gridJQ, $('.autoFilter').serializeObject());
					return false;
				}
			});
		} else {
			if(!$(this).hasClass('dontAutoLoad')) {
				//no filter form
				autoPrintCount(this);
			}
		}
	});	
	
});

function autoPrintCount(domElement, filter) {
	var domJQ = $(domElement);
	var countUrl = domJQ.attr('countUrl');
	if(countUrl) {
		$.ajax({
			url: countUrl,
			data: filter,
			success: function(data) {
				var pattern = domJQ.attr('pattern');
				if(!pattern) {
					pattern = '{0}';
				}
				
				if(data.long != 0 || domJQ.attr('autoPrintCountIgnoreZero') != 'true') {
					domJQ.html(pattern.replace('{0}', data));
				}
			}
		});
	}
}
