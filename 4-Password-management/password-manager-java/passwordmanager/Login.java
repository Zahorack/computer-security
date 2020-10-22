package passwordmanager;

import java.io.IOException;
import java.util.StringTokenizer;

import passwordmanager.Database.MyResult;

public class Login {

    protected static MyResult newSession(String meno, String heslo) throws Exception {
        final MyResult[] sessionResult = {null};
        new Thread(() ->{
            System.out.println("New login session begin");
            try {
                Thread.sleep(100);
                sessionResult[0] = authentication(meno, heslo);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }){{start();}}.join();

        System.out.println("Login session stop");
        return sessionResult[0];
    }

    protected static MyResult authentication(String meno, String heslo) throws IOException, Exception{

        MyResult account = Database.find("shadow", meno);
        if (!account.getFirst()){
            return new MyResult(false, "Nespravne meno.");
        }
        else {
            StringTokenizer st = new StringTokenizer(account.getSecond(), ":");
            st.nextToken();      //prvy token je prihlasovacie meno

            String hashed_pass = st.nextToken();
            byte[] salt = hexStringToByteArray(st.nextToken());

            if (!Security.check(hashed_pass, heslo, salt))
                return new MyResult(false, "Nespravne heslo.");
        }
        return new MyResult(true, "Uspesne prihlasenie.");
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

}
