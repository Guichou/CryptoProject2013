
package ExosCours;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe cr�ant le serveur avec le protocole HTTPS
 */
public class HttpsFileserverServer implements HttpHandler {
    /** Le contexte du serveur */
    private static final String CONTEXT = "/fileserver";
   	/** Le serveur HTTPS */
    private HttpsServer server;
    
    /** Le mot de passe du serveur */
    private static final String PSWD = "x4TRDf4JHY578pth";
    
    /**
     *  Bloc d'initialisation des propri�t�s syst�mes n�cessaires 
     *	� la v�rification de l'identit� du serveur
     */
    static {
        System.setProperty("javax.net.ssl.keyStore", "kssrv.ks");
        System.setProperty("javax.net.ssl.keyStorePassword", PSWD);
        System.setProperty("javax.net.ssl.keyStoreType", "JCEKS");
        System.setProperty("javax.net.debug", "all");
    }

    /**
     * Le gestionnaire impl�mentant le service
     * @param ex l'objet encapsulant la communication client-serveur
     * @throws IOException si la communication �choue
     */
    public void handle(HttpExchange ex) throws IOException {
    	
    	/* R�cup�ration des headers de la r�ponse */
        Headers respHeaders = ex.getResponseHeaders();
        /* R�cup�ration des param�tre de l'URL */
        Map<String,String> url = getQueryMap(ex.getRequestURI().toString());
          
        /* Si le param�tre est "file" */
        if(url.get("/fileserver?file") !=null){
        	/* On signale que l'objet envoy� est un image */
        	respHeaders.set("Content-Type", "image/jpeg");
	        File file = new File ("data/"+url.get("/fileserver?file"));
	        byte [] bytearray  = new byte [(int)file.length()];
	        FileInputStream fis = new FileInputStream(file);
	        BufferedInputStream bis = new BufferedInputStream(fis);
	        bis.read(bytearray, 0, bytearray.length);
	        /* Exp�dition du code r�ponse (ici OK) */
	        ex.sendResponseHeaders(200, file.length());
	        /* Exp�dition du message */
	        OutputStream out = ex.getResponseBody();
	        /* Ecriture du message sur la sortie */
	        out.write(bytearray,0,bytearray.length);
	        out.flush();
	        out.close();
        }
        /* Si le param�tre est "list" */
        String message = "Liste des fichiers :  ";
        if(url.get("/fileserver?list").equals("all")){
        	/* On signale que l'objet envoy� est un text html */
        	respHeaders.set("Content-Type", "text/html");
        	File file = new File("data");
        	/* Liste des fichiers */
        	for(File f : file.listFiles()){
        		message += f.toString().replaceAll("data", "") + "  ";
        	}
        	byte[] messageBytes = message.getBytes();
        	/* Exp�dition du code r�ponse (ici OK) */
        	ex.sendResponseHeaders(200, messageBytes.length);
        	/* Exp�dition du message */
        	OutputStream out = ex.getResponseBody();
        	/* Ecriture du message sur la sortie */
            out.write(messageBytes);
            out.flush();
            out.close();
        }       
    }
    
    /**
     * M�thode permettant de r�cup�rer les param�tre de l'URL
     * @param query l'url
     * @return Une Map contenant les param�tres
     */
    public static Map<String, String> getQueryMap(String query)  
    {  
        String[] params = query.split("&");  
        Map<String, String> map = new HashMap<String, String>();  
        for (String param : params)  
        {  
            String name = param.split("=")[0];  
            String value = param.split("=")[1];  
            map.put(name, value);  
        }  
        return map;  
    }

    /**
     * Cr�ation d'une instance de la classe
     * @param address l'adresse de l'h�te h�bergeant le service
     * @param port le port associ� au service
     * @throws IOException si la cr�ation du serveur �choue
     */
    public HttpsFileserverServer(String address, int port) throws IOException, NoSuchAlgorithmException {
        /* Cr�ation du serveur */
        server = HttpsServer.create(new InetSocketAddress(address, port), 0);
        /* association du contexte et du handler au serveur */
        server.createContext(CONTEXT, this);
        
        server.setHttpsConfigurator(new HttpsConfigurator(javax.net.ssl.SSLContext.getDefault()));
        /* l'ex�cuteur associ� au serveur fait que chaque requ�te 
           sera trait�e dans un thread s�par� */
        server.setExecutor(new Executor() {
            public void execute(Runnable command) {
                new Thread(command).start();
            }
        });
        /* d�marrage du serveur */
        server.start();
        System.out.println("server fileserver running");
    }
    
    /**
     * M�thode main qui lance le serveur HTTPS
     * @param args
     */
    public static void main(String[] args){
        try {
            HttpsFileserverServer server = new HttpsFileserverServer("localhost", 8000);
        } catch (Exception ex) {
            Logger.getLogger(HttpsFileserverServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}


