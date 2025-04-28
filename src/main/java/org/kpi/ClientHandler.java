package org.kpi;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {

            String requestLine = in.readLine();
            if (requestLine == null || !requestLine.startsWith("GET")) {
                return;
            }

            System.out.println("Request: " + requestLine);

            String[] tokens = requestLine.split(" ");
            String filePath = tokens[1];

            if (filePath.equals("/")) {
                filePath = "/index.html";
            }

            InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("static" + filePath);

            if (resourceStream != null) {
                byte[] content = resourceStream.readAllBytes();
                String header = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + getContentType(filePath) + "\r\n" +
                        "Content-Length: " + content.length + "\r\n" +
                        "Connection: close\r\n\r\n";

                out.write(header.getBytes());
                out.write(content);
            } else {
                String notFoundPage = "<h1>404 Not Found</h1>";
                String header = "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: " + notFoundPage.length() + "\r\n" +
                        "Connection: close\r\n\r\n";

                out.write(header.getBytes());
                out.write(notFoundPage.getBytes());
            }


            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getContentType(String filePath) {
        if (filePath.endsWith(".html")) {
            return "text/html";
        } else if (filePath.endsWith(".css")) {
            return "text/css";
        } else if (filePath.endsWith(".js")) {
            return "application/javascript";
        }
        return "application/octet-stream";
    }
}

