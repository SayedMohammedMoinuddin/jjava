package com.microbench.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RemoteCompilerClient {

    private final String host;
    private final int port;

    public RemoteCompilerClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public RemoteCompilerService getService() {
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            return (RemoteCompilerService) registry.lookup("CompilerService");
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to RMI Server at " + host + ":" + port, e);
        }
    }
}
