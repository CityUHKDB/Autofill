google.charts.load("current", {packages: ["corechart", "line"]});
//google.charts.setOnLoadCallback(drawBackgroundColor);

pageLoad = function() {
	$("#tabArea").tabs();
}

displayStatistics = function() {
	displayHistory();
	displayChart();
}

displayChart = function() {
	var synonymPair = document.getElementById("synonymPair");
	var pairID = synonymPair.value;

	var data = new google.visualization.DataTable();
	data.addColumn("number", "index");
	data.addColumn("number", synonymPair.childNodes[pairID*2+1].innerHTML);
	var history = JSON.parse(statistics[pairID].history);
	var dataArray = new Array();
	//dataArray.push([1,1],[2,0.1],[3,0.5]);
	for (var i=0; i<history.length; i++) {
		var probability = history[i].probability;
		var tempData = [i, Number(probability)];
		dataArray.push(tempData);
	}
	data.addRows(dataArray);

	var options = {
        hAxis: {
            title: ""
        },
        vAxis: {
            title: "Probability"
        },
        backgroundColor: "#f1f8e9",
        width: 600
    };

	var chart = new google.visualization.LineChart(document.getElementById("chart"));
	chart.draw(data, options);
}

displayHistory = function() {
	var historyTable = document.getElementById("historyTable");
	var pairID = synonymPair.value;

	var history = JSON.parse(statistics[pairID].history);

	var tr;
	var td;

	// Remove child of history table
	while (historyTable.firstChild) {
		historyTable.removeChild(historyTable.firstChild);
	}

	// Table header
	tr = document.createElement("tr");
	td = document.createElement("th");
	td.innerHTML = "Word 1";
	tr.appendChild(td);
	td = document.createElement("th");
	td.innerHTML = "Word 2";
	tr.appendChild(td);
	td = document.createElement("th");
	td.innerHTML = "Updated Probability";
	tr.appendChild(td);
	historyTable.appendChild(tr);

	// History records
	for (var i=0; i<history.length; i++) {
		tr = document.createElement("tr");
		td = document.createElement("td");
		td.innerHTML = history[i].word1;
		tr.appendChild(td);
		td = document.createElement("td");
		td.innerHTML = history[i].word2;
		tr.appendChild(td);
		td = document.createElement("td");
		td.innerHTML = history[i].probability;
		tr.appendChild(td);
		historyTable.appendChild(tr);
	}

}