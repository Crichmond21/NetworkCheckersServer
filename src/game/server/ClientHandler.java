package game.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * Client Handler for Network Checkers
 * 
 * @author Carter Richmond 
 *
 */
class ClientHandler extends Thread  
{ 
	//Final variables for datastreams and socket
    final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket s; 
    
    //Instance variables for gameboard, team, modified, and modified pair
    private GameBoard game;
    private String team;
    private boolean modified = false;
    private boolean turn = true;
    private static int[] modifiedPair = new int[4];
      
  
    // Constructor 
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, GameBoard game, String team)  
    { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos; 
        this.game = game;
        this.team = team;
        System.out.println("Client Handler Started");
    } 
    
    /**
     * If gameboard state changed send the piece changed. 
     */
    public void sendGameBoard() {
    	//if modified write modified ints to client
    	if(modified) {
    		try {
    			//Send gameboard changed int 
    			dos.writeInt(210);
    			
    			//Send positions of piece to move
    			dos.writeInt(modifiedPair[0]);
    			dos.writeInt(modifiedPair[1]);
    			dos.writeInt(modifiedPair[2]);
    			dos.writeInt(modifiedPair[3]);
    			
    			//Wait for incomming data
    			Thread.sleep(100);
    			
    			//If status code isnt good resend data
    			if(dis.readInt() != 200) {
    				sendGameBoard();
    			}
    			
    		}catch(Exception e) {
    			e.printStackTrace();
    		}
    		
    		//Set modified = false
    		modified = false;

    	}else {
    		//if not modified send code 200
    		try {
    			dos.writeInt(200);
    		}catch(Exception e) {
    			e.printStackTrace();
    		}	
    	}
    }
    
    /**
     * Sets modified to state b
     * @param b state
     */
    public void setModified(boolean b) {
    	modified = b;
    }
    
    /**
     * Sets turn to state b
     * @param b state
     */
    public void setTurn(boolean b) {
    	turn = b;
    }
    
    @Override
    public void run() { 
    	String received;
    	
    	//Indexes for piece and destination for moving
    	int initialRow = -1;
    	int initialColumn = -1;
    	int moveRow = -1;
    	int moveColumn = -1;
    	
        while (true) { 
        	
        	try { 
        		//Get input from client
               	received = dis.readUTF(); 
             
               	//switch the string
               	switch(received) {
               	
               		//Case selectpiece read the two ints and store them
               		case "selectPiece":
               			initialRow = dis.readInt();
               			initialColumn = dis.readInt();
               			dos.writeInt(200);
               			break;
               		
               		//Case selectMovementSpot read the two ints and store them, then 
               		case "selectMovementSpot":
               			moveRow = dis.readInt();
               			moveColumn = dis.readInt();
               			
               			if(turn) {
               				try {
                   				//if different teams
                   				if(initialRow >= 0 && team.equals(game.getTeam(initialRow, initialColumn))) {
                   					//Attempt to move piece
                       				game.movePiece(initialRow, initialColumn, moveRow, moveColumn);
                       				
                       				//Set modified array
                       				modifiedPair[0] = initialRow;
                       				modifiedPair[1] = initialColumn;
                       				modifiedPair[2] = moveRow;
                       				modifiedPair[3] = moveColumn;
                       				
                       				//Check second client handler to modified state
                       				CheckersServer.setModified(this);
                       				
                       				//Send code 200
                           			dos.writeInt(200);
                           			dos.flush();
                           			break;
                   				}else {
                   					//if wrong team send code 300 invalid move
                   					dos.writeInt(300);
                   					dos.flush();
                   				}
                   			}catch(Exception e) {
                   				//If exception send code 300 invalid move
                   				e.printStackTrace();
                   				dos.writeInt(300);
                       			dos.flush();
                   			}
               			}else {
               				dos.writeInt(300);
               				dos.flush();
               			}
               			break;
               		
               		case "getGameState":
               			//sends the modified move to the server
               			sendGameBoard();
               			break;
               	}                	
                	
               	//if exit then close connection
               	if(received.equals("exit")) {  
               		System.out.println("Client " + this.s + " sends exit..."); 
               		System.out.println("Closing this connection."); 
               		this.s.close(); 
                    System.out.println("Connection closed"); 
                    break; 
               	} 
               	
            } catch (SocketException e1) {
            	System.exit(1);
            } catch (Exception e2) { 
            	e2.printStackTrace(); 
            }
        } 
          
        //close input and output steams
        try { 
            // closing resources 
            this.dis.close(); 
            this.dos.close(); 
              
        }catch(IOException e){ 
            e.printStackTrace(); 
        } 
    } 
}