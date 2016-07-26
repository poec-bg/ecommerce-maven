import exceptions.InvalidArgumentException;
import model.Client;
import model.Panier;
import model.Produit;
import model.ProduitPanier;
import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import services.ClientService;
import services.CommandeService;
import services.PanierService;
import services.ProduitService;
import services.date.DateService;
import services.date.FixedDateService;
import services.date.SystemDateService;

import static org.junit.Assert.*;

public class PanierServiceTest {

    Client client;
    @BeforeClass
    public static void avantClass() {
        System.out.println("Avant la classe PanierServiceTest\n");
    }

    @AfterClass
    public static void apresClasse() {
        CommandeService.get().clear();
        PanierService.get().clear();
        ClientService.get().clear();
        ProduitService.get().clear();
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
    }

    // getPanier
    @Test
    public void testGetPanier_everythingWrong() {
        // Given
        Client client1 = null;

        // When
        try {
            Panier panier = PanierService.get().getPanier(client1);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(true);
        }
    }

    @Test
    public void testGetPanier_clientWithoutPanier() {
        // Given
        ClientService.get().enregistrer(client);

        // When
        try {
            Panier panier = PanierService.get().getPanier(client);
            // Then
            assertEquals(client.email, panier.client.email);
        } catch (InvalidArgumentException e) {
            fail();
        }
    }

    @Test
    public void testGetPanier_clientWithPanier() {
        // Given
        ClientService.get().enregistrer(client);

        // When
        try {
            Panier panier = PanierService.get().getPanier(client);
            Panier panier2 = PanierService.get().getPanier(client);

            // Then
            assertEquals(panier.id, panier2.id);
        } catch (InvalidArgumentException e) {
            fail();
        }
    }
    // ----------------------

    // Gestion de la date de Panier
    @Test
    public void testDatePanier_dateMoins20Minutes() {
        // Given
        DateService.configureWith(new FixedDateService() {
            @Override
            public DateTime currentDateTime() {
                return DateTime.now().minusMinutes(20);
            }
        });
        ClientService.get().enregistrer(client);
        Panier oldPanier = null;
        Produit produit = null;
        try {
            oldPanier = PanierService.get().getPanier(client);
            produit = ProduitService.get().creer("Classeur", null, 5.5f);
            ProduitService.get().enregistrer(produit);
            oldPanier = PanierService.get().ajouterProduit(oldPanier, produit);
        } catch (Exception e) {
            fail();
        }

        System.out.println(oldPanier.date.toString("dd/MM/yyyy HH:mm:ss"));

        // When
        try {
            DateService.configureWith(new SystemDateService());
            Panier panier = PanierService.get().getPanier(client);
            System.out.println(panier.date.toString("dd/MM/yyyy HH:mm:ss"));

            // Then
            assertEquals(0, panier.produits.size());
            assertNotEquals(oldPanier.date.toString("dd/MM/yyyy HH:mm:ss"), panier.date.toString("dd/MM/yyyy HH:mm:ss"));

        } catch (InvalidArgumentException e) {
            fail();
        }
    }
    // ----------------------

