
import java.io.*;
import java.net.*;

public class qwr {

	public static int ListenPort=5514;
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("Hello world!");

		ServerSocket echoServer = null;
        String line;
        DataInputStream is;
        Socket clientSocket = null;
        try {
            echoServer = new ServerSocket(ListenPort);
         }
         catch (IOException e) {
            System.out.println(e);
         }   
        try {
            clientSocket = echoServer.accept();
            is = new DataInputStream(clientSocket.getInputStream());

            while (true) {
              line = is.readLine();
              if (line != null)
              {
            	  System.out.println(line);
            	}
              else
              {
            	  try {
            	       is.close();
            	       echoServer.close();
            	    } 
            	    catch (IOException e) {
            	       System.out.println(e);
            	    }
            	  echoServer = new ServerSocket(ListenPort);
            	  clientSocket = echoServer.accept();
                  is = new DataInputStream(clientSocket.getInputStream());
            	  System.out.println(ListenPort);
              }
            }
         }   
     catch (IOException e) {
            System.out.println(e);
         }
	}
}


