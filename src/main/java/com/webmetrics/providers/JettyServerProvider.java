package com.webmetrics.providers;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;

public class JettyServerProvider implements Provider<Server> {

    private Server server;

    @Inject
    public JettyServerProvider() {
        server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        context.setContextPath("/");

        context.addFilter(GuiceFilter.class, "/*", 0);
        context.addServlet(DefaultServlet.class, "/");

        // Allows regular files (css, js, etc) to be served
        Resource resource = Resource.newClassPathResource("/");
        context.setBaseResource(resource);

        server.setHandler(context);
    }

    public Server get() {
        return server;
    }
}
