function Game(canvas, room, name){
  //TODO: is this resizable?
  var cols = 25;
  var rows = 25;
  var padding = 1;

  //http://stackoverflow.com/a/25716620/1181387
  var ctx = canvas.getContext("2d");
  ctx.strokeStyle = 'purple';
  ctx.lineWidth = 3;

  var players = [];

  var gameSocket;
  var tickInterval;
  this.connect = function(){
    console.log("asdf");
    gameSocket = io(window.location.hostname+':1234'+room);
    registerSocketCallbacks(gameSocket);
    registerSocketEmits(gameSocket);

    tickInterval = setInterval(this.tick, 200);

    players.push(new Snake(name, rows, cols));
  };
  this.disconnect = function(){
    clearInterval(tickInterval);
    gameSocket.disconnect();
    $(document).off("keypress.socket");
    ctx.clearRect(0, 0, $(canvas).width(), $(canvas).height());
  };

  var currentTick = 0;
  this.tick = function(){
    currentTick++;

    var board = [];
    for (var y = 0; y < rows; y++){
      for (var x = 0; x < cols; x++){
        board.push('white');
      }
    }

    for(var i = 0; i < players.length; i++){
      players[i].tick();
      players[i].fillBoard(board);
    }

    draw(board);
  };

  function draw(board) {
      ctx.clearRect(0, 0, $(canvas).width(), $(canvas).height());
      ctx.save();
      ctx.globalAlpha = 1.00;

      var w = ($(canvas).width() - padding * cols) / cols; // dimensions of individual squares
      var h = ($(canvas).height() - padding * rows) / rows;

      for (var y = 0; y < rows; y++) {
          for (var x = 0; x < cols; x++) {
              ctx.fillStyle = board[y * cols + x];
              ctx.fillRect(x * (w + padding), y * (h + padding), w, h);
          }
      }
      ctx.stroke();
      ctx.restore();
  };

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

    sock.on('FullUpdate', function(data){
      //every 2 seconds or something

      /*
      players = [
        {
          startPos = {x: int, y: int},
          startDir = int [0,3],
          turns = [ {tick: int, dir: int {1,0,-1}} ]
        }
      ]

      DIRECTION NUMBERS:
         0
       3   1
         2
      */
      console.log("recieved FullUpdate");
      console.log(data)
    });
    sock.on('TurnEvent', function(data){
      for(var i = 0; i < players.length; i++)
        if(players[i].name == data.name)
          players[i].turn(data.twist); //Not sure how to deal with data.tick
      console.log("received TurnEvent")
      console.log(data);
    });
  }
}
function Snake(name, rows, cols){
  this.name = name;
  var queue = []; //head is at end of array
  var dir = 0;
  this.length = 10; //no eating for now

  queue.push({
    //x: Math.floor(Math.random() * (cols - 10) + 5),
    x: 5,
    y: 5
    //y: Math.floor(Math.random() * (rows - 10) + 5)
  });
  //dir = Math.floor(Math.random() * 4);
  dir = 1;


  this.turn = function(twist){
    if(twist == 1) dir = (dir+1)%4;
    else if(twist == -1) dir = (dir+3)%4;
  }
  this.fillBoard = function(board){
    for(var i = 0; i < queue.length; i++)
      board[queue[i].y * cols + queue[i].x] = "black"; //hashStringToColor(this.name)
  }
  this.getCSSColor = function(){
    return hashStringToColor(this.name);
  }
  this.tick = function(){
    var headpos = queue[queue.length-1];
    var newpos = {x: headpos.x, y: headpos.y};
    if(dir == 0) newpos.y--;
    if(dir == 1) newpos.x++;
    if(dir == 2) newpox.y++;
    if(dir == 3) newpos.x--;
    queue.push(newpos);
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
  function hashStringToColor(str) {
    var hash = djb2(str);
    var r = (hash & 0xFF0000) >> 16;
    var g = (hash & 0x00FF00) >> 8;
    var b = hash & 0x0000FF;
    return "#" + ("0" + r.toString(16)).substr(-2) + ("0" + g.toString(16)).substr(-2) + ("0" + b.toString(16)).substr(-2);
  }
}
