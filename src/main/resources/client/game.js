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

  var players = {};

  var tickInterval;
  this.connect = function(){
    registerSocketCallbacks(gameSocket);
    registerSocketEmits(gameSocket);

    tickInterval = setInterval(this.tick, 200);

    var info = {
      name: name
    };
    console.log("Emitting NewSnakeEvent");
    gameSocket.emit("NewSnakeEvent", info, processFullUpdateEvent);
  };
  this.disconnect = function(){
    clearInterval(tickInterval);
    gameSocket.emit("disconnect");
    gameSocket.disconnect();
    $(document).off("keypress.socket");
    ctx.clearRect(0, 0, $(canvas).width(), $(canvas).height());
  };

  var currentTick = 0;
  var board = new Array(rows*cols);
  this.tick = function(){
    currentTick++;

    for(var id in players)
      if(players.hasOwnProperty(id))
        players[id].tick(board);

    board.fill(undefined);

    for(var id in players)
      if(players.hasOwnProperty(id))
        players[id].fillBoard(board);

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

  //HANDLE SERVER-SENT EVENTS ONLY (even ones that change your own state only)
  function processFullUpdateEvent(data){
    for(var id in data.snakes)
      if(data.snakes.hasOwnProperty(id)){
        if(!players.hasOwnProperty(id)){
          console.log("CREATED player "+data.snakes[id].name);
          console.log(data.snakes)
          players[id] = new Snake(data.snakes[id].name, rows, cols, {
            x: data.snakes[id].x,
            y: data.snakes[id].y,
            d: data.snakes[id].dir,
            l: 10,
            currentTick: data.tick
          });
          console.log(players)
        }else{
          //use the queue thing
        }
      }
    console.log("received FullUpdateEvent");
    //console.log(data.snakes, players)
  }
  function registerSocketCallbacks(sock){
    sock.on('NewSnakeEvent', function(data){
      console.log("received NewSnakeEvent: " + data.name);
      players[data.id] = new Snake(data.name, rows, cols, {
        x: data.x,
        y: data.y,
        d: data.dir,
        l: 10
      });
    });
    sock.on('FullUpdateEvent', processFullUpdateEvent);
    sock.on('TurnEvent', function(data){
      players[data.id].turn(data); //Not sure how to deal with data.tick
      console.log("received TurnEvent for" + players[data.id].name)
    });
    sock.on('SnakeDeathEvent', function(data){
      console.log("received SnakeDeathEvent for " + players[data.id].name);
    });
  }
}
function Snake(name, rows, cols, state){
  this.name = name;
  this.dir = state.d;
  if(typeof state.currentQueue == "object")
    this.queue = state.currentQueue;
  else
    this.queue = [{x:state.x, y:state.y}];//head is at end of array
  this.length = state.l;
  this.currentTick = state.tick;



  /* DIRECTION NUMBERS:
       0
     3   1
       2
  */

  this.turn = function(data){ //TODO: gosh this is hard to sync
    if(this.length == 0) return;
    if(data.twist == 1) this.dir = (this.dir+1)%4;
    else if(data.twist == -1) this.dir = (this.dir+3)%4;
  }
  this.fillBoard = function(board){
    for(var i = 0; i < this.queue.length; i++)
      board[this.queue[i].y * cols + this.queue[i].x] = hashStringToColor(this.name);
  }
  this.tick = function(oldboard){
    this.currentTick++;

    if(this.queue.length == 0){
      this.alive = false;
      return;
    }

    var headpos = this.queue[this.queue.length-1];
    var newpos = {x: headpos.x, y: headpos.y};
    if(this.length > 0){ //after dead
      if(this.dir == 0) newpos.y--;
      else if(this.dir == 1) newpos.x++;
      else if(this.dir == 2) newpos.y++;
      else if(this.dir == 3) newpos.x--;
      else console.error("No direction for snake");

      if(newpos.x >= cols) newpos.x -= cols;
      if(newpos.y >= rows) newpos.y -= rows;
      if(newpos.x < 0) newpos.x += cols;
      if(newpos.y < 0) newpos.y += rows;

      if(oldboard[newpos.y * cols + newpos.x] !== undefined)
        this.length = 0; //Can't push the next one, or else it might unexpectedly move one more square when the impeding snake leaves
      else
        this.queue.push(newpos);
    }

    if(this.queue.length > this.length) //Even active when length = 0, so can slowly wind to zero
      this.queue.shift();
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
