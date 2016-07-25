package model;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class Commande {

    public String id;
    public Client client;
    public DateTime date;
    public List<ProduitCommande> produits;
    public float montant;

    public Commande() {
        produits = new ArrayList<>();
    }

}
