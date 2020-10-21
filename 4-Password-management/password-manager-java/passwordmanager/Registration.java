package passwordmanager;

import java.security.NoSuchAlgorithmException;

import passwordmanager.Database.MyResult;


public class Registration {
    protected static MyResult registracia(String meno, String heslo) throws NoSuchAlgorithmException, Exception{
        if (Database.exist("hesla.txt", meno)){
            System.out.println("Meno je uz zabrate.");
            return new MyResult(false, "Meno je uz zabrate.");
        }
        else {

            byte[] salt = Security.getSalt();
            String decoded_salt = new String(salt, "ISO-8859-1");

            Database.add("hesla.txt", meno + ":" + Security.hash(heslo, salt) + ':'+ decoded_salt);
        }
        return new MyResult(true, "");
    }
    
}
