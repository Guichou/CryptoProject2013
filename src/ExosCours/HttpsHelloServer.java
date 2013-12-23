package ExosCours;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpsHelloServer {
    ////////////////////////////////////////////////////////
    // Initialisation des propriétés systèmes nécessaires 
    // �  l'établissement d'un contexte SSL
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
    private HttpServer server;
    
    static class HelloHandler implements HttpHandler {
        /**
         * Le gestionnaire implémentant le service
         * @param ex l'objet encapsulant la communication client-serveur
         * @throws IOException si la communication échoue
         */
        @Override
        public void handle(HttpExchange ex) throws IOException {
            Calendar cal = Calendar.getInstance();
            Date date = cal.getTime();
            // le string exposant la date
            String dateString = "hello";/* ou df.format(date) pour afficher la date */
            // préparation du message
            String message = String.format("<h1 align='center'>%s</h1>", dateString);
            // récupération des headers de la réponse
            Headers respHeaders = ex.getResponseHeaders();
            byte[] messageBytes = message.getBytes();
            // Initialisation des headers nécessaires �  une
            // bonne interprétation de la réponse par le client
            respHeaders.set("Content-Type", "text/html");
            // Expédition du code réponse (ici OK)
            ex.sendResponseHeaders(200, messageBytes.length);
            try (OutputStream out = ex.getResponseBody()) {
                out.write(messageBytes);
                out.flush();
            }
        }
    }
    /**
     * Création d'une instance de la classe
     * @param address l'adresse de l'hôte hébergeant le service
     * @param port le port associé au service
     * @throws IOException si la création du serveur échoue
     */
    public HttpsHelloServer(String address, int port) throws IOException {
        // Création du serveur
        server = HttpServer.create(new InetSocketAddress(address, port), 0);
        // association du contexte et du handler au serveur
        server.createContext(CONTEXT, new HelloHandler());
        // l'exécuteur associé au serveur fait que chaque requète 
        // sera traitée dans un thread séparé
        server.setExecutor(new Executor() {
            @Override
            public void execute(Runnable command) {
                new Thread(command).start();
            }
        });
        // démarrage du serveur
        server.start();
        System.out.println("server running");
    }

    public static void main(String[] args) {
        try {
            HttpsHelloServer hello = new HttpsHelloServer("localhost", 7878);
        } catch (IOException ex) {
            Logger.getLogger(HttpsHelloServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}