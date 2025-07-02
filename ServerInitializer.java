// ServerInitializer.java

import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;

public class ServerInitializer {
    public static ServerSocket createServerSocket(ServerConfig config) throws IOException, GeneralSecurityException {
        if (!config.sslEnabled) {
            return new ServerSocket(config.listenPort);
        }

        char[] passphrase = config.sslKeystorePassword.toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(config.sslKeystorePath), passphrase);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, passphrase);

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(kmf.getKeyManagers(), null, null);

        SSLServerSocketFactory ssf = sc.getServerSocketFactory();
        return ssf.createServerSocket(config.listenPort);
    }
}
