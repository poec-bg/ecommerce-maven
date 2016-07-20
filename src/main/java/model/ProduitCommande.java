package model;

public class ProduitCommande {

    public Produit produit;
    public int quantite;
    public float prixUnitaire;

    public ProduitCommande(Produit produit, int quantite, float prixUnitaire) {
        this.produit = produit;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }
}
