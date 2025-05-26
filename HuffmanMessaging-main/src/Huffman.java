/**
 * A class for creating Huffman trees, as well as their encoding and decoding
 * processes
 * 
 */
public class Huffman {
	/**
	 * Private TreeNode class for creating the Huffman Tree. It stores the weight of
	 * the tree and the character.
	 */
	private class TreeNode {
		private char data;
		private int weight;
		private TreeNode left;
		private TreeNode right;
		private boolean isLeaf;

		public TreeNode(char theData, int weight) {
			this.data = theData;
			this.weight = weight;
			this.left = null;
			this.right = null;
			this.isLeaf = true;
		}

		public TreeNode(int weight) {
			this.data = ' ';
			this.weight = weight;
			this.left = null;
			this.right = null;
			this.isLeaf = false;
		}

		@Override
		public String toString() {
			if (!isLeaf)
				return "[(" + weight + "), LL" + left + ", RR" + right + "]";
			return "<\"" + data + "\", " + weight + ">";
		}
	}

	private TreeNode root; // Root of the Huffman tree
	private TreeNode[] queue; // Queue used for constructing the tree
	private int queueSize;
	private byte[] encoding; // The message encoded into bits
	private int totalBits; // The total amount of bits used for encoding/decoding

	/**
	 * Construct the Huffman tree. PREQUISITE: the text is at least a single
	 * character (no empty "")
	 * 
	 * @param text - text passed in to encode
	 */
	public Huffman(String text) {
		int size = this.buildTree(text);
//		System.out.println(root);
		this.encode(size, text);
	}

	/**
	 * Build the Huffman tree and prep for encoding
	 * 
	 * @param text - The text passed in that is used to construct the tree
	 * @return the range of the Unicode values in the string
	 */
	private int buildTree(String text) {
		boolean[] isCounted = new boolean[text.length()]; // check if a char has already been counted
		char[] uniqueChars = new char[text.length()]; // Store unique chars
		int[] frequencies = new int[text.length()]; // Count the frequency of chars
		int uniqueCount = 0;
		int max = text.charAt(0); // Convert first char into unicode
		int min = text.charAt(0);
		// Find all unique values and their frequencies
		for (int i = 0; i < text.length(); i++) {
			if (!isCounted[i]) {
				char currentChar = text.charAt(i);
				int currentUnicode = currentChar; // convert char to unicode
				// Determine the min and max unicode values for later
				if (max < currentUnicode) {
					max = currentUnicode;
				}
				if (min > currentUnicode) {
					min = currentUnicode;
				}

				int count = 1;
				for (int j = i + 1; j < text.length(); j++) { // Count the unique chars and their frequencies
					if (text.charAt(j) == currentChar) {
						count++;
						isCounted[j] = true;
					}
				}
				uniqueChars[uniqueCount] = currentChar;
				frequencies[uniqueCount] = count;
				uniqueCount++;
			}
		}

		// Create a priority queue using a heap to create TreeNodes
		queue = new TreeNode[uniqueCount];
		queueSize = 0;
		for (int i = 0; i < queue.length; i++) {
			this.insert(i, new TreeNode(uniqueChars[i], frequencies[i]));
		}

		// Construct the Huffman tree
		while (queueSize > 1) {
			TreeNode node1 = this.removeNext();
			TreeNode node2 = this.removeNext();
			TreeNode innerNode = new TreeNode(node1.weight + node2.weight);
			innerNode.left = node1;
			innerNode.right = node2;
			this.insert(queueSize, innerNode);
		}
		if (max == min) { // If message is made up of only a single character
			TreeNode padding = new TreeNode(queue[0].weight);
			padding.left = queue[0];
			root = padding;
		} else
			root = queue[0]; // The last item in the queue is the whole tree

		return max - min + 1;
	}

	/**
	 * A private class used for encoding the original text. It stores a mapping of a
	 * character to its traversal in the tree
	 */
	private class BinNode {
		private char data; // A single character in the text
		private String traversal; // The traversal of the character in the tree

