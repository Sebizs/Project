// TcpForwarder.java

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.X509Certificate;

public class TcpForwarder implements Forwarder {
    private final Socket socket;
    private final BufferedWriter writer;

    public TcpForwarder(ServerConfig.ForwardingConfig cfg) throws Exception {
        if (cfg.useTls) {
            SSLSocketFactory factory = createInsecureTlsFactory();
            this.socket = factory.createSocket(cfg.host, cfg.port);
        } else {
            this.socket = new Socket(cfg.host, cfg.port);
        }
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    private SSLSocketFactory createInsecureTlsFactory() throws Exception {
        SSLContext ctx = SSLContext.getInstance("TLS");
        TrustManager[] trustAll = new TrustManager[] {
            new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] x, String y) {}
                public void checkServerTrusted(X509Certificate[] x, String y) {}
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
            }
        };
        ctx.init(null, trustAll, new SecureRandom());
        return ctx.getSocketFactory();
    }

    @Override
    public void forward(String line) {
        try {
            writer.write(line);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Forwarding hiba: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            writer.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Forwarder zárási hiba: " + e.getMessage());
        }
    }
}
