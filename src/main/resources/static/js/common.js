function runAjax(type, url, data, onload) {
    console.log("Sending request [", url, "] with data [", data, "]");
    const xhttp = new XMLHttpRequest();
    xhttp.onload = function() {
        let response = JSON.parse(this.response);
        if (response["success"])
            onload(response);
        else
            alert(`Server error:\n${response["error"]}`);
    };
    xhttp.open(type, url, true);
    xhttp.send(JSON.stringify(data));
}
