import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Ben on 11/29/2016.
 */
public class ClientHandler implements  Runnable{

    private Socket socket;
    private String userInfo;
    private String username;
    private String password;
    private String cookie;

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
                String message;
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
                    returnMessage = IOUtility.login(tokens[0],tokens[1],this);
                    out.println(returnMessage);
                }

                if(messageType.equals("LOGOUT")){
                    FoilMakerServer.userLogout(userInfo);
                    
                    //out.println(returnMessage);
                }


                //

                /**
                 * Implement case/switch or if statements here.
                 * Errors in the syntax will need to be checked.
                 */
                
                //Recieve from leader that group is complete
                if(messageType.equals("ALLPARTICIPANTSHAVEJOINED")){
                	String userToken = tokens[0];
                    String gameToken = tokens[1];
                    
                    returnMessage = FoilMakerNetworkProtocol.MSG_TYPE.RESPONSE + "--" + FoilMakerNetworkProtocol.MSG_TYPE.ALLPARTICIPANTSHAVEJOINED;
                    //Check if user token is valid
                    if(IOUtility.isValidUserToken(userToken)){
                    	returnMessage += "--" + FoilMakerNetworkProtocol.MSG_DETAIL_T.USERNOTLOGGEDIN;
                    	out.println(returnMessage);
                    }
                    
                    //Check if game token is valid
                    else if(IOUtility.isValidGameToken(gameToken)){
                    	returnMessage += "--" + FoilMakerNetworkProtocol.MSG_DETAIL_T.INVALIDGAMETOKEN;
                    	out.println(returnMessage);
                    }
                    //Check if user already playing the game.
                    else if(false){
                    	returnMessage += "--" + FoilMakerNetworkProtocol.MSG_DETAIL_T.USERNOTGAMELEADER;
                    	out.println(returnMessage);
                    }
                    //tokens are good to go. Start game
                    else{
                    	//Method to start game.
                    }
                }
                
                //Game Round
                ArrayList<String> words = IOUtility.getWords();
                for(String word: words){
                	String[] qa = word.split(" : ");
                	returnMessage = FoilMakerNetworkProtocol.MSG_TYPE.NEWGAMEWORD + "--" + qa[0] + "--" + qa[1];
                	int totalPlayers = FoilMakerServer.getNumUsers(), allSuggests = 0;
                	do{
                		
                	}while(allSuggests<totalPlayers);
                }
                
                
                
                
                
                System.out.println("EYY!");
            }

        }catch (IOException e){
            System.out.println("IO ERROR!");
        }
    }

    public void setUserInfo(String userInfo){
        this.userInfo = userInfo;
        this.username = userInfo.split(":")[0];
        this.password = userInfo.split(":")[1];
        this.cookie = userInfo.split(":")[2];
    }
}
