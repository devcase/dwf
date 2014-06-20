/**
 * Qualquer elemento com a classe autoBackOnClick, ao ser clicado, dispara o clique no primeiro
 * elemento com a classe backButton 
 * 
 */
$(document).on('click', '.autoBackOnClick', function(evt) {
	evt.preventDefault();
	if($('.backButton').length > 0) {
		$('.backButton').click();
		var link = $('.backButton').attr("href");
		location.href = link;
	}
	else history.back();
});