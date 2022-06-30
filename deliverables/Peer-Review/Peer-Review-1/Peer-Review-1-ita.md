# Peer-Review 1: UML

Marco Vitali, Daniele Valenti, Callum Venini

Gruppo 33

Valutazione del diagramma UML delle classi del gruppo 43.

## Lati positivi

- Colour e Team sono enum

- Game:
  - Classe centrale che ha accesso al resto del modello

- Player:
  - team è read only
  - Entrance, DiningRoom e TeacherTable sono hashMap (aspetto descritto solo nella documentazione)
  - TowerTable e coins sono gestiti con degli int

- Island
  - è una lista circolare

- Archipelago
  - Ha un ID che lo differenzia

- Assistant
  - Utilizzare il JSON è un buon metodo per generare le carte


## Lati negativi

- La classe wizard sembra essere superflua

- Molti aspetti della Expert Mode assenti (vedi punto successivo), nonostante l’attributo ‘coins’ nella classe Player

- Manca la classe delle carte personaggio?

- Player:
  - nickname statico
  - teacherTable e hasTeacher ti danno le stesse informazioni
  - Entrance, DiningRoom, TeacherTable e TowerTable potrebbero essere in una classe a parte (e.g. SchoolBoard)
  - Coins in player ma non in Game, dovrebbero comunicare per limitare il numero massimo di coins
  - giveCoins() esiste, ma non un metodo che riduce i coins, in più l'attributo è pubblico
  - compareDominance() è un metodo più adatto al controller

- Game:
  - Metodi in game devono essere esplicitati, per comprenderne l’uso
  - Cloud e Bag sono pubblici
  - Non esistono metodi per ricavare studentsPerColour, totalColour e numPlayers
  - studentsPerColour, totalColour e numPlayers sono ricavabili uno dall’altro, solo uno dei tre è necessario

- Island
  - è una classe superflua, Archipelago la potrebbe inglobare

- Assistant:
  - Mancano gli attributi nel cui è possibile caricare il json

- Archipelago
  - hasMotherNature e students sono pubblici
  - countInfluence() dovrebbe essere in un controller
  - removeStudents() non è una feature del gioco
  - removeTower() dovrebbe cambiare team e non solo rimuovere le torri


## Confronto tra le architetture
