var currentRow;
var originDataType;
var originFieldName;
var originFieldValue;
var originFieldGroup;

var currentGroupRow;
var originGroupName;

pageLoad = function() {
	// Display group
	var groupTable = document.getElementById("groupTable");
	var inputGroupRow = document.getElementById("inputGroupRow")
	
	if (group.length > 0) {
		var tr;
		var td;	
		var button;

		for (var i=0; i<group.length; i++) {
			tr = document.createElement("tr");
			td = document.createElement("td");
			td.innerHTML = group[i].name;
			tr.appendChild(td);
			td = document.createElement("td");
			button = createGroupEditButton();
			td.appendChild(button);
			button = createGroupDeleteButton();
			td.appendChild(button);
			tr.appendChild(td);
			groupTable.appendChild(tr);
		}
		groupTable.appendChild(inputGroupRow);
	}

	// Display data
	var dataTable = document.getElementById("dataTable");
	var inputRow = document.getElementById("inputRow");

	if (data.length > 0) {
		var tr;
		var td;	
		var button;

		for (var i=0; i<data.length; i++) {
			tr = document.createElement("tr");
			td = document.createElement("td");
			td.innerHTML = data[i].dataType;
			tr.appendChild(td);
			td = document.createElement("td");
			td.innerHTML = data[i].name;
			tr.appendChild(td);
			td = document.createElement("td");
			td.innerHTML = data[i].value;
			tr.appendChild(td);
			td = document.createElement("td");
			td.innerHTML = data[i].group;
			tr.appendChild(td);
			td = document.createElement("td");
			button = createEditButton();
			td.appendChild(button);
			button = createDeleteButton();
			td.appendChild(button);
			tr.appendChild(td);
			dataTable.appendChild(tr);
		}
		dataTable.appendChild(inputRow);
	}

	var fieldGroupList = document.getElementById("fieldGroup");
	var option;
	for (var i=0; i<group.length; i++) {
		option = document.createElement("option");
		option.value = group[i].name;
		option.innerHTML = group[i].name;
		fieldGroupList.appendChild(option);
	}

	$("#tabArea").tabs();
}

// Change input box to match the data type
listChanged = function() {
	var list = document.getElementById("dataTypeList");
	var fieldValue = document.getElementById("fieldValue");

	// Reset to empty field
	fieldValue.value = "";
	
	switch (list.value) {
		case "Text":
			$("#fieldValue").datepicker("destroy");
			break;
		case "Date":
			$("#fieldValue").datepicker({dateFormat: "dd/mm/yy"});
			break;
	}
}

// Insert new data record
insertData = function() {
	var dataType = document.getElementById('dataTypeList').value;
	var name = document.getElementById('fieldName').value;
	var value = document.getElementById('fieldValue').value;
	var fieldGroup = document.getElementById('fieldGroup').value;

	// Check if empty
	if (name == "") {
		alert("Action Abort: Field Name Cannot Be Empty!");
		return;
	} else if (value == "") {
		alert("Action Abort: Field Value Cannot Be Empty!");
		return;
	}

	// Check if duplicate
	for (var i=0; i<data.length; i++) {
		if (data[i].group == fieldGroup && data[i].name == name) {
			alert("Action Abort: Duplicate Field Name!");
			return;
		}
	}

	var field = {"dataType" : dataType, "name" : name, "value" : value, "group" : fieldGroup};
	data.push(field);
        
    var dataTable = document.getElementById("dataTable");
	var inputRow = document.getElementById("inputRow");
    var tr;
    var td;	
    var button;

    tr = document.createElement("tr");
    td = document.createElement("td");
    td.innerHTML = dataType;
    tr.appendChild(td);
    td = document.createElement("td");
    td.innerHTML = name;
    tr.appendChild(td);
    td = document.createElement("td");
    td.innerHTML = value;
    tr.appendChild(td);
	td = document.createElement("td");
	td.innerHTML = fieldGroup;
	tr.appendChild(td);
    td = document.createElement("td");
    button = createEditButton();
    td.appendChild(button);
    button = createDeleteButton();
    td.appendChild(button);
    tr.appendChild(td);
    dataTable.appendChild(tr);
	
    dataTable.appendChild(inputRow);
    
    document.getElementById('dataTypeList').value = "Text";
	document.getElementById('fieldName').value = "";
	document.getElementById('fieldValue').value = "";
	document.getElementById('fieldGroup').value = "";
        
}

