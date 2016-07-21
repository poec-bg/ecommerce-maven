package services;

import model.Commande;
import model.Panier;
import model.ProduitCommande;
import model.ProduitPanier;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by formation on 21/07/2016.
 */
public class CommandeService {

    public static Commande creerCommande(Panier panier) {
        Commande commande = new Commande();
        commande.client = panier.client;
        commande.date = new Date();
        commande.produits = new ArrayList<>();
        for (ProduitPanier produitPanier : panier.produits) {
            commande.produits.add(new ProduitCommande(produitPanier.produit,
                    produitPanier.quantite,
                    produitPanier.produit.prixUnitaire));
        }

        return commande;
    }


}
