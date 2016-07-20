package model;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class Panier {
    public String id;
    public Client client;
    public DateTime date;
    public List<ProduitPanier> produits;

    public Panier() {
        produits = new ArrayList<>();
    }
}
