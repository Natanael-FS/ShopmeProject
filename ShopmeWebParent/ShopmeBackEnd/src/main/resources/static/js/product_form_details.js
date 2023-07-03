$(document).ready(function() {
	
	$("a[name='linkRemoveDetail']").each(function(index) {
		$(this).click(function() {
			removeDetailByIndex(index);
		});
	});
});



function addDetailNextSection(){
	allDivDetails = $("[id^='divDetail']")
	divDetailsCount = allDivDetails.length;
	
	htmlDetailSection = `
		<div class="form-inline" id="divDetail${divDetailsCount}">
		    <input type="hidden" name="detailIDs" value="0" />
			<label class="m-3">Name : </label>
				<input type="text" class="form-control w-25" name="detailNames"maxlength="255"/>
			<label class="m-3">Value : </label>
				<input type="text" class="form-control w-25" name="detailValues"maxlength="255"/>			
		</div>
	`;
	
	$("#divProductDetails").append(htmlDetailSection);
	
	previousDivSelection = allDivDetails.last();
//	previousDivDetailId = previousDivSelection.id;
	previousDivDetailId = previousDivSelection.attr("id");
	
	htmlLinkRemove = `
		<a class="btn fas fa-times-circle fa-2x icon-red "
			href="javascript:removeDetailSectionById('${previousDivDetailId}')" 
			title="Remove this details"></a>
	`;

//	htmlLinkRemove = `
//		<a class="btn fas fa-times-circle fa-2x icon-dark "
//			href="removeDetailSectionById(${previousDivDetailId})" 
//			title="Remove this details"></a>
//	`;
	
	previousDivSelection.append(htmlLinkRemove); 
	
	$("input[name='detailNames']").last().focus();
}

function removeDetailSectionById(id){
	$("#" + id).remove();
}

function removeDetailByIndex(index){
	$("#divDetail" + index).remove(); 
}