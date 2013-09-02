/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


function displayDate()
{
    document.getElementById("demo").innerHTML = Date();
}

function callData() {
    var date = displayDate();
}

function testWebSocket() {
    websocket = new WebSocket(wsUri);
    websocket.onopen = function(evt) {
        onOpen(evt);
    };
    websocket.onclose = function(evt) {
        onClose(evt);
    };
    websocket.onmessage = function(evt) {
        onMessage(evt);
    };
    websocket.onerror = function(evt) {
        onError(evt);
    };
}
