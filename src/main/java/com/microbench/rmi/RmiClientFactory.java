package com.microbench.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiClientFactory {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 1099;
    private static final String SERVICE_NAME = "CompilerService";

    public static RemoteCompilerService getService() throws Exception {
        Registry registry = LocateRegistry.getRegistry(DEFAULT_HOST, DEFAULT_PORT);
        return (RemoteCompilerService) registry.lookup(SERVICE_NAME);
    }
}
