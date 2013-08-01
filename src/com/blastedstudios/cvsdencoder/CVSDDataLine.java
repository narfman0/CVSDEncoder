package com.blastedstudios.cvsdencoder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.Control.Type;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * Helper class that one may use the wrap java supplied line interfaces
 * but be reading CVSD (which java does not natively support).
 */
public class CVSDDataLine implements TargetDataLine {
	private final CVSDEncoder encoder;
	private final AudioFormat desiredFormat, actualFormat;
	private final TargetDataLine line; 

	public CVSDDataLine(AudioFormat desiredFormat) throws LineUnavailableException {
		this.desiredFormat = desiredFormat;
		actualFormat = new AudioFormat(Encoding.PCM_SIGNED,8000f,16,1,2,8000f,true);
		encoder = new CVSDEncoder((int)actualFormat.getSampleRate());
		line = AudioSystem.getTargetDataLine(actualFormat);
	}

	@Override public void drain() {
		line.drain();
	}

	@Override public void flush() {
		line.flush();
	}

	@Override public void start() {
		line.start();
	}

	@Override public void stop() {
		line.stop();
	}

	@Override public boolean isRunning() {
		return line.isRunning();
	}

	@Override public boolean isActive() {
		return line.isActive();
	}

	@Override public AudioFormat getFormat() {
		return desiredFormat;
	}

	@Override public int getBufferSize() {
		return line.getBufferSize();
	}

	@Override public int available() {
		return line.available();
	}

	@Override public int getFramePosition() {
		return line.getFramePosition();
	}

	@Override public long getLongFramePosition() {
		return line.getLongFramePosition();
	}

	@Override public long getMicrosecondPosition() {
		return line.getMicrosecondPosition();
	}

	@Override public float getLevel() {
		return line.getLevel();
	}

	@Override public javax.sound.sampled.Line.Info getLineInfo() {
		return line.getLineInfo();
	}

	@Override public void open() throws LineUnavailableException {
		line.open();
	}

	@Override public void close() {
		line.close();
	}

	@Override public boolean isOpen() {
		return line.isOpen();
	}

	@Override public Control[] getControls() {
		return line.getControls();
	}

	@Override public boolean isControlSupported(Type control) {
		return line.isControlSupported(control);
	}

	@Override public Control getControl(Type control) {
		return line.getControl(control);
	}

	@Override public void addLineListener(LineListener listener) {
		line.addLineListener(listener);
	}

	@Override public void removeLineListener(LineListener listener) {
		line.removeLineListener(listener);
	}

	@Override public void open(AudioFormat format, int bufferSize)
			throws LineUnavailableException {
		line.open(actualFormat, bufferSize);
	}

	@Override public void open(AudioFormat format) throws LineUnavailableException {
		line.open(actualFormat);
	}

	@Override public int read(byte[] b, int off, int len) {
		byte[] actualBytes = new byte[len];
		int cvsdByteCount = line.read(actualBytes, 0, len) / 8 / encoder.getBytesPerSample();
		byte[] bytes = encoder.encode(actualBytes);
		for(int i=0; i<cvsdByteCount; i++)
			b[off+i] = bytes[i];
		return cvsdByteCount;
	}
}