		public BinNode(char data, String traversal) {
			this.data = data;
			this.traversal = traversal;
		}

		@Override
		public String toString() {
			return "[" + data + ": " + traversal + "]";
		}
	}

	/**
	 * Find the encoding of the message using the tree. First we use something akin
	 * to BinSort, to create a simple hash table for unique unicode characters. We
	 * calculated the range for the table when constructing the tree. Then we encode
	 * the original message into a byte array by using each characters traversal and
	 * shifting it into various bytes. Since bytes are 8 bits, and a single
	 * character in Java is 16 bits, we can reduce the amount of bits used for the
	 * message by shifting the values into something more cost effective.
	 * 
	 * @param size - The size of the bin table
	 * @param text - the original text passed in
	 */
	private void encode(int size, String text) {
		// Create the bins for each character
		BinNode[] bins = new BinNode[size];
		// Call this helper method which calculates the total amount of bits used to
		// encode the message. The method also inserts each character into the table.
		int totalBits = encodeHelper(root, bins, "", size);

		// Calculate the total amount of bits needed to fully encode our message
		byte[] encoding = new byte[(int) Math.ceil((totalBits + 1) / 7.0)];

		int byteCapacity = 7; // We only use 7/8 bits because they are signed and it caused issues
		int encodingIndex = 0;
		for (int i = 0; i < text.length(); i++) {
			// Get each character and their traversal
			char currentChar = text.charAt(i);
			String currentTraversal = bins[((int) currentChar) % size].traversal;
			for (int j = 0; j < currentTraversal.length(); j++) { // shove the traversal into a byte(s)
				// Pick out each bit one by one and shift it into a byte stored in the byte
				// array
				byte currentBit = Byte.parseByte(currentTraversal.substring(j, j + 1));
				encoding[encodingIndex] = (byte) (encoding[encodingIndex] << 1);
				encoding[encodingIndex] = (byte) (encoding[encodingIndex] + currentBit);
				byteCapacity--;

				// If we ran out of bits we can use in this index of the array, go to the next
				// index and continue
				if (byteCapacity == 0) {
					byteCapacity = 7;
					encodingIndex++;
				}
			}
		}

		// Store these values for decoding.
		this.totalBits = totalBits;
		this.encoding = encoding;
	}

	/**
	 * Insert each character into the BinNode table by following a pre-order
	 * traversal of the Huffman tree. The mapping this creates allows us to skip
	 * repeatedly traversing the tree to find repeat character traversals, since we
	 * can now easily access the traversals of each character in O(1) time.
	 * 
	 * @param currRoot  - The root of the current tree/subtree
	 * @param bins      - The bins we are inserting nodes into
	 * @param traversal - The traversal of the tree to the current leaf node which
	 *                  was built recursively
	 * @param size      - the size of the table used for hashing
	 * @return the total amount of bit used to encode the entire message
	 */
	private int encodeHelper(TreeNode currRoot, BinNode[] bins, String traversal, int size) {
		if (currRoot == null) {
			return 0;
		} else if (currRoot.isLeaf) { // We found a character to add to the table
			bins[((int) currRoot.data) % size] = new BinNode(currRoot.data, traversal);
			return traversal.length() * currRoot.weight; // Return the total bits used for this character overall
		} else {
			// Traverse left and right to find leaf nodes
			return encodeHelper(currRoot.left, bins, traversal + "0", size)
					+ encodeHelper(currRoot.right, bins, traversal + "1", size);
		}
	}

	/**
	 * Return the encoding of the current message of the tree
	 * 
	 * @return a byte array storing the encoded message
	 */
	public byte[] getEncoding() {
		return this.encoding;
	}

	/**
	 * Return the total amount of bits used to encode the message
	 * 
	 * @return an int of the total amount used
	 */
	public int getTotalBits() {
		return this.totalBits;
	}

