import exceptions.InvalidArgumentException;
import model.Client;
import model.Commande;
import model.Panier;
import model.Produit;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import services.ClientService;
import services.CommandeService;
import services.PanierService;
import services.ProduitService;

import java.util.List;

import static org.junit.Assert.*;

public class CommandeServiceTest {

    Client client;

    @AfterClass
    public static void apresClasse() {
        CommandeService.get().clear();
    }

    @Before
    public void avantToutTest() {
        ClientService.get().clear();
        try {
            client = ClientService.get().creer("luke.skywalker@gmail.com", "iamyourfather");
            ClientService.get().modifier(client, "Skywalker", "Luke", "2 rue de Mos Eisley, Tatooine", "0123456789");
        } catch (Exception e) {
            fail();
        }
        ProduitService.get().clear();
        PanierService.get().clear();
        CommandeService.get().clear();
    }

    // creerDepuisPanier
    @Test
    public void testCreerDepuisPanier_everythingWrong() {
        // Given
        Panier panier = null;

        // When
        try {
            Commande commande = CommandeService.get().creerDepuisPanier(panier);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("Le panier ne peut être null"));
        }
    }

    @Test
    public void testCreerDepuisPanier_PanierVide() {
        // Given
        Panier panier = null;
        try {
            ClientService.get().enregistrer(client);
            panier = PanierService.get().getPanier(client);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        // When
        try {
            Commande commande = CommandeService.get().creerDepuisPanier(panier);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("Impossible de créer une commande à partir d'un panier vide"));
        }
    }

    @Test
    public void testCreerDepuisPanier_everythingOk() {
        // Given
        Panier panier = null;
        Produit produit = null;
        try {
            ClientService.get().enregistrer(client);
            panier = PanierService.get().getPanier(client);
            produit = ProduitService.get().creer("Classeur", null, 5.5f);
            ProduitService.get().enregistrer(produit);
            panier = PanierService.get().ajouterProduit(panier, produit);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        // When
        try {
            Commande commande = CommandeService.get().creerDepuisPanier(panier);
            // Then
            assertEquals(produit.prixUnitaire, commande.montant, 2);
        } catch (InvalidArgumentException e) {
            fail();
        }
    }

    @Test
    public void testCreerDepuisPanier_deuxProduitsEtQuantite() {
        // Given
        Panier panier = null;
        Produit produit1 = null;
        Produit produit2 = null;
        int quantiteProduit2 = 3;
        float montant = 0f;
        try {
            ClientService.get().enregistrer(client);
            panier = PanierService.get().getPanier(client);
            produit1 = ProduitService.get().creer("Classeur", null, 5.5f);
            ProduitService.get().enregistrer(produit1);
            panier = PanierService.get().ajouterProduit(panier, produit1);
            produit2 = ProduitService.get().creer("Intercalaire", null, 2.75f);
            ProduitService.get().enregistrer(produit2);
            panier = PanierService.get().ajouterProduit(panier, produit2);
            panier = PanierService.get().modifierQuantite(panier, produit2, quantiteProduit2);
            montant = produit1.prixUnitaire + produit2.prixUnitaire * quantiteProduit2;
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        // When
        try {
            Commande commande = CommandeService.get().creerDepuisPanier(panier);
            // Then
            assertEquals(montant, commande.montant, 2);
        } catch (InvalidArgumentException e) {
            fail();
        }
    }
    // ----------------------

    // enregistrer
    @Test
    public void testEnregistrer_everythingWrong() {
        // Given
        Commande commande = null;
        Panier panier = null;

        // When
        try {
            CommandeService.get().enregistrer(commande, panier);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("La commande ne peut être null"));
        }
    }

    @Test
    public void testEnregistrer_everythingOk() {
        // Given
        Panier panier = null;
        Produit produit = null;
        Commande commande = null;
        try {
            ClientService.get().enregistrer(client);
            panier = PanierService.get().getPanier(client);
            produit = ProduitService.get().creer("Classeur", null, 5.5f);
            ProduitService.get().enregistrer(produit);
            panier = PanierService.get().ajouterProduit(panier, produit);
            commande = CommandeService.get().creerDepuisPanier(panier);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        // When
        try {
            CommandeService.get().enregistrer(commande, panier);

            // Then
            assertTrue(true);
        } catch (InvalidArgumentException e) {
            fail();
        }
    }
    // ----------------------

    // lister
    @Test
    public void testLister_noCommande() {
        // Given

        // When
        List<Commande> commandes = CommandeService.get().lister();

        // Then
        assertEquals(0, commandes.size());

    }

    @Test
    public void testLister_twoCommandes() throws Exception {
        // Given
        Panier panier1 = null;
        Panier panier2 = null;
        Produit produit = null;
        Commande commande1 = null;
        Commande commande2 = null;
        try {
            ClientService.get().enregistrer(client);
            panier1 = PanierService.get().getPanier(client);
            produit = ProduitService.get().creer("Classeur", null, 5.5f);
            ProduitService.get().enregistrer(produit);
            panier1 = PanierService.get().ajouterProduit(panier1, produit);
            commande1 = CommandeService.get().creerDepuisPanier(panier1);
            CommandeService.get().enregistrer(commande1, panier1);
            panier2 = PanierService.get().getPanier(client);
            panier2 = PanierService.get().ajouterProduit(panier2, produit);
            commande2 = CommandeService.get().creerDepuisPanier(panier2);
            CommandeService.get().enregistrer(commande2, panier2);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        // When
        List<Commande> commandes = CommandeService.get().lister();

        // Then
        assertEquals(2, commandes.size());
    }
    // ----------------------

    // getCommande
    @Test
    public void testGetCommande_everythingOk() {
        // Given
        Panier panier = null;
        Produit produit = null;
        Commande commande = null;
        try {
            ClientService.get().enregistrer(client);
            panier = PanierService.get().getPanier(client);
            produit = ProduitService.get().creer("Classeur", null, 5.5f);
            ProduitService.get().enregistrer(produit);
            panier = PanierService.get().ajouterProduit(panier, produit);
            commande = CommandeService.get().creerDepuisPanier(panier);
            CommandeService.get().enregistrer(commande, panier);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        // When
        try {
            Commande commandeDB = CommandeService.get().getCommande(commande.id);

            // Then
            assertEquals(commande.id, commandeDB.id);
            assertEquals(commande.client.id, commandeDB.client.id);
            assertEquals(commande.produits.size(), commandeDB.produits.size());
        } catch (InvalidArgumentException e) {
            fail();
        }
    }
    // ----------------------
}
