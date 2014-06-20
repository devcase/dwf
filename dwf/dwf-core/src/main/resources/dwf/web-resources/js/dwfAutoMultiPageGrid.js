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
	$('.autoMultiPageGrid').each(function() {
		//searches for a filterForm
		if($('.autoFilter').length == 1) {
			var $filter = $('.autoFilter'); 
			var gridJQ = $(this);
			$(document).on("submit", '.autoFilter', function (e) {
				if(!$(this).hasClass('ignoreJS')) {				
					e.preventDefault();
					document.location.hash = '';
					setupAutoMultiPageGrid(gridJQ, 0, $('.autoFilter').serializeObject());
					return false;
				}
			});	
			if(!$(this).hasClass('dontAutoLoad')) {
				setupAutoMultiPageGrid(gridJQ, 0, $('.autoFilter').serializeObject());
			}
		} else {
			if(!$(this).hasClass('dontAutoLoad')) {
				//no filter form
				setupAutoMultiPageGrid(this);
			}
		}
	});
});

$(document).on("change", "#pageSizeSelector", function(){
	var newPageSize = $(document).find("#pageSizeSelector").find("option:selected").attr("value");
	if($("#filterForm").length == 1){
		var gridJQ = $(".autoMultiPageGrid");
		gridJQ.attr("pageSize", newPageSize);
		setupAutoMultiPageGrid(gridJQ, 1, $("#filterForm").serializeObject());
	}
});

function setupAutoMultiPageGrid(domElement, startingPage, filtro) {
	var gridJQ =  $(domElement);
	var countUrl = gridJQ.attr('countUrl');
	var pageUrl = gridJQ.attr('pageUrl');
	var pageSize = gridJQ.attr('pageSize');
	var gridStyleClass = gridJQ.attr('gridStyleClass');
	var currentPage = startingPage ? startingPage : 0;
	var id = gridJQ.attr('id');
	if(id == undefined) {
		//cria id automático - necessário para o reload
		id = Math.floor((Math.random()*1000000000));
		gridJQ.attr('id', id);
	}

	//verifica hash pela primeira vez ao entrar na página
	if(document.location.hash && document.location.hash.indexOf('p') > -1){
		currentPage = document.location.hash.split('p')[1];
	}
	
	var pages = 1;
	
	gridJQ.empty();
	gridJQ.append("<div class=\"autoMultiPageGridData\"></div>");
	gridJQ.append("<div class=\"autoMultiPageGridPaginator\"></div>");
	
	//muda o estilo do paginador, marcando a versão certa com a classe currentPage
	var paintCurrentPage = function() {
		gridJQ.find('.autoMultiPageGridPaginator').find('.pageSelector').removeClass('currentPage');
		gridJQ.find('.autoMultiPageGridPaginator').find('.pageSelector[pageNumber=' + currentPage + ']').addClass('currentPage');
	};
	
	// atualiza hash da página
	var updateURLHash = function(number) {
		document.location.hash = 'p' + number;
	};
	
	//função que busca o conteúdo do grid e atualiza a página
	var buscar = function(pageNumber) {
		currentPage = pageNumber;
		var requestData = {
				'pageNumber': pageNumber,
				'pageSize': pageSize };
		if(filtro){
			requestData = $.extend(requestData, filtro);
		}
		
		$.ajax({
			url: pageUrl,
			data: requestData,
			success: function(d) {
				//draw!
				gridJQ.find('.autoMultiPageGridData').empty();
				gridJQ.find('.autoMultiPageGridData').append(d);
				currentPage = pageNumber;
				paintCurrentPage();
				var queryString = decodeURIComponent($.param(requestData));
				gridJQ.find('.autoMultiPageGridData').attr('url', pageUrl + '?' + queryString);
				if(gridStyleClass != undefined) {
					gridJQ.find('.autoMultiPageGridData').addClass(gridStyleClass);
				}
				
			}
		});
	};
	
	var drawPaginator = function(countTotal) {
		
		
		pages = Math.ceil(countTotal/pageSize);
		var linkText = "";
		
		if(pages > 1) {
			linkText +="<a href=\"#\" class=\"anterior\" >Anterior</a>";
		}
		
	    for(var i = 0; i < pages; i++) {
	    	linkText += "<a href=\"#\" class=\"pageSelector ";
	    	//current page
	    	if(i == currentPage) linkText += "currentPage";
	    	linkText += "\" pageNumber=\"" + i + "\">" + (i+1) + "</a>"; 
	    }

		if(pages > 1) {
			linkText +="<a href=\"#\" class=\"proximo\" >Próximo</a>";
		}

	    
    	linkText += "<span class=\"total\">Total de resultados: <span class=\"countTotal\">" + countTotal + "</span></span><br class=\"clearfix\"/>";
		    
		gridJQ.find('.autoMultiPageGridPaginator').empty();
		gridJQ.find('.autoMultiPageGridPaginator').append(linkText);
		
		if(countTotal > 0) {
			//handlers do paginador
			gridJQ.find('.autoMultiPageGridPaginator').find('.pageSelector').click(function(e) {
				e.preventDefault();
				currentPage = parseInt($(this).attr("pageNumber")); 
				paintCurrentPage();
				updateURLHash(currentPage);
			});
			
			gridJQ.find('.autoMultiPageGridPaginator').find('.proximo').click(function(e) {
				e.preventDefault();
				if(currentPage == (pages -1))
					return;
				currentPage++;
				paintCurrentPage();
				updateURLHash(currentPage);
			});
			
			gridJQ.find('.autoMultiPageGridPaginator').find('.anterior').click(function(e) {
				e.preventDefault();
				if(currentPage == 0)
					return;
				currentPage--;
				paintCurrentPage();
				updateURLHash(currentPage);
			});
			
			// handler do hash para paginação ficar marcada no histórico
			window.onhashchange = function(){
				var hashValue = document.location.hash;
			    if (hashValue.indexOf('p') > -1) {
			    	var page = document.location.hash.split('p')[1];
					refreshTotal();
					buscar(page);
			    } else {
			    	buscar(1);
			    }
			};
			
			buscar(Math.min(currentPage, pages -1));			
		}
	};
	
	var refreshTotal = function() {
		var requestData = {};
		if(filtro) {
			requestData = $.extend(requestData, filtro);
		}
		$.ajax({
			url: countUrl,
			data: requestData,
			success: function(d) {
				gridJQ.find('.countTotal').html(d.long);
			}
		});
	};
	
	//Carrega o total após o autoreload (timer da página de minhas emendas)
	gridJQ.find('.autoMultiPageGridData').bind('sqdasa.autoMultiPageGrid.autoReload', function(event) {
		refreshTotal();
	});
	
	//contar!
	var requestData = {};
	if(filtro) {
		requestData = $.extend(requestData, filtro);
	}
	
	$.ajax({
		url: countUrl,
		data: requestData,
		success: function(d) {
			drawPaginator(d.long);
		}
	});
	
	
	
};


