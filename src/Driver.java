/**
 * Main class of the chat application.
 * 
 * @author Teddy Juntunen
 *
 */

public class Driver {

	public static void main(String[] args) {
		String host = "239.0.0.0";
		int port = 1234;
		
		Chat chat = new Chat(host, port);
		chat.connect();
	}

}
