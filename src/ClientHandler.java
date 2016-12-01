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
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            while (true){
                /**
                 * Waits for a message from the client.
                 * If one is found then the message is saved and used later.
                 */

                // Wait for a message, then save the massage and it's tokens.
                String message = "";
                if (in.ready()){
                    message = in.readLine();
                    System.out.println("Message \"" + message + "\" recieved!");
                }else{
                    continue;
                }

                String messageType = message.split("--")[0];
                String[] tokens = new String[message.split("--").length-1];
                int i = 0;
                for(String token : message.split("--")){
                    if(!token.equals(messageType)){
                        tokens[i] = token;
                        i++;
                    }

                }
                //

                /**
                 * Read messages and do stuff with them.
                 * Implement functionality in IOUtility class.
                 */
                String returnMessage;
                if(messageType.equals("CREATENEWUSER")){
                    returnMessage = IOUtility.createNewUser(tokens[0],tokens[1]);
                    out.println(returnMessage);
                }

                if(messageType.equals("LOGIN")){
                    returnMessage = IOUtility.login(tokens[0],tokens[1]);
                    out.println(returnMessage);
                }


                //

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
