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
 * Enables jquery-datepicker (após carregamento da página e após 
 * carregamento de trecho da página via ajax (ver dwf-remoteload.js)
 * Ver inputDateTime.tag e DwfCustomDateEditor 
 */
$(document).on('dwf-postupdate', function() {
	$(this).find(".date-time-picker").each(function() {
		var domJQ = $(this);
		var selectedDate;
		var timezoneoffset = parseInt($(this).attr('timezoneoffset'));
		$(this).datetimepicker({step: 30,
			formatDate: $(this).attr('datetimepicker-date-format'),
			formatTime: $(this).attr('datetimepicker-time-format'),
			format: $(this).attr('datetimepicker-date-format') + ' ' + $(this).attr('datetimepicker-time-format'),
			initTime: false,
			minDate: 0,
			onChangeDateTime: function(current_time) {
				console.log(timezoneoffset);
				console.log(current_time.getTimezoneOffset()*60000);
				selectedDate = new Date(current_time.getTime() + current_time.getTimezoneOffset()*60000 + timezoneoffset);
			},
			onGenerate: function(current_time) {
				selectedDate = new Date(current_time.getTime() + current_time.getTimezoneOffset()*60000 + timezoneoffset);
			}
		});
		
		$(this).closest('form').on('submit', function(evt) {
			domJQ.val(selectedDate.toISOString());
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
	$(this).find('[minproperty]').each(function() {
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
		$(this).closest('form').find('[name="' + minPropertyName + '"]').change();
	});
});

/**
 * Inicializa os CKEditors da tela (ver <dwf:inputRichText/>)
 */
$(document).on('dwf-postupdate', function() {
	$(this).find('.ckeditor').each(function() {
		CKEDITOR.replace(this);
	});
});

$(document).on('dwf-postupdate', function() {
	$(this).find('.g-recaptcha').each(function() {
		var recaptcha = $(this);
		$(this).closest('form').submit(function (evt) {
			var res = grecaptcha.getResponse();
			var errorSpan = $(this).find('.recaptcha-error');
			if (!res) {
				recaptcha.closest('.form-group').addClass('has-error');
				errorSpan.show();
				errorSpan.addClass('help-block');
				return false;
			} else {
				errorSpan.removeClass('help-block');
				errorSpan.hide();
				recaptcha.closest('.form-group').removeClass('has-error');
				return true;
			}
			return true;
		});
		
	});
});

function reCaptchaRemoveError() {
	$(document).find('.recaptcha-error').each(function () {
		$(this).removeClass('help-block');
		$(this).hide();
		$(this).closest('.form-group').removeClass('has-error');
	});
}

// token-input (autocomplete input with "tags")
$(document).on("dwf-postupdate", function () {
	$(this).find('.token-input').each(function () {
		var containerDiv = $(this).parent().find('.token-div');
		var property = $(this).attr('property');
		var inpt = $(this);
		var th = $(this).attr('theme');;
		var path = "/ajax/tokenInput/"+$(this).attr('hashkey');
		$(this).tokenInput(path, {preventDuplicates: true,
									hintText: null,
									noResultsText: null,
									searchingText: null,
									theme: th,
									onAdd: function (item) {
										containerDiv.append("<input type=\"hidden\" name=\""+property+"[].id\" token-id=\""+item.id+"\" class=\"token-id\" value=\""+item.id+"\" />");
										containerDiv.append("<input type=\"hidden\" name=\""+property+"[].name\" token-id=\""+item.id+"\" class=\"token-name\" value=\""+item.name+"\" />");
										
									},
									onDelete: function (item) {
										containerDiv.find("[token-id=\""+item.id+"\"]").remove();
									},
									onReady: function () {
										var selector = ".token-input-list".concat(th == null? '':'-'+th);
										containerDiv.parent().find(selector).addClass("form-control").on('focusin', function () {
											$(this).addClass("focus");
										}).on('focusout', function () {
											$(this).removeClass("focus");
										});
									}
									});
		containerDiv.find(".init-token-id").each(function() {
			var objid = $(this).val();
			var objname = containerDiv.find(".init-token-name[token-id=\""+$(this).val()+"\"]").val();
			inpt.tokenInput("add", {id: objid, name: objname});
		});
		containerDiv.find(".init-token-id").remove();
		containerDiv.find(".init-token-name").remove();
		
	});
});
