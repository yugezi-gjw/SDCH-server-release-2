package com.varian.oiscn.util.hipaa;

import lombok.Getter;
import lombok.Setter;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

@Singleton
public class HipaaLogger {

    private HipaaLoggerConfiguration configuration;
    private Object sync = new Object();
    Socket socket;
    private OutputStreamWriter socketWriter;

    public HipaaLogger(HipaaLoggerConfiguration configuration){
        this.configuration = configuration;
    }

    @Getter
    @Setter
    public static HipaaLogger instance;

    private void ensureConnection() throws IOException {
        if(socket == null) {
            createSocket();
        } else if(socket.isClosed() || !socket.isConnected()) {
            socket.close();
            createSocket();
        }
    }

    private void createSocket() throws IOException {
        socket = new Socket(configuration.getHostname(), configuration.getPort());
        socket.setKeepAlive(true);
        socket.setTcpNoDelay(true);
        socket.setSoLinger(true, 1);
        socket.setSoTimeout(configuration.getTimeoutInMs());
        socketWriter = new OutputStreamWriter(socket.getOutputStream());
    }

    public void log(HipaaLogMessage message) throws HipaaException {
        synchronized (sync) {
            try {
                writeMessage(message);
            } catch (IOException e) {
                try{
                    if(socket != null) socket.close();
                } catch (Exception ei){
                }
                try {
                    writeMessage(message);
                } catch (IOException eThrow) {
                    throw new HipaaException(message, eThrow);
                }
            }
        }
    }

    private void writeMessage(HipaaLogMessage message) throws IOException {
        ensureConnection();
        socketWriter.write(message.toString());
        socketWriter.flush();
    }
}
