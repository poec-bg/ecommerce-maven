package services;

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
     * Constructeur privé = personne ne peut faire de new ComandeService()
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


    public static Commande creer(Panier panier) {
        Commande commande = new Commande();
        commande.id = UUID.randomUUID().toString();
        commande.client = panier.client;
        commande.produits = creerListeCommande(panier.produits);
        commande.date = DateService.get().now();
//        commande.montant = requeteCommande.executeQuery("SELECT SUM ");


        try {
            PreparedStatement preparedStatement = DBService.get().getConnection().prepareStatement("INSERT INTO Commande (`id`, `client`, `produits`, `date`, `montant`) VALUES (?, ?)");
            preparedStatement.setString(1, commande.id);
            preparedStatement.setTimestamp(2, new java.sql.Timestamp(panier.date.toDate().getTime()));

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commande;
    }

    private static List<ProduitCommande> creerListeCommande(List<ProduitPanier> produits) {
      List<ProduitCommande> produitCommande = new ArrayList<>();
        for (ProduitPanier produit : produits) {
           produitCommande.add(new ProduitCommande(produit.produit, produit.quantite, produit.produit.prixUnitaire));


        }
    return produitCommande;

    }

    private static ProduitCommande creerProduitCommande (ProduitPanier produitPanier){
       return new ProduitCommande(produitPanier.produit, produitPanier.quantite, produitPanier.produit.prixUnitaire);
    }


}
