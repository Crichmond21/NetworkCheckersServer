package game.server;
import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class CheckersServer {
	private static final int PORT_NUMBER = 7065;
	
	public static void sendStatusCode(DataOutputStream out, int code) throws IOException {
		out.writeInt(code);
		out.flush();
	}

	public static void main(String[] args) {
		System.out.println("Starting Server");
		GameBoard test = new GameBoard();
		
		//Initiates Sockets and input/output steams outside the try block
		ServerSocket CheckersServerSocket;
		
		Socket CheckersClient1;
		Socket CheckersClient2;
		
		DataInputStream client1Requests = null;
		DataOutputStream serverResponseClient1 = null;
		DataInputStream client2Requests = null;
		DataOutputStream serverResponseClient2 = null;
		
		//Strings to hold message in and message out
		String msgin1 = "", msgout1 ="";
		String msgin2 = "", msgout2 ="";
		
		try {
			//Opens two server sockets for Player/client 1 and Player/client 2
			CheckersServerSocket = new ServerSocket(PORT_NUMBER);
			
			//Accepts socket for player 1 and creates data input and output streams
			System.out.println("Wating for connection 1");
			CheckersClient1 = CheckersServerSocket.accept();
			client1Requests = new DataInputStream(CheckersClient1.getInputStream());
			serverResponseClient1 = new DataOutputStream(CheckersClient1.getOutputStream());
			sendStatusCode(serverResponseClient1, 100);
			
			Thread t1 = new ClientHandler(CheckersClient1, client1Requests, serverResponseClient1);
			t1.start();
			Thread.sleep(100);
			
			//Accepts socket for player 2 and creates data input and output streams
			System.out.println("Wating for connection 2");
			CheckersClient2 = CheckersServerSocket.accept();
			client2Requests = new DataInputStream(CheckersClient2.getInputStream());
			serverResponseClient2 = new DataOutputStream(CheckersClient2.getOutputStream());
			sendStatusCode(serverResponseClient2, 100);
			
			Thread t2 = new ClientHandler(CheckersClient2, client2Requests, serverResponseClient2);
			t2.start();
			
			//CheckersServerSocket.close();
			
			
		}catch (Exception e) {
			//Prints error message followed by exception if exception is caught
			System.out.println("Error while opening socket on port " + PORT_NUMBER);
			System.out.println(e);
			System.exit(1);
		}
		
		System.out.println("Server Running");
		
		//MAIN LOOP
		while(!msgin1.equals("exit") && !msgin2.equals("exit")) {
			try {
				
				
			}catch(Exception e) {

				e.printStackTrace();
				System.exit(1);
			}
			
		}
	}
}
