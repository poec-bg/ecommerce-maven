package services;

import com.google.common.base.Strings;
import exceptions.InvalidArgumentException;
import model.*;
import org.joda.time.DateTime;
import services.date.DateService;
import services.db.DBService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CommandeService {

    /**
     * Singleton
     */
    private static CommandeService instance;

    /**
     * Constructeur privé = personne ne peut faire de new CommandeService()
     */
    private CommandeService() {
    }

    /**
     * Seule méthode pour récupérer une instance (toujours la même) de CommandeService
     *
     * @return toujours la même instance de CommandeService
     */
    public static CommandeService get() {
        if (instance == null) {
            instance = new CommandeService();
        }
        return instance;
    }

    public static Commande creer(Panier panier){
        Commande commande = new Commande();

        commande.id = UUID.randomUUID().toString();
        commande.date = DateService.get().now();
        commande.client = panier.client;
        commande.montantHT = 0.0f;
        commande.produits = panier.produits;

        for ( ProduitPanier produitPanier : panier.produits) {
            commande.montantHT = commande.montantHT + produitPanier.produit.prixUnitaire;
        }


//        commande.produits = new model.ProduitCommande[panier.produits.length];
//        for (int indexProduitPanier = 0; indexProduitPanier < panier.produits.length; indexProduitPanier++) {
//            commande.produits[indexProduitPanier] = new model.ProduitCommande(panier.produits[indexProduitPanier].produit,
//                    panier.produits[indexProduitPanier].quantite,
//                    panier.produits[indexProduitPanier].produit.prixUnitaire);
//        }
        commande.produits = new ArrayList<>();
        for (ProduitPanier produitPanier : panier.produits) {
            commande.produits.add(new ProduitCommande(produitPanier.produit,
                    produitPanier.quantite,
                    produitPanier.produit.prixUnitaire));
        }

        return commande;
    }

    public void affiche(String id) {
        List<Commande> commandes = new ArrayList<>();

        try {
            Statement requete = DBService.get().getConnection().createStatement();
            ResultSet result = requete.executeQuery("SELECT * FROM Commande");
//            while (result.next()) {
//                Commande commande = new Commande();
//                commande.id = result.getString("id");
//                commande.client =
//
//            }



        } catch (SQLException e) {
            e.printStackTrace();
        }



//        System.out.println(String.format("Date : %s \nmodel.Client : %s \nAdresse : %s", date, client.nom, client.adressePostale));
//        for (ProduitCommande produitCommande : produits) {
//            System.out.println(String.format("model.Produit : %s x %d : %f", produitCommande.produit.nom, produitCommande.quantite, produitCommande.quantite * produitCommande.prixUnitaire));
//        }
    }
}
