**GALCON**

As long as there’s been a galaxy, people have been trying to conquer it. 

Planets produce spaceships. Spaceships can attack planets. Can you create a fleet commander to outwit the rest and assert dominance?

Galcon is a remake of the 2010 Google Planet Wars AI Challenge written to allow educators an easy way to introduce AI concepts through a head to head game. I also hope that others still interested in the AI challenge will utilize this as a way to keep writing bots. 

Each player’s goal is to be the only one left on the map. Each player starts with one planet. Planets produce units while a player controls them (larger ones produce faster) and players can order their fleets at other planets - friendly to reinforce, enemy to conquer. Every ship is equal - for example, 3 ships hitting an enemy planet with 5 fleets will destroy the whole attacking fleet and 3 of the defending ships. Once launched a fleet cannot be recalled.

The way AIs interact with the engine is through Actions. An Action takes a starting planet, a target planet and a number of units to put in the fleet. On each game tick all players create their Actions for the engine to enact before the next tick. The command may be simple but deciding those three crucial arguments is where the Intelligence part of the AI comes into play. 

**How To Use**

Writing your own AI for Galcon is as easy as forking this repository, opening your favorite git client and IDE, adding a package in “ais” and getting started. Several existing AIs and documentation throughout the codebase should help you on your path. If you feel you’ve gotten an AI good enough to publish create a Pull Request to get it added to the set of example AIs to beat. 

Changes to the engine, visualizers or utils are all welcome as well - just open a PR. Please make sure to take care of any merge conflicts, write clean code that follows style, and provide justifications for the changes you’re making. 

**For Educators**

I am currently working on an example lesson plan to utilize Galcon. I believe this codebase can be useful for any class where the teacher is comfortable introducing AI concepts. I am working to create AIs to show off specific AI concepts while leaving enough open for students to use those concepts in their own ways. 

The project is also set up to be useful for introducing students to git and understanding larger code bases. One of my goals in the lesson plan is to show students how git can allow them to work on different tasks in the same code base using git. 

**Language**

This project is maintained in Java because it’s a very common language for new computer science students to be taught. I am working to isolate the “Action” concept in a way that it is possible to have AIs in any language and so that engines can be written and used interchangeably. I hope to one day write alternate engines in most of the common languages. 

The need to prevent AIs from cheating allows this project to give good examples of the differences in Public, Protected, Package Private and Private. 

**History**

This project started in 2011 in my Advanced Topics Computer Science class while at Menlo High School. In our time spent on Artificial Intelligence we wrote games to then write AIs for those games. Galcon (originally developed by Phil Hassey) was one of my favorite games throughout my childhood and was quickly accepted by my friends as a good game to emulate. The concept of [*Galactic Conquest*](https://en.wikipedia.org/wiki/Galcon) has existed for [a while](https://www.galcon.com/classic/history.html), found in various flash games and even Google’s 2010 [Planet Wars](http://aichallenge.org/) AI Challenge (sadly many of Google’s resources seem to have gone dark, but many player’s writeups can be found around the internet). With help from my good friends Chris Sauer and Yujin Ariza we quickly got the base game engine and a visualizer running while many in the class took stabs at making the best AI possible. 

For being a bunch of sixteen year olds the code was actually quite impressive, but left a lot to be desired. Sadly our idea of version control at the time was emailing files back and forth, interspersed with pastebin. My personal version control was my Dropbox. What I would give to have a commit history from then! I do still have my “backup folders” (titled “galcon 1” through “galcon 11”) which I hope to revisit and stick at the start of this repository.

A few years later while at Cal Poly San Luis Obispo I took the AI course with my good friends Tyler Dahl and Jono Chadwell. When asked to create a project to show off AI concepts I had the perfect code base to dust off. I took it as a chance to put in a lot of proper refactors. With the help of them and a few other classmates I got some good example AI’s put in and cleaned the engine code up a bit. Jono was also kind enough to pull in a 3D visualizer he had from another project. Though much of our commit history is rather embarrassing I opted to retain it, as a piece of history on the project. 

Ever since graduating high school I’ve had this goal bouncing around of maintaining this project to the point of sharing it with others, especially educators. That’s where this is at now! While there’s still a lot of stuff I want to do to the engine the Action based API is something I think will remain. As such AIs can start being written in parallel to engine and setup improvements. While there are many resources out there created from the Google AI challenge I chose to keep all of my engine code original as I enjoy the design and implementation process. 

