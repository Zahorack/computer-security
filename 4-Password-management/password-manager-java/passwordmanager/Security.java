package passwordmanager;

import org.passay.*;
import org.passay.dictionary.WordListDictionary;
import org.passay.dictionary.WordLists;
import org.passay.dictionary.sort.ArraysSort;

import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class Security {

    public static String hash(String passwordToHash, byte[] salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);

            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }


    public static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    public static boolean check(String hashed_pass, String plain_pass, byte[] salt) {
        String test_pass = hash(plain_pass, salt);

        if(test_pass.length() == hashed_pass.length()) {
            if(test_pass.equals(hashed_pass))
                return  true;
        }
        return false;
    }

    public static boolean validatePasswordStrength(String password) throws IOException {

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

        RuleResult validate = passwordValidator.validate(new PasswordData(password));

        if(validate.isValid()) {
            return true;
        }

        return  false;
    }
}

