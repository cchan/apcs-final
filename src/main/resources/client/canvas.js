function Game(canvas, room){
  var gameSocket = io.connect(window.location.hostname+':1234/'+room);
  gameSocket.on('TurnEvent', function(data){
    // {player, dir, tick}
  });
  
  //http://stackoverflow.com/a/25716620/1181387
  var ctx = canvas.getContext("2d");
  var $canvas = $(canvas);
  var canvasOffset = $canvas.offset();
  var offsetX = canvasOffset.left;
  var offsetY = canvasOffset.top;

  ctx.strokeStyle = 'black';
  ctx.lineWidth = 3;

  var cw = canvas.width;
  var ch = canvas.height;
  var cols = 25;
  var rows = 25;
  var padding = 5;
  var w = (cw - padding * cols) / cols;
  var h = (ch - padding * rows) / rows;
  var zoom = 1.00;
  var scalePtX, scalePtY;
  
  this.start = function(){
    draw();
  };

  this.draw = function() {
      var colors = []
      for (var y = 0; y < rows; y++) {
          for (var x = 0; x < cols; x++) {
              colors.push(randomColor());
          }
      }
      ctx.clearRect(0, 0, cw, ch);
      ctx.save();
      ctx.translate(scalePtX - scalePtX * zoom, scalePtY - scalePtY * zoom);
      ctx.scale(zoom, zoom);
      ctx.globalAlpha = 0.25;
      for (var y = 0; y < rows; y++) {
          for (var x = 0; x < cols; x++) {
              ctx.fillStyle = colors[y * cols + x];
              ctx.fillRect(x * (w + padding), y * (h + padding), w, h);
          }
      }
      ctx.globalAlpha = 1.00;
      ctx.beginPath();
      ctx.arc(scalePtX, scalePtY, 10, 0, Math.PI * 2);
      ctx.closePath();
      ctx.stroke();
      ctx.restore();
  };

  function randomColor() {
      return ('#' + Math.floor(Math.random() * 16777215).toString(16));
  }
}