package model;

import org.joda.time.DateTime;
import sun.misc.Cleaner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Commande {

    public String id;
    public DateTime date;
    public Client client;
    public float montantHT;
    public List<ProduitPanier> produits; // = new ArrayList<>();
}
