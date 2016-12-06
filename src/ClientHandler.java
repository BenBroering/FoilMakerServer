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
    private String playerAnswer;
    private String playerChoice;
    private ArrayList<String> wordsLeft;
    private int answersGiven;
    private int choicesGiven;
    private int expectedNumAnswers;

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
                    
                    returnMessage = "RESPONSE--ALLPARTICIPANTSHAVEJOINED";
                    //Check if user token is valid

                    if(!IOUtility.isUserLoggedIn(userToken)){
                    	returnMessage += "--USERNOTLOGGEDIN";
                    	out.println(returnMessage);
                    }
                    
                    //Check if game token is valid
                    else if(!FoilMakerServer.getActiveGames().containsKey(gameToken)){
                    	returnMessage += "--INVALIDGAMETOKEN";
                    	out.println(returnMessage);
                    }
                    //Check if user already playing the game.
                    else if(IOUtility.isPlayerAlreadyGaming(this, gameToken)){
                    	returnMessage += "--USERNOTGAMELEADER";
                    	out.println(returnMessage);
                    }
                    //tokens are good to go. Start game
                    else{
                    	IOUtility.sendWord(gameToken);
                    }
                }
                
                if(messageType.equals("PLAYERSUGGESTION")){
                    if(tokens.length != 3){
                        out.println("RESPONSE-­PLAYERSUGGESTION--INVALIDMESSAGEFORMAT");
                    }
                    returnMessage = IOUtility.addSuggestion(tokens[0], tokens[1], tokens[2], this);
                    if(returnMessage != null)
                        out.println(returnMessage);
                }
                
                if(messageType.equals("PLAYERCHOICE")){
                    if(tokens.length != 3){
                        out.println("RESPONSE-­PLAYERCHOICE--INVALIDMESSAGEFORMAT");
                    }
                    returnMessage = IOUtility.addChoice(tokens[0], tokens[1], tokens[2], this);
                    if(returnMessage != null)
                        out.println(returnMessage);
                }
            }

        }catch (IOException e){
            System.out.println("IO ERROR in ClientHandler run() method.");
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

    public int getAnswersGiven() {
        return answersGiven;
    }
    
    public int getChoicesGiven(){
    	return this.choicesGiven;
    }

    public int getExpectedNumAnswers() {
        return expectedNumAnswers;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public String getPlayerAnswer() {
        return playerAnswer;
    }

    public String getPlayerChoice() {
        return playerChoice;
    }

    public void setRightAnswer(String rightAnswer){
    	this.rightAnswer = rightAnswer;
    }

    public void setPlayerAnswer(String playerAnswer) {
        this.playerAnswer = playerAnswer;
    }

    public void setPlayerChoice(String playerChoice) {
        this.playerChoice = playerChoice;
    }

    public void setWordsLeft(ArrayList<String> wordsLeft) {
        this.wordsLeft = wordsLeft;
    }

    public void setAnswersGiven(int answersGiven) {
        this.answersGiven = answersGiven;
    }
    
    public void setChoicesGiven(int choiceGiven){
    	this.choicesGiven = choiceGiven;
    }

    public void setExpectedNumAnswers(int expectedNumAnswers) {
        this.expectedNumAnswers = expectedNumAnswers;
    }

    public ArrayList<String> getWordsLeft() {
        return wordsLeft;
    }
    
    public void increaseScore(int amount){
    	this.score += amount;
    }
    
    public void incrementTimesFooledOthers(){
    	this.timesFooledOthers++;
    }
    
    public void incrementTimesFooledByOther(){
    	this.timesFooledByOther++;
    }
}
