import java.io.*;
public class Debug {
	private static Debug instance;
	static{
		try {
			instance = new Debug();
		}
		catch(IOException e) {
			System.out.println("File handling error");
			e.printStackTrace();
		}
	}
	
	public static void output(String a) {
		instance.out.println(a);
	}
	
	private PrintWriter out;
	
	private Debug() throws IOException{
		out = new PrintWriter(new BufferedWriter(new FileWriter("debug.out")));
	}
}
