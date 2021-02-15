import java.sql.*;
import java.util.Date;
import java.util.Scanner;

public class MobilePay {

    private Object Date;

    private Connection connect(){

                        String url = "jdbc:sqlite:DB/DataBase";
                        Connection conn = null;

        try{ conn = DriverManager.getConnection(url);

        }catch (SQLException e) {

                        System.out.println(e.getMessage());

        }
                        return conn;
    }

    public void getAllInfo(){
        String sqlCostumers = "SELECT * FROM Costumers";
        String sqlTransactions = "SELECT * FROM Transactions";

                    try (Connection conn = this.connect();
                        Statement stmt  = conn.createStatement();
                        ResultSet rs    = stmt.executeQuery(sqlCostumers)){
                        while (rs.next()) {
                            System.out.println( rs.getInt("id") + "\t" +
                                                rs.getString("name") + "\t" +
                                                rs.getString("credit_card_number") + "\t" +
                                                rs.getString("date_created") + "\t" +
                                                rs.getString("password") + "\t" +
                                                rs.getInt("countryCode") + "\t" +
                                                rs.getInt("balance") + "\t");
                        }} catch (SQLException e){

                                    System.out.println(e.getMessage());}

                    try (Connection conn2 = this.connect();
                         Statement stmt2  = conn2.createStatement();
                         ResultSet rs2    = stmt2.executeQuery(sqlTransactions)){
                        while (rs2.next()) {
                            System.out.println( rs2.getInt("transactions_id") + "\t" +
                                                rs2.getInt("sender") + "\t" +
                                                rs2.getInt("recipient") + "\t" +
                                                rs2.getDouble("amount") + "\t" +
                                                rs2.getString("date_of_transaction") + "\t");

                        }}catch (SQLException e2){
                                    System.out.println(e2.getMessage()); }
    }

    public double getUserBalance(int userId){

                        double temp = 0;
                        String sql = "SELECT * FROM Costumers WHERE id ='" + userId +"'";

        try (Connection conn = this.connect();

                        Statement stmt  = conn.createStatement();
                        ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                        System.out.println(rs.getInt("id") +  ". " +
                                rs.getDouble("balance") + "\n");
                                temp = rs.getDouble("balance");
                        System.out.println(temp);

            }
        } catch (SQLException e) {
                        System.out.println(e.getMessage());
        }
                        return temp;
    }

    public void createAccount(){

                        java.util.Date date = new Date();
                        Scanner scanner = new Scanner(System.in);
                        System.out.println("Phone Number:");
                        int userId = Integer.parseInt(scanner.nextLine());

                        System.out.println("Noted!" +
                                "\nWhat is your name:");
                        String userName = scanner.nextLine();

                        System.out.println("Now for some required info" +
                                "\nwhat credit card would you like to use:");
                        Double ccNumber = Double.parseDouble(scanner.nextLine());

                        System.out.println("Now we'd like you to make a password:");
                        String userPW = scanner.nextLine();

                        System.out.println("In what country are you residing?");
                        int countryCode = Integer.parseInt(scanner.nextLine());




        String sql = "INSERT INTO Costumers (id, name, credit_card_number, date_created, password, countryCode, balance)" +
                " VALUES (?,?,?,?,?,?,?)";

        System.out.println(sql);

        try(Connection conn = this.connect();
                        PreparedStatement stmt = conn.prepareStatement(sql)){
                        stmt.setInt(1, userId);
                        stmt.setString(2, userName);
                        stmt.setDouble(3,ccNumber);
                        stmt.setString(4, date.toString());
                        stmt.setString(5,userPW);
                        stmt.setInt(6,countryCode);
                        stmt.setInt(7,0);
                        stmt.executeUpdate();

            }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void transcribeTransaction(double amount, int sender, int recipient){
        Date = new Date();
        String sql = "INSERT INTO Transactions (sender, recipient, amount, date_of_transaction)" +
                " VALUES (?,?,?,?)";

        try(Connection conn = this.connect();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, sender);
            stmt.setInt(2,recipient);
            stmt.setDouble(3, amount);
            stmt.setString(4,Date.toString());
            stmt.executeUpdate();

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void transaction(){
        //First a User is determined though self identification.


                        Scanner scanner = new Scanner(System.in);
                        System.out.println("type Phone number");
                        int userId = Integer.parseInt(scanner.nextLine());

                        System.out.println("password:");
                        String pW = scanner.nextLine();



                        System.out.println("would like to:" +
                                "\ntransfer balance: press 1" +
                                "\nrequest balance: press 2");

        //Transfer from your personal balance
        if (scanner.nextLine().equals("1")){
                        System.out.println("Whom do you want to transfer to?" +
                                            "(Phone number):");
                        int recipientId = Integer.parseInt(scanner.nextLine());
                        System.out.println("how much would you like to transfer:");
                        double transferAmount = Double.parseDouble(scanner.nextLine());

                        System.out.println(
                                "trasfer:" + transferAmount +
                                "\nto:" + recipientId +
                                "\nare you sure?");




            if (scanner.nextLine().equals("yes")) {
                        String sqlSender = "UPDATE Costumers SET balance = ?" +
                                " WHERE id = ?";

                        String sqlRecipient =   "UPDATE Costumers SET balance = ?" +
                                " WHERE id = ?";

                        System.out.println(sqlSender);
                        System.out.println(sqlRecipient);

                        Connection conn = null;
                        PreparedStatement stmt = null, stmt2 = null;
                try {
                        conn = this.connect();
                        if(conn == null) {
                            return;
                        }

                        conn.setAutoCommit(false);

                        stmt = conn.prepareStatement(sqlSender);
                        stmt.setDouble(1, getUserBalance(userId) - transferAmount);
                        stmt.setInt(2,userId);

                        int rowAffected = stmt.executeUpdate();

                        if (rowAffected != 1) {
                            conn.rollback();
                        }

                        stmt2 = conn.prepareStatement(sqlRecipient);
                        stmt2.setDouble(1, getUserBalance(recipientId) + transferAmount);
                        stmt2.setInt(2, recipientId);
                        stmt2.executeUpdate();
                        conn.commit();



                    } catch (SQLException e1) {
                        try{
                            if (conn != null){
                                conn.rollback();
                            }
                        } catch ( SQLException e2){

                            System.out.println(e2.getMessage());
                        }
                        System.out.println(e1.getMessage());
                    }finally {
                        try {
                        if (stmt != null){
                            stmt.close();
                        }
                        if (stmt2 != null){
                            stmt.close();
                        }
                        if (conn != null){
                            conn.close();
                            transcribeTransaction(transferAmount,userId,recipientId);
                        }
                    } catch (SQLException e3) {
                    System.out.println(e3.getMessage());
                }
            }
            }
        } else if (scanner.nextLine().equals("2")){
        } else {


        }
    }

    public static void main(String[] args) {

        MobilePay app = new MobilePay();
        app.getAllInfo();
    }
}


