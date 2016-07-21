package model;

public class ProduitCommande {

    public Produit produit;
    public int quantite;
    public float prixUnitaire;
    public String idCommande;

    public ProduitCommande(Produit produit, int quantite, float prixUnitaire, String idCommande) {
        this.produit = produit;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.idCommande = idCommande;
    }
}
