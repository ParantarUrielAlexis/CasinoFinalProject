package com.example.casinoroyale;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RetriveData {
    public static void main(String[] args) {
        try(Connection c = MySQLConnection.getConnection();
            Statement statement = c.createStatement()){
            String selectQuery = "SELECT * FROM users";
            ResultSet result = statement.executeQuery(selectQuery);
            while (result.next()){
                int id = result.getInt("id");
                String username = result.getString("username");
                String password = result.getString("password");
                System.out.println("ID: " + id + "\nName: " + username + "\nEmail: " + username );
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
