function runAjax(type, url, data, onload) {
    console.log("Sending request [", url, "] with data [", data, "]");
    const xhttp = new XMLHttpRequest();
    xhttp.onload = onload;
    xhttp.open(type, url, true);
    xhttp.send(JSON.stringify(data));
}