// Create edit record button
createEditButton = function() {
	var button = document.createElement("button");
	button.innerHTML = "Edit";
	button.onclick = editRecord;
	return button;
}

// Change the row structure for updating record
editRecord = function() {
	var source = event.target || event.srcElement;
	var parent = source.parentElement;

	if (currentRow != null) {
		cancelEdit();
	}

	currentRow = parent.parentElement;

	// Change Action column
	while (parent.firstChild) {
		parent.removeChild(parent.firstChild);
	}
	var button;
	button = document.createElement("button");
	button.innerHTML = "Update";
	button.onclick = updateRecord;
	parent.appendChild(button);
	button = document.createElement("button");
	button.innerHTML = "Cancel";
	button.onclick = cancelEdit;
	parent.appendChild(button);

	// Save origin data
	originDataType = currentRow.childNodes[0].innerHTML;
	originFieldName = currentRow.childNodes[1].innerHTML;
	originFieldValue = currentRow.childNodes[2].innerHTML;
	originFieldGroup = currentRow.childNodes[3].innerHTML;

	// Change Data Type, Field Name and Value column
	currentRow.childNodes[0].removeChild(currentRow.childNodes[0].firstChild);
	var dataTypeList = document.createElement("select");
	var option;
	option = document.createElement("option");
	option.setAttribute("value", "Text");
	option.innerHTML = "Text";
	dataTypeList.appendChild(option);
	option = document.createElement("option");
	option.setAttribute("value", "Date");
	option.innerHTML = "Date";
	dataTypeList.appendChild(option);
	dataTypeList.value = originDataType;
	dataTypeList.onchange = function(event) {
		var source = event.target || event.srcElement;
		var row = source.parentElement.parentElement;
		var list = row.childNodes[0].childNodes[0];
		var fieldValue = row.childNodes[2].childNodes[0];

		// Reset to empty field
		fieldValue.value = "";

		var id = fieldValue.getAttribute("id");
		fieldValue.setAttribute("id", "temp");
		switch (list.value) {
			case "Text":
				$("#temp").datepicker("destroy");
				break;
			case "Date":
				$("#temp").datepicker({dateFormat: "dd/mm/yy"});
				break;
		}
		fieldValue.setAttribute("id", id);
	};
	currentRow.childNodes[0].appendChild(dataTypeList);

	currentRow.childNodes[1].removeChild(currentRow.childNodes[1].firstChild);
	var fieldNameBox = document.createElement("input");
	fieldNameBox.setAttribute("type", "text");
	fieldNameBox.value = originFieldName;
	currentRow.childNodes[1].appendChild(fieldNameBox);

	currentRow.childNodes[2].removeChild(currentRow.childNodes[2].firstChild);
	var fieldValueBox = document.createElement("input");
	fieldValueBox.setAttribute("type", originDataType);
	fieldValueBox.value = originFieldValue;
	currentRow.childNodes[2].appendChild(fieldValueBox);
	if (dataTypeList.value == "Date") {
		fieldValueBox.setAttribute("id", "fieldValue");
		$("fieldValue").datepicker({dateFormat: "dd/mm/yy"});
	}

	if (currentRow.childNodes[3].childNodes.length > 0) {
		currentRow.childNodes[3].removeChild(currentRow.childNodes[3].firstChild);
	}
	var fieldGroupList = document.createElement("select");
	option = document.createElement("option");
	option.value = "";
	option.innerHTML = "";
	fieldGroupList.appendChild(option);
	for (var i=0; i<group.length; i++) {
		option = document.createElement("option");
		option.value = group[i].name;
		option.innerHTML = group[i].name;
		fieldGroupList.appendChild(option);
	}
	fieldGroupList.value = originFieldGroup;
	currentRow.childNodes[3].appendChild(fieldGroupList);
}

// Create delete button
createDeleteButton = function() {
	var button = document.createElement("button");
	button.innerHTML = "Delete";
	button.onclick = deleteRecord;
	return button;
}

// Delete record from data
deleteRecord = function() {
	var source = event.target || event.srcElement;
	var row = source.parentElement.parentElement;

	// Retrieve the index of the record
	var i = -2;
	while ((row = row.previousSibling) != null) {
		i++;
	}

	data.splice(i, 1);
        
    var dataTable = document.getElementById("dataTable");
    dataTable.removeChild(dataTable.childNodes[i+2]);
}

