package passwordmanager;

public class Database {
    final static class MyResult {
        private final boolean first;
        private final String second;
        
        public MyResult(boolean first, String second) {
            this.first = first;
            this.second = second;
        }
        public boolean getFirst() {
            return first;
        }
        public String getSecond() {
            return second;
        }
    }
    
    protected static MyResult add(String name_value, String hash_value, String salt_value){
        SqlDatabase app = new SqlDatabase();
        SqlDatabase.UserRecord user = app.selectByName(name_value);

        if(user.isValid())
            return new MyResult(false, "Meno uz existuje");

        app.insertNewUser(name_value, hash_value, salt_value);
        return new MyResult(true, "");
    }

    protected static SqlDatabase.UserRecord find(String name_value) {
        SqlDatabase app = new SqlDatabase();
        SqlDatabase.UserRecord user = app.selectByName(name_value);

        return user;
    }
    
    protected static boolean exist(String name_value) {
        SqlDatabase app = new SqlDatabase();
        SqlDatabase.UserRecord user = app.selectByName(name_value);

        if(user.isValid())
            return new MyResult(true, "Meno uz existuje").getFirst();

        return new MyResult(false, "").getFirst();

    }
    
}
