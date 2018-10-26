package net.jetblack.util.io;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StringSerializer implements ByteSerializable {

	private final Charset _charset;
	
	public StringSerializer() {
		_charset = StandardCharsets.UTF_8;
	}

	public StringSerializer(Charset charset) {
		_charset = charset;
	}

	@Override
	public Object deserialize(byte[] bytes) throws Exception {
		return new String(bytes, _charset);
	}

	@Override
	public byte[] serialize(Object data) throws Exception {
		// TODO Auto-generated method stub
		return ((String)data).getBytes(_charset);
	}

}
