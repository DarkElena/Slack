import java.util.Scanner;

import org.apache.log4j.Logger;

public class Main {
	final static Logger mainLogger =  Logger.getLogger(Main.class);
	public static void main(String... args) {
		SlackMessenger messenger = new SlackMessenger();
		Scanner in = new Scanner(System.in);
		System.out.print("Enter message, please: ");
		String message = in.nextLine();
		System.out.print("Enter channel or username: ");
		String channel = in.nextLine();
		mainLogger.info("User's message: " + message + " channel or username: " + channel);
		System.out.println(messenger.sendMessenge("Elena", message, channel));
	}
}