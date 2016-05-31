function log(color, data) {
  var element = $("<div style='color:" + color + "'> " + data + "</div>");
  $('#log').append(element);
  setTimeout(function(){element.remove();}, 5000);
}

function Game(canvas, gameSocket, name){
  //TODO: is this resizable? -- not right now due to canvas strict sizing
  var cols = 25;
  var rows = 25;
  var padding = 1;

  //http://stackoverflow.com/a/25716620/1181387
  var ctx = canvas.getContext("2d");
  ctx.strokeStyle = 'purple';
  ctx.lineWidth = 3;

  var players = [];

  var tickInterval;
  this.connect = function(){
    registerSocketCallbacks(gameSocket);
    registerSocketEmits(gameSocket);

    tickInterval = setInterval(this.tick, 200);

    var info = {
      name: name,
      x:Math.floor(Math.random() * (cols - 10) + 5),
      y:Math.floor(Math.random() * (rows - 10) + 5),
      d:Math.floor(Math.random() * 4)
    };
    console.log("Emitting NewSnakeEvent");
    gameSocket.emit("NewSnakeEvent", info, function(data){
      console.log("Recieved implicit FullUpdateEvent");
      console.log(data);
    });
  };
  this.disconnect = function(){
    clearInterval(tickInterval);
    gameSocket.disconnect();
    $(document).off("keypress.socket");
    ctx.clearRect(0, 0, $(canvas).width(), $(canvas).height());
  };

  var currentTick = 0;
  var board = new Array(rows*cols);
  this.tick = function(){
    currentTick++;

    for(var i = 0; i < players.length; i++)
      players[i].tick(board);

    board.fill(undefined);

    for(var i = 0; i < players.length; i++)
      players[i].fillBoard(board);

    draw(board.map(function(x){
      if(x===undefined)return 'white';
      else return x;
    }));
  };

  function draw(board) {
      ctx.clearRect(0, 0, $(canvas).width(), $(canvas).height());

      var w = ($(canvas).width()) / cols - padding; // dimensions of individual squares
      var h = ($(canvas).height()) / rows - padding;

      for (var y = 0; y < rows; y++) {
          for (var x = 0; x < cols; x++) {
              ctx.fillStyle = board[y * cols + x];
              ctx.fillRect(x * (w + padding), y * (h + padding), w, h);
          }
      }
  }

  //HANDLE KEYBOARD INPUT AND SEND IT TO THE SERVER
  function registerSocketEmits(sock){
    var keypress = function(e){
      switch(String.fromCharCode(e.which)){
        case 'z': sock.emit('TurnEvent', {player: name, twist: -1, tick: currentTick}); break;
        case 'x': sock.emit('TurnEvent', {player: name, twist: +1, tick: currentTick}); break;
      }
    }
    $(document).on("keypress.socket", keypress);
  }

  //HANDLE SERVER-SENT EVENTS AS TURNS (even your own)
  function registerSocketCallbacks(sock){
    sock.on('NewSnakeEvent', function(data){
      console.log("recieved NewSnakeEvent");
      console.log(data);
      players.push(new Snake(data.name, rows, cols, {
        x: data.x,
        y: data.y,
        d: data.dir,
        l: 10,
        id: data.id
      }));
    });
    sock.on('FullUpdateEvent', function(data){
      console.log("recieved FullUpdateEvent");
      console.log(data);
    });
    sock.on('TurnEvent', function(data){
      for(var i = 0; i < players.length; i++)
        if(players[i].id == data.id)
          players[i].turn(data.twist); //Not sure how to deal with data.tick
      console.log("received TurnEvent")
      console.log(data);
    });
  }
}
function Snake(name, rows, cols, state){
  this.name = name;
  var queue = []; //head is at end of array

  var dir = state.d;
  queue.push({x:state.x, y:state.y});
  this.length = state.l;
  this.id = state.id;

  /* DIRECTION NUMBERS:
       0
     3   1
       2
  */

  this.turn = function(twist){
    if(twist == 1) dir = (dir+1)%4;
    else if(twist == -1) dir = (dir+3)%4;
  }
  this.fillBoard = function(board){
    for(var i = 0; i < queue.length; i++)
      board[queue[i].y * cols + queue[i].x] = hashStringToColor(this.name);
  }
  this.getCSSColor = function(){
    return hashStringToColor(this.name);
  }
  this.tick = function(oldboard){
    if(queue.length == 0)
      return;

    var headpos = queue[queue.length-1];
    var newpos = {x: headpos.x, y: headpos.y};
    if(this.length > 0){ //after dead
      if(dir == 0) newpos.y--;
      if(dir == 1) newpos.x++;
      if(dir == 2) newpos.y++;
      if(dir == 3) newpos.x--;
      if(newpos.x >= cols) newpos.x -= cols;
      if(newpos.y >= rows) newpos.y -= rows;
      if(newpos.x < 0) newpos.x += cols;
      if(newpos.y < 0) newpos.y += rows;

      queue.push(newpos);
    }

    if(oldboard[newpos.y * cols + newpos.x] !== undefined)
      this.length = 0;

    if(queue.length > this.length)
      queue.shift();
  }

  //http://stackoverflow.com/a/16533568
  function djb2(str){
    var hash = 5381;
    for (var i = 0; i < str.length; i++) {
      hash = ((hash << 5) + hash) + str.charCodeAt(i); /* hash * 33 + c */
    }
    return hash;
  }
  function hashStringToColor(str) { //TODO: maybe use HSB and increase B?
    var hash = djb2(str);
    var r = (hash & 0xFF0000) >> 16;
    var g = (hash & 0x00FF00) >> 8;
    var b = hash & 0x0000FF;
    return "#" + ("0" + r.toString(16)).substr(-2) + ("0" + g.toString(16)).substr(-2) + ("0" + b.toString(16)).substr(-2);
  }
}
