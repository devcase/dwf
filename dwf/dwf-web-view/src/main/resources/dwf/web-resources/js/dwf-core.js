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
 * Creates a locale-aware validation rule for jquery-validator (ver moment.js e dwfScripts.tag)  
 */
$.extend($.validator.methods, {
	date: function(value, element) {
		return this.optional(element) ||  moment(value, $datePatternMoment).isValid();
	},
	number: function(value, element) {
		if($decimalSeparator == ".") {
			return this.optional(element) || /^-?(?:\d+|\d{1,3}(?:\,\d{3})+)(?:.\d+)?$/.test(value);
		} else {
			return this.optional(element) || /^-?(?:\d+|\d{1,3}(?:\.\d{3})+)(?:,\d+)?$/.test(value);
		}
	}
});
$.validator.addClassRules("validate-date", { date: true });
$.validator.addClassRules("validate-number", { number: true });
$.validator.addClassRules("validate-digits", { digits: true });
$.validator.addClassRules("validate-email", { email: true });
$.validator.addClassRules("required", { required: true });

/**
 * Enables jquery-validate and adapts to bootstrap
 */
$(document).on('dwf-postupdate', function(evt) {
	$(evt.target).find("form.validate").each(function() {
		$(this).validate({
			errorElement: 'span',
			//errorClass: 'help-block',
			errorPlacement: function(label, element) {
				
				if(!$(element).closest('.form-group').hasClass('has-error')) {
					$(element).closest('.form-group').addClass('has-error');
					$(element).closest('.form-group-content').append(label);
					label.addClass('help-block error-label');
				}
				$(element).addClass('has-error');
			},
			success: function(label, element) {
				$(element).removeClass('has-error');
				if($(element).closest('.form-group').find('.has-error').size() == 0) {
					$(element).closest('.form-group').removeClass('has-error');
					label.remove();
				}
			}
		});
	});
});


/**
 * Enables jquery-datepicker (após carregamento da página e após 
 * carregamento de trecho da página via ajax (ver dwf-remoteload.js)
 */
$(document).on('dwf-postupdate', function(evt) {
	$(evt.target).find(".date-picker").each(function() {
		$(this).datepicker({
			dateFormat: $(this).attr('data-date-format') 
		});
	});
});

/**
 * Enables jquery-datetime (após carregamento da página e após 
 * carregamento de trecho da página via ajax (ver dwf-remoteload.js)
 * Ver inputDateTime.tag e DwfCustomDateEditor 
 */
$(document).on('dwf-postupdate', function(evt) {
	$(evt.target).find(".date-time-picker").each(function() {
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
				if (current_time != null)
					selectedDate = new Date(current_time.getTime() + current_time.getTimezoneOffset()*60000 + timezoneoffset);
			},
			onGenerate: function(current_time) {
				if (domJQ.val() != "")
					selectedDate = new Date(current_time.getTime() + current_time.getTimezoneOffset()*60000 + timezoneoffset);
			}
		});
		
		$(this).closest('form').on('submit', function(evt) {
			if (selectedDate != null)
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
$(document).on('dwf-postupdate', function(evt) {
	$(evt.target).find('[minproperty]').each(function() {
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
$(document).on('dwf-postupdate', function(evt) {
	$(evt.target).find('.ckeditor').each(function() {
		CKEDITOR.replace(this);
	});
});

$(document).on('dwf-postupdate', function(evt) {
	$(evt.target).find('.g-recaptcha').each(function() {
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



/**
 * dwf:input{*}BoxCheckbox
 */
$(document).on("dwf-postupdate", function (evt) {
	$(evt.target).find(".dwf-boxcheckbox").trigger("verifyChecked");
});

$(document).on("verifyChecked", ".dwf-boxcheckbox", function () {
	$(this).find(".checked").remove();
	if ($(this).find('input').prop('checked')) {
		$(this).addClass("selected");
		$(this).append("<span class=\"checked glyphicon glyphicon-ok\"></span>");
	} else {
		$(this).removeClass("selected");
		$(this).find(".checked").remove();
	}
});
$(document).on("click", ".dwf-boxcheckbox", function () {
	$(this).trigger("verifyChecked");
});

/*
 * price format mask (inputPrice.tag)
 */
$(document).on("dwf-postupdate", function (evt) {
	$(evt.target).find(".price-format").each(function () {
		var inpt = $(this);
		inpt.priceFormat({
			prefix: '',
			centsSeparator: $(this).attr('decimalSeparator'),
			thousandsSeparator: $(this).attr('groupingSeparator')
		});
		inpt.closest('form').on("submit", function () {
			inpt.val(inpt.val().replace(new RegExp("\\"+inpt.attr('groupingSeparator'), 'g'), ''));
		});
	});
});

/**
 * Locale selector localeSelector.tag
 */
$(document).on("change", "form.dwf-language-selector-form select.language-selector", function (evt) {
	$(this).closest('form').submit();
});


/** 
 * <dwf:loadingmodal>
 */
$(document).on("dwf-postupdate", function (evt) {
	$(evt.target).find(".dwf-loadingmodal").fadeOut();
});

/**
 * Oculta o conteúdo do form e desenha uma barra de progresso 
 */
$(document).on("submit", "form.dwf-progressbaronsubmit", function(evt) {
	$(this).find('*').css("visibility", "hidden");
	$(this).css({'position': 'relative'});
	$(this).append('<div style="position:absolute; height: 100%; width: 100%; z-index: 1000; top:0; "><div class="progress no-margin"  style="left: 20%; width: 60%; top: 50%;position: relative;transform: translateY(-50%);"><div class="progress-bar progress-bar-striped active"  role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%;"></div></div></div>');
});

/**
 * <dwf:inputPrice>
 */
$(document).on("change", ".dwf-input-price-dropdown-item input[type='radio']", function() {
	var labelText = $(this).attr('labelText');
	$(this).closest('.dwf-input-price').find('.dwf-input-price-dropdown-button-text').html(labelText);
});

/**
 * <dwf:inputEmail>
 */
$(document).on("change", "input.dwf-auto-trimandlowercase", function() {
	$(this).val($(this).val().trim().toLowerCase());
});