// Cancel edit mode of a row
cancelEdit = function() {
	while (currentRow.lastChild.firstChild) {
        currentRow.lastChild.removeChild(currentRow.lastChild.firstChild);
	}	
	currentRow.childNodes[0].innerHTML = originDataType;
	currentRow.childNodes[1].innerHTML = originFieldName;
	currentRow.childNodes[2].innerHTML = originFieldValue;
	currentRow.childNodes[3].innerHTML = originFieldGroup;
	currentRow.childNodes[4].appendChild(createEditButton());
	currentRow.childNodes[4].appendChild(createDeleteButton());

	// Reset global variable
	currentRow = null;
	originDataType = null;
	originFieldName = null;
	originFieldValue = null;
	originFieldGroup = null;
}

// Update record according to the edited value
updateRecord = function(event) {
	var source = event.target || event.srcElement;
	var row = source.parentElement.parentElement;
	var tempRow = row;

	// Check if empty
	if (row.childNodes[1].firstChild.value == "") {
		alert("Action Abort: Field Name Cannot Be Empty!");
		return;
	} else if (row.childNodes[2].firstChild.value == "") {
		alert("Action Abort: Field Value Cannot Be Empty!");

	}

	// Check if duplicate
	for (var i=0; i<data.length; i++) {
		if (data[i].group == row.childNodes[3].firstChild.value && data[i].name == row.childNodes[1].firstChild.value) {
			alert("Action Abort: Duplicate Field Name!");
			return;
		}
	}

	// Retrieve the index of the record
	var i = -2;
	while ((tempRow = tempRow.previousSibling) != null) {
		i++;
	}

    // Update record
	data[i].dataType = row.childNodes[0].firstChild.value;
	data[i].name = row.childNodes[1].firstChild.value;
	data[i].value = row.childNodes[2].firstChild.value;
	data[i].group = row.childNodes[3].firstChild.value;
	
    row.childNodes[0].removeChild(row.childNodes[0].firstChild);
    row.childNodes[0].innerHTML = data[i].dataType;
    
    row.childNodes[1].removeChild(row.childNodes[1].firstChild);
    row.childNodes[1].innerHTML = data[i].name;
    
    row.childNodes[2].removeChild(row.childNodes[2].firstChild);
    row.childNodes[2].innerHTML = data[i].value;

    row.childNodes[3].removeChild(row.childNodes[3].firstChild);
    row.childNodes[3].innerHTML = data[i].group;
    
    while (row.lastChild.firstChild) {
        row.lastChild.removeChild(row.lastChild.firstChild);
	}	
	currentRow.childNodes[4].appendChild(createEditButton());
	currentRow.childNodes[4].appendChild(createDeleteButton());

	currentRow = null;
	originDataType = null;
	originFieldName = null;
	originFieldValue = null;
	originFieldGroup = null;
}

// Save data to database
saveData = function() {
	data.sort(sortData);
    $.ajax({
        url: "DataSaver",
        type: 'POST',
        data: JSON.stringify(data),
        
        success: function(data) {
        	location.reload();
        },
        
        error:function(data, status, error) {
            alert("error: "+data+" status: "+status+" error:"+error);
        }
    });
}

// Create edit group record button
createGroupEditButton = function() {
	var button = document.createElement("button");
	button.innerHTML = "Edit";
	button.onclick = editGroupRecord;
	return button;
}

// Edit group
editGroupRecord = function(event) {
	var source = event.target || event.srcElement;
	var parent = source.parentElement;

	if (currentGroupRow != null) {
		cancelEditGroup();
	}

	currentGroupRow = parent.parentElement;

	// Change Action column
	while (parent.firstChild) {
		parent.removeChild(parent.firstChild);
	}
	var button;
	button = document.createElement("button");
	button.innerHTML = "Update";
	button.onclick = updateGroupRecord;
	parent.appendChild(button);
	button = document.createElement("button");
	button.innerHTML = "Cancel";
	button.onclick = cancelEditGroup;
	parent.appendChild(button);

	// Save origin data
	originGroupName = currentGroupRow.childNodes[0].innerHTML;

	// Change Group Name
	currentGroupRow.childNodes[0].removeChild(currentGroupRow.childNodes[0].firstChild);
	var GroupNameBox = document.createElement("input");
	GroupNameBox.setAttribute("type", "text");
	GroupNameBox.value = originGroupName;
	currentGroupRow.childNodes[0].appendChild(GroupNameBox);

}

// Create delete group button
createGroupDeleteButton = function() {
	var button = document.createElement("button");
	button.innerHTML = "Delete";
	button.onclick = deleteGroupRecord;
	return button;
}

