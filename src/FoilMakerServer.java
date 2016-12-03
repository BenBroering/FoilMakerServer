import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Ben on 11/29/2016.
 */
public class FoilMakerServer {

    private static ArrayList<String> loggedInUsers = new ArrayList<>();

    public static void main(String[] args) throws IOException{

        /**
         * This waits for a connection and then creates a new ClientHandler if a connection is found.
         *
         * We may keep a list of the connections later.
         */
        System.out.println("Looking for connections...");
        ServerSocket listener = new ServerSocket(9999);
        while(true){
            Socket socket = listener.accept();
            System.out.println(socket.getInetAddress().toString() + " connected!");
            ClientHandler newUser = new ClientHandler(socket);
            newUser.run();
        }
    }
    
    
    
    public static void userLogin(String userInfo){
        loggedInUsers.add(userInfo);
    }
    public static void userLogout(String userInfo){
        for(String user : loggedInUsers){
            if(user.contains(userInfo)){
                loggedInUsers.remove(userInfo);
                return;
            }

        }

    }
}
