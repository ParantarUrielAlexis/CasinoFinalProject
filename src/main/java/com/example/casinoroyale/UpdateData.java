package com.example.casinoroyale;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class UpdateData {
    public static void main(String[] args) {
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = c.prepareStatement(
                     "UPDATE users SET name = ?, email = ? WHERE id = ?")) {
            String newName = "Manuel Gonzalez";
            String newEmail = "manuginobili@gmail.com";
            int idToUpdate = 2;

            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newEmail);
            preparedStatement.setInt(3, idToUpdate);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Data updated successfully!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}