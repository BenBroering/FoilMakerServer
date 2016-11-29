import java.io.*;
import java.net.Socket;

/**
 * Created by Ben on 11/29/2016.
 */
public class ClientHandler implements  Runnable{

    private Socket socket;

    public ClientHandler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try {
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            BufferedReader in = new BufferedReader(isr);
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            while (true){
                /**
                 * Waits for a message from the client.
                 * If one is found then the message is saved and used later.
                 */

                if (in.ready()){
                    String message = in.readLine();
                    System.out.println("Message \"" + message + "\" recieved!");
                }else{
                    continue;
                }

                /**
                 * Implement case/switch or if statements here.
                 * Errors in the syntax will need to be checked.
                 */

                System.out.println("EYY!");
            }

        }catch (IOException e){
            System.out.println("IO ERROR!");
        }
    }
}
