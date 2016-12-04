import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Ben on 11/29/2016.
 *
 * This class handles the messages that the Server recieves.
 * A message it returned that will be sent to the client in the ClientHandler class.
 */
public class IOUtility {
	/*private static File userFile = new File("C:\\Users\\Noah\\Desktop\\FoilMaker\\UserDatabase");
    private static File wordFile = new File("C:\\Users\\Noah\\Desktop\\FoilMaker\\WordleDeck");
    private static File userKeyFile = new File("C:\\Users\\Noah\\Desktop\\FoilMaker\\UserTokenData");
    private static File gameKeyFile = new File("C:\\Users\\Noah\\Desktop\\FoilMaker\\GameTokenData");*/
	private static File userFile = new File("C:\\Users\\Ben\\Desktop\\foilmaker_server\\UserDatabase");
    private static File wordFile = new File("C:\\Users\\Ben\\Desktop\\foilmaker_server\\WordleDeck");
    private static File userKeyFile = new File("C:\\Users\\Ben\\Desktop\\foilmaker_server\\UserKeyData");
    private static File gameKeyFile = new File("C:\\Users\\Ben\\Desktop\\foilmaker_server\\GameTokenData");

    private static BufferedReader in = null;
    private static BufferedWriter out = null;

    public static ArrayList<String> getWords() throws IOException{
        try{
            in = new BufferedReader(new FileReader(wordFile));

            ArrayList<String> words = new ArrayList<>();
            String word;
            while ((word = in.readLine()) != null){
                words.add(word);
            }
            return words;

        }finally {
            if(in != null)
                in.close();
            if(out != null)
                out.close();
        }
    }

    public static ArrayList<String> getUsers() throws IOException{
        try{
            in = new BufferedReader(new FileReader(userFile));

            ArrayList<String> users = new ArrayList<>();
            String user;
            while ((user = in.readLine()) != null){
                users.add(user);
            }
            return users;

        }finally {
            if(in != null)
                in.close();
            if(out != null)
                out.close();
        }
    }

    public static boolean isValidUsername(String username){
        if(username == null || username.length() < 10)
            return false;

        for(char letter : username.toCharArray()){
            if((letter >= 48 && letter <= 57) || (letter >= 65 && letter <= 90) || (letter >= 97 && letter <= 122) || (letter == 95)){
                continue;
            }else
                return false;
        }

        return true;
    }

    public static boolean isValidPassword(String password){
        if(password == null || password.length() < 10)
            return false;

        boolean hasCaps = false;
        boolean hasNumber = false;

        for(char letter : password.toCharArray()){
            if((letter >= 48 && letter <= 57) || (letter >= 65 && letter <= 90) || (letter >= 97 && letter <= 122) || (letter == 95) || (letter == 35) || (letter == 36) || (letter == 38)|| (letter == 42)){
                // Do Nothing
            }else
                return false;

            if(Character.isUpperCase(letter))
                hasCaps = true;

            if(Character.isDigit(letter))
                hasNumber = true;
        }

        if(hasCaps && hasNumber)
            return true;

        return false;
    }
    
    public static boolean isValidUserToken(String userToken) throws IOException{
    	if(userToken==null||userToken.length()!=10){
    		return false;
    	}
    	try{
    		in = new BufferedReader(new FileReader(userKeyFile));

            String user;
            while ((user = in.readLine()) != null){
                String key = user.split(":")[1];
                if(userToken.equals(key)){
                	return true;
                }
            }
            return false;
    	} catch(IOException e){
    		throw e;	
    	}finally {
            if(in != null)
                in.close();
            if(out != null)
                out.close();
        }
    }
    
    public static boolean isValidGameToken(String gameToken) throws IOException{
    	if(gameToken==null||gameToken.length()!=3){
    		return false;
    	}
    	try{
    		in = new BufferedReader(new FileReader(gameKeyFile));

            String user;
            while ((user = in.readLine()) != null){
                String key = user.split(":")[0];
                if(gameToken.equals(key)){
                	return true;
                }
            }
            return false;
    	} catch(IOException e){
    		throw e;	
    	}finally {
            if(in != null)
                in.close();
            if(out != null)
                out.close();
        }
    }
    
    public static String generateCookie(){
        String cookie = "";
        Random r = new Random();
        for(int x = 0; x < 10; x++){
            char cookieChar = (char)(r.nextInt(25) + 65);
            int caps = r.nextInt(2);
            if(caps == 0)
                cookieChar = Character.toLowerCase(cookieChar);

            cookie += cookieChar;
        }
        return cookie;

    }

