import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Shell {
	
	// ******** ENVIRONMENT VARIABLES ******** //
	private final static String user = System.getProperty("user.name");		// grabs user name
	private static String currentDirName = System.getProperty("user.home");	// grabs home directory
	private static File currentDir = new File(currentDirName);	
	private File cwd;
 	/** the instance of the current shell. */
	private static Shell Shell = null;
	private static boolean quit = false;
	private static int lineCount = 1;
	
	public static void main(String[] args) {
		
		// sets current directory to user's home directory
		System.setProperty("user.dir", currentDirName);
		
		if (args.length > 0) {
		
			String filename = args[0];
			File file = new File(filename);
			BufferedReader reader;
			
			try {
				
				reader = new BufferedReader(new FileReader(file));
				
				String inputCommand = "";
				
				while ( (inputCommand=reader.readLine()) != null ) {
					parseInput(inputCommand);
				}
				
				quit();
				
			} catch (IOException e) {
				System.out.println(e.getMessage()); 
				System.out.println("starting shell");
				lineCount += 2;
			}
			
		}
		
		loop();
		
	}
	
	/**
	 * This is a loop that will make sure our prompt keeps going as long as a true value is not passed into it. 
	 * When a true value is passed in it allows the user to exit our command prompt.
	 * See switch statement.
	 * @param q
	 */
	private static void loop(){
		
		String input = "";
		
		while( quit == false){
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
		String command = inputTokens[0];		// grabs command from input line, which should be first element of input
		
		// add other commands in switch statement
		switch (command) {
		
			case "": 
				break;	// essentially do nothing when user presses enter
				
			case "cd":
				cd(argus);
				break;

	        case "echo":
	            echo(argus);
	            break;
	            
	        case "environ":
	            environ(argus);
	            break;
			

			case "clr":
				clr();
				break;
				
			case "dir":
				dir(argus);
				break;
				
			case "quit":
				quit();
				break;
			default:

				try{
					fullPath(inputTokens);
				}
				catch(IOException e){
					System.out.println("Command '" + command + "' not recognized");
					lineCount++;
				}	
		}
	}

	
	/*
	 * cd method is called when user enters cd command.
	 * updates environment variables and changes working directory accordingly.
	 * if no arguments are given with cd command, then prints current directory, much like pwd
	 */
	private static void cd(String[] argus) {
		if (argus.length == 0) { 
			System.out.println(currentDirName); // pwd
			lineCount++;
		}
		else {
			String dir = argus[0];			// directory to change to
			String tmp = currentDirName + "/" + dir;
			File tmpFile = new File(tmp);	// creates temporary file object
			try {
				if (tmpFile.exists()) {		// checks if file object is actually a real directory
					currentDir = tmpFile.getCanonicalFile();			        // update currentDir environment variable
					currentDirName = currentDir.getAbsolutePath();		// update currentDirName environment variable
					System.setProperty("user.dir", currentDirName);		// changes current working directory
				} 
				else{
					System.out.println("no such file exists");
					lineCount++;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}

	private static void environ(String[] argus){
		Map<String, String> environMap = System.getenv();
		SortedMap<String, String> sortedEnvironMap = new TreeMap<String, String>(environMap);
		Set<String> keySet = sortedEnvironMap.keySet();
		for (String key : keySet) {
			String value = environMap.get(key);
			System.out.println("[" + key + "] " + value);}
	}
	private static void echo(String[] argus){   
		String arguments = "";
		for (int i = 0;i < argus.length; i++){
			arguments = arguments + argus[i]+ " ";
		}
		System.out.println( user + "\n" + currentDirName + "\n" + arguments);
		lineCount += 3;    
	}

	
	/**
	 * This method should be able to clear the console screen and should activate when the user types clr.
	 * This will work on a CentOs Redhat linux system.
	 * Every time anything is printed to the screen we will be incrementing a counter lineCount that will 
	 * Let us know how many lines have been printed. This way we can delete them to clear the screen.
	 */
	private static void clr(){
		
		// This will work on Linux.
		
		for (int n = 0; n < lineCount; n++){
			System.out.print("\033[1A");  //Move up n spaces. 
			System.out.print("\033[2K");  //Delete current line.
		}
	}
	
	/**
	* Shell should fork and execute programs as child processes.
	*/

	private static void fullPath(String[] commands) throws IOException {
		
		//Shell should contain a full path from where it was executed.
		final File f = new File(Shell.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		System.out.println("Shell = " + f);
		lineCount++;
		 
		Process process;

		Thread thread = new Thread();
		
		
	    // In java the process builder command forks a new process and executes. 
		process = new ProcessBuilder().command(commands).inheritIO().start();
		System.out.println("Press enter to return to the shell.");
		lineCount++;

	}
	
	/**
	 * This method should be able to list all the current files in a directory.
	 * This also takes in a parameter. 
	 */
	private static void dir(String[] arg){
		
		// If the argument is null then it will print the current directory because the 
		// cd(arg) method won't change the currentDirName
		cd(arg);
		dirHelper(currentDirName);

	}
	
	// This method does all the work for the dir function.
	private static void dirHelper(String directory){
		//Make an array of files based on the current directory you are in.
		File[] currentDirectory = new File(currentDirName).listFiles();
		//Make an ArrayList in which to put the names of the files.
		ArrayList<String> directoryList = new ArrayList<String>();
		//Iterate through the array of files and if there are files then add them to our string array.
		//Print out the list as we are adding them. 
			if(currentDirectory != null){
				for(int i = 0; i < currentDirectory.length; i++){
					directoryList.add(currentDirectory[i].getName());
					System.out.println(directoryList.get(i));
					lineCount++;
				    }
				}
				//If there are no files in the list. 
			else{
					lineCount++;
					System.out.println("No files in current directory.");
				}
	}
    
	/**
	 * method is called when shell needs to quit.
	 * pretty self-explanatory
	 */
    private static void quit() {
    	System.out.println("Exiting the shell...");
    	System.exit(0);
    }

	/*
	 * displays basic command prompt
	 * shows user name and current directory name
	 */
	private static String prompt() {
		String fileName = currentDir.getName();
		if (fileName.equals(user)) fileName = "~";		// displays if current directory is home
		
		System.out.print(user + " " + fileName + " >");
		lineCount++;
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
