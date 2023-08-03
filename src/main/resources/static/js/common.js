/**
 * Send an ajax request with the provided parameters.
 * @param {string} type Request type (post, get, etc.).
 * @param {string} url Route to the desired ajax call.
 * @param {?Object} data Data to be passed to the server.
 * @param {?function(Object):void} onload Callback to be executed on successful response.
 * @param {?function(Object):void} onerror Callback to be executed on failed response (where a server error has been
 * caught).
 */
function runAjax(type, url, data, onload, onerror) {
    console.log("Sending request [", url, "] with data [", data, "]");
    const xhttp = new XMLHttpRequest();
    xhttp.onload = function() {
        let response = JSON.parse(this.response);
        console.log("Received response [", response, "]");
        if (response["success"]) {
            if (onload)
                onload(response);
        } else {
            if (onerror)
                onerror(response);
            else
                alert(`Server error:\n${response["error"]}`);
        }
    };
    xhttp.open(type, url, true);
    xhttp.send(JSON.stringify(data));
}

function mod(n, m) {
    return ((n % m) + m) % m;
}

function clamp(n, min, max) {
    return (n <= min) ? min : ((n >= max) ? max : n);
}
