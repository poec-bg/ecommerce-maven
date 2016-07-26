package services;

import com.google.common.base.Strings;
import exceptions.InvalidArgumentException;
import model.Client;
import model.Produit;
import services.db.DBService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProduitService {

    /**
     * Singleton
     */
    private static ProduitService instance;

    /**
     * Constructeur privé = personne ne peut faire de new ProduitService()
     */
    private ProduitService() {
    }

    /**
     * Seule méthode pour récupérer une instance (toujours la même) de ProduitService
     *
     * @return toujours la même instance de ProduitService
     */
    public static ProduitService get() {
        if (instance == null) {
            instance = new ProduitService();
        }
        return instance;
    }

    public Produit creer(String nom, String description, float prixUnitaire) throws InvalidArgumentException {

        List<String> validationMessages = new ArrayList<>();
        if (Strings.isNullOrEmpty(nom)) {
            validationMessages.add("Le nom ne peut être null ou vide");
        }
        if (prixUnitaire < 0) {
            validationMessages.add("Le prix unitaire ne peut être inférieur à 0");
        }
        if (validationMessages.size() > 0) {
            throw new InvalidArgumentException((String[]) validationMessages.toArray(new String[0]));
        }

        Produit produit = new Produit();
        produit.id = UUID.randomUUID().toString();
        produit.nom = nom;
        produit.description = description;
        produit.prixUnitaire = prixUnitaire;
        return produit;
    }

    public void modifier(Produit produit, String nom, String description, float prixUnitaire) throws InvalidArgumentException {

        List<String> validationMessages = new ArrayList<>();
        if (produit == null) {
            validationMessages.add("Le produit ne peut être null");
        }
        if (Strings.isNullOrEmpty(nom)) {
            validationMessages.add("Le nom ne peut être null ou vide");
        }
        if (prixUnitaire < 0) {
            validationMessages.add("Le prix unitaire ne peut être inférieur à 0");
        }
        if (validationMessages.size() > 0) {
            throw new InvalidArgumentException((String[]) validationMessages.toArray(new String[0]));
        }

        produit.nom = nom;
        produit.description = description;
        produit.prixUnitaire = prixUnitaire;
    }

    public void supprimer(Produit produit) throws InvalidArgumentException {
        if (produit == null) {
            throw new InvalidArgumentException(new String[]{"Le produit ne peut être null"});
        }
        produit.isSupprime = true;
    }

    public void categoriser(Produit produit, String categorie) {
        System.out.println("categoriser");
    }

    public List<Produit> lister() {
        List<Produit> produitsDb = new ArrayList<>();
        try {
            Statement requete = DBService.get().getConnection().createStatement();
            ResultSet result = requete.executeQuery("SELECT * FROM Produit ORDER BY nom");
            while (result.next()) {
                Produit produit = new Produit();
                produit.id = result.getString("id");
                produit.nom = result.getString("nom");
                produit.description = result.getString("description");
                produit.prixUnitaire = result.getFloat("prixUnitaire");
                produit.isSupprime = result.getBoolean("isSupprime");
                produitsDb.add(produit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produitsDb;
    }

    public void enregistrer(Produit produit) {
        try {
            PreparedStatement preparedStatement = DBService.get().getConnection().prepareStatement("INSERT INTO Produit (`id`, `nom`, `description`, `prixUnitaire`, `isSupprime`) VALUES (?, ? , ? , ? , ?)");
            preparedStatement.setString(1, produit.id);
            preparedStatement.setString(2, produit.nom);
            preparedStatement.setString(3, produit.description);
            preparedStatement.setFloat(4, produit.prixUnitaire);
            preparedStatement.setBoolean(5, produit.isSupprime);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Produit getProduit(String idProduit) throws InvalidArgumentException {

        List<String> validationMessages = new ArrayList<>();
        if (Strings.isNullOrEmpty(idProduit)) {
            validationMessages.add("L'idProduit ne peut être null ou vide");
        }
        if (validationMessages.size() > 0) {
            throw new InvalidArgumentException((String[]) validationMessages.toArray(new String[0]));
        }

        try {
            Statement requete = DBService.get().getConnection().createStatement();
            ResultSet result = requete.executeQuery("SELECT * FROM Produit WHERE id='" + idProduit + "'");
            if(result.next()) {
                Produit produit = new Produit();
                produit.id = result.getString("id");
                produit.nom = result.getString("nom");
                produit.description = result.getString("description");
                produit.prixUnitaire = result.getFloat("prixUnitaire");
                produit.isSupprime = result.getBoolean("isSupprime");
                return produit;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clear() {
        try {
            Statement requete = DBService.get().getConnection().createStatement();
            requete.executeUpdate("DELETE FROM Produit");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
