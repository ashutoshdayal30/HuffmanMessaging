import java.util.Scanner;

/**
 * This class is used with the GUI class 'MessagingApp' to communicate back to
 * the GUI using the console. This class is observed by the GUI.
 * 
 * @author Jakob Garcia
 */
public class Console extends Observable {
	private Scanner keyboard;
	private Huffman tree;

	public Console() {
		keyboard = new Scanner(System.in);
	}

	/**
	 * Receive a message from the GUI and display it to the console. The message is
	 * encrypted so it uses the Huffman tree to decode it.
	 * 
	 * @param encoding    - The encoding of the Huffman tree
	 * @param huffmanTree - the Huffman tree
	 */
	public void receiveMessage(byte[] encoding, int totalBits, Huffman huffmanTree) { // Change encoding type
		String decodedMessage = huffmanTree.decode(encoding, totalBits);
		System.out.println("GUI user sent:");
		System.out.println(decodedMessage);
		System.out.println("-------------------");

	}

	/**
	 * Setup the console to run on a separate thread to not interfere with GUI
	 * functions. Allows both the console and GUI to communicate back and forth.
	 */
	public void setUpConsole() {
		Thread inputThread = new Thread(() -> {
			while (true) {
				if (keyboard.hasNextLine()) { // Take user input at any time
					String input = keyboard.nextLine().trim();
					if (input.length() < 1)
						return;
					System.out.println("-------------------");

					tree = new Huffman(input);
					// Notify the GUI that they need to receive a message from the console
					notifyObservers(this);
				}
			}
		});
		inputThread.setDaemon(true); // Set the thread as daemon so it will stop when the main thread stops
		inputThread.start();
	}

	public Huffman getTree() {
		return tree;
	}
}
