package ExosCours;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Classe qui lance le client (� lancer apr�s le serveur)
 */
public class HttpsClient {

	/** L'user-agent */
	private final String USER_AGENT = "Mozilla/5.0";

	/**
	 * M�thode main qui lance le client
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		/* Lancement du client */
		HttpsClient http = new HttpsClient();


		/* Lancement de la requ�te au serveur */
		http.sendGet();

	}

	/**
	 * Lancement d'une requ�te HTTPS avec un GET
	 * @throws Exception
	 */
	private void sendGet() throws Exception {

		
		File file = new File("data");
		String req="";
		/*Pour savoir si le fichier existe*/
		boolean isExisting = true;

		/*Scanner pour lire ce qu'�crit l'utilisateur*/
		Scanner sc = new Scanner(System.in);

		System.out.println("Bonjour et bienvenue sur le client fileserver.");
		/*Instanciation de str : tant qu'elle sera diff�rente de "q", le client tournera*/
		String str ="g";
		do{
			System.out.println("\nVoulez-vous la liste des images (tapez 1) ou une une image en particulier (tapez 2) ? \nPour quitter, tapez q.");
			str = sc.nextLine();
			/* Par d�faut, la requ�te sera de r�cup�rer la liste des fichiers contenus dans le r�pertoire data */
			req="list=all";
			isExisting = true;
			if(!str.equals("q"))
			{
				/*Tant que le choix de l'utilisateur n'est pas valide, on reste dans la boucle*/
				while(!str.equals("1") && !str.equals("2") && !str.equals("q"))
				{
					System.out.println("Vous n'avez tap� ni 1, ni 2. Voulez-vous la liste des images (tapez 1) ou une une image en particulier (tapez 2) ? ");
					str=sc.nextLine();
				}
				
				if(str.equals("2"))
				{
					System.out.println("Tapez le nom de l'image que vous d�sirez (ex : tiger.jpg)");
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

				while ((inputLine = in.readLine()) != null) 
				{
					response.append(inputLine);
				}
				in.close();

				/* R�sultat de la requ�te */
				if(str.equals("1"))
					System.out.println(response.toString());
				else if(str.equals("2") && isExisting )
					System.out.println("L'image est disponible via un navigateur Internet � l'addresse ci-dessus.");
			}
		}while(!str.equals("q"));
		System.out.println("Merci, et � bient�t.");
		sc.close();
	}
	/**
	 * M�thode permettant de v�rifier si le fichier existe ou pas dans le r�pertoire
	 * @param File[] contient les noms de tous les fichiers
	 * @param s est le nom du fichier dont on veut v�rifier l'existence
	 * */
	public boolean exist(File[] fs, String s){
		for(File f: fs){
			if(f.toString().equals("data\\"+s))
				return true;
		}
		return false;
	}

}