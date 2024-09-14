import java.io.*;
import java.net.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class Client extends JFrame {

  Socket socket;

  BufferedReader br;
  PrintWriter out;

  //Declare Componenet
  private JLabel heading  = new JLabel("CLient Area");
  private JTextArea messageArea = new JTextArea();
  private JTextField messageInput = new JTextField();
  private Font font = new Font("Roboto", Font.PLAIN ,20);

  // Volatile boolean flag for stopping both threads
  private volatile boolean isRunning = true;

  //Constructor
  public Client() {
    try {
      System.out.print("Sending Request to Server : ");
      socket = new Socket("127.0.0.1", 7777);
      System.out.println("Connection Done");

      br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(socket.getOutputStream());

      createGUI();
      handleEvents();

      startReading();
      //startWriting();  //already implemented by using GUI
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void handleEvents(){

    messageInput.addKeyListener(new KeyListener() {

      @Override
      public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
      }

      @Override
      public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
      }

      @Override
      public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        if (e.getKeyCode() == 10) {
          //System.out.println("you have entered enter button");
          String messageToSende = messageInput.getText();
          messageArea.append("Me: " + messageToSende + "\n");
          out.println(messageToSende);
          out.flush();
          messageInput.setText("");
          
        }
      }
      
    });
  }

  private void createGUI(){
    //GUI code...
    this.setTitle("Client Messanger [END]");
    this.setSize(600,700);
    this.setLocationRelativeTo(null);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //coding for component
    heading.setFont(font);
    messageArea.setFont(font);
    messageInput.setFont(font);

    heading.setIcon(new ImageIcon("Images/clogo.png"));
    heading.setHorizontalTextPosition(SwingConstants.CENTER);
    heading.setVerticalTextPosition(SwingConstants.BOTTOM);
    heading.setHorizontalAlignment(SwingConstants.CENTER);
    heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    messageArea.setEditable(false);
    messageInput.setHorizontalAlignment(SwingConstants.CENTER);

    //frame Layout
    this.setLayout( new BorderLayout());

    //adding the components to frame
    this.add(heading, BorderLayout.NORTH);
    JScrollPane jScrollPane = new JScrollPane(messageArea);
    this.add(jScrollPane, BorderLayout.CENTER);
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
            System.out.println("Server terminated the chat");
            JOptionPane.showMessageDialog(this, "Server Terminated the chat");
            messageInput.setEnabled(false);
            isRunning = false; // Stop both threads
            socket.close(); // Close the socket
            break;
          }
          messageArea.append("Server: " + msg + "\n");
        }
      } catch (Exception e) {
        System.out.println("Connection closed or error in reading.");
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
        while (isRunning) {
          BufferedReader br1 = new BufferedReader(
            new InputStreamReader(System.in)
          );
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
    System.out.println("Client is running...");
    new Client();
  }
}
