$(function() {
	$(document).on('submit', 'form', function(evt) {
		var $form = $(this);
		
		var encontrouRequiredVazio = false;
		$form.find('[required]').each(function () {
			if($(this).val() == "") {
				encontrouRequiredVazio = true;
			}
		});
		if(encontrouRequiredVazio) {
			$form.addClass('form-invalid');
			evt.preventDefault();
			evt.stopPropagation();
			alert("Por favor, revisar os campos obrigatórios.");
		}

	});
});