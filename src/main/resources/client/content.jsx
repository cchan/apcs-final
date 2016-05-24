var NameSection = React.createClass({
  propTypes: {
    name: React.PropTypes.func.isRequired,
    processRoomList: React.PropTypes.func.isRequired,
  },
  onChange: function(e){
    this.props.name(e.target.value);
  },
  onSubmit: function(e){
    if(this.props.name().length < 3)
      log('red', 'You must enter a name of at least three characters');
    else
      socket.emit('ConnectEvent', {name: this.props.name()}, this.props.processRoomList);
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
      if(roomName != "")
        return <li><a href={'#'+roomName} onClick={self.props.processRoomSelect}>{roomName}</a></li>;
    };
    var list = this.props.roomList.map(createRoom);
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
      socket.emit('RoomCreateEvent', {name: this.state.newRoom}, this.props.processRoomList);
    e.preventDefault();
  },
  render: function(){
    return (
      <section>
        <RoomList roomList={this.props.roomList} processRoomSelect={this.props.processRoomSelect} />
        <form onSubmit={this.onSubmit}>
          <input onChange={this.onChange} value={this.state.newRoom} />
          <button>Create Game</button>
        </form>
      </section>
    );
  }
});

var GameSection = React.createClass({
  render: function(){
    return (
      <section>
        <canvas></canvas>
      </section>
    );
  }
});

var Main = React.createClass({
  getInitialState: function(){
    return {tabIndex: 0, name: '', roomList: [], room: ''};
  },
  
  processRoomList: function(data){
    this.setState({roomList: data, tabIndex: 1});
  },
  processRoomSelect: function(e){
    this.setState({room: e.target.innerText, tabIndex: 2});
    e.preventDefault();
  },
  processReturnToName: function(){
    this.setState({tabIndex: 0});
  },
  
  name: function(n){
    if(n === undefined)return this.state.name;
    else this.setState({name: n});
  },
  
  render: function(){
    var tabs = [
      <NameSection 
          name={this.name.bind(this)} 
          processRoomList={this.processRoomList.bind(this)} />, 
      <RoomSection 
          roomList={this.state.roomList} 
          processRoomList={this.processRoomList.bind(this)} 
          processRoomSelect={this.processRoomSelect.bind(this)} 
          processReturnToName={this.processReturnToName.bind(this)} />, 
      <GameSection 
          room={this.state.room} />
    ];
    return (
      <main>
        <header>
          <h1>MultiSnake</h1>
        </header>
        {tabs[this.state.tabIndex]}
      </main>
    );
  }
});

ReactDOM.render(<Main />, document.getElementById('react-wrapper'));
