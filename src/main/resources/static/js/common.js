function runAjax(type, url, data, onload) {
    const xhttp = new XMLHttpRequest();
    xhttp.onload = onload;
    xhttp.open(type, url, true);
    xhttp.send();
}