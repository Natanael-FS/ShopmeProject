function addDetailNextSection(){
	allDivDetails = $("[id^='divDetail']")
	divDetailsCount = allDivDetails.length;
	
	htmlDetailSection = `
		<div class="form-inline" id="divDetail${divDetailsCount}">
		<label>Name : </label>
			<input type="text" class="form-control w-25" name="detailNames"maxlength="255"/>
		<label>Value : </label>
			<input type="text" class="form-control w-25" name="detailvalues"maxlength="255"/>
		</div>
	`;
	
	$("#divProductDetails").append(htmlDetailSection);
	
	previousDivSelection = allDivDetails.last();
	previousDivDetailId = previousDivSelection.attr("id");
	
	htmlLinkRemove = `
		<a class="btn fas fa-times-circle fa-2x icon-dark "
			href="javascript:removeDetailSectionById(${previousDivDetailId})" 
			title="Remove this details"></a>
	`;
	
	previousDivSelection.append(htmlLinkRemove); 
}

function removeDetailSectionById(id){
	$("#" + id).remove();
}
