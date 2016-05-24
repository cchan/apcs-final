var NameSection = React.createClass({
  getInitialState: function(){
    return {name: ''};
  },
  onChange: function(e){
    this.setState({name: e.target.value});
  },
  onSubmit: function(e){
    e.preventDefault();
  },
  render: function(){
    return (
      <section>
        <form onSubmit={this.onSubmit}>
          <input onChange={this.onChange} value={this.state.name} placeholder="Name" />
          <button>Connect</button>
        </form>
      </section>
    )
  }
});

var RoomList = React.createClass({
  render: function(){
    var createRoom = function(roomName){
      return <li>{roomName}</li>;
    };
    console.log(this.props);
    return <ul>{this.props.rooms.map(createRoom)}</ul>;
  }
});

var RoomSection = React.createClass({
  getInitialState: function(){
    return {rooms: [], newRoom: ''};
  },
  onChange: function(e){
    this.setState({newRoom: e.target.value});
  },
  onSubmit: function(e){
    e.preventDefault();
    this.setState({rooms: this.state.rooms.concat([this.state.newRoom]), newRoom: ''});
  },
  render: function(){
    return (
      <section>
        <RoomList rooms={this.state.rooms} />
        <form onSubmit={this.onSubmit}>
          <input onChange={this.onChange} value={this.state.newRoom} />
          <button>Create Room</button>
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
  render: function(){
    return (
      <main>
        <header>
          <h1>MultiSnake</h1>
        </header>
        <NameSection />
        <RoomSection />
        <GameSection />
      </main>
    );
  }
});

ReactDOM.render(<Main />, document.getElementById('react-wrapper'));
