package com.blastedstudios.cvsdencoder;

import java.io.IOException;
import java.io.InputStream;

/**
 * CVSD decoder wrapping InputStream. Helper for reading from file, receiving
 * over network, etc.
 */
public class CVSDDecodeStream extends InputStream{
	private final InputStream stream;
	private final CVSDEncoder encoder;
	private byte[] currentBytes;
	private int lastByteIndex;

	public CVSDDecodeStream(InputStream stream, int sampleRate){
		this.stream = stream;
		encoder = new CVSDEncoder(sampleRate);
	}
	
	public int read(byte b[], int off, int len) throws IOException {
		int index = off;
		while(available() > 0 && len-- > 0)
			b[index++] = (byte)(read()-128);
		return index - off == 0 ? -1 : index - off;
	}

	@Override public int read() throws IOException {
		if(lastByteIndex % (encoder.getBytesPerSample() * 8) == 0){
			currentBytes = encoder.decode(new byte[]{(byte)stream.read()});
			lastByteIndex = 0;
		}
		return 128+(int)currentBytes[lastByteIndex++];
	}
	
	@Override public int available() throws IOException{
		int available = stream.available() * encoder.getBytesPerSample() * 8;
		if(currentBytes != null)
			available += (encoder.getBytesPerSample() * 8) - lastByteIndex;
		return available;
	}
	
	@Override public void close() throws IOException{
		stream.close();
	}
}
