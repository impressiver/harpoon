package com.webmetrics;

import com.webmetrics.web.TestResultsService;
import com.webmetrics.util.Log;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.sitebricks.SitebricksModule;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.ServletContextEvent;

public class Main {
    private static final Log LOG = new Log();

    public static void main(String[] args) throws Exception {
        LOG.info("Starting up...");

        final Injector injector = Guice.createInjector(new JettyModule(), new SitebricksModule() {
            @Override
            protected void configureSitebricks() {
                scan(TestResultsService.class.getPackage());
            }
        });

        Server server = injector.getInstance(Server.class);
        GuiceServletContextListener gscl = new GuiceServletContextListener() {
            @Override
            protected Injector getInjector() {
                return injector;
            }
        };
        server.start();

        ServletContextHandler context = (ServletContextHandler) server.getHandler();
        gscl.contextInitialized(new ServletContextEvent(context.getServletContext()));

        server.join();
    }
}
