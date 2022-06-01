const parser = new DOMParser();

async function randomize() {
        
    const url = "../do/match?randomize";
	
    const response = await fetch(url);
    // console.log('response :', response);
    const result = await response.text();
    // console.log('result :', result);

	const htmlDoc = parser.parseFromString(result, "text/html");
    showResult(htmlDoc);

}

async function match() {

    const url = "../do/match";

    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/x-www-form-urlencoded");

    var urlencoded = new URLSearchParams();
    urlencoded.append("submit", "Go!");
    urlencoded.append("loglines", document.getElementById('loglines').value);
    urlencoded.append("pattern", document.getElementById('pattern').value);
    urlencoded.append("multiline", "");
    urlencoded.append("grokadditional", "");

    var requestOptions = {
        method: 'POST',
        headers: myHeaders,
        body: urlencoded
    };

    var response = await fetch(url, requestOptions);
    // console.log('response :', response);
    const result = await response.text();
    // console.log('result :', result);

	const htmlDoc = parser.parseFromString(result, "text/html");
    showResult(htmlDoc);

}

function showResult(htmlDoc) {
    
	document.getElementById('loglines').value = htmlDoc.getElementById('loglines').value;
	document.getElementById('pattern').value = htmlDoc.getElementById('pattern').value;

	//document.getElementById('matchresult').innerHTML = '<div class="_3rxK6">Detailed match information will be displayed here automatically.</div>';

	let matchresult = '';
	matchresult += '<div class="_20RCA">';
	matchresult += '	<div class="_2tlKL">';
	matchresult += '		<table class="_3oU8P">';
	matchresult += '			<tbody>';
	
	
	var table = htmlDoc.getElementsByClassName('bordertable narrow')[0];
	var rows = table.rows;
    var line = 1;

	for (let i = 0; i < rows.length; i++) {
		
		var cells = rows[i].cells;
		
		if (cells.length == 1) {
			var header = cells[0].textContent;
			header = header.substr(1,header.length-1).trim();
			// console.log('header : ' + header);
			i++; // skipping match status for now : todo later

			matchresult += '				<tr class="_3pEUO">';
			matchresult += '					<td class="_2RLnO _3A_j4 _2CHi4" aria-disabled="false"';
			matchresult += '						role="button" tabindex="0"><span';
			matchresult += '						style="border-color: rgb(213, 235, 255);">Match <span';
			matchresult += '							class="_37mBu">'+(line++)+'</span></span></td>';
			matchresult += '					<td class="_1EGgE"></td>';
			matchresult += '					<td class="Wksgz"><span>' + header +'</span></td>';
			matchresult += '				</tr>';
			
		} else {
			var key = cells[0].textContent.trim();
			var value = cells[1].textContent.trim();
			// console.log(key + " : " + value);

			matchresult += '				<tr class="_3pEUO">';
			matchresult += '					<td class="_2RLnO _3A_j4 _2CHi4" aria-disabled="false"';
			matchresult += '						role="button" tabindex="0"><span';
			matchresult += '						style="border-color: rgb(196, 232, 172);"><span';
			matchresult += '							class="_37mBu">'+ key +'</span></span></td>';
			matchresult += '					<td class="_1EGgE"></td>';
			matchresult += '					<td class="Wksgz"><span>'+ value+'</span></td>';
			matchresult += '				</tr>';
		}
		
	}

	matchresult += '			</tbody>';
	matchresult += '		</table>';
	matchresult += '	</div>';
	
	document.getElementById('matchresult').innerHTML = matchresult;
	
}