package com.ni3.ag.navigator;

import org.apache.commons.dbcp.BasicDataSource;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.plus.naming.Resource;
import org.mortbay.jetty.webapp.WebAppContext;

import javax.naming.NamingException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Main {
    public static final String JETTY_CONFIG_RESOURCE_NAME = "WEB-INF/classes/jetty.properties";
    public static final String CONFIG_SERVER_PORT = "server.port";
    public static final String CONFIG_CONTEXT_PATH = "context.path";
    public static final String CONFIG_DATA_SOURCE_DRIVER_CLASS = "dataSource.driverClassName";
    public static final String CONFIG_DATA_SOURCE_URL = "dataSource.url";
    public static final String CONFIG_DATA_SOURCE_USER_NAME = "dataSource.userName";
    public static final String CONFIG_DATA_SOURCE_PASSWORD = "dataSource.password";
    public static final String CONFIG_DATA_SOURCE_INITIAL_SIZE = "dataSource.initialSize";
    public static final String CONFIG_DATA_SOURCE_MAX_IDLE = "dataSource.maxIdle";
    public static final String CONFIG_DATA_SOURCE_NAME = "dataSource.name";
    public static final String DEFAULT_SERVER_PORT = "8080";
    public static final String DEFAULT_CONTEXT_PATH = "/Ni3Web";
    public static final String DEFAULT_DATA_SOURCE_NAME = "jdbc/ni3_ag";
    public static final String DEFAULT_DATA_SOURCE_DRIVER = "org.postgresql.Driver";

    protected final Properties config = new Properties();
    protected final Server server = new Server();


    public static void main(String[] args) {
        try {
            final Main main = new Main();
            main.init(args);
            main.run();
            main.destroy();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * JSVC interface methods.
     */

    public void init(@SuppressWarnings("UnusedParameters") String[] arguments) throws IOException, NamingException {
        final InputStream configResourceInputStream =
                getClass().getClassLoader().getResourceAsStream(JETTY_CONFIG_RESOURCE_NAME);
        if (configResourceInputStream == null) {
            throw new RuntimeException("Jetty configuration resource file " + JETTY_CONFIG_RESOURCE_NAME + " cannot " +
                    "be loaded from a classpath");
        }
        config.load(configResourceInputStream);
        System.out.println("loaded configs");
        System.out.println(config);

        initDataSource();
        initConnector();
        initContext();
    }

    @SuppressWarnings("UnusedDeclaration")
    public void start() throws Exception {
        start(false);
    }

    public void stop() throws Exception {
        server.stop();
        server.join();
    }

    @SuppressWarnings("UnusedDeclaration")
    public void destroy() {
        server.destroy();
    }

    /*
     * Jetty management methods.
     */

    private void run() throws NamingException, IOException {
        try {
            start(false);
            //noinspection ResultOfMethodCallIgnored
            System.in.read();
            stop();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
    }

    private void start(boolean join) throws Exception {
        System.out.println("Launching Ni3 web application in embedded Jetty container.");
        server.start();
        System.out.print("started.");
        if (join) {
            server.join();
            System.out.print("joined");
        }
    }

    private void initContext() {
        final Map<String, String> contextInitParams = new HashMap<String, String>();
        contextInitParams.put("org.mortbay.jetty.servlet.Default.dirAllowed", "false");

        final WebAppContext context = new WebAppContext();
        context.setServer(server);
        context.setInitParams(contextInitParams);
        context.setContextPath(config.getProperty(CONFIG_CONTEXT_PATH, DEFAULT_CONTEXT_PATH));

        final ProtectionDomain protectionDomain = Main.class.getProtectionDomain();
        final URL location = protectionDomain.getCodeSource().getLocation();
        context.setWar(location.toExternalForm());

        server.addHandler(context);
    }

    private void initConnector() {
        final SocketConnector connector = new SocketConnector();

        // Set some timeout options to make debugging easier.
        connector.setMaxIdleTime(1000 * 60 * 60);
        connector.setSoLingerTime(-1);
        connector.setPort(Integer.parseInt(config.getProperty(CONFIG_SERVER_PORT, DEFAULT_SERVER_PORT)));
        server.setConnectors(new Connector[]{connector});
    }

    private void initDataSource() throws NamingException {
        final String dataSourceName = config.getProperty(CONFIG_DATA_SOURCE_NAME, DEFAULT_DATA_SOURCE_NAME);
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(config.getProperty(CONFIG_DATA_SOURCE_DRIVER_CLASS, DEFAULT_DATA_SOURCE_DRIVER));
        dataSource.setUrl(config.getProperty(CONFIG_DATA_SOURCE_URL));
        dataSource.setUsername(config.getProperty(CONFIG_DATA_SOURCE_USER_NAME));
        dataSource.setPassword(config.getProperty(CONFIG_DATA_SOURCE_PASSWORD));
        dataSource.setInitialSize(Integer.parseInt(config.getProperty(CONFIG_DATA_SOURCE_INITIAL_SIZE)));
        dataSource.setMaxIdle(Integer.parseInt(config.getProperty(CONFIG_DATA_SOURCE_MAX_IDLE)));

        server.getContainer().addBean(new Resource("", dataSourceName, dataSource));
    }
}