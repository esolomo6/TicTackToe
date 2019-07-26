/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3166_final_memorymatch;
//import java packages
import java.net.Socket;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Random;
import java.util.StringJoiner;


/**
 *
 * @author Eli Solomon
 */
public class Server {

        //declare board to send to client
	private static String[][] board = {{" ","A", "B","C", "D"},
			{"1","*","*","*","*"},
			{"2","*","*","*","*"},
			{"3","*","*","*","*"},
			{"4","*","*","*","*"}};
        //int of integers to check against
	private static int[][] DECK = new int[4][4];
        //array to see if each should be shown to client
	private static boolean[][] SHOW = new boolean[4][4];
        //int switches check game status
	private static int checkExit =0, check_1 = 1,check_2 =2;
        //string for the column and row
	private static String col_row;
        
        //string to check for player one 
	private static String playing = "Player1";
        //temp ints to store from clients
	private static int mem_first =-1,mem_first_col=-1;
	private static int mem_second =-1,mem_second_col=-1;
	
        //ints for score of the players
        private static int score_1 =0, score_2=0;

        //server socket
	static ServerSocket server;

        //socket for first client
	static Socket client1; 
        // socket for second client
	static Socket client2; 

	//handles data input and output streams
	static DataOutputStream client1writer;
	static DataOutputStream client2writer;

	//input streams from clients
	static DataInputStream reader1;
	static DataInputStream reader2;

        //port numbers
	static final int PORT = 9999;

	//Main Method
	public static void main(String[]args)
	{
		//game logic methods
		initDeck();

		shuffleDeck();
		showDeck();
                //start try-Catch Block for Socket Errors.
		try
		{
			server = new ServerSocket(PORT);// server wth port

			System.out.println("Server Started");//Diplay server started msg

			client1 = server.accept();//accept connection from client

			//this is reader and writer for player 1
			client1writer = new DataOutputStream(client1.getOutputStream());
			reader1 = new DataInputStream(client1.getInputStream());

			client2 = server.accept();//accept connection from second client

			//this is reader and writer for player 2
			client2writer = new DataOutputStream(client2.getOutputStream());
			reader2 = new DataInputStream(client2.getInputStream());

			//message to let server admin know clients connected
			System.out.println("Player 1 connected with disc X.");
			System.out.println("Player 2 connected with disc O.");

			//IDs for player 1 & 2
                        //this is for player 1
			String ID1 = "X";
                        //this is for player 2
			String ID2 = "O";

			//sends the clients(players) IDs 
			client1writer.writeUTF(ID1);
			client2writer.writeUTF(ID2);


			//to know the current state of the board game
			StringJoiner sj = new StringJoiner(System.lineSeparator());
                        //for each row on the board
			for (String[] row : board) {
				sj.add(Arrays.toString(row));
			}//end for loop
                        //display message
			sj.add("Player 1 score : " + score_1);
			sj.add("Player 2 score : " + score_2);
                        //string joined together to send to the clients
			String result = sj.toString();
                        //sends to player 1
			client1writer.writeUTF(result);
                        //flush the writer
			client1writer.flush();

			client2writer.writeUTF(result);//sends to player 2
                        //flush the writer
			client2writer.flush();


			int count =0;
                        
			while(checkExit!=8)//this is an int which will be used to check and exit the program
			{

				System.out.println("\nWaiting on Player Input");
				count++;



				//if statement to check if player1s turn
				if ("Player1".equals(playing)) {
					//put input from client into temp
					col_row = reader1.readUTF();
                                        //display msg
					System.out.println("\n\nPlayer 1 entered " + col_row);
				}//end if
                                //player2's turn
				else{
                                        //put input from client into temp
					col_row = reader2.readUTF();
					System.out.println("\n\nPlayer 2 entered " + col_row);
				}//end else
				if(col_row.equals("2")){

					check_1=8;
					check_2=8;
                                        //null temp msg
					String msg= "";
                                        //if to check scores
					if(score_1>score_2){
						msg = "Player1 Won";
					}//end if
					else if(score_1==score_2){
						msg  = "Both players won";
					}//end if
					else{
						msg  = "Player2 Won";
					}//last option
                                        
					client1writer.writeUTF(msg);//sends to player 1
					client1writer.flush();//flushes the writer1
					client2writer.writeUTF(msg);//sends to player 2
					client2writer.flush();//flush writer 2
                                        
                                        
                                        //write ints to clients
					client1writer.writeInt(check_1);
					client2writer.writeInt(check_2);
                                        
                                        
                                        //flush client one and two
					client1writer.flush();
					client2writer.flush();

					return;

				}//end if row equals
                                //spilt the colum and row
				String[] ary = col_row.split("");
				int column = get_col(ary[0]);
				int row = Integer.parseInt(ary[1])-1;
				//enters the value entered by user into the board
				

                                //if to check count
				if(count==1){
					mem_first = row;
					mem_first_col = column;
				}//end if

                                //to have one string to send
				StringJoiner data = get_deck_str(row, column);
                                //if to check count
				if(count==2){
                                        //temp int = row and column
					mem_second = row;
					mem_second_col = column;
                                        //if statement to compare cards
					if(compareCards(mem_first, mem_first_col, mem_second, mem_second_col)){
						//client guessed correnctly
                                            data.add("match found");
                                            
						if("Player1".equals(playing)){
                                                    //increment score if player one
							score_1++;
						}//end if
						else{
                                                    //increment score if player 2
							score_2++;
						}//end else
					}//end if
					else{
						data.add("no match found");
					}//end else
					//reset temps
					mem_first=-1;
					mem_second=-1;
					mem_first_col=-1;
					mem_second_col=-1;
				}//end if

				data.add("Player 1 score : " + score_1);
				data.add("Player 2 score : " + score_2 + "\n");

				if ("Player1".equals(playing) && count==2) {
					data.add("[INFO]: Switched to 2");
					String[][] board_ = {{" ","A", "B","C", "D"},
							{"1","*","*","*","*"},
							{"2","*","*","*","*"},
							{"3","*","*","*","*"},
							{"4","*","*","*","*"}};
					board=null;
					board = board_;
				}
				else if ("Player2".equals(playing) && count==2){
					data.add("[INFO]: Switched to 1");
					String[][] board_ = {{" ","A", "B","C", "D"},
							{"1","*","*","*","*"},
							{"2","*","*","*","*"},
							{"3","*","*","*","*"},
							{"4","*","*","*","*"}};
					board=null;
					board = board_;
				}

				client1writer.writeUTF(data.toString());//sends to player 1
				client1writer.flush();//flushes the writer1
				client2writer.writeUTF(data.toString());//sends to player 2
				client2writer.flush();

				if (checkExit==8)
				{
					client1writer.writeInt(0);
					break;
				}
				else
				//this is sent to player 1 as a check to continue and not stop
				{

					if(count==2 && "Player1".equals(playing)){
						playing="Player2";
                                                //reinitialize count to 0
						count=0;
						check_1=2;
						check_2=1;
					}
					else if(count==2 && "Player2".equals(playing)){
						playing="Player1";
                                                //reinitialize count to 0
						count=0;
						check_1=1;
						check_2=2;
					}
					client1writer.writeInt(check_1);
					client2writer.writeInt(check_2);


				}
				client1writer.flush();
				client2writer.flush();


			}//end of while loop

			System.out.println("\nServer closed. Thank You.");

			//closes the reader and writers for both clients
			client1writer.close();
			reader1.close();
			client1.close();

			client2writer.close();
			reader2.close();
			client2.close();

			//closes the server
			server.close();
		}//end try
		catch (IOException IOex){
			System.out.println("Server Error.");
			IOex.printStackTrace();
		}//end catch
	}//end main

