import exceptions.InvalidArgumentException;
import exceptions.MetierException;
import model.*;
import services.ClientService;
import services.CommandeService;
import services.PanierService;
import services.ProduitService;
import services.db.DBService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
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
            System.out.println("[0] : Exit");
            System.out.println("[1] : Créer un Produit");
            System.out.println("[2] : Lister les Produits");
            System.out.println("[3] : Créer un Client");
            System.out.println("[4] : Lister les Clients");
            System.out.println("[5] : Détail d'un client");
            System.out.println("[6] : Ajouter un produit au panier");
            System.out.println("[7] : Passer une commande");
            System.out.println("[8] : Lister les commandes");
            System.out.println("[9] : Détail d'une commande");
            System.out.print("Votre choix : ");

            String command = br.readLine();
            if (command.equals("0")) {
                DBService.get().close();
                System.out.println("Au revoir");
                break;
            } else if (command.equals("1")) {
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
            } else if (command.equals("2")) {
                List<Produit> produits = ProduitService.get().lister();
                for (Produit produit : produits) {
                    System.out.println(String.format("%s / %s / %s / %f", produit.id, produit.nom, produit.description, produit.prixUnitaire));
                }
            } else if (command.equals("3")) {
                System.out.println("Veuillez saisir les données du Client");
                System.out.print("Email : ");
                String email = br.readLine();
                System.out.print("Mot de Passe : ");
                String motDePasse = br.readLine();
                System.out.print("Nom : ");
                String nom = br.readLine();
                System.out.print("Prenom : ");
                String prenom = br.readLine();
                try {
                    Client client = ClientService.get().creer(email, motDePasse);
                    ClientService.get().modifier(client, nom, prenom, null, null);
                    ClientService.get().enregistrer(client);
                    System.out.println("Vous venez de créer le client : " + client.nom + " " + client.prenom);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (command.equals("4")) {
                List<Client> clients = ClientService.get().lister();
                for (Client client : clients) {
                    System.out.println(String.format("%s / %s / %s / %s", client.id, client.email, client.nom, client.prenom));
                }
            } else if (command.equals("5")) {
                System.out.print("Veuillez saisir l'id du client : ");
                String idClient = br.readLine();
                try {
                    Client client = ClientService.get().getClient(idClient);
                    Panier panier = PanierService.get().getPanier(client);
                    System.out.println(String.format("%s / %s / %s / %s", client.id, client.email, client.nom, client.prenom));
                    for (ProduitPanier produit : panier.produits) {
                        System.out.println(String.format("%s / %.2f / %d", produit.produit.nom, produit.produit.prixUnitaire, produit.quantite));
                    }
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            } else if (command.equals("6")) {
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
            } else if (command.equals("7")) {
                System.out.print("Veuillez saisir l'id du client : ");
                String idClient = br.readLine();
                try {
                    Client client = ClientService.get().getClient(idClient);
                    Panier panier = PanierService.get().getPanier(client);
                    Commande commande = CommandeService.get().creerDepuisPanier(panier);
                    CommandeService.get().enregistrer(commande, panier);
                    System.out.println(String.format("Le client [%s %s] vient de créer une commande d'un montant de %f", commande.client.prenom, commande.client.nom, commande.montant));
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            } else if (command.equals("8")) {
                List<Commande> commandes = CommandeService.get().lister();
                for (Commande commande : commandes) {
                    System.out.println(String.format("%s / %s / %s / %s / %f", commande.id, commande.date, commande.client.nom, commande.client.prenom, commande.montant));
                }
            } else if (command.equals("9")) {
                System.out.print("Veuillez saisir l'id de la commande : ");
                String idCommande = br.readLine();
                try {
                    Commande commande = CommandeService.get().getCommande(idCommande);
                    System.out.println(String.format("%s / %s / %s / %s / %f", commande.id, commande.date, commande.client.nom, commande.client.prenom, commande.montant));
                    for (ProduitCommande produit : commande.produits) {
                        System.out.println(String.format("%s / %.2f / %d", produit.produit.nom, produit.produit.prixUnitaire, produit.quantite));
                    }
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
