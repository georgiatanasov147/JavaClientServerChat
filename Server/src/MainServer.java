import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public final class MainServer {

	public static void main(String[] args) {
		try {
			new Thread(new ServerThread("Server", 6969)).start();
			System.out.println("Server started..");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class ServerThread implements Runnable {

		private String username;
		private int port;
		private ServerSocket serverSocket;
		private Socket socket;
		private PrintStream toClient;
		private Scanner fromConsole;
		private Scanner fromClient;

		public ServerThread(String username, int port) throws IOException {
			this.username = username;
			this.port = port;
			serverSocket = new ServerSocket(port);
		}

		@Override
		public void run() {
			while (true) {
				try {
					socket = serverSocket.accept();
					new ReceiverThread(socket).start();
					new SenderThread(username, socket).start();
				} catch (IOException e) {
					System.out.println("Error.");
					e.printStackTrace();
				}
			}
		}

		public class SenderThread extends Thread {

			private String username;
			private PrintStream toClient;
			private Scanner fromConsole;

			public SenderThread(String username, Socket socket) throws IOException {
				this.username = username;
				fromConsole = new Scanner(System.in);
				toClient = new PrintStream(socket.getOutputStream());
			}

			@Override
			public void run() {
				while (fromConsole.hasNextLine()) {
					toClient.println(username + ": " + fromConsole.nextLine());
				}
			}
		}
		
		public class ReceiverThread extends Thread {

			private Scanner fromClient;
			
			public ReceiverThread(Socket socket) throws IOException {
				fromClient = new Scanner(socket.getInputStream());
			}
			@Override
			public void run() {
				while (fromClient.hasNextLine()) {
					System.out.println(fromClient.nextLine());
				}
			}
		}
	}

}
