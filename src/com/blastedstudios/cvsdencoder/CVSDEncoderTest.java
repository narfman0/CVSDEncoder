package com.blastedstudios.cvsdencoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class CVSDEncoderTest extends TestCase {
	public void testRead8000() {
		testBytes(("0 0 0 0 0 0 0 0").split(" "), 11, 8000);
		testBytes(("10 10 10 10 10 10 10 10").split(" "), 11, 8000);
		testBytes(("10 20 25 20 30 30 30 30").split(" "), 11, 8000);
	}
	
	public void testRead16000() {
		testBytes(("0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0").split(" "), 11, 16000);
		testBytes(("0 -1 0 -1 0 -1 0 -1 0 -1 0 -1 0 -1 0 -1").split(" "), 11, 16000);
		testBytes(("0 10 0 20 0 40 0 40 0 40 0 40 0 40 0 40").split(" "), 30, 16000);
	}
	
	public void testStream(){
		byte[] bytes = parseByteArray(("64 64 64 64 64 64 64 64").split(" "));
		ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
		CVSDDecodeStream cvsdStream = new CVSDDecodeStream(stream, 8000);
		byte[] newArray = new byte[1024];
		try {
			cvsdStream.read(newArray, 0, newArray.length);
			printBytes(newArray);
			cvsdStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void testBytes(String[] bytesString, int delta, int sampleRate){
		byte[] originalBytes = parseByteArray(bytesString);
		System.out.print("Original:");
		printBytes(originalBytes);
		
		CVSDEncoder cvsdEncoder = new CVSDEncoder(sampleRate);
		byte[] encodedBytes = cvsdEncoder.encode(originalBytes);
		System.out.print("Encoded:");
		printBytes(encodedBytes);

		CVSDEncoder cvsdDecoder = new CVSDEncoder(sampleRate);
		byte[] decodedBytes = cvsdDecoder.decode(encodedBytes);
		System.out.print("Decoded:");
		printBytes(decodedBytes);
		
		assertTrue(originalBytes[0] > 0 ? decodedBytes[0] > 0 : decodedBytes[0] <= 0);
		for(int i=0; i+1<bytesString.length; i++)
			assertEquals(originalBytes[i], decodedBytes[i], delta);
	}
	
	private static byte[] parseByteArray(String[] bytesString){
		byte[] originalBytes = new byte[bytesString.length];
		for(int i=0; i<bytesString.length; i++)
			originalBytes[i] = (byte) Integer.parseInt(bytesString[i]);
		printBytes(originalBytes);
		return originalBytes;
	}
	
	private static void printBytes(byte[] bytes){
		System.out.print("{");
		for(int i=0; i<bytes.length; i++)
			System.out.print(bytes[i]+ (i==bytes.length-1?"":","));
		System.out.println("}");
	}
}
