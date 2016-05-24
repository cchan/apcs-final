var socket =  io.connect(window.location.hostname+':1234');
socket.on('LogEvent', function(data) {
  log(data.color, data.text);
});
//socket.on('RoomListUpdate', function(data){});

function sendConnect(){
  if($('input#username').val().length < 3)
    log('red', 'You must enter a name of at least three characters')
  else
    socket.emit('ConnectEvent', {name: $('input#username').val()}, function(data){
      var noRooms = true;
      for(var i = 0; i < data.length; i++){
        if(data[i].length > 0){
          noRooms = false;
          var element = $("<li>" + data[i] + "</li>");
          element.click(function(e){
            //assign the room
          });
          $('#room-list').append(element);
        }
      }
      if(noRooms)
        $('#room-list').append($("<li>There are no rooms to display</li>"));
    });
}
function sendChat(){
  socket.emit('ChatEvent', {author: $('#chat-author').val(), message: $('#chat-message').val()}, function(data){
    $('#chat-message').val('').focus();
  });
}

var gameActive = false;
var ticksSinceLast = 0;

$(document).keydown(function(e){
  var dir = '';
  if(gameActive){
    switch(String.fromCharCode(e.which)){
      case 'z': socket.emit('TurnEvent', {direction: 'LEFT', ticksSinceLast: ticksSinceLast}); ticksSinceLast = 0; return;
      case 'x': socket.emit('TurnEvent', {direction: 'RIGHT', ticksSinceLast: ticksSinceLast}); ticksSinceLast = 0; return;
      default: return;
    }
  }
});
function log(color, data) {
  var element = $("<div style='color:" + color + "'> " + data + "</div>");
  $('#log').append(element);
  setTimeout(function(){element.remove();}, 5000);
}
