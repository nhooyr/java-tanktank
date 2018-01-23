# java-tanktank

A tank battle game implemented in java using javafx.

## Help

### Gameplay

Tank Tank is a multi-player game involving two tanks that are controlled by
two players. The two tanks spawn randomly in a maze. Each tank can fire bullets.
If a bullet hits another tank then the tank dies and the surviving tank is the
winner. If both tanks die at once, then the game is declared a tie.

Bullets have a lifetime of 15 seconds and will disappear after that.
Each tank can have 5 bullets max in flight at once. If a tank has 5
bullets in flight, then its head will be changed to a lighter color
to indicate this to the player. A tank can kill itself from its own bullet.


Tanks may pass through each other and may even spawn on the same cell in the maze.
The two tanks will always spawn facing opposite directions. 

### Controls

| Action      | Pink Tank key | Blue Tank Key |
| ----------- | ------------- | ------------- |
| Forward     | W             | ↑             |
| Reverse     | S             | ↓             |
| Right       | D             | →             |
| Left        | A             | ←             |
| Fire Bullet | v             | .             |

That should be all you need to know to begin playing. Have fun! :smiley:
