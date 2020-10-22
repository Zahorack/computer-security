package passwordmanager;

import java.security.NoSuchAlgorithmException;

import passwordmanager.Database.MyResult;


public class Registration {
    protected static MyResult registracia(String meno, String heslo) throws NoSuchAlgorithmException, Exception{
        if (Database.exist("shadow", meno)){
            System.out.println("Meno je uz zabrate.");
            return new MyResult(false, "Meno je uz zabrate.");
        }
        else if(!Security.validatePasswordStrength(heslo)) {
            return new MyResult(false, "Heslo je slabe! Neuspesna registracia.");
        }
        else {

            byte[] salt = Security.getSalt();

            StringBuilder sb = new StringBuilder();
            for (byte b : salt) {
                sb.append(String.format("%02X", b));
            }

            Database.add("shadow", meno + ":" + Security.hash(heslo, salt) + ':'+ sb.toString());
        }
        return new MyResult(true, "");
    }
    
}
