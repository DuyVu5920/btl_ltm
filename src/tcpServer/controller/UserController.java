package tcpServer.controller;

import tcpServer.connection.DatabaseConnection;
import tcpServer.model.UserModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author admin
 */
public class UserController {
    //  SQL
    private static final String INSERT_USER = "INSERT INTO users (username, password, score, win, draw, lose, avgCompetitor, avgTime) VALUES (?, ?, 0, 0, 0, 0, 0, 0)";

    private static final String CHECK_USER = "SELECT userId from users WHERE username = ? limit 1";

    private static final String LOGIN_USER = "SELECT username, password, score FROM users WHERE username=? AND password=?";

    private static final String GET_INFO_USER = "SELECT username, score, win, draw, lose, avgCompetitor, avgTime FROM users WHERE username=?";

    private static final String GET_ALL_USERS = "SELECT username, score, win, draw, lose, avgCompetitor, avgTime FROM users";
    
    private static final String UPDATE_USER = "UPDATE users SET score = ?, win = ?, draw = ?, lose = ?, avgCompetitor = ?, avgTime = ? WHERE username=?";
    //  Instance
    private static Connection connection = DatabaseConnection.getInstance().getConnection();

    public static String register(String username, String password) {
        //  Check user exit
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(CHECK_USER,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                return "failed;User Already Exit";
            } else {
                resultSet.close();
                preparedStatement.close();
                preparedStatement = connection.prepareStatement(INSERT_USER);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return "success;";
    }

    public static String login(String username, String password) {
        //  Check user exit
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(LOGIN_USER,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            //  Login User
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.first()) {
                float score = resultSet.getFloat("score");
                return "success;" + username + ";" + score;
            } else {
                return "failed;" + "Please enter the correct account password!";
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public static String getInfoUser(String username) {
        UserModel user = new UserModel();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_INFO_USER);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                user.setUsername(resultSet.getString("username"));
                user.setScore(resultSet.getFloat("score"));
                user.setWin(resultSet.getInt("win"));
                user.setDraw(resultSet.getInt("draw"));
                user.setLose(resultSet.getInt("lose"));
                user.setAvgCompetitor(resultSet.getFloat("avgCompetitor"));
                user.setAvgTime(resultSet.getFloat("avgTime"));
            }
            return "success;" + user.getUsername() + ";" + user.getScore() + ";" + user.getWin() + ";" + user.getDraw() + ";" + user.getLose() + ";" + user.getAvgCompetitor() + ";" + user.getAvgTime() ;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
    
    public static String getAllUsers() {
        ArrayList<UserModel> users = new ArrayList<>();
        String returnMsg = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_USERS);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                UserModel user = new UserModel();
                user.setUsername(resultSet.getString("username"));
                user.setScore(resultSet.getFloat("score"));
                user.setWin(resultSet.getInt("win"));
                user.setDraw(resultSet.getInt("draw"));
                user.setLose(resultSet.getInt("lose"));
                user.setAvgCompetitor(resultSet.getFloat("avgCompetitor"));
                user.setAvgTime(resultSet.getFloat("avgTime"));
                users.add(user);
            }
            returnMsg = "success;";
            StringBuilder strb = new StringBuilder(returnMsg);
            for (UserModel user : users) {
                strb.append(user.getUsername()).append(",")
                        .append(user.getScore()).append(",")
                        .append(user.getWin()).append(",")
                        .append(user.getDraw()).append(",")
                        .append(user.getLose()).append(",")
                        .append(user.getAvgCompetitor()).append(",")
                        .append(user.getAvgTime()).append(";");
            }
            return strb.deleteCharAt(strb.length()-1).toString();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return returnMsg;
    }

    public static boolean updateUser(UserModel user) throws SQLException {
        boolean rowUpdated;
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER);
        //  Login User
        preparedStatement.setFloat(1, user.getScore());
        preparedStatement.setInt(2, user.getWin());
        preparedStatement.setInt(3, user.getDraw());
        preparedStatement.setInt(4, user.getLose());
        preparedStatement.setFloat(5, user.getAvgCompetitor());
        preparedStatement.setFloat(6, user.getAvgTime());
        preparedStatement.setString(7, user.getUsername());

//            ResultSet r = preparedStatement.executeQuery();
        rowUpdated = preparedStatement.executeUpdate() > 0;
        return rowUpdated;
    }

    public static UserModel getUser(String username) {
        UserModel user = new UserModel();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_INFO_USER);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                user.setUsername(resultSet.getString("username"));
                user.setScore(resultSet.getFloat("score"));
                user.setWin(resultSet.getInt("win"));
                user.setDraw(resultSet.getInt("draw"));
                user.setLose(resultSet.getInt("lose"));
                user.setAvgCompetitor(resultSet.getFloat("avgCompetitor"));
                user.setAvgTime(resultSet.getFloat("avgTime"));
            }
            return user;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

//    public static void main(String[] args) {
//        System.out.println(getAllUsers());
//    }
}
