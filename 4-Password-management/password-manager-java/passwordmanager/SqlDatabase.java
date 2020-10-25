package passwordmanager;

import java.sql.*;


public class SqlDatabase {

    final static class UserRecord {
        private boolean valid = false;
        private final String name;
        private final String hash;
        private final String salt;

        public UserRecord(String name, String hash, String salt) {
            this.name = name;
            this.hash = hash;
            this.salt = salt;
            this.valid = true;
        }

        public UserRecord() {
            this.name = null;
            this.hash = null;
            this.salt = null;
            this.valid = false;
        }

        public String getName() {
            return name;
        }
        public String getHash() {
            return hash;
        }
        public String getSalt() {
            return salt;
        }
        public boolean isValid() {return valid; }
    }

    private Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:shadow.db";
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException  e) {
            System.out.println("Error " + e.getMessage());
        }

        return conn;
    }

    private void close(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void insertNewUser(String name_value, String hash_value, String salt_value) {
        String sql = "INSERT INTO users(name, hash, salt) VALUES(?,?,?)";

        Connection conn = null;
        try {
            conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, name_value);
            pstmt.setString(2, hash_value);
            pstmt.setString(3, salt_value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            this.close(conn);
        }
    }

    public UserRecord selectByName(String name_value){
        String sql = "SELECT name, hash, salt FROM users WHERE name == ?";

        Connection conn = null;
        try {
            conn = this.connect();
            PreparedStatement pstmt  = conn.prepareStatement(sql);

            pstmt.setString(1, name_value);
            ResultSet rs  = pstmt.executeQuery();

            while (rs.next()) {
                return new UserRecord(rs.getString("name"),rs.getString("hash"), rs.getString("salt"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            this.close(conn);
        }
        return new UserRecord();
    }

//    public static void main(String[] args) {
//        SqlDatabase app = new SqlDatabase();
//
//        UserRecord user = app.selectByName("roots");
//
//        System.out.println(user.isValid());
//        if(user.isValid()) {
//            System.out.println(user.getHash());
//        }
//    }
}


