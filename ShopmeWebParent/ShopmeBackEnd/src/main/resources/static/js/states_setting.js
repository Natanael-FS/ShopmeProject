var buttonLoad;
var dropDownCountry;
var dropDownStates;
var buttonAddStates;
var buttonUpdateStates;
var buttonDeleteStates;
var labelStatesName;
var fieldStatesName;
var fieldCountryCode;

$(document).ready(function() {
	buttonLoad = $("#btnLoadCountriesForStates");
	dropDownCountry = $("#dropDownCountriesForStates");
	dropDownStates = $("#dropDownStates");
	labelStatesName = $("#labelStatesName");
	fieldStatesName = $("#fieldStatesName");
	buttonAddStates = $("#btnAddStates");
	buttonUpdateStates = $("#btnUpdateStates");
	buttonDeleteStates = $("#btnDeleteStates");
	
	buttonLoad.click(function() {
		loadCountries();
	});
	
	dropDownCountry.on("change", function(){
		id = dropDownCountry.val().split("-")[0];
		changeFormStateToSelectedCountry(id); 
	}); 

	dropDownStates.on("change", function(){
		changeFormStateToSelectedState(); 
	}); 
	
	buttonAddStates.click(function() {
		if (buttonAddStates.val() == "Add"){
			addStates();
		}else{
			changeFormStateToNew();
		}
	});
	
	buttonUpdateStates.click(function(){
		updateStates();
	});

	buttonDeleteStates.click(function(){
		deleteStates();
	});	
});

function loadCountries(){
	url = contextPath + "countries/list";
	$.get(url, function (responseJSON){
		dropDownCountry.empty();
		$.each(responseJSON, function(index, country){
			optionValue = country.id + "-" + country.code;
			$("<option>").val(optionValue).text(country.name).appendTo(dropDownCountry);
				
		});
	}).done(function() {
		buttonLoad.val("Refresh Country List");
		showToastMessage("All countries have been loaded")
	}).fail(function() {
		showToastMessage("Error : could not connect to server or server encountered an error")
	});
}

function showToastMessage(message){
	$("#toastMessage").text(message);
	$(".toast").toast('show');
}

function changeFormStateToSelectedCountry(id){
	url = contextPath + "states/list_by_country/"+id;
	$.get(url, function (responseJSON){
		dropDownStates.empty();
		$.each(responseJSON, function(index, state){
			optionValue = state.id;
			$("<option>").val(optionValue).text(state.name).appendTo(dropDownStates);
		});
	}).done(function() {
		buttonLoad.val("Refresh State List");
		showToastMessage("All states have been loaded")
	}).fail(function() {
		showToastMessage("Error : could not connect to server or server encountered an error")
	});
}

function changeFormStateToSelectedState(){
	buttonAddStates.prop("value", "New");
	buttonUpdateStates.prop("disabled", false);
	buttonDeleteStates.prop("disabled", false);
	
	labelStatesName.text("Selected State : ");
	selectedStateName = $("#dropDownStates option:selected").text();
	fieldStatesName.val(selectedStateName);
}

function changeFormStateToNew(){
	selectedCountry = $("#dropDownCountriesForStates option:selected").val();

	if(selectedCountry!=null){
		buttonAddStates.val("Add");
		labelStatesName.text("States Name : ");
		
		buttonUpdateStates.prop("disabled", true);
		buttonDeleteStates.prop("disabled", true);
		
		fieldStatesName.val("").focus();		
	}else{
		showToastMessage("Please Select a Country first")
	}
}

function addStates(){
	url = contextPath + "states/save";
	stateName = fieldStatesName.val();
	selectedCountry = $("#dropDownCountriesForStates option:selected");
	countryId = selectedCountry.val().split("-")[0];
	countryName = selectedCountry.text();

	jsonData = {name: stateName, country: {id: countryId, name: countryName}};
	if (stateName != null){
		$.ajax({
			type: 'POST',
			url : url,
			beforeSend: function(xhr){
				xhr.setRequestHeader(csrfHeaderName, csrfValue);
			},
			data: JSON.stringify(jsonData),
			contentType: 'application/json'
		}).done(function(stateId){
			selectedNewlyAddedStates(stateId, stateName);
			showToastMessage("A new country has been added ");
		}).fail(function() {
			showToastMessage("Error : could not connect to server or server encountered an error")
		});		
	}else{
		showToastMessage("State Name is Null")
	}
}		
		
function updateStates(){
	url = contextPath + "states/save";
	stateName = fieldStatesName.val();
	stateId = $("#dropDownStates option:selected").val();
	
	selectedCountry = $("#dropDownCountriesForStates option:selected");
	countryId = selectedCountry.val().split("-")[0];
	countryName = selectedCountry.text();
	
	jsonData = {id:stateId, name: stateName, country: {id: countryId, name: countryName}};
	
	$.ajax({
		type: 'POST',
		url : url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(stateId){
		$("#dropDownStates option:selected").val(stateId);
		$("#dropDownStates option:selected").text(stateName);
		showToastMessage("The country has been updated ");
		
		changeFormStateToNew();
	}).fail(function() {
		showToastMessage("Error : could not connect to server or server encountered an error")
	});
}		
		
function deleteStates(){
	stateId = $("#dropDownStates option:selected").val();
	url = contextPath + "states/delete/" + stateId ;
	
	$.ajax({
		type: 'DELETE',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(){
		$("#dropDownStates option[value='" + stateId + "']").remove();
		changeFormStateToNew();
		showToastMessage("The Country has been deleted");
	}).fail(function(){
		showToastMessage("Error : could not connect to server or server encountered an error");
	});
}		

function selectedNewlyAddedStates(stateId, stateName){
	optionValue = stateId;
	$("<option>").val(optionValue).text(stateName).appendTo(dropDownStates);
	
	$("#dropDownStates option[value= '" + optionValue + "']").prop("selected", true);
	fieldStatesName.val("").focus();
}