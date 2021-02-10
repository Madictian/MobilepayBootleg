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




        String sql = "INSERT INTO Costumers (id, name, credit_card_number, date_created, password, countryCode, balance) VALUES (?,?,?,?,?,?,?)";

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
                        String sql = "UPDATE Costumers SET balance = ?" +
                                " WHERE id = ?";

                        System.out.println(sql);

                try (Connection conn = this.connect();
                        PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setDouble(1, getUserBalance(userId) - transferAmount);
                        stmt.setInt(2,userId);
                        stmt.executeUpdate();

                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                        sql =   "UPDATE Costumers SET balance = ?" +
                                " WHERE id = ?";

                        System.out.println(sql);

                try (Connection conn = this.connect();
                        PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setDouble(1, getUserBalance(recipientId) + transferAmount);
                        stmt.setInt(2, recipientId);
                        stmt.executeUpdate();

                } catch (SQLException e) {
                        System.out.println(e.getMessage());
                }
            }
        } else if (scanner.nextLine().equals("2")){
        } else {


        }
    }

    public static void main(String[] args) {

        MobilePay app = new MobilePay();
        app.transaction();
    }
}


