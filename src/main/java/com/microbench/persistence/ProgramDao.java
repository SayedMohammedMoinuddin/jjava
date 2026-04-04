package com.microbench.persistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgramDao {

    private final DatabaseManager dbManager;

    public ProgramDao(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public ProgramEntity saveOrGet(String name, String source, String description) {
        // Simple heuristic: if same name and source exists, return it.
        // Otherwise, insert new. We'll check by name.
        String checkSql = "SELECT id, name, source, description FROM programs WHERE name = ? ORDER BY created_at DESC LIMIT 1";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, name);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                long id = rs.getLong("id");
                String existingSource = rs.getString("source");

                // If it matches exactly, just return it.
                if (existingSource.equals(source)) {
                     return new ProgramEntity(id, name, source, rs.getString("description"));
                }
                // If it doesn't match, we will insert a new row to preserve history/versions.
                // Could append a version or just let the timestamp distinguish.
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Insert new
        String insertSql = "INSERT INTO programs (name, source, description) VALUES (?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

            insertStmt.setString(1, name);
            insertStmt.setString(2, source);
            insertStmt.setString(3, description);

            insertStmt.executeUpdate();

            ResultSet keys = insertStmt.getGeneratedKeys();
            if (keys.next()) {
                long newId = keys.getLong(1);
                return new ProgramEntity(newId, name, source, description);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ProgramEntity> listAll() {
        List<ProgramEntity> list = new ArrayList<>();
        String sql = "SELECT id, name, source, description FROM programs ORDER BY created_at DESC";

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new ProgramEntity(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("source"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
