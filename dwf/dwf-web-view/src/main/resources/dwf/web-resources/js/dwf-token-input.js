// token-input (autocomplete input with "tags")
// http://loopj.com/jquery-tokeninput/
$(document).on('dwf-postupdate', function(evt) {
	$(evt.target).find('.token-input-container').each(function () {
		var containerDiv = $(this);
		var inpt = $(this).find('input[type="text"]');
		var property = $(inpt).attr('property');
		var th = $(inpt).attr('theme');;
		var path = "/ajax/tokenInput/"+$(inpt).attr('hashkey');
		var tokenInput = $(inpt).tokenInput(path, {preventDuplicates: true,
									hintText: null,
									noResultsText: null,
									searchingText: null,
									theme: th,
									tokenLimit: $(inpt).attr('maxTokens'),
									onAdd: function (item) {
										containerDiv.append("<input type=\"hidden\" name=\""+property+"[].id\" token-id=\""+item.id+"\" class=\"addedtokenfield token-id\" value=\""+item.id+"\" />");
										containerDiv.append("<input type=\"hidden\" name=\""+property+"[].name\" token-id=\""+item.id+"\" class=\"addedtokenfield token-name\" value=\""+item.name+"\" />");
										
									},
									onClear: function () {
										containerDiv.find(".addedtokenfield").remove();
									},
									
									onDelete: function (item) {
										containerDiv.find(".addedtokenfield[token-id=\""+item.id+"\"]").remove();
									},
									onReady: function () {
										var selector = ".token-input-list".concat(th == null? '':'-'+th);
										containerDiv.parent().find(selector).addClass("form-control").on('focusin', function () {
											$(inpt).addClass("focus");
										}).on('focusout', function () {
											$(inpt).removeClass("focus");
										});
									}
		});
		

		containerDiv.find(".init-token-id").each(function() {
			var objid = $(this).val();
			var objname = containerDiv.find(".init-token-name[token-id=\""+objid+"\"]").val();
			inpt.tokenInput("add", {id: objid, name: objname});
		});
		containerDiv.find(".init-token-id").remove();
		containerDiv.find(".init-token-name").remove();
		
		$(this).find(".token-input-example").on("click", function(evt) {
			evt.preventDefault();
			inpt.tokenInput("clear");
			inpt.tokenInput("add", {id: $(this).attr('id'), name: $(this).attr('name')});
		});
	});
});