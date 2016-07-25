package services;

import exceptions.InvalidArgumentException;
import model.Client;
import model.Panier;
import model.Produit;
import model.ProduitPanier;
import org.joda.time.DateTime;
import services.date.DateService;
import services.db.DBService;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PanierService {

    /**
     * Singleton
     */
    private static PanierService instance;

    /**
     * Constructeur privé = personne ne peut faire de new PanierService()
     */
    private PanierService() {

    }

    /**
     * Seule méthode pour récupérer une instance (toujours la même) de PanierService
     *
     * @return toujours la même instance de PanierService
     */
    public static PanierService get() {
        if (instance == null) {
            instance = new PanierService();
        }
        return instance;
    }

    /**
     * Si le panier n'existe pas alors on en crée un pour ce client
     *
     * @param client
     * @return
     */
    public Panier getPanier(Client client) throws InvalidArgumentException {
        List<String> validationMessages = new ArrayList<>();
        if (client == null) {
            validationMessages.add("Le client ne peut être null");
        }
        if (validationMessages.size() > 0) {
            throw new InvalidArgumentException((String[]) validationMessages.toArray(new String[0]));
        }

        try {
            Statement requetePanier = DBService.get().getConnection().createStatement();
            ResultSet resultPanier = requetePanier.executeQuery("SELECT * FROM Panier WHERE idClient='" + client.id + "'");
            if (resultPanier.next()) {
                Panier panier = new Panier();
                panier.id = resultPanier.getString("id");
                panier.client = ClientService.get().getClient(resultPanier.getString("idClient"));
                panier.date = new DateTime(resultPanier.getTimestamp("date"));
                panier.produits = new ArrayList<>();

                if (panier.date.isBefore(DateService.get().now().minusMinutes(15))) {
                    invalider(panier);
                } else {
                    Statement requeteProduitPanier = DBService.get().getConnection().createStatement();
                    ResultSet resultProduitPanier = requeteProduitPanier.executeQuery("SELECT * FROM ProduitPanier WHERE idPanier='" + panier.id + "'");
                    while (resultProduitPanier.next()) {
                        String idProduit = resultProduitPanier.getString("idProduit");
                        Produit produit = ProduitService.get().getProduit(idProduit);
                        ProduitPanier produitPanier = new ProduitPanier(produit, resultProduitPanier.getInt("quantite"));
                        panier.produits.add(produitPanier);
                    }
                    return panier;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return creer(client);
    }

    public void invalider(Panier panier) throws InvalidArgumentException {
        List<String> validationMessages = new ArrayList<>();
        if (panier == null) {
            validationMessages.add("Le panier ne peut être null");
        }
        if (validationMessages.size() > 0) {
            throw new InvalidArgumentException((String[]) validationMessages.toArray(new String[0]));
        }

        try {
            Statement requete = DBService.get().getConnection().createStatement();
            requete.executeUpdate("DELETE FROM ProduitPanier WHERE idPanier = '" + panier.id + "'");
            requete.executeUpdate("DELETE FROM Panier WHERE id = '" + panier.id + "'");
            panier = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Panier ajouterProduit(Panier panier, Produit produit) throws InvalidArgumentException {
        List<String> validationMessages = new ArrayList<>();
        if (panier == null) {
            validationMessages.add("Le panier ne peut être null");
        }
        if (produit == null) {
            validationMessages.add("Le produit ne peut être null");
        }
        if (validationMessages.size() > 0) {
            throw new InvalidArgumentException((String[]) validationMessages.toArray(new String[0]));
        }

        boolean founded = false;
        for (ProduitPanier produitPanier : panier.produits) {
            if (produitPanier.produit.id.equals(produit.id)) {
                modifierQuantite(panier, produit, produitPanier.quantite + 1);
                founded = true;
            }
        }

        if (!founded) {
            panier.produits.add(new ProduitPanier(produit, 1));
            try {
                PreparedStatement preparedStatement = DBService.get().getConnection().prepareStatement("INSERT INTO ProduitPanier (`idPanier`, `idProduit`, `quantite`) VALUES (?, ? , ?)");
                preparedStatement.setString(1, panier.id);
                preparedStatement.setString(2, produit.id);
                preparedStatement.setInt(3, 1);
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return getPanier(panier.client);
    }

    public Panier retirerProduit(Panier panier, Produit produit) throws InvalidArgumentException {
        List<String> validationMessages = new ArrayList<>();
        if (panier == null) {
            validationMessages.add("Le panier ne peut être null");
        }
        if (produit == null) {
            validationMessages.add("Le produit ne peut être null");
        }
        if (validationMessages.size() > 0) {
            throw new InvalidArgumentException((String[]) validationMessages.toArray(new String[0]));
        }

        try {
            Statement requete = DBService.get().getConnection().createStatement();
            requete.executeUpdate("DELETE FROM ProduitPanier WHERE idPanier='" + panier.id + "' AND idProduit='" + produit.id + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return getPanier(panier.client);
    }

    public Panier modifierQuantite(Panier panier, Produit produit, int quantite) throws InvalidArgumentException {
        List<String> validationMessages = new ArrayList<>();
        if (panier == null) {
            validationMessages.add("Le panier ne peut être null");
        }
        if (produit == null) {
            validationMessages.add("Le produit ne peut être null");
        }
        if (quantite < 0) {
            validationMessages.add("La quantité ne peut être inférieur à 0");
        }
        if (validationMessages.size() > 0) {
            throw new InvalidArgumentException((String[]) validationMessages.toArray(new String[0]));
        }


        if (quantite == 0) {
            return retirerProduit(panier, produit);
        } else {
            try {
                Statement requete = DBService.get().getConnection().createStatement();
                requete.executeUpdate("UPDATE ProduitPanier SET quantite=" + quantite + " WHERE idPanier='" + panier.id + "' AND idProduit='" + produit.id + "'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return getPanier(panier.client);
        }
    }

    private Panier creer(Client client) {
        Panier panier = new Panier();
        panier.id = UUID.randomUUID().toString();
        panier.client = client;
        panier.date = DateService.get().now();

        try {
            PreparedStatement preparedStatement = DBService.get().getConnection().prepareStatement("INSERT INTO Panier (`id`, `idClient`, `date`) VALUES (?, ? , ?)");
            preparedStatement.setString(1, panier.id);
            preparedStatement.setString(2, panier.client.id);
            preparedStatement.setTimestamp(3, new Timestamp(panier.date.toDate().getTime()));

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return panier;
    }

    public void clear() {
        Connection connection = null;
        try {
            connection = DBService.get().getConnection();
            connection.setAutoCommit(false);
            Statement requete = connection.createStatement();
            requete.executeUpdate("DELETE FROM ProduitPanier");
            requete.executeUpdate("DELETE FROM Panier");
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
