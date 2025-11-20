/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

 /*FROM ACTIVITY 2 WEEK 2 */

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
    private final int port = 43221;                  // keep or change; see Level 4 notes
    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) throws IOException {
        new ChatServer().start();
    }

    public void start() throws IOException {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Server listening on " + port);
            while (true) {
                Socket s = server.accept();          // one socket per client
                ClientHandler handler = new ClientHandler(s, this);
                clients.add(handler);
                handler.start();
            }
        }
    }

    public void broadcast(String msg, ClientHandler exclude) {
        for (ClientHandler c : clients) {
            if (c != exclude) c.send(msg);
        }
    }

    public void remove(ClientHandler c) {
        clients.remove(c);
    }
}
