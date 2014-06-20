/**
 * Cria um grid de resultados de pesquisa automaticamente, desde que exista uma 
 * div com a classe autoMultiPageGrid, com os atributos pageUrl e countUrl.<br/> 
 * Também procura por um filtro com o id filterForm - se existir, transforma os elementos do form
 * em parâmetros para a busca do grid. Também associa ao submit do form um novo handler que,
 * ao invés de realizar o post/ get, atualiza os resultados da pesquisa via Ajax.<br/>
 * Se o form tiver a classe 'ignoreJS', o handler executa o submit normalmente, de acordo
 * com o padrão HTML.<br/>
 * 
 * Exemplo:
 * <div class="autoMultiPageGrid" pageUrl="${baseApp}/emendaContrato/find${findType}" countUrl="${baseApp}/emendaContrato/count${findType}" pageSize="15">
 * </div>
 */

$(function () {
	$('.autoGrid').each(function() {
		//searches for a filterForm
		if($('.autoFilter').length == 1) {
			var $filter = $('.autoFilter'); 
			var gridJQ = $(this);
			$(document).on("submit", '.autoFilter', function (e) {
				if(!$(this).hasClass('ignoreJS')) {				
					e.preventDefault();
					setupAutoGrid(gridJQ, 0, $('.autoFilter').serializeObject());
					return false;
				}
			});
		} else {
			if(!$(this).hasClass('dontAutoLoad')) {
				//no filter form
				setupAutoGrid(this);
			}
		}
	});
});

function setupAutoGrid(domElement, startingPage, filter) {
	var gridJQ =  $(domElement);
	var pageUrl = gridJQ.attr('pageUrl');
	var fetchSize = 6;
	gridJQ.empty();
	gridJQ.parent().find('.autoGridLoadMoreLink').remove();
	var offset = 0;
	

	//função que busca o conteúdo do grid e atualiza a página
	var buscar = function(offset, fetchSize) {
		var totalExpected = offset + fetchSize;
		var requestData = {
				'offset': offset,
				'fetchSize': fetchSize };
		if(filter){
			requestData = $.extend(requestData, filter);
		}
		
		$.ajax({
			url: pageUrl,
			data: requestData,
			method: 'POST',
			success: function(d) {
				//draw!
				gridJQ.append(d);
				
				offset = gridJQ.find('.grid-item').length;
				if(offset == totalExpected) {
					gridJQ.parent().append('<a href="#" class="autoGridLoadMoreLink">Carregar mais</a> ');
					gridJQ.parent().find('.autoGridLoadMoreLink').on('click', function(evt) {
						evt.preventDefault();
						$(this).remove();
						buscar(offset, fetchSize);
					});
				}
				
			}
		});
		
	};
	
	buscar(offset, fetchSize);
	
};

$(document).on('scroll', function () {
	if($(document).height() <= ($(window).height() + $(window).scrollTop())) {
		//CHEGOU NO FIM
		$('.autoGridLoadMoreLink').trigger('click');
	}
});		


