package com.mauromidolo.windowsstartup.jetty;

import com.mauromidolo.windowsstartup.rest.HttpRest;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class JettyHttpServer implements WebServer {
    private final Server jettyServer;

    public JettyHttpServer(int httpPort) {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        jettyServer = configureJettyServer(context, httpPort);
        setServerHeader(jettyServer);
    }

    private void setServerHeader(Server jettyServer) {
        for(Connector y : jettyServer.getConnectors()) {
            for(ConnectionFactory x  : y.getConnectionFactories()) {
                if(x instanceof HttpConnectionFactory) {
                    ((HttpConnectionFactory)x).getHttpConfiguration().setSendServerVersion(false);
                }
            }
        }
    }

    private static Server configureJettyServer(ServletContextHandler context, int httpPort) {
        Server jettyServer = new Server(httpPort);
        jettyServer.setHandler(context);
        addedError404Handler(context);
        ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);
        jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", HttpRest.class.getCanonicalName());
        return jettyServer;
    }

    private static void addedError404Handler(ServletContextHandler context) {
        ErrorPageErrorHandler errorHandler = new ErrorPageErrorHandler();
        errorHandler.addErrorPage(404, "/command/status");
        context.setErrorHandler(errorHandler);
    }

    @Override
    public void start() throws Exception {
        jettyServer.start();
    }

    @Override
    public void join() throws InterruptedException {
        jettyServer.join();
    }

    @Override
    public void destroy() {
        try {
            jettyServer.stop();
        } catch (Exception ignored) {}
    }
}
