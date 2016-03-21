pageLoad = function() {
	var formList = document.getElementById("formList");
        var formPath = document.getElementById("formPath");
        formPath.value = formList.value;
        if (formList.value != "other") {
            $("#uploadBox").hide();
        } else {
            $('#existingBox').hide();
        }
}

formListChanged = function() {
	var formList = document.getElementById("formList");
	if (formList.value == "other") {
            $("#uploadBox").show();
            $('#existingBox').hide();
	} else {
            var formPath = document.getElementById("formPath");
            formPath.value = formList.value;
            $("#uploadBox").hide();
            $('#existingBox').show();
	}
}