// Delete group record
deleteGroupRecord = function() {
	var source = event.target || event.srcElement;
	var row = source.parentElement.parentElement;

	// Retrieve the index of the record
	var i = -2;
	while ((row = row.previousSibling) != null) {
		i++;
	}

	// If some records belong to this group, abort action
	for (var j=0; j<data.length; j++) {
		if (data[j].group == group[i].name) {
			alert("Group Cannot Be Deleted Because It Is In Use By Some Fields.");
			return;
		}
	}

	group.splice(i, 1);
        
    var groupTable = document.getElementById("groupTable");
    groupTable.removeChild(groupTable.childNodes[i+2]);
}

// Cancel group edit mode of a row
cancelEditGroup = function() {
	while (currentGroupRow.lastChild.firstChild) {
        currentGroupRow.lastChild.removeChild(currentGroupRow.lastChild.firstChild);
	}	
	currentGroupRow.childNodes[0].innerHTML = originGroupName;
	currentGroupRow.childNodes[1].appendChild(createGroupEditButton());
	currentGroupRow.childNodes[1].appendChild(createGroupDeleteButton());

	// Reset global variable
	currentGroupRow = null;
	originGroupName = null;
}

// Update group record according to the edited value
updateGroupRecord = function(event) {
	var source = event.target || event.srcElement;
	var row = source.parentElement.parentElement;
	var tempRow = row;

	// Check if empty
	if (row.childNodes[0].firstChild.value == "") {
		alert("Action Abort: Group Name Cannot Be Empty!");
		return;
	}

	// Check if duplicate
	for (var i=0; i<group.length; i++) {
		if (group[i].name == row.childNodes[0].firstChild.value) {
			alert("Action Abort: Duplicate Group Name!");
			return;
		}
	}
	
	// Retrieve the index of the record
	var i = -2;
	while ((tempRow = tempRow.previousSibling) != null) {
		i++;
	}

    // Update group
	group[i].name = row.childNodes[0].firstChild.value;
	
    row.childNodes[0].removeChild(row.childNodes[0].firstChild);
    row.childNodes[0].innerHTML = group[i].name;
                
    while (row.lastChild.firstChild) {
        row.lastChild.removeChild(row.lastChild.firstChild);
	}	
	currentGroupRow.childNodes[1].appendChild(createGroupEditButton());
	currentGroupRow.childNodes[1].appendChild(createGroupDeleteButton());

	// Update record group name
	for (var j=0; j<data.length; j++) {
		if (data[j].group == originGroupName) {
			data[j].group = group[i].name;
		}
	}

	currentGroupRow = null;
	originGroupName = null;
}

// Insert new group data record
insertGroup = function() {
	var name = document.getElementById('groupName').value;

	// Check if empty
	if (name == "") {
		alert("Action Abort: Group Name Cannot Be Empty!");
		return;
	}

	// Check if duplicate
	for (var i=0; i<group.length; i++) {
		if (group[i].name == name) {
			alert("Action Abort: Duplicate Group Name!");
			return;
		}
	}

	var groupRecord = {"name" : name};
	group.push(groupRecord);
        
    var groupTable = document.getElementById("groupTable");
	var inputGroupRow = document.getElementById("inputGroupRow");
    var tr;
    var td;	
    var button;

    tr = document.createElement("tr");
    td = document.createElement("td");
    td.innerHTML = name;
    tr.appendChild(td);
    td = document.createElement("td");
    button = createGroupEditButton();
    td.appendChild(button);
    button = createGroupDeleteButton();
    td.appendChild(button);
    tr.appendChild(td);
    groupTable.appendChild(tr);
    groupTable.appendChild(inputGroupRow);
    
	document.getElementById('groupName').value = "";
        
}

// Save group list to database
saveGroup = function() {
	group.sort(sortGroup);
    $.ajax({
        url: "GroupSaver",
        type: 'POST',
        data: JSON.stringify(group),
        
        success: function(data) {},
        
        error:function(data, status, error) {
            alert("error: "+data+" status: "+status+" error:"+error);
        }
    });
    saveData();
}

sortData = function(a, b) {
	if (a.group > b.group) {
		return 1;
	} else if (a.group < b.group) {
		return -1;
	} else {
		return 0;
	}
}

sortGroup = function(a, b) {
	if (a.name > b.name) {
		return 1;
	} else if (a.name < b.name) {
		return -1;
	} else {
		return 0;
	}
}

