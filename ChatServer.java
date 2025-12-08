import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import java.sql.*;                          
import java.security.MessageDigest;        
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class ChatServer {
    private final int port = 43221;
    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();

    private Connection db;

    public static void main(String[] args) throws Exception {
        System.setProperty("javax.net.ssl.keyStore", "ServerKeyStore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "serverpass");

        ChatServer server = new ChatServer();
        server.start();
    }

    public ChatServer() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        //Login for restricted db user
        db = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/chatdb",
                "chatapp",
                "chatpass"
        );

        System.out.println("Connected to MySQL database");
    }

    // Called by ClientHandler to validate login
    public boolean authenticateUser(String username, String password) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (PreparedStatement ps = db.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;

                String storedHash = rs.getString(1);
                String suppliedHash = hashPassword(password);

                return storedHash.equals(suppliedHash);
            }
        } catch (SQLException e) {
            System.err.println("Authentication DB error: " + e.getMessage());
        }
        return false;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return password; 
        }
    }

    public void start() throws IOException {
        SSLServerSocketFactory factory =
                (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        try (SSLServerSocket server =
                     (SSLServerSocket) factory.createServerSocket(port)) {

            System.out.println("Secure ChatServer listening on port " + port);

            while (true) {
                Socket s = server.accept();
                System.out.println("Client connected: " + s);

                ClientHandler handler = new ClientHandler(s, this);
                clients.add(handler);
                handler.start();
            }
        }
    }

    public void broadcast(String msg, ClientHandler exclude) {
        for (ClientHandler c : clients) {
            if (c != exclude) {
                c.send(msg);
            }
        }
    }

    public void remove(ClientHandler c) {
        clients.remove(c);
    }
}
