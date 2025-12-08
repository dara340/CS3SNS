/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
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

/* now using SSL */

/* Level 2 – ChatClient using SSL/TLS */

import java.io.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class ChatClient {
    public static void main(String[] args) {
       
        String host = (args.length > 0) ? args[0] : "localhost";
        int    port = (args.length > 1) ? Integer.parseInt(args[1]) : 43221;

        // Truststore that holds caroot.cer
        System.setProperty("javax.net.ssl.trustStore", "ClientTrustStoreServer.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "clientpass");

        try {
            SSLSocketFactory sf =
                    (SSLSocketFactory) SSLSocketFactory.getDefault();

            try (SSLSocket socket =
                         (SSLSocket) sf.createSocket(host, port)) {

                socket.startHandshake();

                System.out.println("Connected securely to " + host + ":" + port);

                BufferedReader in  = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), "UTF-8"));
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
                BufferedReader kb  = new BufferedReader(
                        new InputStreamReader(System.in));

                Thread reader = new Thread(() -> {
                    try {
                        String srv;
                        while ((srv = in.readLine()) != null) {
                            System.out.println(srv);
                        }
                        System.out.println("Server closed the connection.");
                        System.exit(0);
                    } catch (IOException e) {
                        System.exit(0);
                    }
                });
                reader.setDaemon(true);
                reader.start();

                String line;
                while ((line = kb.readLine()) != null) {
                    out.println(line);
                    if ("/quit".equalsIgnoreCase(line)) break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
