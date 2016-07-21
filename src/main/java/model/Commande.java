package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Commande {

    public int id;
    public Client client;
    public Date date;
    public List<ProduitCommande> produits;

    public void affiche() {
        System.out.println(String.format("Date : %s \nmodel.Client : %s \nAdresse : %s", date, client.nom, client.adressePostale));
        for (ProduitCommande produitCommande : produits) {
            System.out.println(String.format("model.Produit : %s x %d : %f", produitCommande.produit.nom, produitCommande.quantite, produitCommande.quantite * produitCommande.prixUnitaire));
        }
    }
}
