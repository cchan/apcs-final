APCS-FINAL
==========
[![Build Status](https://travis-ci.org/cchan/apcs-final.svg?branch=master)](https://travis-ci.org/cchan/apcs-final)
[![Coverage Status](https://coveralls.io/repos/github/cchan/apcs-final/badge.svg?branch=master)](https://coveralls.io/github/cchan/apcs-final?branch=master)


Clive Chan, 2016

An online multiplayer game for my AP Computer Science final project. (INCOMPLETE)


Play
----
You can play it at [apcs.clive.io](http://apcs.clive.io)!

At the moment, it's a multiplayer snake game, where you can join and leave and create new games and stuff like that.


Code
----
My code isn't currently very well documented, but I'll be trying to do that sometime.
All server code is in `src\main\java\com\cliveio\apcs`, and all client code is in `src\main\resources\client`.

The main Java file is `ServerStarter`, which starts `HTTPServer` and `SocketServer`. `HTTPServer` just serves the client files (using the HTTP server provided by Java). `SocketServer` does the interesting stuff, dealing with events from the client and everything like that. The SocketIO library handles only the mechanics of the Sockets protocol, so I have to write lots of callbacks and manage state. Java is not ideal for this sort of project, with its strict and highly object-oriented system.


Build
-----
First, install Maven.

Then simply run `mvn clean install exec:java` in the root directory and Maven should take care of the rest for you.
Make sure ports `10002` (for http) and `1234` (for sockets) are open.

Then browse to `localhost:10002`.


Challenges
----------
It was a little too ambitious of a project, since I don't know Maven, SocketIO, or React, and learned them on the spot. I also don't know anything about network data synchronization, which it turns out is pretty hard. I'm pretty happy that I learned so much, though, and hopefully I'll be working more on this over the summer and adding even more random stuff.


New technologies learned/used in this project
---------------------------------------------
* Java (especially lots of more obscure stuff)
* Maven
  * Unit testing (or automated testing at all) as a concept
* Travis CI
* Coveralls
* SocketIO
* React
* Chrome DevTools (persistent workspaces are soooooo cool)
* Push-to-deploy hooks with Git (so that my server restarts and runs right away when I `git push` to it over `ssh`)
