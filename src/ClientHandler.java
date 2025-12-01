import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final ChatServer server;
    private PrintWriter out;
    private BufferedReader in;
    private String name = "anon";

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server  = server;
    }

    @Override
    public void run() {
        try {
            in  = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

            // Ask for name
            out.println("Enter your name:");
            String n = in.readLine();
            if (n != null && !n.isBlank()) {
                name = n.trim();
            }
            server.broadcast(name + " joined.", this);

            // Main chat loop
            String line;
            while ((line = in.readLine()) != null) {
                if (line.equalsIgnoreCase("/quit")) break;
                server.broadcast("[" + name + "]: " + line, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.remove(this);
            server.broadcast("👋 " + name + " left.", this);
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    public void send(String msg) {
        out.println(msg);
    }
}
