package services;

import com.google.common.base.Strings;
import exceptions.InvalidArgumentException;
import model.*;
import org.joda.time.DateTime;
import services.date.DateService;
import services.db.DBService;

import java.sql.*;
import java.util.ArrayList;
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

    public Commande creerDepuisPanier(Panier panier) throws InvalidArgumentException {
        List<String> validationMessages = new ArrayList<>();
        if (panier == null) {
            validationMessages.add("Le panier ne peut être null");
        } else {
            if (panier.produits == null || panier.produits.size() == 0) {
                validationMessages.add("Impossible de créer une commande à partir d'un panier vide");
            }
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
            commande.montant += produitPanier.produit.prixUnitaire * produitPanier.quantite;
        }
        return commande;
    }

    public void enregistrer(Commande commande, Panier panier) throws InvalidArgumentException {
        List<String> validationMessages = new ArrayList<>();
        if (commande == null) {
            validationMessages.add("La commande ne peut être null");
        }
        if (panier == null) {
            validationMessages.add("Le panier ne peut être null");
        }
        if (validationMessages.size() > 0) {
            throw new InvalidArgumentException((String[]) validationMessages.toArray(new String[0]));
        }

        try {
            PreparedStatement preparedStatement = DBService.get().getConnection().prepareStatement("INSERT INTO Commande (`id`, `idClient`, `date`, `montant`) VALUES (?, ? , ? , ?)");
            preparedStatement.setString(1, commande.id);
            preparedStatement.setString(2, commande.client.id);
            preparedStatement.setTimestamp(3, new Timestamp(commande.date.toDate().getTime()));
            preparedStatement.setFloat(4, commande.montant);
            preparedStatement.execute();

            for (ProduitCommande produitCommande : commande.produits) {
                PreparedStatement preparedStatementProduitCommande = DBService.get().getConnection().prepareStatement("INSERT INTO ProduitCommande (`idCommande`, `idProduit`, `quantite`, `prixUnitaire`) VALUES (?, ? , ? , ?)");
                preparedStatementProduitCommande.setString(1, commande.id);
                preparedStatementProduitCommande.setString(2, produitCommande.produit.id);
                preparedStatementProduitCommande.setInt(3, produitCommande.quantite);
                preparedStatementProduitCommande.setFloat(4, produitCommande.prixUnitaire);
                preparedStatementProduitCommande.execute();
            }
            PanierService.get().invalider(panier);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Commande> lister() {
        List<Commande> commandes = new ArrayList<>();
        try {
            Statement requeteCommande = DBService.get().getConnection().createStatement();
            ResultSet resultCommande = requeteCommande.executeQuery("SELECT * FROM Commande");
            while (resultCommande.next()) {
                Commande commande = new Commande();
                commande.id = resultCommande.getString("id");
                commande.date = new DateTime(resultCommande.getTimestamp("date"));
                commande.client = ClientService.get().getClient(resultCommande.getString("idClient"));
                commande.montant = resultCommande.getFloat("montant");

                Statement requeteProduitCommande = DBService.get().getConnection().createStatement();
                ResultSet resultProduitPanier = requeteProduitCommande.executeQuery("SELECT * FROM ProduitCommande WHERE idCommande='" + commande.id + "'");
                while (resultProduitPanier.next()) {
                    String idProduit = resultProduitPanier.getString("idProduit");
                    Produit produit = ProduitService.get().getProduit(idProduit);
                    ProduitCommande produitCommande = new ProduitCommande(produit, resultProduitPanier.getInt("quantite"), resultProduitPanier.getFloat("prixUnitaire"));
                    commande.produits.add(produitCommande);
                }
                commandes.add(commande);
            }
        } catch (SQLException | InvalidArgumentException e) {
            e.printStackTrace();
        }
        return commandes;
    }

    public Commande getCommande(String idCommande) throws InvalidArgumentException {

        List<String> validationMessages = new ArrayList<>();
        if (Strings.isNullOrEmpty(idCommande)) {
            validationMessages.add("L'idCommande ne peut être null ou vide");
        }
        if (validationMessages.size() > 0) {
            throw new InvalidArgumentException((String[]) validationMessages.toArray(new String[0]));
        }

        try {
            Statement requeteCommande = DBService.get().getConnection().createStatement();
            ResultSet resultCommande = requeteCommande.executeQuery("SELECT * FROM Commande WHERE id='" + idCommande + "'");
            if (resultCommande.next()) {
                Commande commande = new Commande();
                commande.id = resultCommande.getString("id");
                commande.date = new DateTime(resultCommande.getTimestamp("date"));
                commande.client = ClientService.get().getClient(resultCommande.getString("idClient"));
                commande.montant = resultCommande.getFloat("montant");

                Statement requeteProduitCommande = DBService.get().getConnection().createStatement();
                ResultSet resultProduitPanier = requeteProduitCommande.executeQuery("SELECT * FROM ProduitCommande WHERE idCommande='" + commande.id + "'");
                while (resultProduitPanier.next()) {
                    String idProduit = resultProduitPanier.getString("idProduit");
                    Produit produit = ProduitService.get().getProduit(idProduit);
                    ProduitCommande produitCommande = new ProduitCommande(produit, resultProduitPanier.getInt("quantite"), resultProduitPanier.getFloat("prixUnitaire"));
                    commande.produits.add(produitCommande);
                }
                return commande;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void clear() {
        Connection connection = null;
        try {
            connection = DBService.get().getConnection();
            connection.setAutoCommit(false);
            Statement requete = connection.createStatement();
            requete.executeUpdate("DELETE FROM ProduitCommande");
            requete.executeUpdate("DELETE FROM Commande");
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
}