    // invalider
    @Test
    public void testInvalider_everythingOk() {
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
            PanierService.get().invalider(panier);
            Panier newPanier = PanierService.get().getPanier(client);
            // Then
            assertNotEquals(panier, newPanier);
        } catch (InvalidArgumentException e) {
            fail();
        }
    }

    @Test
    public void testInvalider_everythingWrong() {
        // Given
        Panier panier = null;

        // When
        try {
            PanierService.get().invalider(panier);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("Le panier ne peut être null"));
        }
    }
    // ----------------------

    // ajouterProduit
    @Test
    public void testAjouterProduit_everythingWrong() {
        // Given
        Panier panier = null;
        Produit produit = null;

        // When
        try {
            panier = PanierService.get().ajouterProduit(panier, produit);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("Le panier ne peut être null"));
            assertTrue(e.getRealMessage().contains("Le produit ne peut être null"));
        }
    }

    @Test
    public void testAjouterProduit_everythingOk() {
        // Given
        ClientService.get().enregistrer(client);
        Panier panier = null;
        Produit produit = null;
        try {
            panier = PanierService.get().getPanier(client);
            produit = ProduitService.get().creer("Classeur", null, 5.5f);
            ProduitService.get().enregistrer(produit);
        } catch (Exception e) {
            fail();
        }

        // When
        try {
            panier = PanierService.get().ajouterProduit(panier, produit);
            // Then
            assertEquals(1, panier.produits.size());
        } catch (InvalidArgumentException e) {
            fail();
        } finally {
            try {
                PanierService.get().invalider(panier);
            } catch (InvalidArgumentException e) {
                fail();
            }
        }
    }

    @Test
    public void testAjouterProduit_alreadyAdded() {
        // Given
        ClientService.get().enregistrer(client);
        Panier panier = null;
        Produit produit = null;
        try {
            panier = PanierService.get().getPanier(client);
            produit = ProduitService.get().creer("Classeur", null, 5.5f);
            ProduitService.get().enregistrer(produit);
            panier = PanierService.get().ajouterProduit(panier, produit);
        } catch (Exception e) {
            fail();
        }

        // When
        try {
            panier = PanierService.get().ajouterProduit(panier, produit);
            // Then
            assertEquals(1, panier.produits.size());
        } catch (InvalidArgumentException e) {
            fail();
        } finally {
            try {
                PanierService.get().invalider(panier);
            } catch (InvalidArgumentException e) {
                fail();
            }
        }
    }
    // ----------------------

    // retirerProduit
    @Test
    public void testRetirerProduit_everythingWrong() {
        // Given
        Panier panier = null;
        Produit produit = null;

        // When
        try {
            panier = PanierService.get().retirerProduit(panier, produit);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("Le panier ne peut être null"));
            assertTrue(e.getRealMessage().contains("Le produit ne peut être null"));
        }
    }

    @Test
    public void testRetirerProduit_productNotFoundInPanier() {
        // Given
        ClientService.get().enregistrer(client);
        Panier panier = null;
        Produit produit = null;
        try {
            panier = PanierService.get().getPanier(client);
            produit = ProduitService.get().creer("Classeur", null, 5.5f);
            ProduitService.get().enregistrer(produit);
        } catch (Exception e) {
            fail();
        }

        // When
        try {
            panier = PanierService.get().retirerProduit(panier, produit);
            // Then
            assertEquals(0, panier.produits.size());
        } catch (InvalidArgumentException e) {
            fail();
        } finally {
            try {
                PanierService.get().invalider(panier);
            } catch (InvalidArgumentException e) {
                fail();
            }
        }
    }

    @Test
    public void testRetirerProduit_everythingOk() {
        // Given
        ClientService.get().enregistrer(client);
        Panier panier = null;
        Produit produit = null;
        try {
            panier = PanierService.get().getPanier(client);
            produit = ProduitService.get().creer("Classeur", null, 5.5f);
            ProduitService.get().enregistrer(produit);
            Produit produit2 = ProduitService.get().creer("Intercalaire", null, 2.75f);
            ProduitService.get().enregistrer(produit2);
            panier = PanierService.get().ajouterProduit(panier, produit);
            panier = PanierService.get().ajouterProduit(panier, produit2);
        } catch (Exception e) {
            fail();
        }

        // When
        try {
            panier = PanierService.get().retirerProduit(panier, produit);
            // Then
            assertEquals(1, panier.produits.size());
        } catch (InvalidArgumentException e) {
            fail();
        } finally {
            try {
                PanierService.get().invalider(panier);
            } catch (InvalidArgumentException e) {
                fail();
            }
        }
    }
    // ----------------------

    // modifierQuantite
    @Test
    public void testModifierQuantite_everythingWrong() {
        // Given
        Panier panier = null;
        Produit produit = null;
        int quantite = -1;

        // When
        try {
            panier = PanierService.get().modifierQuantite(panier, produit, quantite);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("Le panier ne peut être null"));
            assertTrue(e.getRealMessage().contains("Le produit ne peut être null"));
            assertTrue(e.getRealMessage().contains("La quantité ne peut être inférieur à 0"));
        }
    }

    @Test
    public void testModifierQuantite_quantityThree() {
        // Given
        ClientService.get().enregistrer(client);
        Panier panier = null;
        Produit produit = null;
        int quantite = 3;
        try {
            panier = PanierService.get().getPanier(client);
            produit = ProduitService.get().creer("Classeur", null, 5.5f);
            ProduitService.get().enregistrer(produit);
            panier = PanierService.get().ajouterProduit(panier, produit);
        } catch (Exception e) {
            fail();
        }

        // When
        try {
            panier = PanierService.get().modifierQuantite(panier, produit, quantite);

            // Then
            for (ProduitPanier produitPanier : panier.produits) {
                if (produitPanier.produit.id.equals(produit.id)) {
                    assertEquals(quantite, produitPanier.quantite);
                    break;
                }
            }
        } catch (InvalidArgumentException e) {
            fail();
        } finally {
            try {
                PanierService.get().invalider(panier);
            } catch (InvalidArgumentException e) {
                fail();
            }
        }
    }

    @Test
    public void testModifierQuantite_quantityZero() {
        // Given
        ClientService.get().enregistrer(client);
        Panier panier = null;
        Produit produit = null;
        int quantite = 0;
        try {
            panier = PanierService.get().getPanier(client);
            produit = ProduitService.get().creer("Classeur", null, 5.5f);
            ProduitService.get().enregistrer(produit);
            panier = PanierService.get().ajouterProduit(panier, produit);
        } catch (Exception e) {
            fail();
        }

        // When
        try {
            panier = PanierService.get().modifierQuantite(panier, produit, quantite);

            // Then
            for (ProduitPanier produitPanier : panier.produits) {
                if (produitPanier.produit.id.equals(produit.id)) {
                    fail();
                }
            }
        } catch (InvalidArgumentException e) {
            fail();
        } finally {
            try {
                PanierService.get().invalider(panier);
            } catch (InvalidArgumentException e) {
                fail();
            }
        }
    }
    // ----------------------
}
