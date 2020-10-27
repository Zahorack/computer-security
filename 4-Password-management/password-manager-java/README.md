## password-manager: Implementácia správy používatelských hesiel

## Opis aplikácie
Aplikacia je implementovana v jazyku `Java` s JDK verziou `jdk-15.0.1`. Pomocou aplikacie je mozne vyskusat bezpecnostne
implmentacie ulozenia a spravy uzivatelskych hesiel.

### Secure password hashing with salt
Sprava hesiel vyzaduje bezpecne ukladanie uzivatelskych hesiel. Tieto hesla musia byt zasifrovane tak,
aby ich nebolo mozne spatne odsifrovat. Na to sa vyuziva matematicka operacia  HASH-ovanie. V mojom programe
pouzivam v sucastnosti bezpecny algoritmus SHA-256. Navyse je implementovana pridavna bezpecnostna funkcionalita SALTING.
Vdaka nahodne vugenerovanemu SALT klucu je tazie pre utocnika prelomit heslo.

### Secure login request delay
Jeden zo sposobou utoku, ktoreho je cielom prelomenie hesla je brute-force attack. Vtedy utocnik 
opakovane skusa zadavat pokusne hesla, ak by odpoved utocnikovy bola dostatocne rychla je riziko, ze
utocnik uhadne nase heslo bez toho aby prelomil HASH ochranu. Preto je Login session oneskorena o 0.1 sekundy.
Aby sme pri DOD utoku nezahltili resources, kazda Login session je pustana v novom vlakne.

### Secure password requirements
Aplikacia vyzaduje pri registracii zadat heslo, ktore splna poziadacky na bezpecnostne standarty. Konkretne
heslo musi obsahovat male a velke pismena, cisla, musi mat minimalnu dlzku a nesmie sa heslo nacahdzat v 
slovniku slabych hesiel. Implmentácia v java vyuziva package `passay`.


```
        CharacterCharacteristicsRule characterCharacteristicsRule = new CharacterCharacteristicsRule(
                3,
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.Digit,1),
                new CharacterRule(EnglishCharacterData.Special,1)
        );

        DictionaryRule dictionaryRule = new DictionaryRule(
                new WordListDictionary(WordLists.createFromReader(
                new FileReader[] {new FileReader("banned-passwords.txt")},
                false,
                new ArraysSort()))
        );

        PasswordValidator passwordValidator = new PasswordValidator(
                new LengthRule(8, 24),
                characterCharacteristicsRule,
                new WhitespaceRule(),
                dictionaryRule
        );
```


Poziadavky na heslo su zverejnene v dialogovom okne pri registracii a vybere hesla.
V pripade nesplenia bezpecnostnych poziadaviek vypise sa chybova hlaska: `Heslo je slabe! Neuspesna registracia`.



### Databaza hesiel
Zahashovane hesla, pristupove mena a salty su ulozene v sqlite databaze `shadow.db`. Pristup k databaze je implementovany
cez standartne `java.sql` funkcie a driver je JBDC `sqlite-jdbc-3.32.3.2.jar`
Format je nasledovny:


``
name [TEXT]   |   hash [TEXT]   |   salt [TEXT]
``

### Implementacne detaily
V aplikacii je pouzity Java package `MessageDigest` na HASH funkcie. Na generovanie nahodnych SALT klucov je pouzity
package `SecureRandom`. Mal som problem s ulozenim SALT byetoveho pola do subora spolu so stringami pre meno a zahashovane heslo.
Vyriesil som to tak, ze byte pole SALTu najprv prekonvertujem na pole HEX znakov


```
    byte[] salt = Security.getSalt();
    
    StringBuilder sb = new StringBuilder();
    for (byte b : salt) {
        sb.append(String.format("%02X", b));
    }
```

Takto ulozeny salt mozme znovu nacitat bezpecne ako string a prekonvertovat nazad na pole bytov.

```
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
```

Priklad ulozeneho konta:

```
root:32683dce088fa3324502d15393787c2ba47500b51dbdd6ba2bda6d32e07b2174:26C5FD00C6F37FCB19C2B61D113ACDF6
```


## Uzivatelsky manual
Aplikacia disponuje Grafickym uzivatelskym rozhranim GUI s moznostami registracie a prihlasenia.

## Spustenie aplikacie
V subore `\out\artifacts\password_manager_src` sa nachadzaju 3 subory `banned-passwords.txt`, `password-manager-src.jar`, `shadow.db`.
Na spustenie `.jar` executable aplikacie je nutne mat nainstalovany JRE alebo kompletny JDK.
Otestovane na jdk-15.0.1 - `https://www.oracle.com/java/technologies/javase-jdk15-downloads.html`.


Spustenie cez cmd line:
```
$ java -jar .\out\artifacts\password_manager_src\password-manager-src.jar
```

Alebo jednoducho dvojklikom spustit `password-manager-src.jar` v subore `\out\artifacts\password_manager_src\`.

