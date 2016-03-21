pageLoad = function() {
	var personalFieldList = document.getElementsByClassName("personalFieldList");
	var option;

	for (var i=0; i<data.length; i++) {
		for (var j=0; j<personalFieldList.length; j++) {
			option = document.createElement("option");
			option.setAttribute("value", i);
			option.innerHTML = data[i].name;
			personalFieldList[j].appendChild(option);
			personalFieldList[j].onchange = function(event) {
				var source = event.target || event.srcElement;
				var row = source.parentElement.parentElement;
				if (this.value != "") {
					row.childNodes[3].firstChild.value = data[this.value].value;
					row.childNodes[5].childNodes[3].value = data[this.value].name;
				}
			}
		}
	}
}
