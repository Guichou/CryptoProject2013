package Exo1_1;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;


public class HttpsHelloServer {
    ////////////////////////////////////////////////////////
    // Initialisation des propriÃ©tÃ©s systÃ¨mes nÃ©cessaires 
    // Ã  l'Ã©tablissement d'un contexte SSL
    static {
        System.setProperty("javax.net.ssl.keyStore", "kssrv.ks");
        System.setProperty("javax.net.ssl.keyStorePassword", "x4TRDf4JHY578pth");
        System.setProperty("javax.net.ssl.keyStoreType", "JCEKS");
        System.setProperty("javax.net.debug", "all");
    }

    // le contexte du service
    private static final String CONTEXT = "/hello";
    // l'objet responsable du formattage de la date
    private static DateFormat df =
            DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM);
    // le serveur HTTP
    private HttpsServer server;
    
    static class HelloHandler implements HttpHandler {
        /**
         * Le gestionnaire implÃ©mentant le service
         * @param ex l'objet encapsulant la communication client-serveur
         * @throws IOException si la communication Ã©choue
         */
        @Override
        public void handle(HttpExchange ex) throws IOException {
            Calendar cal = Calendar.getInstance();
            Date date = cal.getTime();
            // le string exposant la date
            String dateString = df.format(date);
            // prÃ©paration du message
            String message = String.format("<h1 align='center'><code>%s</code></h1>", dateString);
            // rÃ©cupÃ©ration des headers de la rÃ©ponse
            Headers respHeaders = ex.getResponseHeaders();
            byte[] messageBytes = message.getBytes();
            // Initialisation des headers nÃ©cessaires Ã  une
            // bonne interprÃ©tation de la rÃ©ponse par le client
            respHeaders.set("Content-Type", "text/html");
            // ExpÃ©dition du code rÃ©ponse (ici OK)
            ex.sendResponseHeaders(200, messageBytes.length);
            try (OutputStream out = ex.getResponseBody()) {
                out.write(messageBytes);
                out.flush();
            }
        }
    }
    /**
     * CrÃ©ation d'une instance de la classe
     * @param address l'adresse de l'hÃ´te hÃ©bergeant le service
     * @param port le port associÃ© au service
     * @throws IOException si la crÃ©ation du serveur Ã©choue
     * @throws NoSuchAlgorithmException 
     */
    public HttpsHelloServer(String address, int port) throws IOException, NoSuchAlgorithmException {
        // CrÃ©ation du serveur
        server = HttpsServer.create(new InetSocketAddress(address, port), 0);
        // association du contexte et du handler au serveur
        server.createContext(CONTEXT, new HelloHandler());
        server.setHttpsConfigurator(new HttpsConfigurator(SSLContext.getDefault()));
        // l'exÃ©cuteur associÃ© au serveur fait que chaque requÃ¨te 
        // sera traitÃ©e dans un thread sÃ©parÃ©
        server.setExecutor(new Executor() {
            @Override
            public void execute(Runnable command) {
                new Thread(command).start();
            }
        });
        // dÃ©marrage du serveur
        server.start();
        System.out.println("server running");
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        try {
            HttpsHelloServer hello = new HttpsHelloServer("localhost", 7878);
        } catch (IOException ex) {
            Logger.getLogger(HttpsHelloServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

/*A rajouter :
 * SSLcontext.getDefault() pour creer setHttpsConfigurator
 * manque start, create, createContext
 * 
 * 
 */
