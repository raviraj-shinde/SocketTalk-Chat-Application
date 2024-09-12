import java.io.*;
import java.net.*;

public class Client {

  Socket socket;

  BufferedReader br;
  PrintWriter out;

  //Constructor
  public Client() {
    try {
      System.out.print("Sending Request to Server : ");
      socket = new Socket("127.0.0.1", 7777);
      System.out.println("Connection Done");

      br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(socket.getOutputStream());

      startReading();
      startWriting();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void startReading() {
    //Thread for reading continuously
    Runnable r1 = () -> {
      System.out.println("Reader Started... ");
      while (true) {
        try {
          String msg = br.readLine();
          if (msg.equals("exit")) {
            System.out.println("Server terminated the chat");
            break;
          }
          System.out.println("Server: " + msg);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
    new Thread(r1).start();
  }

  public void startWriting() {
    //Thread for geting data from user and sending it to client continuously
    Runnable r2 = () -> {
        System.out.println("Writer Started... ");
        while (true) {
            try{
                BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                String content = br1.readLine();
                out.println(content);
                out.flush(); //forcefully send data
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    new Thread(r2).start();
  }

  public static void main(String[] args) {
    System.out.println("Client is running...");
    new Client();
  }
}