	// method to get card at particular spot on board
	private static int getCardOnBoard(int x, int y) {

		return DECK[x][y];

	}

	// method to compare two cards on board

	private static boolean compareCards(int i, int j, int k, int l) {
                //if one card equals another
		if (getCardOnBoard(i, j) == getCardOnBoard(k, l)) {

			return true;

		}
                //else false
		return false;

	}//end method
        //method to tell wether or not the card should be showing
	private static void hideCardOnBoard(int i, int j) {
                
		SHOW[i][j] = false;

	}//end method

	// Initialize the Deck
	private static void initDeck() {

		int card = 0;

		for (int i = 0; i < DECK.length; i++) {
                        //nested for loop to cycle through
			for (int j = 0; j < DECK.length; j++) {

				if (card == DECK.length * 2) {

					card = 1;

				}

				DECK[i][j] = ++card;
                                //call method to hide card on board
				hideCardOnBoard(i,j);

			}

		}//end for loop to cycle through array

	}//end init deck method

	// Shuffle Deck

	private static void shuffleDeck() {

		for (int i = 0; i < DECK.length * 2; i++) {

			swapCards(getRandom(), getRandom(), getRandom(), getRandom());

		}//for loop to cycle through deck array

	}//end method
        
        //method to get random
	private static int getRandom() {

		Random random = new Random();

		return random.nextInt(3);

	}

	private static void swapCards(int i, int j, int k, int l) {

		int tmp = DECK[i][j];

		DECK[i][j] = DECK[k][l];

		DECK[k][l] = tmp;

	}

        //method to return the string/letter sent by client into the index of array
	private static int get_col(String col_s) {
            //switch statement to convert string to int
            switch (col_s) {
                case "A":
                    return 0;
                case "B":
                    return 1;
                case "C":
                    return 2;
                default:
                    return 3;
            }
	}

	private static StringJoiner get_deck_str(int row, int col) {
                //temp int to store index
		int a = DECK[row][col];
		board[row + 1][col + 1] = String.valueOf(a);
		StringJoiner sj = new StringJoiner(System.lineSeparator());
		if("Player1".equals(playing)){
			sj.add("Player 1 is playing");
		}//end if
		else{
			sj.add("Player 2 is playing");
		}//end else
                
                //for each loop to cycle through board
		for (String[] row_ : board) {
			sj.add(Arrays.toString(row_));
		}//for each loop
		return sj;
	}//end method

        //method to show the deck
	private static void showDeck() {
            //nested loop to cycle throught arrays
            for (int[] DECK1 : DECK) {
                for (int j = 0; j < DECK.length; j++) {
                    System.out.print(" ");
                    System.out.print(DECK1[j]);
                    System.out.print(" ");
                }//end for
                System.out.println();
            }//end for

	}//end showDeck method

}//end server class