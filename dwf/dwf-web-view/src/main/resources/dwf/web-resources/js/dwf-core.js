/**
 * Enables formaction attribute of submit buttons in IE9 and lower (editForm.tag) 
 */
$(document).on('click', 'form [type="submit"]', function(evt) {
	evt.preventDefault();
    var $this  = $(this);
    var action = $this.attr('formaction');
    var $form  = $this.closest('form');
    if(action) {
    	$form.prop('action', action).submit();
    } else {
    	$form.submit();
    }
});

/**
 * Enables jquery-validate and adapts to bootstrap
 */
$(document).on('dwf-postupdate', function() {
	$(this).find("form.validate").validate({
		errorElement: 'span',
		errorClass: 'help-block',
		errorPlacement: function(label, element) {
			$(element).closest('.form-group').addClass('has-error');
			label.insertAfter(element);
		},
		success: function(label, element) {
			$(element).closest('.form-group').removeClass('has-error');
			label.remove();
		}
	});
});


/**
 * Enables jquery-datepicker (após carregamento da página e após 
 * carregamento de trecho da página via ajax (ver dwf-remoteload.js)
 */
$(document).on('dwf-postupdate', function() {
	$(this).find(".date-picker").each(function() {
		$(this).datepicker({
			dateFormat: $(this).attr('data-date-format') 
		});
	});
});

/**
 * Disables buttons with data-loading-text attr - after validation
 */
$(document).on('submit', function(evt) {
	var $form = $(this);
	$form.find('[data-loading-text]').each(function() {
    	$(this).button('loading');
    });
});

/**
 * Button with loading state - Firefox's BF cache treatment
 */
$(document).ready(function() {
	window.onpageshow=function() {
		$('[type="submit"][data-loading-text]').each(function () {
			$(this).button('reset');
		});
	};
});


/**
 * Previne o comportamento padrão de um click.
 * <p>ex: {@code <a href="#" class="prevent-default-click"/>}
 */
$(document).on('click', '.prevent-default-click', function(evt) { evt.preventDefault(); });


/**
 * Ajax in progress indicator
 */

$(document).ajaxStart(function() {
	$(".ajax-indicator").fadeIn(100);
});
$(document).ajaxStop(function() {
	$(".ajax-indicator").fadeOut(100);
});

/**
 * Limpa conteúdo de modals remotos - 
 */
$(function() {
	 $('.modal.remote').on('hidden.bs.modal', function () {
		 //limpa conteúdo ao fechar para recarregar o modal
		 $(this).empty();
		 $(this).removeData('bs.modal');
	 }); 
});


/**
 * Lançar evento dwf-postupdate após o carregamento da página (ver também dwf-remoteload)
 */
$(document).ready(function() {
	$(this).trigger('dwf-postupdate');
});

/**
 * Tratamento da propriedade minproperty para combos (ver inputNumberSelect.tag)
 */ 
$(document).on('dwf-postupdate', function() {
	$('[minproperty]').each(function() {
		var minPropertyName = $(this).attr('minproperty');
		var targetDom = $(this);
		$(this).closest('form').find('[name="' + minPropertyName + '"]').on('change', function(e) {
			var selectedValue = parseInt($(this).val(), 10);
			if (parseInt(targetDom.val()) < selectedValue) {
				targetDom.val(selectedValue);
			}
			targetDom.find('option').each(function () {
				if (parseInt($(this).attr('value'), 10) < selectedValue) {
					$(this).hide();
					$(this).prop('disabled', true);
				} else {
					$(this).show();
					$(this).prop('disabled', false);
				}
			});
		});
	});
});
