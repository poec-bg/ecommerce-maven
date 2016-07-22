import com.google.common.net.InetAddresses;
import exceptions.InvalidArgumentException;
import exceptions.MetierException;
import model.*;
import services.ClientService;
import services.CommandeService;
import services.PanierService;
import services.ProduitService;
import services.date.SystemDateService;
import services.db.DBService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.*;
import java.util.Enumeration;
import java.util.List;

public class Lanceur {
    public static void main(String[] args) throws IOException {
//        try {
//            Client client = ClientService.get().creer("nicolas.giard@coaxys.com", "test");
//            ClientService.get().modifier(client, "Giard", "Nicolas", "", "");
//            ClientService.get().enregistrer(client);
//            Produit produitClasseur = ProduitService.get().creer("Classeur", "Super Classeur", 5.5f);
//            ProduitService.get().enregistrer(produitClasseur);
//            Produit produitIntercalaire = ProduitService.get().creer("Intercalaire", "Rouge", 2.75f);
//            ProduitService.get().enregistrer(produitIntercalaire);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println("Menu:");
            System.out.println("[0] : exit");
            System.out.println("[1] : créer un Produit");
            System.out.println("[2] : lister les Produits");
            System.out.println("[3] : créer un Client");
            System.out.println("[4] : lister les Clients");
            System.out.println("[5] : Ajouter un produit au panier");
            System.out.println("[6] : Détail d'un client");
            System.out.println("[7] : Lister Les paniers");
            System.out.println("[8] : Invalider le Panier");
            System.out.println("[9] : Créer Commande");
            System.out.println("[10] : lister Commandes Client");
            System.out.print("Votre choix : ");

            String command = br.readLine();
            switch (command){
                case "0" :
                    DBService.get().close();
                    System.out.println("Au revoir");
                    System.exit(0);
                case "1" :
                    System.out.println("Veuillez saisir les données du Produit");
                    System.out.print("Nom : ");
                    String nom = br.readLine();
                    System.out.print("Description : ");
                    String description = br.readLine();
                    System.out.print("Prix Unitaire : ");
                    float prixUnitaire = Float.parseFloat(br.readLine());

                    try {
                        Produit produit = ProduitService.get().creer(nom, description, prixUnitaire);
                        ProduitService.get().enregistrer(produit);

                        System.out.println("Vous venez de créer le produit : " + produit.nom);
                    } catch (InvalidArgumentException e) {
                        e.printStackTrace();
                    }
                    break;
                case "2" :
                    List<Produit> produits = ProduitService.get().lister();
                    for (Produit produit : produits) {
                        System.out.println(String.format("%s / %s / %s / %f", produit.id, produit.nom, produit.description, produit.prixUnitaire));
                    }
                    break;
                case "3" :
                    System.out.println("Veuillez saisir les données du Client");
                    System.out.print("Email : ");
                    String email = br.readLine();
                    System.out.print("Mot de Passe : ");
                    String motDePasse = br.readLine();
                    System.out.print("Nom : ");
                    String nomClient = br.readLine();
                    System.out.print("Prenom : ");
                    String prenom = br.readLine();
                    try {
                        Client client = ClientService.get().creer(email, motDePasse);
                        ClientService.get().modifier(client, nomClient, prenom, null, null);
                        ClientService.get().enregistrer(client);
                        System.out.println("Vous venez de créer le client : " + client.nom + " " + client.prenom);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "4" :
                    List<Client> clients = ClientService.get().lister();
                    for (Client client : clients) {
                        System.out.println(String.format("%s / %s / %s / %s", client.id, client.email, client.nom, client.prenom));
                    }
                    break;
                case "5" :
                    System.out.print("Veuillez saisir l'id du client : ");
                    String idClient = br.readLine();
                    System.out.print("Veuillez saisir l'id du produit : ");
                    String idProduit = br.readLine();
                    try {
                        Client client = ClientService.get().getClient(idClient);
                        Panier panier = PanierService.get().getPanier(client);
                        Produit produit = ProduitService.get().getProduit(idProduit);
                        PanierService.get().ajouterProduit(panier, produit);
                    } catch (InvalidArgumentException e) {
                        e.printStackTrace();
                    }
                    break;
                case "6" :
                    System.out.print("Veuillez saisir l'id du client : ");
                    String idClientBis = br.readLine();
                    try {
                        Client client = ClientService.get().getClient(idClientBis);
                        Panier panier = PanierService.get().getPanier(client);
                        System.out.println(String.format("%s / %s / %s / %s", client.id, client.email, client.nom, client.prenom));
                        for (ProduitPanier produit : panier.produits) {
                            System.out.println(String.format("%s / %.2f / %d", produit.produit.nom, produit.produit.prixUnitaire, produit.quantite));
                        }
                    } catch (InvalidArgumentException e) {
                        e.printStackTrace();
                    }
                    break;
                case "7" :
                    System.out.print("Veuillez saisir l'idClient : ");
                    idClient = br.readLine();
                    try {
                        List<Panier> paniers = PanierService.get().lister(ClientService.get().getClient(idClient));
                        for (Panier panier : paniers ) {
                            System.out.println(String.format("%s",panier.id));
                        }
                    } catch (InvalidArgumentException e) {
                        e.printStackTrace();
                    }

                    break;
                case "8" :
                    // Invalider Panier
                    System.out.print("Veuillez saisir l'idClient : ");
                    idClient = br.readLine();
                    try {
                        Panier panier = PanierService.get().getPanier(ClientService.get().getClient(idClient));
                        PanierService.get().invalider(panier);
                    } catch (InvalidArgumentException e) {
                        e.printStackTrace();
                    }
                    break;
                case "9" :
                    // Créer commande
                    System.out.print("Veuillez saisir l'idClient : ");
                    idClient = br.readLine();
                    try {
                        Commande commande = CommandeService.get().creer(PanierService.get().getPanier(ClientService.get().getClient(idClient)));
                        CommandeService.get().enregistrer(commande);
                        System.out.println("Commande enregistrée");
                        PanierService.get().invalider(PanierService.get().getPanier(ClientService.get().getClient(idClient)));
                    } catch (InvalidArgumentException e) {
                        e.printStackTrace();
                    } catch (MetierException e) {
                        e.printStackTrace();
                    }
                    break;
                case "10" :
                    System.out.print("Veuillez saisir l'idClient : ");
                    idClient = br.readLine();
                    try {
                        List<Commande> commandes = CommandeService.get().listerCommandesClient(idClient);
                        for (Commande commande : commandes) {
                            Client client = commande.client;
                            System.out.println(String.format("Numéro de commande %s du client %s %s",commande.id, client.nom, client.prenom));
                            for (ProduitCommande produitCommande : commande.produits) {
                                System.out.println(String.format("Produit : %s %s %f avec une quantité de %d",produitCommande.produit.nom, produitCommande.produit.description, produitCommande.prixUnitaire,produitCommande.quantite));
                            }
                        }
                    } catch (InvalidArgumentException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
            }
        }
    }
}
