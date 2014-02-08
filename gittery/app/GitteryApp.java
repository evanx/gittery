/*
 * Source https://github.com/evanx by @evanxsummers

 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements. See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership. The ASF licenses this file to
 you under the Apache License, Version 2.0 (the "License").
 You may not use this file except in compliance with the
 License. You may obtain a copy of the License at:

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.  
 */
package gittery.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.HttpServerProperties;
import vellum.httpserver.VellumHttpServer;

/**
 *
 * @author evanx
 */
public class GitteryApp implements HttpHandler {
    Logger logger = LoggerFactory.getLogger(GitteryApp.class);
    VellumHttpServer httpServer = new VellumHttpServer();
    String repo = "https://raw.github.com/evanx/angulardemo/master";
    
    public void start() throws Exception {
        httpServer.start(new HttpServerProperties(8081), this);
    }

    @Override
    public void handle(HttpExchange he) throws IOException {
        String path = he.getRequestURI().getPath();
        if (path.equals("/")) {
            path = "/index.html";
        }
        String repoPath = repo + path;
        logger.info("repoPath {}", repoPath);
        URLConnection connection = new URL(repoPath).openConnection();
        int length = connection.getContentLength();
        byte[] content = new byte[length];
        connection.getInputStream().read(content);
        logger.info("content {}", new String(content));
        he.sendResponseHeaders(200, length);
        String contentType = "text/html";
        if (path.endsWith(".json")) {
            contentType = "text/json";
        } else if (path.endsWith(".js")) {
            contentType = "text/javascript";
        }
        he.getResponseHeaders().set("Content-Type", contentType);
        he.getResponseBody().write(content);
        he.close();
    }
    
    public static void main(String[] args) throws Exception {
        try {
            BasicConfigurator.configure();
            GitteryApp app = new GitteryApp();            
            app.start();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
}
