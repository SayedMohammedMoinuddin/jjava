package com.microbench.persistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BenchmarkRunDao {
    private final DatabaseManager dbManager;

    public BenchmarkRunDao(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public BenchmarkRunEntity save(long programId, long jvmTimeNs, long nativeTimeNs,
                                   long jvmPeakRssKb, long nativePeakRssKb, int jvmExitCode,
                                   int nativeExitCode, String optimizationLevel,
                                   boolean warmupEnabled, int warmupIterations) {

        String sql = "INSERT INTO benchmark_runs (program_id, jvm_time_ns, native_time_ns, " +
                     "jvm_peak_rss_kb, native_peak_rss_kb, jvm_exit_code, native_exit_code, " +
                     "optimization_level, warmup_enabled, warmup_iterations) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, programId);
            stmt.setLong(2, jvmTimeNs);
            stmt.setLong(3, nativeTimeNs);
            stmt.setLong(4, jvmPeakRssKb);
            stmt.setLong(5, nativePeakRssKb);
            stmt.setInt(6, jvmExitCode);
            stmt.setInt(7, nativeExitCode);
            stmt.setString(8, optimizationLevel);
            stmt.setBoolean(9, warmupEnabled);
            stmt.setInt(10, warmupIterations);

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                long id = keys.getLong(1);
                // Can fetch it right back to get the 'run_at' timestamp if needed
                return new BenchmarkRunEntity(id, programId, jvmTimeNs, nativeTimeNs,
                    jvmPeakRssKb, nativePeakRssKb, jvmExitCode, nativeExitCode,
                    optimizationLevel, warmupEnabled, warmupIterations, "Just now");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<BenchmarkRunEntity> listRecent(int limit) {
        List<BenchmarkRunEntity> list = new ArrayList<>();
        String sql = "SELECT id, program_id, run_at, jvm_time_ns, native_time_ns, " +
                     "jvm_peak_rss_kb, native_peak_rss_kb, jvm_exit_code, native_exit_code, " +
                     "optimization_level, warmup_enabled, warmup_iterations " +
                     "FROM benchmark_runs ORDER BY run_at DESC LIMIT ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new BenchmarkRunEntity(
                    rs.getLong("id"),
                    rs.getLong("program_id"),
                    rs.getLong("jvm_time_ns"),
                    rs.getLong("native_time_ns"),
                    rs.getLong("jvm_peak_rss_kb"),
                    rs.getLong("native_peak_rss_kb"),
                    rs.getInt("jvm_exit_code"),
                    rs.getInt("native_exit_code"),
                    rs.getString("optimization_level"),
                    rs.getBoolean("warmup_enabled"),
                    rs.getInt("warmup_iterations"),
                    rs.getString("run_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
