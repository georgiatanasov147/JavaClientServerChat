import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public final class MainClient {

	public static void main(String[] args) throws UnknownHostException, IOException {
		try {
			new Thread(new ClientThread("Client", "127.0.0.1", 6969)).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class ClientThread implements Runnable {

		private String username;
		private int port;
		private Socket socket;
		private PrintStream toServer;
		private Scanner fromConsole;
		private Scanner fromServer;

		public ClientThread(String username, String ipAddress, int port) throws IOException {
			this.username = username;
			this.port = port;
			socket = new Socket(ipAddress, port);
			System.out.println("Connection started");
			
		}

		@Override
		public void run() {
			try {
				new ReceiverThread(socket).start();
				new SenderThread(username, socket).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public class SenderThread extends Thread {

			private String username;
			private PrintStream toServer;
			private Scanner fromConsole;

			public SenderThread(String username, Socket socket) throws IOException {
				this.username = username;
				fromConsole = new Scanner(System.in);
				toServer = new PrintStream(socket.getOutputStream());
			}

			@Override
			public void run() {
				while (fromConsole.hasNextLine()) {
					toServer.println(username + ": " + fromConsole.nextLine());
				}
			}
		}
		
		public class ReceiverThread extends Thread {

			private Scanner fromServer;
			
			public ReceiverThread(Socket socket) throws IOException {
				fromServer = new Scanner(socket.getInputStream());
			}
			@Override
			public void run() {
				while (fromServer.hasNextLine()) {
					System.out.println(fromServer.nextLine());
				}
			}
		}
	}

}
