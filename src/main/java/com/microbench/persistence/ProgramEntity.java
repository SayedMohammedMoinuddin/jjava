package com.microbench.persistence;

import java.io.Serializable;

public class ProgramEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long id;
    private final String name;
    private final String source;
    private final String description;

    public ProgramEntity(long id, String name, String source, String description) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.description = description;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getSource() { return source; }
    public String getDescription() { return description; }
}
