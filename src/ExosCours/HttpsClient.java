package ExosCours;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Classe qui lance le client (à lancer après le serveur)
 */
public class HttpsClient {

	/** L'user-agent */
	private final String USER_AGENT = "Mozilla/5.0";

	/**
	 * Méthode main qui lance le client
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		/* Lancement du client */
		HttpsClient http = new HttpsClient();


		/* Lancement de la requête au serveur */
		http.sendGet();

	}

	/**
	 * Lancement d'une requête HTTPS avec un GET
	 * @throws Exception
	 */
	private void sendGet() throws Exception {

		
		File file = new File("data");
		String req="";
		/*Pour savoir si le fichier existe*/
		boolean isExisting = true;

		/*Scanner pour lire ce qu'écrit l'utilisateur*/
		Scanner sc = new Scanner(System.in);

		System.out.println("Bonjour et bienvenue sur le client fileserver.");
		/*Instanciation de str : tant qu'elle sera différente de "q", le client tournera*/
		String str ="g";
		do{
			System.out.println("\nVoulez-vous la liste des images (tapez 1) ou une une image en particulier (tapez 2) ? \nPour quitter, tapez q.");
			str = sc.nextLine();
			/* Par défaut, la requête sera de récupérer la liste des fichiers contenus dans le répertoire data */
			req="list=all";
			isExisting = true;
			if(!str.equals("q"))
			{
				/*Tant que le choix de l'utilisateur n'est pas valide, on reste dans la boucle*/
				while(!str.equals("1") && !str.equals("2") && !str.equals("q"))
				{
					System.out.println("Vous n'avez tapé ni 1, ni 2. Voulez-vous la liste des images (tapez 1) ou une une image en particulier (tapez 2) ? ");
					str=sc.nextLine();
				}
				
				if(str.equals("2"))
				{
					System.out.println("Tapez le nom de l'image que vous désirez (ex : tiger.jpg)");
					String s = "";
					s=sc.nextLine();
					req="file="+s;
					
					if(!exist(file.listFiles(),s))
					{
						System.out.println("Ce fichier n'existe pas");
						isExisting =false;
						continue;
					}

				}

				String url = "https://localhost:8000/fileserver?"+req;/* On peut aussi mettre "...?file=tiger.jpg" par exemple*/

				/* Ouverture de la connection */
				URL obj = new URL(url);
				HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
				/* On enlève la certification */
				TrustModifier.relaxHostChecking(con);

				/* Envoie en GET */
				con.setRequestMethod("GET");

				/* On donne l'USER-AGENT */
				con.setRequestProperty("User-Agent", USER_AGENT);
				con.setDoOutput(true);

				/* Réponse */
				int responseCode = con.getResponseCode();
				System.out.println("\nEnvoi du 'GET' a l'URL : " + url);
				System.out.println("Reponse de la requete : " + responseCode);

				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) 
				{
					response.append(inputLine);
				}
				in.close();

				/* Résultat de la requête */
				if(str.equals("1"))
					System.out.println(response.toString());
				else if(str.equals("2") && isExisting )
					System.out.println("L'image est disponible via un navigateur Internet à l'addresse ci-dessus.");
			}
		}while(!str.equals("q"));
		System.out.println("Merci, et à bientôt.");
		sc.close();
	}
	/**
	 * Méthode permettant de vérifier si le fichier existe ou pas dans le répertoire
	 * @param File[] contient les noms de tous les fichiers
	 * @param s est le nom du fichier dont on veut vérifier l'existence
	 * */
	public boolean exist(File[] fs, String s){
		for(File f: fs){
			if(f.toString().equals("data\\"+s))
				return true;
		}
		return false;
	}

}