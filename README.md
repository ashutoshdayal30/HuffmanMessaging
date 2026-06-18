# Huffman Messaging

Huffman Messaging is a small Java messaging demo built as a CSC345 final project. It connects a JavaFX chat window to a terminal-side console, then uses a Huffman tree to encode each message before the other side decodes and displays it.

This is not a network chat app and it does not generate replies by itself. It is a local two-sided demo: one side is the GUI, the other side is the terminal running the program.

## What It Does

- Opens a JavaFX messaging window where the GUI user can type messages.
- Keeps the terminal open as the second messaging side.
- Builds a new Huffman tree for each message that gets sent.
- Encodes the message into a byte array using the Huffman tree paths.
- Sends the encoded data, bit count, and tree to the other side.
- Decodes the message before displaying it.
- Uses a simple observer pattern so the console can notify the GUI when a terminal message is ready.

## How the App Works

When a message is typed in the GUI, `MessagingApp` creates a `Huffman` object from that text. The Huffman tree counts character frequencies, builds a min-heap-style priority queue, and assigns shorter bit paths to characters that appear more often. The encoded bytes are passed to `Console`, which decodes them and prints the original message in the terminal.

Messages can also go the other direction. While the app is running, type into the terminal and press Enter. The console builds its own Huffman tree for that message, notifies the GUI, and the GUI decodes and shows the message in the window.

## Project Structure

```text
HuffmanMessaging/
├── README.md
└── HuffmanMessaging-main/
    ├── README.md
    └── src/
        ├── Console.java
        ├── Huffman.java
        ├── HuffmanTest.java
        ├── MessagingApp.java
        ├── Observable.java
        └── Observer.java
```

## Requirements

- Java JDK
- JavaFX SDK
- JUnit 5, only if you want to run the tests in `HuffmanTest.java`

This project does not currently include a build tool setup like Maven or Gradle, so the commands below use `javac` and `java` directly.

## Running the App

From the repository root, move into the Java project folder:

```bash
cd HuffmanMessaging-main
```

Set `PATH_TO_FX` to the `lib` folder inside your JavaFX SDK. On this machine, JavaFX is installed here:

```bash
export PATH_TO_FX=/Applications/javafx-sdk-21.0.1/lib
```

Compile the app into an `out` folder:

```bash
mkdir -p out
javac --module-path "$PATH_TO_FX" --add-modules javafx.controls -d out \
  src/Console.java \
  src/Huffman.java \
  src/MessagingApp.java \
  src/Observable.java \
  src/Observer.java
```

Run it:

```bash
java --module-path "$PATH_TO_FX" --add-modules javafx.controls -cp out MessagingApp
```

The JavaFX window is the GUI side of the conversation. The terminal where you ran the command is the console side.

## Sending Messages

To send from the GUI:

1. Type a message in the text box at the bottom of the window.
2. Press Enter.
3. The decoded message will print in the terminal.

To send from the terminal:

1. Click back into the terminal running the program.
2. Type a message.
3. Press Enter.
4. The decoded message will show up in the JavaFX window.

To stop the app, close the JavaFX window or press `Ctrl+C` in the terminal.

## Testing

`src/HuffmanTest.java` contains JUnit tests for the Huffman encoder and decoder. The tests cover normal messages, repeated characters, unique characters, numbers, special characters, mixed case, and a longer mixed-input string.

Because the repo does not include a build configuration yet, running the tests requires adding JUnit 5 to the classpath manually or creating a small Maven/Gradle setup.

## Current Limitations

- The app runs locally only.
- The GUI and console are the two chat sides.
- There is no automatic reply system.
- There is no saved message history.
- The GUI only shows the most recent few messages.
- JavaFX must be installed separately.
