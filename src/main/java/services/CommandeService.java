package services;

import com.google.common.base.Strings;
import com.sun.javaws.exceptions.InvalidArgumentException;
import model.*;
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

/**
 * Created by formation on 21/07/2016.
 */
public class CommandeService {

    private static CommandeService instance;

    public static CommandeService get (){
        if(instance == null){
            instance = new CommandeService();
        }
        return instance;
    }
    public Commande creer(Panier panier) {
        Commande commande = new Commande();
        commande.id = UUID.randomUUID().toString();
        commande.client = panier.client;
        commande.date = DateService.get().now();
        commande.produits = new ArrayList<>();
        for (ProduitPanier produitPanier : panier.produits) {
            commande.produits.add(new ProduitCommande(produitPanier.produit,
                    produitPanier.quantite,
                    produitPanier.produit.prixUnitaire));
        }
        return commande;
    }

    public Commande getCommande(String idCommande) throws InvalidArgumentException {
        List<String> validationMessages = new ArrayList<>();
        if (Strings.isNullOrEmpty(idCommande)) {
            validationMessages.add("L'idClient ne peut être null ou vide");
        }
        if (validationMessages.size() > 0) {
            throw new InvalidArgumentException((String[]) validationMessages.toArray(new String[0]));
        }

        try {
            Statement requete = DBService.get().getConnection().createStatement();
            ResultSet result = requete.executeQuery("SELECT * FROM `Commande` WHERE `id`=" + idCommande);
            if(result.next()){
                Commande commande = new Commande();
                commande.client = ClientService.get().getClient(result.getString("idClient"));
                commande.id = result.getString("id");
                commande.produits = new ArrayList<>();
                ResultSet resultProduitsCommande = requete.executeQuery("SELECT * FROM ProduitCommande WHERE idCommande = "+commande.id);
                while(resultProduitsCommande.next()){
                    String idProduit = resultProduitsCommande.getString("idProduit");
                    Produit produit = ProduitService.get().getProduit(idProduit);
                    ProduitCommande produitCommande = new ProduitCommande(produit,resultProduitsCommande.getInt("quantite"),resultProduitsCommande.getFloat("prixUnitaire"));
                    commande.produits.add(produitCommande);
                }
                return commande;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (exceptions.InvalidArgumentException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void enregistrer(Commande commande){
        String sRequeteInsertCommande = "INSERT INTO Commande (`id`,`date`,`idClient`) VALUES (?,?,?)";
        String sRequeteInsertProduitCommande = "INSERT INTO ProduitCommande (`idCommande`,`idProduit`,`quantite`,`prixUnitaire`) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatementInsertCommande = DBService.get().getConnection().prepareStatement(sRequeteInsertCommande);
            preparedStatementInsertCommande.setString(0,commande.id);
            preparedStatementInsertCommande.setTimestamp(1,new java.sql.Timestamp(commande.date.toDate().getTime()));
            preparedStatementInsertCommande.setString(2,commande.client.id);

            preparedStatementInsertCommande.execute();

            PreparedStatement preparedStatementInsertProduitCommande = DBService.get().getConnection().prepareStatement(sRequeteInsertProduitCommande);
            preparedStatementInsertProduitCommande.setString(0,commande.id);

            preparedStatementInsertProduitCommande.setString(1,commande.id);
            preparedStatementInsertProduitCommande.setString(2,commande.id);
            preparedStatementInsertProduitCommande.setString(3,commande.id);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
