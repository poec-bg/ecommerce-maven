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

    private static CommandeService instance;

    public static CommandeService get (){
        if(instance == null){
            instance = new CommandeService();
        }
        return instance;
    }
    public static Commande creer(Panier panier) {
        Commande commande = new Commande();
        commande.client = panier.client;
        commande.date = new Date();
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


}
