
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * A GUI application for a small messaging app that communicates with the
 * console using Huffman trees to encrypt messages. The class observes the
 * Console class for new messages to process.
 * 
 * @author Jakob Garcia
 */
public class MessagingApp extends Application implements Observer {

	private BorderPane pane;
	private TextArea[] messages; // The 4 messages able to be displayed on the screen
	private boolean[] messageSide; // true = right side; false = left side of screen
	private Console console;

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage stage) throws Exception {
		pane = new BorderPane();
		messages = new TextArea[4];
		messageSide = new boolean[4];

		initializeDisplayAndHandlers();

		Scene scene = new Scene(pane, 400, 600);
		stage.setScene(scene);
		stage.show();
		stage.setResizable(false);
		stage.setTitle("Huffman Messaging");

		console = new Console();
		console.addObserver(this);
		console.setUpConsole();
	}

	private void initializeDisplayAndHandlers() {
		// Create the bottom textbox
		GridPane botGrid = new GridPane();
		pane.setBottom(botGrid);

		TextArea field = new TextArea();
		field.setPrefWidth(320);
		field.setMaxHeight(100);
		field.setWrapText(true);
		botGrid.setPadding(new Insets(0, 0, 20, 0));
		botGrid.setHgap(10);
		botGrid.add(field, 4, 0);

		for (int i = 0; i < 4; i++) { // Fill the message array with invisible values for padding
			TextArea temp = new TextArea();
			temp.setMaxSize(175, 105);
			temp.setEditable(false);
			temp.setWrapText(true);
			temp.setVisible(false);
			messages[i] = temp;
		}

		// Set an event handler for when the user presses enter on the text box
		field.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				if (field.getText().trim().length() < 1)
					return;

				TextArea message = new TextArea(field.getText().trim());
				this.updateMessageDisplay(message, true); // Update the display with the new message

				// Encode and send the message to the console
				Huffman huffmanTree = new Huffman(field.getText().trim());
				console.receiveMessage(huffmanTree.getEncoding(), huffmanTree.getTotalBits(), huffmanTree);

				field.clear();
			}
		});
	}

	/**
	 * Updates the GUI to display a new message passed from either the console or
	 * the GUI. Messages from the console go on the left, and messages from the GUI
	 * go on the right. Newest messages are at the bottom.
	 * 
	 * @param message - A TextArea representing the new message to add
	 * @param side    - A boolean representing which side of the screen the message
	 *                should go on
	 */
	private void updateMessageDisplay(TextArea message, boolean side) {
		// Shift the position of all the previous messages. Gets rid of the old 4th
		// message.
		messages[3] = this.createMessage(messages[2]);
		messages[2] = this.createMessage(messages[1]);
		messages[1] = this.createMessage(messages[0]);
		messageSide[3] = messageSide[2];
		messageSide[2] = messageSide[1];
		messageSide[1] = messageSide[0];
		// Add the new message to the array
		messages[0] = message;
		messageSide[0] = side;
		messages[0].setMaxSize(175, 105);
		messages[0].setEditable(false);
		messages[0].setWrapText(true);

		GridPane topGrid = new GridPane();
		pane.setTop(topGrid);
		topGrid.setHgap(10);
		topGrid.setVgap(10);
		// Display all the message bubbles again
		for (int i = 0; i < 4; i++) {
			if (messages[i] != null && messageSide[i]) { // Handles GUI messages
				messages[i].setStyle("-fx-control-inner-background: #147efb;");
				topGrid.add(messages[i], 3, 4 - i);
				TextArea padding = this.createMessage(messages[i]);
				padding.setVisible(false);
				topGrid.add(padding, 2, 4 - i); // Padding so the alignment is correct

			} else if (messages[i] != null && !messageSide[i]) { // Handle console messages
				topGrid.add(messages[i], 2, 4 - i);
			}
		}
	}

	/**
	 * Create a new TextArea message to avoid runtime error of redisplaying a
	 * TextArea. It essentially clones a TextArea
	 * 
	 * @param oldText - the TextArea to copy
	 * @return The new instance of the TextArea
	 */
	private TextArea createMessage(TextArea oldText) {
		TextArea newText = new TextArea(oldText.getText());
		newText.setMaxSize(175, 105);
		newText.setEditable(false);
		newText.setWrapText(true);
		newText.setVisible(oldText.isVisible());
		return newText;

	}

	/**
	 * Receive a message from the console and display it on the GUI by decoding an
	 * encrypted message using a Huffman tree.
	 * 
	 * @param encoding    - The encoding of the Huffman tree
	 * @param huffmanTree - The Huffman tree
	 */
	public void receiveMessage(byte[] encoding, int totalBits, Huffman huffmanTree) {
		String decodedMessage = huffmanTree.decode(encoding, totalBits);
		TextArea message = new TextArea(decodedMessage);

		this.updateMessageDisplay(message, false);
	}

	/**
	 * Receive a notification from the console to receive their message.
	 * 
	 * @param theObserved - the Observable Console object
	 */
	@Override
	public void update(Object theObserved) {
		Console console = (Console) theObserved;
		Huffman tree = console.getTree();
		// The console is run on a separate thread, so we need to ensure it runs on the
		// Javafx thread
		Platform.runLater(() -> this.receiveMessage(tree.getEncoding(), tree.getTotalBits(), tree));

	}
}
