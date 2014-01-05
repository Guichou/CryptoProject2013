
package ExosCours;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
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

/**
 * Une classe implémentant le service DayTime sous le protocole HTTP
 * @author Patrick Guichet
 */
public class HttpsHelloServer implements HttpHandler {
    // le contexte du service
    private static final String CONTEXT = "/hello";
    // l'objet responsable du formattage de la date
    private static DateFormat df =
            DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM);
    // le serveur HTTP
    private HttpsServer server;
    
    private static final String PSWD = "x4TRDf4JHY578pth";
    
    ///////////////////////////////////////////////////////////////////////////
    // Bloc d'initialisation des propriétés systèmes nécessaires 
    // à la vérification de l'identité du serveur
    //
    static {
        System.setProperty("javax.net.ssl.keyStore", "kssrv.ks");
        System.setProperty("javax.net.ssl.keyStorePassword", PSWD);
        System.setProperty("javax.net.ssl.keyStoreType", "JCEKS");
        System.setProperty("javax.net.debug", "all");
    }

    /**
     * Le gestionnaire implémentant le service
     * @param ex l'objet encapsulant la communication client-serveur
     * @throws IOException si la communication échoue
     */
    public void handle(HttpExchange ex) throws IOException {
        //Calendar cal = Calendar.getInstance();
        //Date date = cal.getTime();
        // le string exposant la date
        //String dateString = df.format(date);
        // préparation du message
        String message = String.format("<h1 align='center'>%s</h1>", "Hello world!");
        // récupération des headers de la réponse
        Headers respHeaders = ex.getResponseHeaders();
        byte[] messageBytes = message.getBytes();
        // Initialisation des headers nécessaires à une
        // bonne interprétation de la réponse par le client
        respHeaders.set("Content-Type", "text/html");
        // Expédition du code réponse (ici OK)
        ex.sendResponseHeaders(200, messageBytes.length);
        // Expédition du message
        OutputStream out = ex.getResponseBody();
        out.write(messageBytes);
        out.flush();
        out.close();
    }

    /**
     * Création d'une instance de la classe
     * @param address l'adresse de l'hôte hébergeant le service
     * @param port le port associé au service
     * @throws IOException si la création du serveur échoue
     */
    public HttpsHelloServer(String address, int port) throws IOException, NoSuchAlgorithmException {
        // Création du serveur
        server = HttpsServer.create(new InetSocketAddress(address, port), 0);
        // association du contexte et du handler au serveur
        server.createContext(CONTEXT, this);
        
        server.setHttpsConfigurator(new HttpsConfigurator(javax.net.ssl.SSLContext.getDefault()));
        // l'exécuteur associé au serveur fait que chaque requète 
        // sera traitée dans un thread séparé
        server.setExecutor(new Executor() {
            public void execute(Runnable command) {
                new Thread(command).start();
            }
        });
        // démarrage du serveur
        server.start();
        System.out.println("server hello running");
    }

    public static void main(String[] args){
        try {
            HttpsHelloServer server = new HttpsHelloServer("localhost", 8000);
        } catch (Exception ex) {
            Logger.getLogger(HttpsHelloServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}


