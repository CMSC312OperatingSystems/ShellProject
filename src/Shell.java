import java.io.*;
import java.util.Arrays;

public class Shell {
	
	// ******** ENVIRONMENT VARIABLES ******** //
	private final static String user = System.getProperty("user.name");		// grabs user name
	private static String currentDirName = System.getProperty("user.home");	// grabs home directory
	private static File currentDir = new File(currentDirName);				//
	
	public static void main(String[] args) {
		
		// sets current directory to user's home directory
		System.setProperty("user.dir", currentDirName);
		
		String input = "";
		
		// condition will need to be replaced with condition to check if user inputs 'quit' command
		while (true) {
			input = prompt();
			parseInput(input);
		}	
	}
	
	/*
	 * parses user input and executes commands accordingly
	 * this method will have to be augmented with more commands as we add more functionality
	 */
	private static void parseInput(String input) {
		String[] inputTokens = input.split(" ");	// tokenizes input based on white space
		String[] argus = new String[0]; 			// array for input arguments
		String command = inputTokens[0];			// grabs command from input line, which should be first element of input
		if (inputTokens.length > 1) argus = Arrays.copyOfRange(inputTokens, 1, inputTokens.length); // separates rest of arguments
		
		// add other commands in switch statement
		switch (command) {
			case "": break;	// essentially do nothing when user presses enter
			case "cd":
				cd(argus);
				break;
			default:
				System.out.println("Command '" + command + "' not recognized");
		}
	}
	
	/*
	 * cd method is called when user enters cd command.
	 * updates environment variables and changes working directory accordingly.
	 * if no arguments are given with cd command, then prints current directory, much like pwd
	 */
	private static void cd(String[] argus) {
		if (argus.length == 0) System.out.println(currentDirName); // pwd
		else {
			String dir = argus[0];			// directory to change to
			String tmp = currentDirName + "/" + dir;
			File tmpFile = new File(tmp);	// creates temporary file object
			try {
				if (tmpFile.exists()) {		// checks if file object is actually a real directory
					currentDir = tmpFile.getCanonicalFile();			// update currentDir environment variable
					currentDirName = currentDir.getAbsolutePath();		// update currentDirName environment variable
					System.setProperty("user.dir", currentDirName);		// changes current working directory
				} else System.out.println("no such file exists");
			}
			catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
	
	/*
	 * displays basic command prompt
	 * shows username and current directory name
	 */
	private static String prompt() {
		String fileName = currentDir.getName();
		if (fileName.equals(user)) fileName = "~";		// displays if current directory is home
		
		System.out.print(user + " " + fileName + " >");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String result = "";
		try {
			result = in.readLine();
		} catch (Exception e) {
			result = "Error reading line";
		}
		return result;
	}

}