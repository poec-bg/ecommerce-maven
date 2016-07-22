package model;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Commande {

    public String id;
    public Client client;
    public DateTime date;
    public List<ProduitCommande> produits;
    public Float montant;

//    public void affiche() {
//        System.out.println(String.format("Date : %s \nmodel.Client : %s \nAdresse : %s", date, client.nom, client.adressePostale));
//        for (ProduitCommande produitCommande : produits) {
//            System.out.println(String.format("model.Produit : %s x %d : %f", produitCommande.produit.nom, produitCommande.quantite, produitCommande.quantite * produitCommande.prixUnitaire));
//        }
//    }

}