    public static String createNewUser(String username, String password) throws IOException{
        if(username == null || password == null){
            return "RESPONSE--CREATENEWUSER--INVALIDMESSAGEFORMAT";
        }

        try{
            ArrayList<String> users = getUsers();
            for(String user : users){
                if(user.split(":")[0].equals(username)){
                    return "RESPONSE--CREATENEWUSER--USERALREADYEXISTS";
                }
            }

            if(!isValidUsername(username))
                return "RESPONSE--CREATENEWUSER--INVALIDUSERNAME";
            if(!isValidPassword(password))
                return "RESPONSE--CREATENEWUSER--INVALIDUSERPASSWORD";


            out = new BufferedWriter(new FileWriter(userFile, true));
            out.newLine();
            String infoToAdd = (username+ ":" + password + ":" + 0 + ":" + 0 + ":" + 0);
            out.append(infoToAdd);
            out.flush();
            return "RESPONSE--CREATENEWUSER--SUCCESS";
        } finally {
            if(out != null)
                out.close();
            if(in != null)
                in.close();
        }
    }
    
    public static String login(String username, String password, ClientHandler clientHandler) throws IOException{
        if(username == null || password == null){
            return "RESPONSE--CREATENEWUSER--INVALIDMESSAGEFORMAT";
        }

        try{
            ArrayList<String> users = getUsers();
            boolean userFound = false;
            String loginAttempt = "xxx:xxx";
            for(String user : users){
                if(user.split(":")[0].equals(username)){
                    userFound = true;
                    loginAttempt = user;
                }
            }

            for(String loggedInUser : FoilMakerServer.getLoggedInUsers()){
                if(loggedInUser.contains(loginAttempt))
                    return "RESPONSE--LOGIN--USERALREADYLOGGEDIN";
            }

            if(!userFound)
                return "RESPONSE--LOGIN--UNKNOWNUSER";

            //Add file reader for userKey file.
            if(password.equals(loginAttempt.split(":")[1])){
                String cookie = generateCookie();
                String userInfo = loginAttempt + ":" + cookie;
                clientHandler.setUserInfo(userInfo);
                out = new BufferedWriter(new FileWriter(userKeyFile, true));
                out.append("\n" + username + ":" + cookie);
                out.flush();
                FoilMakerServer.userLogin(userInfo);
                return "RESPONSE--LOGIN--SUCCESS--" + cookie;
            }else {
                return "RESPONSE--LOGIN--INVALIDUSERPASSWORD";
            }

        } finally {
            if(out != null)
                out.close();
            if(in != null)
                in.close();
        }
    }

    // Creates a new game.
    public static String createNewGame(String userToken, String cookie, ClientHandler clientHandler) throws IOException {
        if(userToken == null || userToken.equals(""))
            return "RESPONSE--STARTNEWGAME--FAILURE";
        if(cookie == null || cookie.equals(""))
            return "RESPONSE--STARTNEWGAME--USERNOTLOGGEDIN";

        if(userToken.equals(cookie)){
            String gameToken = generateCookie().substring(0,3).toLowerCase();
            FoilMakerServer.getActiveGames().put(gameToken, new ArrayList<>());
            FoilMakerServer.getActiveGames().get(gameToken).add(clientHandler);
            return "RESPONSE--STARTNEWGAME--SUCCESS--" + gameToken;
        }else{
            return "RESPONSE--STARTNEWGAME--USERNOTLOGGEDIN";
        }
    }

    public static String joinGame(String userToken, String gameToken, ClientHandler clientHandler) throws IOException {
        if(userToken == null || userToken.equals(""))
            return "RESPONSE--STARTNEWGAME--USERNOTLOGGEDIN";
        if(gameToken == null || gameToken.equals(""))
            return "RESPONSE--STARTNEWGAME--GAMEKEYNORFOUND";

        if(userToken.equals(clientHandler.getCookie()) && FoilMakerServer.getActiveGames().get(gameToken) != null){
            FoilMakerServer.getActiveGames().get(gameToken).add(clientHandler);
            ClientHandler host = FoilMakerServer.getActiveGames().get(gameToken).get(0);
            host.sendMessage("NEWPARTICIPANT--" + clientHandler.getUsername() + "--" + host.getScore());
            return "RESPONSE--STARTNEWGAME--SUCCESS--" + gameToken;
        }else if(!userToken.equals(clientHandler.getCookie())){
            return "RESPONSE--STARTNEWGAME--USERNOTLOGGEDIN";
        }else{
            return "RESPONSE--STARTNEWGAME--GAMEKEYNORFOUND";
        }
    }

}
