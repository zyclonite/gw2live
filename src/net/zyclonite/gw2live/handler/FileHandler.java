/*
 * gw2live - GuildWars 2 Dynamic Map
 * 
 * Website: http://gw2map.com
 *
 * Copyright 2013   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.gw2live.handler;

import java.io.File;
import java.io.IOException;
import net.zyclonite.gw2live.util.AppConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

/**
 *
 * @author zyclonite
 */
public class FileHandler implements Handler<HttpServerRequest> {

    private static final Log LOG = LogFactory.getLog(FileHandler.class);
    private final String webdir;

    public FileHandler() throws IOException {
        final AppConfig config = AppConfig.getInstance();
        final File directory = new File(config.getString("webservice.staticfiles", "./web"));
        if (directory.exists() && directory.isDirectory()) {
            webdir = directory.getCanonicalPath();
        } else {
            throw new IOException("Static-files directory does not exist or is not a directory");
        }
    }

    @Override
    public void handle(final HttpServerRequest req) {
        LOG.debug("got request: " + req.path());
        final File fs = new File(webdir + req.path());
        try {
            if (fs.exists() && fs.getCanonicalPath().startsWith(webdir)) {
                if (fs.isDirectory()) {
                    if (req.path().endsWith("/")) {
                        req.response().sendFile(fs.getCanonicalPath() + File.separator + "/index.html");
                    } else {
                        req.response().setStatusCode(301);
                        req.response().putHeader("Location", req.path() + "/");
                        req.response().end();
                    }
                } else {
                    req.response().sendFile(fs.getCanonicalPath());
                }
            } else {
                throw new IOException("Static-files access problem");
            }
        } catch (IOException ex) {
            req.response().setStatusCode(404);
            req.response().end();
        }

    }
}
