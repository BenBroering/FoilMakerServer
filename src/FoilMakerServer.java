import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ben on 11/29/2016.
 */
public class FoilMakerServer{

    private static ArrayList<String> loggedInUsers = new ArrayList<>();
    private static HashMap<String, ArrayList<ClientHandler>> activeGames = new HashMap<String, ArrayList<ClientHandler>>();
    //Reference in HashMap is game key of each game.
    
    public static void main(String[] args) throws IOException{
        /**
         * This waits for a connection and then creates a new ClientHandler if a connection is found.
         *
         * We may keep a list of the connections later.
         */
        ServerSocket listener = new ServerSocket(9999);
        while(true) {
            System.out.println("Waiting for a connection...");
            Socket socket = listener.accept();
            System.out.println(socket.getInetAddress().toString() + " connected!");
            ClientHandler newUser = new ClientHandler(socket);
            Thread thread = new Thread(newUser);
            thread.start();
            
        }
    }

    public static HashMap<String, ArrayList<ClientHandler>> getActiveGames() {
        return activeGames;
    }

    public static ArrayList<String> getLoggedInUsers(){
        return loggedInUsers;
    }

    public static int getNumUsers(){
    	return loggedInUsers.size();
    }
    
    public static void userLogin(String userInfo){
        loggedInUsers.add(userInfo);
    }
    public static String userLogout(String userInfo){
        for(String user : loggedInUsers){
            if(user.contains(userInfo)){
                loggedInUsers.remove(userInfo);
                return "RESPONSE--LOGOUT--SUCCESS";
            }
        }
        return "RESPONSE--LOGOUT--USERNOTLOGGEDIN";
    }
}
