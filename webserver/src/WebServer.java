import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class WebServer {

	public void ServerStart(int port){
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			
			while(true){
				Socket socket = serverSocket.accept();
				new Processor(socket).start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		int port = 80;
		if(args.length==1){
		port = Integer.parseInt(args[0]);
		}
		new WebServer().ServerStart(port);
	}
}
