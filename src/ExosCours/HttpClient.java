package ExosCours;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Classe qui lance le client
 */
public class HttpClient {
	
	/** L'user-agent */
	private final String USER_AGENT = "Mozilla/5.0";
	
	/**
	 * M�thode main qui lance le client
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		/* Lancement du client */
		HttpClient http = new HttpClient();
		
		System.out.println("Client Fileserver");
		/* Lancement de la requ�te au serveur */
		http.sendGet();

	}

	/**
	 * Lancement d'une requ�te HTTPS avec un GET
	 * @throws Exception
	 */
	private void sendGet() throws Exception {
		
		/* URL de la requ�te */
		String url = "https://localhost:8000/fileserver?list=all";

		/* Ouverture de la connection */
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		/* On enl�ve la certification */
		TrustModifier.relaxHostChecking(con);
		
		/* Envoie en GET */
		con.setRequestMethod("GET");

		/* On donne l'USER-AGENT */
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setDoOutput(true);
		
		/* R�ponse */
		int responseCode = con.getResponseCode();
		System.out.println("\nEnvoi du 'GET' a l'URL : " + url);
		System.out.println("Reponse de la requete : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		/* R�sultat de la requ�te */
		System.out.println(response.toString());

	}
}