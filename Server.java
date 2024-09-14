import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class Server extends JFrame {

  ServerSocket server;
  Socket socket;

  BufferedReader br;
  PrintWriter out;

  //Declare Component
  private JLabel heading = new JLabel("Server Area");
  private JTextArea messageArea = new JTextArea();
  private JTextField messageInput = new JTextField();
  private Font font = new Font("Roboto", Font.PLAIN, 20);

  // Volatile boolean flag for stopping both threads
  private volatile boolean isRunning = true;

  //Constructor
  public Server() {
    try {
      // server = new ServerSocket(7777);
      // System.out.print("Server is ready to accept connection \n waiting...");
      // socket = server.accept();
      // System.out.println("Connection Done.");
      // br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      // out = new PrintWriter(socket.getOutputStream());

      createGUI();
      //handleEvents();

      // startReading();
      // startWriting();
    } catch (Exception e) {
      System.out.println("Connection closed or error in writing.");
      isRunning = false; // Stop both threads if there's an exception
    }
  }

  private void createGUI() {
    //GUI code (Window)
    this.setTitle("Server Messanger");
    this.setSize(600, 700);
    this.setLocationRelativeTo(null);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //Font for component
    heading.setFont(font);
    messageArea.setFont(font);
    messageInput.setFont(font);

    //coding for component ( Location, image etc )
    heading.setIcon(new ImageIcon("Images/clogo.png"));
    heading.setHorizontalTextPosition(SwingConstants.CENTER);
    heading.setVerticalTextPosition(SwingConstants.BOTTOM);
    heading.setHorizontalAlignment(SwingConstants.CENTER);
    heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    messageArea.setEditable(false);
    messageInput.setHorizontalAlignment(SwingConstants.CENTER);

    //frame Layout North west etc to arrange Components)
    this.setLayout(new BorderLayout());
    this.add(heading, BorderLayout.NORTH);
    this.add(messageArea, BorderLayout.CENTER);
    this.add(messageInput, BorderLayout.SOUTH);

    this.setVisible(true);
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
