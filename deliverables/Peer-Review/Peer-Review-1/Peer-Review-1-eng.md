# Peer-Review 1: UML

Marco Vitali, Daniele Valenti, Callum Venini

Group 33

Evaluation of group 43's UML class diagram.


## Pros

- Colour and Team are enums

- Game:
  - it's a central class to access other parts of the model

- Player:
  - team is read only
  - Entrance, DiningRoom and TeacherTable are hashMaps (described only in the documentation)
  - TowerTable and coins are handled with ints

- Island
  - it's a circular linked list

- Archipelago
  - it uses an ID to differentiate instances

- Assistant
  - Using JSON is a great way to generate cards


## Cons

- The wizard class enum seems to be superfluous

- Expert Mode aspects are mostly absent (referenced in the bullet point below), even though the 'coins' attribute is present in the Player class

- Character cards are absent

- Player:
  - nickname is of type static
  - teacherTable and hasTeacher offer the same information
  - Entrance, DiningRoom, TeacherTable and TowerTable could be in another class (e.g. SchoolBoard)
  - Coins attribute only in player but not in Game, they should communicate to limit maximum coins
  - giveCoins() exists, but there is no method to remove them, also coins is public anyway
  - compareDominance() fits more as a method for a controller class

- Game:
  - Methods in game are not explicitly described, only inferred, so it's not possible to understand if the right quota is reached
  - Cloud e Bag are public
  - There are no methods to obtain the values of studentsPerColour, totalColour and numPlayers as they are private
  - studentsPerColour, totalColour and numPlayers can be calculated from each other, so only one of them is necessary

- Island
  - it's a superfluous class, it could be merged with Archipelago

- Assistant:
  - Missing attributes for JSON imports

- Archipelago
  - hasMotherNature and students are public
  - countInfluence() should be in a controller class
  - removeStudents() is not a game feature for island tiles
  - removeTower() should only alter the team and swap out the tower colour


## Comparison between architectures
