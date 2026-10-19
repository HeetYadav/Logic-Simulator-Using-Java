# Digital Logic Circuit Simulator 

A small console program that builds digital circuits from logic gates, calculates their outputs and prints
truth tables. Plain Java 8 syntax, standard library only (`java.util`). There is no file input or output:
everything is typed in the terminal.

## How to run
Linux / macOS:
```
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out logicsim.Main
java -cp out logicsim.Tests
```
Windows (Command Prompt):
```
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
java -cp out logicsim.Main
```
Any IDE (Eclipse, IntelliJ, VS Code) also works: create a project, copy the `src` folder, run `logicsim.Main`.

## Using the program
```
Main menu
 1. Single gate        -> choose AND / OR / XOR / NAND / NOR / NOT (2 to 4 inputs; NOT has 1), then circuit menu
 2. All gates together -> type A and B once, see all six gates at the same time
 3. Half adder         -> circuit menu
 4. Full adder         -> circuit menu
 0. Exit

Circuit menu (same for gates and adders)
 1. Enter my own inputs -> type 0 or 1 for every input, see the gates and outputs,
                           then "Try other inputs? (y/n)" - repeat as often as you want
 2. Show truth table
 0. Back
```
Wrong input (letters, numbers other than 0 or 1, wrong menu numbers) never crashes the program:
it prints a message and asks again.

## Project structure
```
src/logicsim/
  Main.java                      driver class: all menus and keyboard input, builds the adders
  Tests.java                     small test program (prints PASS / FAIL)
  gates/                         package 1: the building blocks
    Displayable.java             interface (display)
    Wire.java                    one wire = name + 0/1 value
    Gate.java                    abstract class, base of all gates
    AndGate, OrGate, XorGate, NandGate, NorGate, NotGate   child classes
  circuit/                       package 2: putting gates together
    Circuit.java                 holds wires + gates, simulates, builds truth table
    InvalidCircuitException.java custom exception
```

## How the simulation works
1. Every wire starts at 0. The user (or the truth-table loop) sets the input wires.
2. `Circuit.simulate()` asks every gate to recalculate its output (`Gate.update()`).
3. This repeats until no output changes any more (the circuit is stable). If it never stabilises
   (for example a NOT gate fed by its own output), an `InvalidCircuitException` is thrown.
4. The truth table does this for all `2^n` input combinations and prints the outputs.

## Which lab concepts are used where
| Practical | Concept | Where in the project |
|---|---|---|
| 1 | Classes, program structure, `main()`, console output | `Main.java` |
| 2 | `do-while`, `while`, `for`, `switch`, `Scanner` input, menu | `Main.main`, `circuitMenu`, `enterInputs`, input helpers |
| 3 | Arrays, bitwise operators (`>>`, `<<`, `&`), ternary `? :` | `Circuit.buildTruthTable`, `Gate.toBit`, `Tests.run` |
| 5 | String handling (`equals`, `toLowerCase`, `trim`, `String.valueOf`) | input helpers, `Main.singleGate` |
| 5/6 | Encapsulation, constructors, getters/setters, composition | `Wire`, `Gate`, `Circuit` |
| 6 | Inheritance, method overriding, array of objects, runtime polymorphism | `Gate` + `AndGate`...; `Main.allGates` |
| 7 | Abstract class, abstract method, dynamic binding | `Gate.evaluate()` |
| 8 | Interface and packages, class outside the package imports them | `Displayable`; `gates`, `circuit`; `Main` |
| 9 | Custom exception, `throw`/`throws`, `try`/`catch`, `NumberFormatException` | `InvalidCircuitException`, `Main.main`, `readChoice` |
| 10 | Multithreading | not used |
