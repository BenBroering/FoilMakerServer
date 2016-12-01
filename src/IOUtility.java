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
	private static File userFile = new File("C:\\Users\\Noah\\Desktop\\FoilMaker\\UserDatabase");
    private static File wordFile = new File("C:\\Users\\Noah\\Desktop\\FoilMaker\\WordleDeck");
	//private static File userFile = new File("C:\\Users\\Ben\\Desktop\\foilmaker_server\\UserDatabase");
    //private static File wordFile = new File("C:\\Users\\Ben\\Desktop\\foilmaker_server\\WordleDeck");
    private static BufferedReader in = null;
    private static BufferedWriter out = null;

    public static ArrayList<String> getWords() throws IOException{
        try{
            in = new BufferedReader(new FileReader(wordFile));
            out = new BufferedWriter(new FileWriter(wordFile));

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

    public static String generateCookie(){
        String cookie = "";
        Random r = new Random();
        for(int x = 0; x < 10; x++){
            char cookieChar = (char)(r.nextInt(25) + 65);
            int caps = r.nextInt(2);
            System.out.println("CAPS VALUE: " + caps);
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


            out = new BufferedWriter(new FileWriter(userFile));
            out.append("\n" + username+ ":" + password);
            out.flush();
            return "RESPONSE--CREATENEWUSER--SUCCESS";


        } finally {
            if(out != null)
                out.close();
        }
    }

    public static String login(String username, String password) throws IOException{
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

            if(!userFound)
                return "RESPONSE--LOGIN--UNKNOWNUSER";

            
            if(password.equals(loginAttempt.split(":")[1])){
                String cookie = generateCookie();
                FoilMakerServer.userLogin(cookie);
                return "RESPONSE--LOGIN--SUCCESS--" + cookie;
            }else {
                return "RESPOMSE--LOGIN--INVALIDUSERPASSWORD";
            }




        } finally {
            if(out != null)
                out.close();
        }
    }
}
