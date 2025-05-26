# Huffman Messaging

This is our final project for CSC345. In this project, we used Huffman trees to encode and decode messages back and forth between a GUI and the console. Every message that is generated on one end is used to create a Huffman tree that is passed to the other end, along with the encrypted message. Each side then has to decode the messages passed to them before displaying them.

## Authors
1. Jakob Garcia
2. Pri Vaghela
3. Apurbo Barua
4. Ashutosh Dayal

## Dependencies

This project uses JUnit for testing, and JavaFX for the GUI

## Usage

When running the GUI, you will want to open up the console as well, so that you can message back and forth between the GUI and console.

## Features
Every message that is sent is compressed and encrypted using Huffman trees. The compression technique is interesting because it uses bit shifting and masking to load individual bits into bytes.

