/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3166_final_memorymatch;
//import java packages
import java.net.Socket;
import java.util.Scanner;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.InputMismatchException;
/**
 *
 * @author Eli Solomon
 */
public class Client{

    
        //decalare objects and things
	static Socket client;
	static String col_row = null;
	static String[][] hghtCk;
	private static DataInputStream reader;
	private static DataOutputStream writer;

	//Main Method Begins
	public static void main(String[]args) throws ClassNotFoundException {
		try{
                        //this will be used as a switch to cause the program to exit after a winner has been found
			int check=0;
                        
			client = new Socket("localhost",9999);//
			System.out.println("Connecting to Server...");

			//this will handle the transmission of data to and from the server and client
			reader = new DataInputStream(client.getInputStream());
			writer = new DataOutputStream(client.getOutputStream());

			//accepts the Player ID from server and displays it
			//the ID is also used as the disc in the game

			String ID = reader.readUTF();

			///if the ID is 'X' its player 1
			///if ID is 'O' its player 2

			if (ID.equals("X")){
				System.out.println("PLAYER 1");
				check=1;
			}//end if

			if (ID.equals("O")){
				System.out.println("PLAYER 2");
				check=2;
			}//end if
                        
                        //display player ID
			System.out.println("Your ID is: " + ID);

			
			//server sends check to client everytime it sends a request and if check=0 then a winner has been found
			//the the game exit te loop
                        
			System.out.println();

			//this will read and display the board to player
			String board=reader.readUTF();

                        //removes any other character form the string
			System.out.println("\n" + board.replace("[", "").replace("]", "").replace(",",""));
                        
                        //while loop for game status
			while(check!=0){
                            
				if (check==1){
					GetRowCol();//gets the column

					
					writer.writeUTF(col_row);//sends column to server

					System.out.println();

					//flushes the writer
					writer.flush();
				}//end if

				//this will read and print the board to player
				board=reader.readUTF();
                                //removes any other character form the string
				System.out.println("\n" + board.replace("[", "").replace("]", "").replace(",",""));

				try {
					if(check==8 || col_row.equals("2")) {
						System.exit(1);

					}//end if
				} catch (Exception e) {
					// TODO Auto-generated catch block
					                               System.out.println(e);
				}//end catch
                                //reads check from server one more time
				check= reader.readInt();
				
			}//end while loop
			//whoever first wins gets this message
			if (check==0){
				System.out.println("*****You have won!!!******");
			}

			//closes the client socket  reader and writer
			client.close();
			reader.close();
			writer.close();
		}catch (IOException IOex){
			
			                 System.out.println(IOex);
		}
	}

	//methods

        //method to check if user input is correct
	private static boolean isValidCoordinate(String input){

            
            //if input is correct length
		if(input.length() == 2){
                        //break up user input into an string array
			String[] arr = input.split("");
                        //string array to check user input
			String[] arr_str = {"A","B","C","D"};
                        //if statements to check the the column input
			if(arr[0].equals(arr_str[0])){
			}
			else if(arr[0].equals(arr_str[1])){
			}
			else if(arr[0].equals(arr_str[2])){
			}
			else if(arr[0].equals(arr_str[3])){
			}
			else{
				return false;
			}//if not then input is not valid
			try {
				int x = (Integer.parseInt(arr[1]));
				if(x>=1 && x <=4){
					return true;
				}
			} catch (NumberFormatException e){
				                        System.out.println(e);
				return false;
			}//if it is not an integer 
			
			return true;

		}//end if

		return false;//TODO

	}//end method to check for input

	//checks and validates user input to make the right numbers are entered
	public static void GetRowCol()
	{
		try {
			Scanner scanner = new Scanner(System.in);

			int opt = 0;
                        //do while loop for menu and to propt user for input
			do {

				System.out.println("Choose option");
				System.out.println("1. Continue game");
				System.out.println("2. Exit");

				try {
					
					if (scanner.hasNext()) {
						opt = scanner.nextInt();
					}
					else{
						opt = scanner.nextInt();
					}
				//catch statement to check reprompt user for a 1 or 2	
				} catch (InputMismatchException e) {
					
					opt = 0;
					scanner.next();
					System.out.println("Choose option - either `1` or  `2`");
					continue;
				}//end catch

				switch (opt) {

				case 1:

					System.out.println("Enter coordinates for the card");

					System.out.print(">:");

					col_row = scanner.next();
					if(!isValidCoordinate(col_row)) {

						System.out.println("Not a valid coordinate!");
						System.out.println();						

						break;

					}

					return;

				case 2:

					scanner.close();
					col_row = "2";
					return;

				default:

					System.out.println("Choose appropriate option - either `1` or  `2`");

				}

			} while (opt != 2);

			scanner.close();
		} catch (Exception e) {
                    System.out.println(e);
		}
	}

}