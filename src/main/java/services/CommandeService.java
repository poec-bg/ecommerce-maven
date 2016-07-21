package services;

import exceptions.InvalidArgumentException;
import model.*;
import services.date.DateService;
import services.db.DBService;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringJoiner;

/**
 * Created by formation on 21/07/2016.
 */
public class CommandeService {

    public static Commande creerCommande(Panier panier) throws InvalidArgumentException {

        if (panier == null) {
            throw new exceptions.InvalidArgumentException(new String[]{"Le panier ne peut être null"});

        }
        Commande commande = new Commande();
        commande.client = panier.client;
        commande.date = DateService.get().now();
        commande.produits = new ArrayList<>();
        commande.montant=0;


        for (ProduitPanier produitPanier : panier.produits) {
            commande.produits.add(new ProduitCommande(produitPanier.produit,
                    produitPanier.quantite,
                    produitPanier.produit.prixUnitaire));
            commande.montant=commande.montant+produitPanier.quantite*produitPanier.produit.prixUnitaire;

        }


        return commande;}

    public void enregistrer(Commande commande) {
        try {
            PreparedStatement preparedStatement = DBService.get().getConnection().prepareStatement("INSERT INTO Produit (`id`, `date`, `idClient`, `montant`) VALUES (?, ? , ? , ? , ?)");
            preparedStatement.setString(1, commande.id);
            preparedStatement.setDate(2,new java.sql.Date(commande.date);
            preparedStatement.setString(3, commande.client.id);
            preparedStatement.setDouble(4, commande.montant);

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    }
