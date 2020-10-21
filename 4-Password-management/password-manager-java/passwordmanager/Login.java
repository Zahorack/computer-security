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
                Thread.sleep(1000);
                sessionResult[0] = prihlasovanie(meno, heslo);
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

    protected static MyResult prihlasovanie(String meno, String heslo) throws IOException, Exception{
        /*
        *   Delay je vhodne vytvorit este pred kontolou prihlasovacieho mena.
        */
        MyResult account = Database.find("hesla.txt", meno);
        if (!account.getFirst()){
            return new MyResult(false, "Nespravne meno.");
        }
        else {
            StringTokenizer st = new StringTokenizer(account.getSecond(), ":");
            st.nextToken();      //prvy token je prihlasovacie meno

            String hashed_pass = st.nextToken();
            byte[] salt = st.nextToken().getBytes("ISO-8859-1");

            if (!Security.check(hashed_pass, heslo, salt))
                return new MyResult(false, "Nespravne heslo.");
        }
        return new MyResult(true, "Uspesne prihlasenie.");
    }
}
