package com.webmetrics.harpoon.providers;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.servlet.GuiceFilter;
import com.webmetrics.harpoon.util.Log;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class JettyServerProvider implements Provider<Server> {
    private static final Log LOG = new Log();

    private Server server;

    @Inject
    public JettyServerProvider() throws IOException {
        server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        context.setContextPath("/");

        context.addFilter(GuiceFilter.class, "/*", 0);
        context.addServlet(DefaultServlet.class, "/");

        // Allows regular files (css, js, etc) to be served
        Resource resource = null;
        File file = new File("src/main/webapp");
        if (file.exists()) {
            LOG.info("Developer-mode: using relative directory of src/main/webapp");
            resource = Resource.newResource(file.toURI());
        } else {
            String classPath = getClass().getName();
            classPath = classPath.replaceAll("\\.", "/") + ".class";
            URL url = JettyServerProvider.class.getResource("/" + classPath);

            if (url == null) {
                throw new IllegalStateException("Could not find my own class in the class path");
            }

            String s = url.toString();

            if (!s.startsWith("jar:file:")) {
              throw new IllegalStateException("Class is not stored in a file, this is unexpected and can't continue");
            }

            s = s.substring("jar:file:".length());
            s = s.substring(0, s.indexOf('!'));

            File path = new File(s);
            File webappDir = new File(path.getParentFile().getParentFile(), "webapp");

            if (!webappDir.exists()) {
                throw new IllegalStateException("Webapp directory " + webappDir.getPath() + " not found");
            }

            LOG.info("Using webapp directory " + webappDir.getPath());
            resource = Resource.newResource(webappDir.toURI());
        }

        context.setBaseResource(resource);

        server.setHandler(context);
    }

    public Server get() {
        return server;
    }
}
