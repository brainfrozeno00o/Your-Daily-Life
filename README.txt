1) Make a text file and copy this shit:

@echo off
javac -d bin src/anno/*.java
javac -cp lib/*;bin -d bin src/room/*.java
javac -cp lib/*;bin -d bin src/maze/*.java
java -cp lib/*;bin maze/DailyLifeGUI
EXIT

2) Save it as YourDailyLife.bat.

3) Run YourDailyLife.bat and enjoy the magic! :)