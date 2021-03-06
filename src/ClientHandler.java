import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Ben on 11/29/2016.
 */
public class ClientHandler implements Runnable{

    private Socket socket;
    private String userInfo;
    private String username;
    private String password;
    private int score;
    private int timesFooledOthers;
    private int timesFooledByOther;
    private String cookie;
    private String rightAnswer;
    private String playerAnswer; //answer choice sent by player as trick
    private String playerChoice;

    public ClientHandler(Socket socket){
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
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

                if(messageType.equals("STARTNEWGAME")){
                    returnMessage = IOUtility.createNewGame(tokens[0], cookie, this);
                    out.println(returnMessage);
                }

                if(messageType.equals("JOINGAME")){
                    returnMessage = IOUtility.joinGame(tokens[0], tokens[1], this);
                    out.println(returnMessage);
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
                    if(!IOUtility.isValidUserToken(userToken)){
                    	returnMessage += "--" + FoilMakerNetworkProtocol.MSG_DETAIL_T.USERNOTLOGGEDIN;
                    	out.println(returnMessage);
                    }
                    
                    //Check if game token is valid
                    else if(!IOUtility.isValidGameToken(gameToken)){
                    	returnMessage += "--" + FoilMakerNetworkProtocol.MSG_DETAIL_T.INVALIDGAMETOKEN;
                    	out.println(returnMessage);
                    }
                    //Check if user already playing the game.
                    else if(IOUtility.isPlayerAlreadyGaming(this, gameToken)){
                    	returnMessage += "--" + FoilMakerNetworkProtocol.MSG_DETAIL_T.USERNOTGAMELEADER;
                    	out.println(returnMessage);
                    }
                    //tokens are good to go. Start game
                    else{
                    	IOUtility.round(gameToken);
                    }
                }
                
                
                
                
                
                
            }

        }catch (IOException e){
            System.out.println("IO ERROR in ClientHandler run() method.");
        }
    }

    public void sendMessage(String message){
        PrintWriter out = null;
        try{
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println(message);
            out.println(message);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(out != null)
                out.close();
        }

    }


    public void setUserInfo(String userInfo){
        this.userInfo = userInfo;
        this.username = userInfo.split(":")[0];
        this.password = userInfo.split(":")[1];
        this.score = Integer.parseInt(userInfo.split(":")[2]);
        this.timesFooledOthers = Integer.parseInt(userInfo.split(":")[3]);
        this.timesFooledByOther = Integer.parseInt(userInfo.split(":")[4]);
        this.cookie = userInfo.split(":")[5];
    }

    public String getUserInfo() {
        return userInfo;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getScore() {
        return score;
    }

    public int getTimesFooledOthers() {
        return timesFooledOthers;
    }

    public int getTimesFooledByOther() {
        return timesFooledByOther;
    }

    public String getCookie() {
        return cookie;
    }
    
    public void setRightAnswer(String rightAnswer){
    	this.rightAnswer = rightAnswer;
    }
    
    public String getRoundResults(){
    	if(this.rightAnswer.equals(this.playerChoice)){
    		return "hasWon";
    	}
    	else{
    		return this.playerChoice;
    	}
    }
    
}