	/**
	 * Decode the original message using the encoded byte array and a int of the
	 * total amount of bits used.
	 * 
	 * @param encoded   - An encoded byte array of the original message
	 * @param totalBits - The total amount of bits used to encode
	 * @return the original string decoded
	 */
	public String decode(byte[] encoded, int totalBits) {
		StringBuilder decoded = new StringBuilder();
		TreeNode current = root;
		int bitCount = 0;
		// Iterate through the byte array and shift right each bit we encoded
		for (byte b : encoded) {
			for (int i = 6; i >= 0; i--) { // shift right each bit and traverse down the Huffman tree
				byte currentBit;
				if (totalBits - bitCount < 7 && totalBits - bitCount > 0) { // Handle a byte that wasn't fully used
					currentBit = (byte) (b >> Math.min((totalBits - bitCount - 1), i) & 0x1);

				} else if (totalBits <= bitCount) { // Return the decoded string
					return decoded.toString();

				} else {
					currentBit = (byte) ((b >> i) & 0x1); // Shift the next bit to look at.
				}
				bitCount++;

				if (currentBit == 0) { // Traverse left on 0
					current = current.left;
				} else { // Traverse right on 1
					current = current.right;
				}
				if (current.isLeaf) { // If at a leaf, add the character to our string
					decoded.append(current.data);
					current = root; // reset for next character
				}
			}
		}
		return decoded.toString();
	}

	/**
	 * Insert a TreeNode into the priority queue.
	 * 
	 * @param index   - index where the node currently is
	 * @param newNode - the node to shift
	 */
	private void insert(int index, TreeNode newNode) {
		queue[queueSize] = newNode;
		this.swim(queue, index);
		queueSize++;
	}

	/**
	 * Sink method for heaps. It sinks a value down in order to create/maintain a
	 * heap ordering.
	 * 
	 * @param A - the array/heap
	 * @param i - the index of the node we are looking to sink
	 * @param j - the last index in the array
	 */
	private void sink(TreeNode[] A, int i, int j) {
		TreeNode val = A[i];
		int minChildIndex = -1;
		TreeNode minChild = val;
		int start = i * 2 + 1; // Start index for checking children
		int end = Math.min(i * 2 + 2, j); // End index for checking children

		for (int k = start; k <= end; k++) { // Find min child to swap with
			TreeNode cur = A[k];
			if (cur.weight < minChild.weight) {
				minChild = cur;
				minChildIndex = k;
			}
		}

		if (minChildIndex != -1) {
			A[minChildIndex] = val;
			A[i] = minChild;
			sink(A, minChildIndex, j); // Recursively sink the swapped element until we find the correct position
		}
	}

	/**
	 * Swim method for heaps. It swims a value up in order to maintain a heap
	 * ordering.
	 * 
	 * @param A     - the array/heap
	 * @param start - an index for the node we are looking to swim
	 */
	private void swim(TreeNode[] A, int start) {
		// start is where the last added value is
		if (0 >= start)
			return;

		TreeNode val = A[start];
		int parentIndex = Math.max(Math.floorDiv(start - 1, 2), 0); // End index for checking parent

		if (val.weight < A[parentIndex].weight) { // If the child is less than the parent, swap
			A[start] = A[parentIndex];
			A[parentIndex] = val;
			swim(A, parentIndex); // Recursively swim until a parent with a lesser value is found
		}
	}

	/**
	 * Remove the next node in the queue which has the lowest weight. This is the
	 * node at the front of the queue.
	 * 
	 * @return the TreeNode
	 */
	private TreeNode removeNext() {
		if (queueSize <= 0)
			return null;
		TreeNode node = queue[0];
		queueSize--;
		queue[0] = queue[queueSize];
		queue[queueSize] = null;
		this.sink(queue, 0, queueSize - 1);
		return node;
	}

	@Override
	public String toString() {
		String s = "[";
		for (TreeNode node : queue) {
			if (node == null)
				s += "null, ";
			else
				s += node.toString() + ", ";
		}
		return s.substring(0, s.length() - 2) + "]";
	}

}