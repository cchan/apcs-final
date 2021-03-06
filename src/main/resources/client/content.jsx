var NameSection = React.createClass({
  propTypes: {
    name: React.PropTypes.func.isRequired,
    processRoomList: React.PropTypes.func.isRequired,
    socket: React.PropTypes.object.isRequired
  },
  onChange: function(e){
    this.props.name(e.target.value);
  },
  onSubmit: function(e){
    if(this.props.name().length < 3)
      log('red', 'You must enter a name of at least three characters');
    else
      this.props.socket.emit('ConnectEvent', {name: this.props.name()}, this.props.processRoomList);
    e.preventDefault();
  },
  render: function(){
    return (
      <section>
        <form onSubmit={this.onSubmit}>
          <input onChange={this.onChange} value={this.props.name()} placeholder="Name" />
          <button>Connect</button>
        </form>
      </section>
    )
  }
});

var RoomList = React.createClass({
  propTypes: {
    roomList: React.PropTypes.array.isRequired,
    processRoomSelect: React.PropTypes.func.isRequired,
  },
  render: function(){
    var self = this;
    var createRoom = function(roomName){
      if(roomName != "" && roomName != "/") //TODO: actually do something with this #anchor
        return <li><a href={'#'+roomName.substring(1)} onClick={self.props.processRoomSelect}>{roomName.substring(1)}</a></li>;
    };
    var list = this.props.roomList.map(createRoom).filter(function(x){return typeof x !== 'undefined';});

    if(list.length == 0)
      return <ul><li><i>No games to display</i></li></ul>;
    else
      return <ul>{list}</ul>;
  }
});

var RoomSection = React.createClass({
  propTypes: {
    roomList: React.PropTypes.array.isRequired,
    processRoomList: React.PropTypes.func.isRequired,
    processRoomSelect: React.PropTypes.func.isRequired,
    processReturnToName: React.PropTypes.func.isRequired,
    socket: React.PropTypes.object.isRequired
  },
  getInitialState: function(){
    return {newRoom: ''};
  },
  onChange: function(e){
    this.setState({newRoom: e.target.value});
  },
  onSubmit: function(e){
    if(this.state.newRoom.length < 3)
      log('red', 'A game name must be at least three characters');
    else
      this.props.socket.emit('RoomCreateEvent', {name: this.state.newRoom}, this.props.processRoomList);
    e.preventDefault();
  },
  refreshList: function(e){
    this.props.socket.emit('RoomRefresh', {}, this.props.processRoomList);//<div><button onClick={this.props.refreshList}>Refresh List</button></div>
  },
  render: function(){
    return (
      <section>
        <div><button onClick={this.props.processReturnToName}>Back to Name Entry</button></div>
        <br />
        <RoomList roomList={this.props.roomList} processRoomSelect={this.props.processRoomSelect} />
        <form onSubmit={this.onSubmit}>
          <input onChange={this.onChange} value={this.state.newRoom} />
          <button>Create Game</button>
        </form>
      </section>
    );
  }
});

var ChatBox = React.createClass({
  propTypes: {
    name: React.PropTypes.string.isRequired,
    chats: React.PropTypes.array.isRequired,
    gameSocket: React.PropTypes.object.isRequired
  },
  getInitialState: function(){
    return {newChat: '', chats: []};
  },
  componentWillMount: function(){
    this.props.gameSocket.on("ChatEvent", function(data){
      setState({chats: chats.concat([data])}); //There's probably a way to make a React class that deletes itself after, say, 5 seconds.
    });
  },
  onchange: function(e){
    this.setState({newChat: e.target.value});
  },
  send: function(){
    this.props.gameSocket.emit("ChatEvent", {author: this.props.name, message: this.state.newChat}, function(data){
      this.setState({newChat: ''});
    });
  },
  chatHTML: function(chat){
    return <li><b>{chat.author}:</b> {chat.message}</li>;
  },
  render: function(){
    return (
      <div>
        <input onChange={this.onchange} />
        <ul>{this.state.chats.map(this.chatHTML)}</ul>
      </div>
    );
  }
});

var GameSection = React.createClass({
  game: undefined,
  gameSocket: undefined,
  propTypes: {
    name: React.PropTypes.string.isRequired,
    room: React.PropTypes.string.isRequired,
    processReturnToRoomSelect: React.PropTypes.func.isRequired
  },
  getInitialState: function(){
    return {chats: []};
  },
  componentWillMount: function(){
    this.gameSocket = io(window.location.hostname + ':1234/' + this.props.room);
  },
  componentDidMount: function(){
    this.game = new Game(document.getElementById("gameCanvas"), this.gameSocket, this.props.name);
    this.game.connect();
  },
  componentWillUnmount: function(){
    this.game.disconnect();
    this.gameSocket.disconnect();
  },
  render: function(){
    return (
      <section>
        <h2>{this.props.room}</h2>
        <span>Controls: z=left, x=right</span>
        <div><button onClick={this.props.processReturnToRoomSelect}>Back to Game Select</button></div>
        <canvas id="gameCanvas" width="300" height="300"></canvas>
        <ChatBox name={this.props.name} chats={this.state.chats} gameSocket={this.gameSocket}/>
      </section>
    );
  }
});

var Main = React.createClass({
  getInitialState: function(){
    var socket =  io(window.location.hostname+':1234');
    socket.on('LogEvent', function(data) {
      log(data.color, data.text);
    });
    return {tabIndex: 0, name: '', roomList: [], room: '', socket: socket};
  },
  processRoomList: function(data){
    this.setState({roomList: data, tabIndex: 1});
  },
  processRoomSelect: function(e){
    this.setState({room: e.target.innerText, tabIndex: 2});
    e.preventDefault();
  },
  processReturnToName: function(){
    this.setState({tabIndex: 0, room: ''});
  },
  processReturnToRoomSelect: function(){
    this.setState({tabIndex: 1, room: ''});
  },

  name: function(n){
    if(n === undefined)return this.state.name;
    else this.setState({name: n});
  },
  getUsernameMessage: function(){
    if(this.state.tabIndex > 0)
      return <span>You are <b>{this.state.name}</b></span>;
    else return '';
  },

  render: function(){
    var tabs = [ //This will create new element objects every time. This is good. Although potentially unstable... render() can be called anytime
      <NameSection
          name={this.name.bind(this)}
          processRoomList={this.processRoomList.bind(this)}
          socket={this.state.socket} />,
      <RoomSection
          roomList={this.state.roomList}
          processRoomList={this.processRoomList.bind(this)}
          processRoomSelect={this.processRoomSelect.bind(this)}
          processReturnToName={this.processReturnToName.bind(this)}
          socket={this.state.socket} />,
      <GameSection
          room={this.state.room}
          processReturnToRoomSelect={this.processReturnToRoomSelect.bind(this)}
          name={this.state.name} />
    ];
    return (
      <main>
        <header>
          <h1>MultiSnake</h1>
          <div>{this.getUsernameMessage()}</div>
        </header>
        {tabs[this.state.tabIndex]}
      </main>
    );
  }
});

ReactDOM.render(<Main />, document.getElementById('react-wrapper'));
