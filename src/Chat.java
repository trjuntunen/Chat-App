import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;


/**
 * Class which provides functionality for group messaging through the
 * port and host given in the constructor.
 * 
 * @author Teddy Juntunen
 */

public class Chat {
	
	private String host;
	private int port;
	private static boolean chatFinished;
	
	public Chat(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	/**
	 * Connect to the chat to start sending messages. Connects to the host and 
	 * port that are given in the Chat constructor.
	 */
	public void connect() {
		try {
			InetAddress groupAddress = InetAddress.getByName(host);
			Scanner scanner = new Scanner(System.in);
			String name = getUserNameFromInput(scanner);
			MulticastSocket socket = new MulticastSocket(port);
			
			socket.setTimeToLive(0);
			socket.joinGroup(groupAddress);

			Thread t = new Thread(new MessageThread(groupAddress, socket, port));
			t.start();
			System.out.println("You entered the group chat...\n");
			
			while(true) {
				String message = scanner.nextLine();
				if(message.equalsIgnoreCase("Exit")) {
					chatFinished = true;
					socket.leaveGroup(groupAddress);
					socket.close();
					scanner.close();
					break;
				}
				message = name + ": " + message;
				byte[] buffer = message.getBytes();
				DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, groupAddress, port);
				socket.send(datagram);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getUserNameFromInput(Scanner scanner) {
		System.out.print("Enter your name: ");
        return scanner.nextLine();
	}
	
	/**
	 * Thread which reads the message from the DatagramPacket through the socket.
	 *
	 */
	private class MessageThread implements Runnable {

		private InetAddress groupAddress;
		private MulticastSocket socket;
		private int port;
		
		public MessageThread(InetAddress groupAddress, MulticastSocket socket, int port) {
			this.groupAddress = groupAddress;
			this.socket = socket;
			this.port = port;
		}
		
		public void run() {
			while(!chatFinished) {
				byte[] buffer = new byte[1000];
				DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, groupAddress, port);
				try {
					socket.receive(datagram);
					String message = new String(buffer, 0, datagram.getLength(), "UTF-8");
					System.out.println(message);
				} catch (IOException e) {
					System.out.println("Socket closed.");
				}
			}
		}
		
	}
	
}
