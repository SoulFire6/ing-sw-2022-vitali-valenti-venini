# Peer-Review 1: UML

Marco Vitali, Daniele Valenti, Callum Venini

Group 33

Evaluation of group 53's Model, Controller and Network diagrams.

## Pros

- Tile: using an abstract class reduces code duplication
- Student: it is possible to distinguish individual student disks
- Cloud: contains only what is needed, it's a very compact class
- Isle: it uses a strategy pattern for influence calculation
- Board: entrance and dinning room are limited by attributes
- Using a client model is a great way to pass only stricly necessary data to client's view
- ActionTurnHandler: good use of the Strategy pattern in CheckProfessorStrategy
- Action phase: sequence diagram relative to the movement of student disks is clear and correct
- Character sequence diagram: clear and correct

## Cons

- Enum: faction has an unneeded extra value (Empty) which can be replaced in your code with a null
- Student: it is more efficient to use a hashmap for calculations
- Cloud: it's not clear from the model if the max slots on a cloud are checked, it could be in the controller
- Isle: influence calculation should be in the controller, non in the model
- AggregatedIsland: contains duplicate methods of Isle and also inherits them from Isle, join method should be in a controller
- Board: the dinning room attribute uses an array of integers
- Team: could be replaced with a reference to the player's teammate as only teams of 1/2 players are possible
- GameModel: active character cards are managed as an arraylist in a model class
- It is not clear what MoveMnStrategy is for
- Phase is not clear - could be action phases o general game phases with assistant card choice ommitted
- Planning phase: the sequence diagram has a slight mistake, whilst choosing an assistant card player order is not only not  considered, but is disregarded as being important because player who make a mistake or do not submit in time will be asked to submit them again at the end
- Action phase: it seems some mother nature max movement checks are missing. Also it should send player schoolboard back to the clients when islands get conquered and the number of towers on the board changes.
- CloudChoiceMessage phase: missing empty cloud error and also doesn't send updated player board with new student disks in the entrance
- Be warned that many values of arity on the UML diagram are inverted

## Comparison between architectures

We also use an abstract Tile class in our model to manage student disk, avoiding duplicate code and so it's a great idea.
We implemented message classes as well to identify various game moves.

We manage islands a bit differently by using one class instead of two, merged islands are identified by the presence of multiple towers.
Our controller is divided into multiple classes that manage different parts of the model, separating game logic more evenly.
