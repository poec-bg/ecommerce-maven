package services;

import com.google.common.base.Strings;
import exceptions.InvalidArgumentException;
import exceptions.MetierException;
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
    public Commande creer(Panier panier) throws exceptions.InvalidArgumentException, MetierException {
        if(panier == null){
            throw new exceptions.InvalidArgumentException(new String[] {
                "Le panier ne peut pas être null"
            });
        }
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
        if(commande.produits == null){
            throw new MetierException("Une commande doit contenir au moins un produits");
        }
        return commande;
    }

    public Commande getCommande(String idCommande) throws exceptions.InvalidArgumentException {
        List<String> validationMessages = new ArrayList<>();
        if (Strings.isNullOrEmpty(idCommande)) {
            validationMessages.add("L'idCommande ne peut être null ou vide");
        }
        if (validationMessages.size() > 0) {
            throw new exceptions.InvalidArgumentException((String[]) validationMessages.toArray(new String[0]));
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

    /**
     * Fonction qui va enregistrer en base de données une commande
     * @param commande
     * @throws exceptions.InvalidArgumentException
     */
    public void enregistrer(Commande commande) throws exceptions.InvalidArgumentException {
        if(commande == null){
            throw new exceptions.InvalidArgumentException(new String[] {"La commande ne doit pas être null"});
        }
        String sRequeteInsertCommande = "INSERT INTO Commande (`id`,`date`,`idClient`) VALUES (?,?,?)";
        String sRequeteInsertProduitCommande = "INSERT INTO ProduitCommande (`idCommande`,`idProduit`,`quantite`,`prixUnitaire`) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatementInsertCommande = DBService.get().getConnection().prepareStatement(sRequeteInsertCommande);
            preparedStatementInsertCommande.setString(1,commande.id);
            preparedStatementInsertCommande.setTimestamp(2,new java.sql.Timestamp(commande.date.toDate().getTime()));
            preparedStatementInsertCommande.setString(3,commande.client.id);

            preparedStatementInsertCommande.execute();
            for (ProduitCommande produitCommande : commande.produits) {
                PreparedStatement preparedStatementInsertProduitCommande = DBService.get().getConnection().prepareStatement(sRequeteInsertProduitCommande);
                preparedStatementInsertProduitCommande.setString(1,commande.id);
                preparedStatementInsertProduitCommande.setString(2,produitCommande.produit.id);
                preparedStatementInsertProduitCommande.setInt(3,produitCommande.quantite);
                preparedStatementInsertProduitCommande.setFloat(4,produitCommande.prixUnitaire);

                preparedStatementInsertProduitCommande.execute();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Commande> listerCommandesClient(String idClient) throws InvalidArgumentException {
        System.out.println("IdClient => " + idClient);
        if(idClient == null || idClient.equals("")){
            throw new InvalidArgumentException(new String[] {"L'idClient ne doit pas être null ou vide"});
        }
        List<Commande> commandesClient = new ArrayList<>();
        try {
            Statement requete = DBService.get().getConnection().createStatement();
            ResultSet result = requete.executeQuery("SELECT * FROM Commande where idClient='" + idClient + "'");
            while(result.next()){
                Commande commande = new Commande();
                commande.id = result.getString("id");
                commande.client = ClientService.get().getClient(result.getString("idClient"));
                commande.date = new DateTime(result.getTimestamp("date"));
                Statement requeteProduitCommande = DBService.get().getConnection().createStatement();
                ResultSet resultProduitCommande = requeteProduitCommande.executeQuery("SELECT * FROM ProduitCommande WHERE idCommande='" + commande.id + "'");
                while (resultProduitCommande.next()) {
                    String idProduit = resultProduitCommande.getString("idProduit");
                    Produit produit = ProduitService.get().getProduit(idProduit);
                    ProduitCommande produitCommande = new ProduitCommande(produit, resultProduitCommande.getInt("quantite"),resultProduitCommande.getFloat("prixUnitaire"));
                    commande.produits.add(produitCommande);
                }
                commandesClient.add(commande);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandesClient;
    }
}
