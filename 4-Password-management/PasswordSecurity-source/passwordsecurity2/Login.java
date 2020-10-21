//////////////////////////////////////////////////////////////////////////
// TODO:                                                                //
// Uloha2: Upravte funkciu na prihlasovanie tak, aby porovnavala        //
//         heslo ulozene v databaze s heslom od uzivatela po            //
//         potrebnych upravach.                                         //
// Uloha3: Vlozte do prihlasovania nejaku formu oneskorenia.            //
//////////////////////////////////////////////////////////////////////////
package passwordsecurity2;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;
import passwordsecurity2.Database.MyResult;

public class Login {
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
