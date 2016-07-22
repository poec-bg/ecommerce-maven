package services;

import com.google.common.base.Strings;
import exceptions.InvalidArgumentException;
import model.*;
import org.joda.time.DateTime;
import services.date.DateService;
import services.db.DBService;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by formation on 21/07/2016.
 */
public class CommandeService {


    private CommandeService() {

    }

    public static Commande verifierStock(Commande commande) {

        return null;
    }

    public static Commande creer(Panier panier) throws InvalidArgumentException {
        if (panier == null) {
            throw new InvalidArgumentException(new String[] {"Le panier ne peut être null"});
        }
        List<String> validationMessages = new ArrayList<>();
        if (Strings.isNullOrEmpty(panier.client.nom)) {
            validationMessages.add("Le nom du client ne peut être null ou vide");
        }
        if (Strings.isNullOrEmpty(panier.client.prenom)) {
            validationMessages.add("Le prenom du client ne peut être null ou vide");
        }
//        if (Strings.isNullOrEmpty(panier.client.adressePostale)) {
//            validationMessages.add("L'adresse du client ne peut être null ou vide");
//        }
        if (panier.produits.size() == 0) {
            validationMessages.add("Le panier ne peut être vide");
        }
        if (validationMessages.size() > 0) {
            throw new InvalidArgumentException((String[]) validationMessages.toArray(new String[0]));
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

        return commande;
    }

    public static void enregistrerEtInvaldier(Commande commande, Panier panier) throws InvalidArgumentException {
        Connection connection = null;
        try {
            connection = DBService.get().getConnection();
            boolean temp = connection.getAutoCommit();
            connection.setAutoCommit(false);

            // Do
            enregistrer(commande);
            PanierService.get().invalider(panier);

            connection.commit();
            connection.setAutoCommit(temp);
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void enregistrer(Commande commande) throws InvalidArgumentException {
        List<String> validationMessages = new ArrayList<>();
        if (commande == null) {
            validationMessages.add("La commande ne peut être null");
        }
        if (validationMessages.size() > 0) {
            throw new InvalidArgumentException((String[]) validationMessages.toArray(new String[0]));
        }

        PreparedStatement preparedStatement;
        try {
            // Insert Commande
            preparedStatement = DBService.get().getConnection().prepareStatement("INSERT INTO Commande (`id`, `idClient`, `date`) VALUES (?, ?, ?)");
            preparedStatement.setString(1, commande.id);
            preparedStatement.setString(2, commande.client.id);
            preparedStatement.setTimestamp(3, new java.sql.Timestamp(commande.date.toDate().getTime()));
            preparedStatement.execute();

            // Insert ProduitCommande
            for (ProduitCommande produitCommande : commande.produits) {
                preparedStatement = DBService.get().getConnection().prepareStatement("INSERT INTO ProduitCommande (`idProduit`, `idCommande`, `quantite`, `prixUnitaire`) VALUES (?, ?, ?, ?)");
                preparedStatement.setString(1, produitCommande.produit.id);
                preparedStatement.setString(2, commande.id);
                preparedStatement.setInt(3, produitCommande.quantite);
                preparedStatement.setFloat(4, produitCommande.prixUnitaire);
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Commande> lister(Client client) throws InvalidArgumentException {
        List<String> validationMessages = new ArrayList<>();
        if (client == null) {
            validationMessages.add("Le client ne peut être null");
        }
        if (validationMessages.size() > 0) {
            throw new InvalidArgumentException((String[]) validationMessages.toArray(new String[0]));
        }
        List<Commande> commandes = new ArrayList<>();
        Connection connection = DBService.get().getConnection();
        try {
            // Récupère la liste des commandes
            PreparedStatement preparedStatementCommande = connection.prepareStatement("SELECT * FROM Commande WHERE idClient = ?");
            preparedStatementCommande.setString(1, client.id);
            ResultSet resultCommande = preparedStatementCommande.executeQuery();
            while (resultCommande.next()) {
                Commande commande = new Commande();
                commande.id = resultCommande.getString("id");
                commande.client = client;
                commande.date = new DateTime(resultCommande.getTimestamp("date"));
                commande.produits = new ArrayList<>();

                commandes.add(commande);

                // Récupère la liste des produits pour la commande courante
                PreparedStatement preparedStatementProduitCom = connection.prepareStatement("SELECT * FROM ProduitCommande WHERE idCommande = ?");
                preparedStatementProduitCom.setString(1, commande.id);
                ResultSet resultProduitCom = preparedStatementProduitCom.executeQuery();

                while (resultProduitCom.next()) {
                    String produitId = resultProduitCom.getString("idProduit");
                    PreparedStatement preparedStatementProduit = connection.prepareStatement("SELECT * FROM Produit WHERE id = ?");
                    preparedStatementProduit.setString(1, produitId);
                    ResultSet resultProduit = preparedStatementProduit.executeQuery();

                    resultProduit.next(); // toujours vrai
                    Produit produit = new Produit();
                    produit.id = produitId;
                    produit.nom = resultProduit.getString("nom");
                    produit.description = resultProduit.getString("description");
                    produit.prixUnitaire = resultProduit.getFloat("prixUnitaire");
                    produit.isSupprime = resultProduit.getBoolean("isSupprime");

                    ProduitCommande produitCommande = new ProduitCommande(produit,
                            resultProduitCom.getInt("quantite"),
                            resultProduitCom.getFloat("prixUnitaire"));

                    commande.produits.add(produitCommande);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commandes;
    }

    public static Commande getCommande(String idCommande) throws InvalidArgumentException {
        List<String> validationMessages = new ArrayList<>();
        if (Strings.isNullOrEmpty(idCommande)) {
            validationMessages.add("L'ID de la commande ne peut être null ou vide");
        }
        if (validationMessages.size() > 0) {
            throw new InvalidArgumentException((String[]) validationMessages.toArray(new String[0]));
        }

        Connection connection = DBService.get().getConnection();
        try {
            // Récupère la Commande
            PreparedStatement preparedStatementCommande = connection.prepareStatement("SELECT * FROM Commande WHERE id = ?");
            preparedStatementCommande.setString(1, idCommande);
            ResultSet resultCommande = preparedStatementCommande.executeQuery();
            if (resultCommande.next()) {
                Commande commande = new Commande();
                commande.id = resultCommande.getString("id");
                Client client = new Client();
                commande.client = client;
                commande.date = new DateTime(resultCommande.getTimestamp("date"));
                commande.produits = new ArrayList<>();

                // Récupère le Client
                String idClient = resultCommande.getString("idClient");
                PreparedStatement preparedStatementClient = connection.prepareStatement("SELECT * FROM Client WHERE id = ?");
                preparedStatementClient.setString(1, idClient);
                ResultSet resultClient = preparedStatementClient.executeQuery();
                resultClient.next(); // toujours vrai
                client.email = resultClient.getString("email");
                client.motDePasse = resultClient.getString("motDePasse");
                client.id = resultClient.getString("id");
                client.nom = resultClient.getString("nom");
                client.prenom = resultClient.getString("prenom");
                client.adressePostale = resultClient.getString("adressePostale");
                client.telephone = resultClient.getString("telephone");
                client.isSupprime = resultClient.getBoolean("isSupprime");

                // Récupère la liste des produits pour la commande courante
                PreparedStatement preparedStatementProduitCom = connection.prepareStatement("SELECT * FROM ProduitCommande WHERE idCommande = ?");
                preparedStatementProduitCom.setString(1, commande.id);
                ResultSet resultProduitCom = preparedStatementProduitCom.executeQuery();

                while (resultProduitCom.next()) {
                    String produitId = resultProduitCom.getString("idProduit");
                    PreparedStatement preparedStatementProduit = connection.prepareStatement("SELECT * FROM Produit WHERE id = ?");
                    preparedStatementProduit.setString(1, produitId);
                    ResultSet resultProduit = preparedStatementProduit.executeQuery();

                    resultProduit.next(); // toujours vrai
                    Produit produit = new Produit();
                    produit.id = produitId;
                    produit.nom = resultProduit.getString("nom");
                    produit.description = resultProduit.getString("description");
                    produit.prixUnitaire = resultProduit.getFloat("prixUnitaire");
                    produit.isSupprime = resultProduit.getBoolean("isSupprime");

                    ProduitCommande produitCommande = new ProduitCommande(produit,
                            resultProduitCom.getInt("quantite"),
                            resultProduitCom.getFloat("prixUnitaire"));

                    commande.produits.add(produitCommande);
                }

                return commande;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void clear() {
        Connection connection = null;
        try {
            connection = DBService.get().getConnection();
            boolean temp = connection.getAutoCommit();
            connection.setAutoCommit(false);
            Statement requete = connection.createStatement();
            requete.executeUpdate("DELETE FROM ProduitCommande");
            requete.executeUpdate("DELETE FROM Commande");
            connection.commit();
            connection.setAutoCommit(temp);
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
}
