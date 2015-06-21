package fitbook;

import fitlibrary.SequenceFixture;

public class ChatStartSequence extends SequenceFixture {
	private ChatStart chat = new ChatStart();
	
	public Object getSystemUnderTest() {
		return chat;
	}
}
