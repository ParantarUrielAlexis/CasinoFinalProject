package com.example.casinoroyale;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertData {
    public static void main(String[] args) {

        try(Connection c = MySQLConnection.getConnection();
            PreparedStatement statement = c.prepareStatement(
                    "INSERT INTO users(name, email) VALUES(?, ?)"
            )){
            String createTableQuery = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(50) NOT NULL," +
                    "password Int(50) NOT NULL)";
            String name = "Carla";
            String email = "carlagwapa@cit.edu";
            statement.setString(1,name);
            statement.setString(2,email);
            int rowsInserted = statement.executeUpdate();
            if(rowsInserted > 0){
                System.out.println("Data Inserted Successfully!");
            }
            statement.execute(createTableQuery);
            System.out.println("Table Created Successfuly");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
