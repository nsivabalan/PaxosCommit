package message;

import java.util.UUID;

//base class for all Messages
public abstract class MessageBase {
	protected UUID uid;
	public abstract String toString();
}
