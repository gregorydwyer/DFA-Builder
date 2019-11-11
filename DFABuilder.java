/*
DFA Builder
This program will take input or read a file to build a DFA based on the language described.
*/
import java.util.Scanner;
import java.io.*;
public class DFABuilder {

	public static void main(String[] args) throws FileNotFoundException{
		Scanner input = new Scanner(System.in);
		DFA automata;
		if(args.length == 1){
			// use command line input as filename
			automata = new DFA(args[0]);
		}
		else {
			System.out.print("Load DFA from File? (y or n) ");
			char ans = input.nextLine().charAt(0);
			if(ans == 'n'){
				//get user input and build/test the DFA
				automata = new DFA();
				automata.userStringTest();
			}
			else{
				System.out.print("Enter file name: ");
				String filename = input.nextLine();
				try{
					//Try to read the file, build and test the DFA
					automata = new DFA(filename);
					automata.userStringTest();
				}
				catch ( Exception e){
					System.out.println(e);
					System.exit(0);
				}
			}
		}
	}
}

class DFA {

	int states = 0;
	char[] alph;
	String[][][] dfa;
	String[] accepting;
	String starting;
	
	public DFA() {
		// The following secion takes the user's input and saves it into the DFA
		Scanner input = new Scanner(System.in);
		System.out.print("Enter number of states: ");
		states = input.nextInt();

		input.nextLine();

		System.out.print("Enter the elements of the alphabet separated by a single space: ");
		String[] temp = input.nextLine().split(" ");
		alph = new char[temp.length];
		for (int i = 0;
				i < temp.length;
				i++) {
			alph[i] = temp[i].charAt(0);
		}
		dfa = new String[states][alph.length][1];

		System.out.println(
				"Enter the correct destination state for each input:");
		for (int i = 0;
				i < states;
				i++) {
			for (int j = 0; j < alph.length; j++) {
				System.out.printf("From state %s on an input of %s go to: ", i + 1, alph[j]);
				dfa[i][j][0] = input.next();
				dfa[i][j][0] = Integer.toString(Integer.parseInt(dfa[i][j][0])-1);
				input.nextLine();
			}
		}
				
		System.out.print("Enter the starting state: ");
		starting = input.nextLine();
		starting = Integer.toString(Integer.parseInt(starting)-1);
		
		System.out.print("Enter accepting states separated by a single space: ");
		String[] temp2 = input.nextLine().split(" ");
		accepting = new String[temp2.length];
		for (int i = 0;i < temp2.length; i++) {
			accepting[i] = Integer.toString(Integer.parseInt(temp2[i])-1);
		}

	}

	public DFA(String filename) throws FileNotFoundException{
		File input = new File(filename);
		Scanner file = new Scanner(input);
		String temp;
		do{
			temp = file.nextLine();
			if(temp.charAt(0) == '%' || (temp.charAt(0)=='/' && temp.charAt(1) == '/' )){
				//skip line it is comments
			}
			else if(temp.contains("Number of states")){
				String[] inputs = temp.split("\\s+");
				for(int i = 0; i < inputs.length; i++){
					if(inputs[i].matches("\\d+")){
					states = Integer.parseInt(inputs[i]);
					}
				}
			}
			else if(temp.contains("Alphabet")){
				int place = 0;
				String[] inputs = temp.split("\\s+");
				alph = new char[inputs.length - 1];
				for(int i = 1; i < inputs.length; i++){
					alph[place] = inputs[i].charAt(0);
					place++;
				}	
			}
			else if (temp.contains("Transitions begin")){
				temp = file.nextLine();
				dfa = new String[states][alph.length][1];
				do{
				String[] values = temp.split("\\s+");
				for(int i = 0; i < alph.length; i++){
					if(alph[i] == values[1].charAt(0)){
						dfa[Integer.parseInt(values[0])-1][i][0] = Integer.toString(Integer.parseInt(values[2])-1);
					}
				}
				
				temp = file.nextLine();
				}while(!temp.contains("Transitions end"));
			}
			else if (temp.contains("Start state")){
				String[] inputs = temp.split("\\s+");
				starting = Integer.toString(Integer.parseInt( inputs[2])-1);
			}
			else if (temp.contains("Accept states")){
				String[] accept = temp.split("\\s+");
				int place = 0;
				accepting = new String[accept.length - 2];
				for(int i = 2; i < accept.length; i++){
					if(accept[i].matches("\\d+")){
					accepting[place] = Integer.toString(Integer.parseInt(accept[i])-1);
					place++;
					}
				}
			}	
		}while(file.hasNextLine());
	}
	
	public boolean TestString(String str) {
		if(str == null){
			//empty string
			for(int i = 0; i < accepting.length; i++){
				//check all accepting states to see if one is the starting state
				if(starting.equals(accepting[i])){
					return true;
				}
			}
			return false;
		}
		else{
			return TestString(str.toCharArray(), starting);
		}
	}

	public boolean TestString(char[] str, String q) {
		int a = Integer.parseInt(q);
		if (str.length == 0) {
			for(int i = 0; i < accepting.length; i++){
				if(accepting[i].equals(q)){
				return true;
				}
				
			}
			return false;
		} else {
			for (int i = 0; i < alph.length; i++) {
				if (str[0] == alph[i]) {
					//System.out.println("At " + q + " going to " + dfa[a][i][0] );
					char[] newStr = new char[str.length - 1];
					System.arraycopy(str, 1, newStr, 0, newStr.length);
					return TestString(newStr, dfa[a][i][0]);
				}
			}
		}
		//if program reaches this point there are charcters not in the alphabet
		System.out.println("Invalid characters found.");
		return false;
		
	}
	public void userStringTest(){
		Scanner input = new Scanner(System.in);
		//prompt user and get input
		System.out.print("The Alphabet is ");
		for (int i = 0; i < alph.length; i++) {
			System.out.print(alph[i] + " ");
		}
		System.out.println("");
		System.out.print("Enter a string: ");
		String test = input.nextLine();
		if(TestString(test)){
			System.out.printf("The string %s is accepted.\n", test);
		}
		else{
			System.out.printf("The String %s is not accepted.\n", test);
		}
		System.out.print("Test another string? (y or n) ");
		char temp = input.nextLine().charAt(0);
		if(temp == 'y'){
			userStringTest();
		}
	}
}
