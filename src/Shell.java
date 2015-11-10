import java.io.*;
import java.util.Arrays;

public class Shell {
	
	// ******** ENVIRONMENT VARIABLES ******** //
	private final static String user = System.getProperty("user.name");
	private static String currentDirName = System.getProperty("user.home");
	private static File currentDir = new File(currentDirName);
	
	public static void main(String[] args) {
		
		System.setProperty("user.dir", currentDirName);
		
		String input = "";
		
		// condition will need to be replaced with condition to check if user inputs 'quit' command
		while (true) {
			input = prompt();
			parseInput(input);
		}	
	}
	
	private static void parseInput(String input) {
		String[] inputTokens = input.split(" ");
		String[] argus = new String[0];
		String command = inputTokens[0];
		if (inputTokens.length > 1) argus = Arrays.copyOfRange(inputTokens, 1, inputTokens.length);
		
		// add other commands in switch statement
		switch (command) {
			case "": break;
			case "cd":
				cd(argus);
				break;
			default:
				System.out.println("Command '" + command + "' not recognized");
		}
	}
	
	private static void cd(String[] argus) {
		if (argus.length == 0) System.out.println(currentDirName);
		else {
			String dir = argus[0];
			String tmp = currentDirName + "/" + dir;
			File tmpFile = new File(tmp);
			try {
				if (tmpFile.exists()) {
					currentDir = tmpFile.getCanonicalFile();
					currentDirName = currentDir.getAbsolutePath();
					System.setProperty("user.dir", currentDirName);
				} else System.out.println("no such file exists");
			}
			catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
	
	private static String prompt() {
		String fileName = currentDir.getName();
		if (fileName.equals(user)) fileName = "~";
		
		System.out.print(user + " " + fileName + " >");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String result = "Error reading input";
		try {
			result = in.readLine();
		} catch (Exception e) {
			result = "Error reading line";
		}
		return result;
	}

}