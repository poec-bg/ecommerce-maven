package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Commande {

    public int id;
    public Client client;
    public Date date;
    public List<ProduitCommande> produits;

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

    public void affiche() {
        System.out.println(String.format("Date : %s \nmodel.Client : %s \nAdresse : %s", date, client.nom, client.adressePostale));
        for (ProduitCommande produitCommande : produits) {
            System.out.println(String.format("model.Produit : %s x %d : %f", produitCommande.produit.nom, produitCommande.quantite, produitCommande.quantite * produitCommande.prixUnitaire));
        }
    }
}
