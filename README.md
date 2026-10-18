# Digital Logic Circuit Simulator (simple, for the Java lab)

A small console program that builds digital circuits from logic gates, calculates their outputs, prints
truth tables and saves them in a text file. Plain Java 8 syntax, standard library only (`java.util`, `java.io`).

## How to run
Linux / macOS:
```
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out logicsim.Main          # menu program
java -cp out logicsim.Tests         # self-check (11 checks)
```
Windows (Command Prompt):
```
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
java -cp out logicsim.Main
```
Run from the project folder so that `examples/...` paths work. Any IDE (Eclipse, IntelliJ, VS Code) also works:
create a project, copy the `src` folder, run `logicsim.Main`.

## Project structure
```
src/logicsim/
  Main.java                      driver class: menu, gate demo, file load / save
  Tests.java                     small test program (prints PASS / FAIL)
  gates/                         package 1: the building blocks
    Displayable.java             interface (display)
    Wire.java                    one wire = name + 0/1 value
    Gate.java                    abstract class, base of all gates
    AndGate, OrGate, XorGate, NandGate, NorGate, NotGate   child classes
  circuit/                       package 2: putting gates together
    Circuit.java                 holds wires + gates, simulates, builds truth table
    CircuitParser.java           reads a circuit from a text file
    InvalidCircuitException.java custom exception
examples/                        half_adder.net, full_adder.net, mux2.net (+ 2 wrong files for testing errors)
```

## Circuit file format
```
# comment
INPUT  A B CIN
OUTPUT SUM COUT
XOR    P A B          <- GATETYPE  outputWire  inputWire(s)
XOR    SUM P CIN
AND    G A B
AND    T P CIN
OR     COUT G T
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
| 2 | `do-while`, `while`, `for`, `switch`, `Scanner` input, menu | `Main.main`, `testInputsFromFile` |
| 3 | Arrays, bitwise operators (`>>`, `<<`, `&`), ternary `? :` | `Circuit.buildTruthTable`, `Gate.toBit`, `Tests.run` |
| 5 | String handling (`split`, `trim`, `indexOf`, `substring`, `equals`), file read and write | `CircuitParser`, `Main.truthTableFromFile` |
| 5/6 | Encapsulation, constructors, getters/setters, composition | `Wire`, `Gate`, `Circuit` |
| 6 | Inheritance, method overriding, array of objects, runtime polymorphism | `Gate` + `AndGate`...; `Main.gateDemo` |
| 7 | Abstract class, abstract method, dynamic binding | `Gate.evaluate()` |
| 8 | Interface and packages, class outside the package imports them | `Displayable`; `gates`, `circuit`; `Main` |
| 9 | Custom exception, `throw`/`throws`, multiple `catch` for one `try`, `finally` | `InvalidCircuitException`, `Main.main`, `CircuitParser` |
| 10 | Multithreading | not used (a gate simulator does not need threads) |
