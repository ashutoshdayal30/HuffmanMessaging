import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

public class HuffmanTest {

	@Test
	public void testEncodeDecode() {
		String testMessage = "This message is a bit more convoluted";
		Huffman huffman = new Huffman(testMessage);

		String encodedMessage = huffman.getEncoding().toString();
		System.out.println("Encoded: " + (encodedMessage));

		String decodedMessage = huffman.decode(huffman.getEncoding(), huffman.getTotalBits());
		System.out.println("Decoded: " + decodedMessage);

		assertEquals(testMessage, decodedMessage, "Decoded message does not match the original message");
	}

	@Test
	public void testSingleCharacter() {
		Huffman huffman = new Huffman("a");
		byte[] encoded = huffman.getEncoding();
		String decoded = huffman.decode(encoded, huffman.getTotalBits());
		assertEquals("a", decoded);
	}

	@Test
	public void testRepeatedCharacters() {
		Huffman huffman = new Huffman("aaaaaaa");
		byte[] encoded = huffman.getEncoding();
		String decoded = huffman.decode(encoded, huffman.getTotalBits());
		assertEquals("aaaaaaa", decoded);
	}

	@Test
	public void testUniqueCharacters() {
		Huffman huffman = new Huffman("abcdef");
		byte[] encoded = huffman.getEncoding();
		String decoded = huffman.decode(encoded, huffman.getTotalBits());
		assertEquals("abcdef", decoded);
	}

	@Test
	public void testNumericalData() {
		Huffman huffman = new Huffman("1234567890");
		byte[] encoded = huffman.getEncoding();
		String decoded = huffman.decode(encoded, huffman.getTotalBits());
		assertEquals("1234567890", decoded);
	}

	@Test
	public void testSpecialCharacters() {
		Huffman huffman = new Huffman("!@#$%^&*()_+");
		byte[] encoded = huffman.getEncoding();
		String decoded = huffman.decode(encoded, huffman.getTotalBits());
		assertEquals("!@#$%^&*()_+", decoded);
	}

	@Test
	public void testCaseSensitivity() {
		Huffman huffman = new Huffman("aAaAaAaA");
		byte[] encoded = huffman.getEncoding();
		String decoded = huffman.decode(encoded, huffman.getTotalBits());
		assertEquals("aAaAaAaA", decoded);
	}

	@Test
	public void testGiberish() {
		Huffman huffman = new Huffman(
				"djflkjas fkj32jqueroemf hakj1@#$!#@ GD RT Y&IRJHS][].'.^asz<>/;'{>}> }{ddfg dfg }{>");
		byte[] encoded = huffman.getEncoding();
		String decoded = huffman.decode(encoded, huffman.getTotalBits());
		assertEquals("djflkjas fkj32jqueroemf hakj1@#$!#@ GD RT Y&IRJHS][].'.^asz<>/;'{>}> }{ddfg dfg }{>" + "",
				decoded);
	}
}
