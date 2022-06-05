# Software Engineering Project A.Y. 2021-2022

## Simulation of the board game Eriantys 

### [![Report](https://github.com/Callum-Venini/ing-sw-2022-vitali-valenti-venini/actions/workflows/report.yml/badge.svg)](https://github.com/Callum-Venini/ing-sw-2022-vitali-valenti-venini/actions/workflows/report.yml)

<img src="https://www.craniocreations.it/wp-content/uploads/2021/06/Eriantys_scatolaFrontombra-300x300.png" alt="Eriantys' box cover">



**Group GC33**:
- [Marco Vitali](https://github.com/MarcoVitali0)
- [Daniele Valenti](https://github.com/danielevalenti)
- [Callum Venini](https://github.com/Callum-Venini)

# Table of contents

1. [Project specification](#project-specification)
2. [Implemented Features](#implemented-features)
3. [How to install](#how-to-install)
4. [How to run](#how-to-run)
   1. [Terminal](#terminal)
   2. [Linux/MacOsX](#linux--macosx)
   3. [Windows](#windows)

## Project specification

The project consists of a Java application for the board game [Eriantys](https://www.craniocreations.it/prodotto/eriantys/)

The final version will include:
- [ ] initial high level UML diagram
- [ ] final UML diagrams, automatically generated from the code
- [ ] working implementation of the game, compliant to the rules and requirements specified for the project
- [ ] documentation for the communication protocol between client and server
- [ ] documentation for the peer reviews (2 total)
- [ ] source code for the implementation
- [ ] source code for the JUnit tests

## Implemented features

| Feature                  |       Status       | Details                                       |
|--------------------------|:------------------:|-----------------------------------------------|
| Simplified rules         | :white_check_mark: | Normal mode only                              |
| Complete rules           |     :warning:      | Expert mode with 8 character cards            |
| CLI                      | :white_check_mark: | Command line input                            |
| GUI                      |     :warning:      | Graphical user interface                      |
| Socket                   | :white_check_mark: | Client-server architecture                    |
| Character cards          |     :warning:      | All 12 cards instead of just 8                |
| 4 Player mode            | :white_check_mark: | Allows 4 players to participate in teams of 2 |
| Multiple games           | :white_check_mark: | Allows simultaneous games on the same server  |
| Persistence              | :white_check_mark: | Save and load game state to disk              |
| Disconnection Resilience |        :x:         | Allows players to reconnect                   |

**Table Legend:**

:x: Not implemented

:warning: Work in progress

:white_check_mark: Implemented

## How to install

## How to run

### Terminal
    
To get info on command line arguments

    java Eriantys --help

To run client

    java Eriantys -client -u <username> [-cli | -gui ] -ip <server-ip> -p <server-port>

To run server

    java Eriantys -server -p <server-port>

### Linux / MacOsX

bash scripts not yet implemented

### Windows

batch files not yet implemented