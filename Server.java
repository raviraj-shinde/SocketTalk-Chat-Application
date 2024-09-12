import java.io.*;
import java.net.*;

public class Server {

  ServerSocket server;
  Socket socket;

  BufferedReader br;
  PrintWriter out;

  // Volatile boolean flag for stopping both threads
  private volatile boolean isRunning = true;

  //Constructor
  public Server() {
    try {
      server = new ServerSocket(7777);
      System.out.print("Server is ready to accept connection \n waiting...");
      socket = server.accept();
      System.out.println("Connection Done.");
      br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(socket.getOutputStream());

      startReading();
      startWriting();
    } catch (Exception e) {
      System.out.println("Connection closed or error in writing.");
      isRunning = false; // Stop both threads if there's an exception
    }
  }

  public void startReading() {
    //Thread for reading continuously
    Runnable r1 = () -> {
      System.out.println("Reader Started... ");
      try {
      while (isRunning) {
          String msg = br.readLine();
          if (msg.equals("exit")) {
            System.out.println("Client terminated the chat");
            isRunning = false; // Stop both threads
            socket.close(); // Close the socket
            break;
          }
          System.out.println("Client: " + msg);
        }
        } catch (Exception e) {
            System.out.println("Connection closed or error in Reading.");
            isRunning = false; // Stop both threads if there's an exception
        }
    };
    new Thread(r1).start();
  }

  public void startWriting() {
    //Thread for geting data from user and sending it to client continuously
    Runnable r2 = () -> {
      System.out.println("Writer Started... ");
      try {
        BufferedReader br1 = new BufferedReader(
          new InputStreamReader(System.in)
        );
        while (isRunning) {
          String content = br1.readLine();
          out.println(content);
          out.flush(); //forcefully send data
          if (content.equalsIgnoreCase("exit")) {
            isRunning = false; // Stop both threads if user types "exit"
            socket.close(); // Close the socket
            break;
          }
        }
      } catch (Exception e) {
        System.out.println("Connection closed or error in writing.");
        isRunning = false; // Stop both threads if there's an exception
      }
    };
    new Thread(r2).start();
  }

  public static void main(String[] args) {
    System.out.println("Server is running...");
    new Server();
  }
}
