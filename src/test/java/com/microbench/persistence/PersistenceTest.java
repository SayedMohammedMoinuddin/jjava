package com.microbench.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenceTest {
    private DatabaseManager dbManager;
    private ProgramDao programDao;
    private BenchmarkRunDao runDao;

    @BeforeEach
    public void setup() {
        try {
            // Memory databases in SQLite only live for the duration of the connection.
            // Using a temporary file ensures all DAOs see the same database instance.
            File tempDb = File.createTempFile("testbenchmarks", ".db");
            tempDb.deleteOnExit();
            dbManager = new DatabaseManager("jdbc:sqlite:" + tempDb.getAbsolutePath());
            dbManager.initializeSchema();
            programDao = new ProgramDao(dbManager);
            runDao = new BenchmarkRunDao(dbManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testProgramSaveAndGet() {
        ProgramEntity p1 = programDao.saveOrGet("test1", "int x = 5;", "desc");
        assertNotNull(p1);
        assertEquals("test1", p1.getName());

        ProgramEntity p2 = programDao.saveOrGet("test1", "int x = 5;", "desc");
        assertEquals(p1.getId(), p2.getId()); // Should return the existing one

        ProgramEntity p3 = programDao.saveOrGet("test1", "int x = 10;", "desc");
        assertNotEquals(p1.getId(), p3.getId()); // Should create new due to different source

        List<ProgramEntity> all = programDao.listAll();
        assertEquals(2, all.size());
    }

    @Test
    public void testBenchmarkRunSaveAndList() {
        ProgramEntity p1 = programDao.saveOrGet("test_bench", "int x = 1;", "");

        BenchmarkRunEntity r1 = runDao.save(p1.getId(), 1000, 500, 2048, 1024, 0, 0, "-O3", true, 10);
        assertNotNull(r1);
        assertEquals(1000, r1.getJvmTimeNs());

        List<BenchmarkRunEntity> recent = runDao.listRecent(5);
        assertEquals(1, recent.size());
        assertEquals(p1.getId(), recent.get(0).getProgramId());
    }
}
