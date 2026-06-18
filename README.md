# Huffman Messaging

Huffman Messaging is a Java application built as a CSC345 final project. It demonstrates Huffman coding in a local messaging workflow between a JavaFX chat window and a terminal-side console. Each message is encoded with a Huffman tree before being decoded and displayed on the receiving side.

The project focuses on compression, tree construction, byte-level encoding, and event-driven communication between the GUI and console.

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

The commands below use `javac` and `java` directly, which keeps the setup simple and makes the compile/run steps easy to inspect.

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

The tests can be run by adding JUnit 5 to the classpath manually or by opening the project in an IDE with JUnit support.

## Project Focus

- Huffman tree construction from message character frequencies.
- Encoding strings into compact byte arrays using tree traversal paths.
- Decoding byte arrays back into the original message.
- JavaFX interface for one side of the conversation.
- Terminal-side console for the other side of the conversation.
- Observer-style updates between the console and GUI.
