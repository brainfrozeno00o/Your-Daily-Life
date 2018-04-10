@echo off
javac -d bin src/anno/*.java
javac -cp lib/*;bin -d bin src/room/*.java
javac -cp lib/*;bin -d bin src/maze/*.java
java -cp lib/*;bin maze/DailyLifeGUI
EXIT
