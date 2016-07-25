import exceptions.InvalidArgumentException;
import model.Produit;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import services.ProduitService;

import java.util.List;

import static org.junit.Assert.*;

public class ProduitServiceTest {

    @BeforeClass
    public static void avantClass() {
        System.out.println("Avant la classe Produit\n");
    }

    @Before
    public void avantToutTest() {
        ProduitService.get().clear();
    }

    // creer
    @Test
    public void testCreer_everythingWrong() {
        // Given
        String nom = null;
        String description = null;
        float prixUnitaire = -1;

        // When
        try {
            Produit produit = ProduitService.get().creer(nom, description, prixUnitaire);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(true);
        }
    }

    @Test(expected = InvalidArgumentException.class)
    public void testCreer_everythingWrongExpectedException() throws InvalidArgumentException {
        // Given
        String nom = null;
        String description = null;
        float prixUnitaire = -1;

        // When
        Produit produit = ProduitService.get().creer(nom, description, prixUnitaire);

        // Then
        fail();
    }

    @Test
    public void testCreer_nomVide() {
        // Given
        String nom = "";
        String description = null;
        float prixUnitaire = 5;

        // When
        try {
            Produit produit = ProduitService.get().creer(nom, description, prixUnitaire);

            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("Le nom ne peut être null ou vide"));
        }
    }

    @Test
    public void testCreer_OK() {
        // Given
        String nom = "Classeur";
        String description = "Super classeur pratique";
        float prixUnitaire = 5.75f;

        // When
        try {
            Produit produit = ProduitService.get().creer(nom, description, prixUnitaire);
            // Then
            assertEquals("Classeur", produit.nom);
        } catch (InvalidArgumentException e) {
            fail();
        }
    }
    // ----------------------

    // modifier
    @Test
    public void testModifier_everythingWrong() {
        // Given
        String nom = "Classeur";
        String description = "Super classeur pratique";
        float prixUnitaire = 5.75f;
        Produit produit = null;
        try {
            produit = ProduitService.get().creer(nom, description, prixUnitaire);
            ProduitService.get().enregistrer(produit);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        nom = null;
        description = null;
        prixUnitaire = -1;

        // When
        try {
            ProduitService.get().modifier(produit, nom, description, prixUnitaire);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(true);
        }
    }

    @Test
    public void testModifier_nomNull() {
        // Given
        Produit produit = new Produit();
        produit.nom = "Classeur";
        produit.description = "Super classeur";
        produit.prixUnitaire = 5;

        String nom = null;
        String description = null;
        float prixUnitaire = 1;

        // When
        try {
            ProduitService.get().modifier(produit, nom, description, prixUnitaire);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("Le nom ne peut être null ou vide"));
        }
    }

    @Test
    public void testModifier_everythingOk() {
        // Given
        Produit produit = new Produit();
        produit.nom = "Classeur";
        produit.description = "Super classeur";
        produit.prixUnitaire = 5;

        String nom = "Classeur Bleu";
        String description = null;
        float prixUnitaire = 5.5f;

        // When
        try {
            ProduitService.get().modifier(produit, nom, description, prixUnitaire);

            assertEquals(nom, produit.nom);
            assertEquals(description, produit.description);
            assertEquals(prixUnitaire, produit.prixUnitaire, 0);

        } catch (InvalidArgumentException e) {
            fail();
        }
    }
    // ----------------------

    // supprimer
    @Test
    public void testSupprimer_produitNull() {
        // Given
        Produit produit = null;

        // When
        try {
            ProduitService.get().supprimer(produit);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(true);
        }
    }

    @Test
    public void testSupprimer_produitOk() {
        // Given
        Produit produit = new Produit();
        produit.nom = "Classeur";
        produit.description = "Super classeur";
        produit.prixUnitaire = 5;

        // When
        try {
            ProduitService.get().supprimer(produit);

            // Then
            assertEquals(true, produit.isSupprime);

        } catch (InvalidArgumentException e) {
            fail();
        }
    }
    // ----------------------

    // lister
    @Test
    public void testLister_noProduct() {
        // Given

        // When
        List<Produit> produits = ProduitService.get().lister();

        // Then
        assertEquals(0, produits.size());

    }

    @Test
    public void testLister_twoProducts() throws InvalidArgumentException {
        // Given
        // enregistrer un premier Produit
        Produit produit1 = ProduitService.get().creer("Classeur", "", 5);
        ProduitService.get().enregistrer(produit1);
        // enregistrer un deuxième Produit
        Produit produit2 = ProduitService.get().creer("Intercalaire", "", 2.5f);
        ProduitService.get().enregistrer(produit2);

        // When
        List<Produit> produits = ProduitService.get().lister();

        // Then
        assertEquals(2, produits.size());
    }
    // ----------------------

    // ----------------------
    // getProduit
    @Test
    public void testGetProduit_everythingWrong() {
        // Given
        String idProduit = null;

        // When
        try {
            Produit produit = ProduitService.get().getProduit(idProduit);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("L'idProduit ne peut être null ou vide"));
        }
    }

    @Test
    public void testGetProduit_unknownIdProduit() {
        // Given
        Produit produit1 = null;
        try {
            produit1 = ProduitService.get().creer("Classeur", "", 5.5f);
            ProduitService.get().enregistrer(produit1);
        } catch (Exception e) {
            fail();
        }
        String idProduit = "unknown";

        // When
        try {
            Produit produit = ProduitService.get().getProduit(idProduit);
            // Then
            assertEquals(null, produit);
        } catch (InvalidArgumentException e) {
            fail();
        }
    }

    @Test
    public void testGetProduit_goodIdProduit() {
        // Given
        Produit produit1 = null;
        try {
            produit1 = ProduitService.get().creer("Classeur", "", 5.5f);
            ProduitService.get().enregistrer(produit1);
        } catch (Exception e) {
            fail();
        }
        String idProduit = produit1.id;

        // When
        try {
            Produit produit = ProduitService.get().getProduit(idProduit);
            // Then
            assertEquals(produit1.nom, produit.nom);
        } catch (InvalidArgumentException e) {
            fail();
        }
    }
    // ----------------------

}
