import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;


public class Processor extends Thread{
	
	private Socket socket;
	private InputStream in;
	private PrintStream out;
	private final static String WEB_ROOT="F:\\JEECG\\MyProject\\webserver\\htdocs";
	
	public Processor(Socket socket){
		this.socket = socket;
		try {
			in = socket.getInputStream();
			out = new PrintStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		String filename = parse(in);
		sendFile(filename);
	}
	
	public String parse(InputStream in){
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String filename=null;
		
		try {
			String httpMessage = br.readLine();
			String[] content = httpMessage.split(" ");
			
			if(content.length!=3){
				sendErrorMessage(400, "客户端求情错误！");
				return null;
			}
			
			System.out.println("code="+content[0]+",filename="+content[1]+",http version="+content[2]);
			filename=content[1];
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filename;
	}
	
	public void sendErrorMessage(int errorCode,String errorMessage){
		out.println("HTTP/1.0 "+errorCode+" "+errorMessage);
		out.println("content-type:text/html");
		out.println();
		out.println("<html>");
		out.println("<title>Error Message</title>");
		out.println("<body>");
		out.println("<h1>ErrorCode:"+errorCode+"<br>"+"message:"+errorMessage+"</h1>");
		out.println("</body>");
		out.println("</html>");
		out.flush();
		out.close();
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendFile(String filename){
		File file = new File(Processor.WEB_ROOT+filename);
		if(!file.exists()){
			sendErrorMessage(404, "File not Found!");
			return;
		}
		
		try {
			InputStream in = new FileInputStream(file);
			byte content[] = new byte[(int)file.length()];
			in.read(content);
			out.println("HTTP/1.0 200 success");
			out.println("content-length:"+content.length);
			out.println();
			out.write(content);
			out.flush();
			out.close();
			in.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
