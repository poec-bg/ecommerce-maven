import exceptions.InvalidArgumentException;
import exceptions.MetierException;
import model.Client;
import model.Commande;
import model.Panier;
import model.Produit;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import services.ClientService;
import services.CommandeService;
import services.PanierService;
import services.ProduitService;
import services.date.DateService;
import services.date.FixedDateService;
import services.date.SystemDateService;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by formation on 21/07/2016.
 */
public class CommandeServiceTest {

    @Before
    public void avantChaqueTest() {
        CommandeService.clear();
        ClientService.get().clear();
        ProduitService.get().clear();
        PanierService.get().clear();
        DateService.configureWith(new SystemDateService());
    }

    private Panier initPanierOk() throws MetierException, InvalidArgumentException {
        Client client = ClientService.get().creer("mail.test@gmail.com", "mdpTest");
        ClientService.get().modifier(client, "nonmTest", "prenomTest", "adrTest", "telTest");
        ClientService.get().enregistrer(client);
        Panier panier = PanierService.get().getPanier(client);
        Produit produit = ProduitService.get().creer("styloTest", "descTest", 1.0f);
        ProduitService.get().enregistrer(produit);
        panier = PanierService.get().ajouterProduit(panier, produit);
        return panier;
    }

    // ------------ creer ------------
    @Test
    public void testCreer_allNull() {
        // Given
        Panier panier = null;
        try {
            // When
            CommandeService.creer(panier);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("Le panier ne peut être null"));
        }
    }

    @Test
    public void testCreer_panierVide() throws MetierException, InvalidArgumentException {
        // Given
        Client client = ClientService.get().creer("mail.test@gmail.com", "mdpTest");
        ClientService.get().modifier(client, "nonmTest", "prenomTest", "adrTest", "telTest");
        ClientService.get().enregistrer(client);
        Panier panier = PanierService.get().getPanier(client);
        try {
            // When
            CommandeService.creer(panier);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("Le panier ne peut être vide"));
        }
    }

    @Test
    public void testCreer_infoClientNull() throws MetierException, InvalidArgumentException {
        // Given
        Client client = ClientService.get().creer("mail.test@gmail.com", "mdpTest");
        ClientService.get().modifier(client, null, null, null, "telTest");
        ClientService.get().enregistrer(client);
        Panier panier = PanierService.get().getPanier(client);
        Produit produit = ProduitService.get().creer("styloTest", "descTest", 1.0f);
        ProduitService.get().enregistrer(produit);
        panier = PanierService.get().ajouterProduit(panier, produit);
        try {
            // When
            CommandeService.creer(panier);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("Le nom du client ne peut être null ou vide"));
            assertTrue(e.getRealMessage().contains("Le prenom du client ne peut être null ou vide"));
//            assertTrue(e.getRealMessage().contains("L'adresse du client ne peut être null ou vide"));
        }
    }

    @Test
    public void testCreer_infoClientVide() throws MetierException, InvalidArgumentException {
        // Given
        Client client = ClientService.get().creer("mail.test@gmail.com", "mdpTest");
        ClientService.get().modifier(client, "", "", "", "telTest");
        ClientService.get().enregistrer(client);
        Panier panier = PanierService.get().getPanier(client);
        Produit produit = ProduitService.get().creer("styloTest", "descTest", 1.0f);
        ProduitService.get().enregistrer(produit);
        panier = PanierService.get().ajouterProduit(panier, produit);
        try {
            // When
            CommandeService.creer(panier);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("Le nom du client ne peut être null ou vide"));
            assertTrue(e.getRealMessage().contains("Le prenom du client ne peut être null ou vide"));
//            assertTrue(e.getRealMessage().contains("L'adresse du client ne peut être null ou vide"));
        }
    }

    @Test
    public void testCreer_allOk() throws MetierException, InvalidArgumentException {
        // Given
        DateService.configureWith(new FixedDateService() {
            @Override
            public DateTime currentDateTime() {
                return new DateTime(1945,06,18,10,25);
            }
        });
        Client client = ClientService.get().creer("mail.test@gmail.com", "mdpTest");
        ClientService.get().modifier(client, "nonmTest", "prenomTest", "adrTest", "telTest");
        ClientService.get().enregistrer(client);
        Panier panier = PanierService.get().getPanier(client);
        Produit produit = ProduitService.get().creer("styloTest", "descTest", 1.0f);
        ProduitService.get().enregistrer(produit);
        panier = PanierService.get().ajouterProduit(panier, produit);
        try {
            // When
            Commande commande = CommandeService.creer(panier);
            // Then
            assertEquals(client, commande.client);
            assertEquals(panier.produits.size(), commande.produits.size());
            assertEquals((new DateTime(1945,06,18,10,25)).toString(), commande.date.toString());
        } catch (InvalidArgumentException e) {
            fail();
        }
    }
    // -------------------------------

    // ------------ getCommande ------------
    @Test
    public void testGetCommande_allNull() {
        // Given
        String idCommande = null;
        try {
            // When
            CommandeService.getCommande(idCommande);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("L'ID de la commande ne peut être null ou vide"));
        }
    }

    @Test
    public void testGetCommande_unknownCommande() throws MetierException, InvalidArgumentException {
        // Given
        Commande commandeInit = CommandeService.creer(initPanierOk());
        CommandeService.enregistrer(commandeInit);
        String commandId = commandeInit.id + "isUnknown";

        try {
            // When
            Commande commande = CommandeService.getCommande(commandId);
            // Then
            assertEquals(null, commande);
        } catch (InvalidArgumentException e) {
            fail();
        }
    }

    @Test
    public void testGetCommande_allOk() throws MetierException, InvalidArgumentException {
        // Given
        Commande commandeInit = CommandeService.creer(initPanierOk());
        CommandeService.enregistrer(commandeInit);

        try {
            // When
            Commande commande = CommandeService.getCommande(commandeInit.id);
            // Then
            assertEquals(commandeInit.id, commande.id);
            assertEquals(commandeInit.produits.size(), commande.produits.size());
            // With MySQL v5.5.1, Date format do not store Milliseconds. Can be done with v5.6.0.
            // Date variables from Java are rounded to the nearest second.
            long dateMillisRounded = (long) (Math.round(commandeInit.date.toDate().getTime()/1000.0)*1000.0);
            assertEquals(new DateTime(dateMillisRounded).toString(), commande.date.toString());
        } catch (InvalidArgumentException e) {
            fail();
        }
    }
    // -------------------------------

    // ------------ lister ------------
    @Test
    public void testLister_allNull() {
        // Given
        Client client = null;
        try {
            // When
            List<Commande> commandes = CommandeService.lister(client);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("Le client ne peut être null"));
        }
    }

    @Test
    public void testLister_noCommande() throws InvalidArgumentException, MetierException {
        // Given
        Client client = ClientService.get().creer("mail.test@gmail.com", "mdpTest");
        try {
            // When
            List<Commande> commandes = CommandeService.lister(client);
            // Then
            assertEquals(0, commandes.size());
        } catch (InvalidArgumentException e) {
            fail();
        }
    }

    @Test
    public void testLister_twoCommandes() throws InvalidArgumentException, MetierException {
        // Given
        Client client = ClientService.get().creer("mail.test@gmail.com", "mdpTest");
        ClientService.get().modifier(client, "nonmTest", "prenomTest", "adrTest", "telTest");
        ClientService.get().enregistrer(client);
        // Commande 1
        Panier panier = PanierService.get().getPanier(client);
        Produit produit = ProduitService.get().creer("styloTest", "descTest", 1.0f);
        ProduitService.get().enregistrer(produit);
        panier = PanierService.get().ajouterProduit(panier, produit);
        Commande commande1 = CommandeService.creer(panier);
        CommandeService.enregistrerEtInvaldier(commande1, panier);
        // Commande 2
        panier = PanierService.get().getPanier(client);
        produit = ProduitService.get().creer("ballon", "jaune", 2.0f);
        ProduitService.get().enregistrer(produit);
        panier = PanierService.get().ajouterProduit(panier, produit);
        Commande commande2 = CommandeService.creer(panier);
        CommandeService.enregistrerEtInvaldier(commande2, panier);
        try {
            // When
            List<Commande> commandes = CommandeService.lister(client);
            // Then
            assertEquals(2, commandes.size());
            // Test client
            assertEquals(commande1.client, commandes.get(0).client);
            assertEquals(commande2.client, commandes.get(1).client);
            assertEquals(commandes.get(0).client, commandes.get(1).client);
            // Test produits
            assertEquals(commande1.produits.size(), commandes.get(0).produits.size());
            assertEquals(commande2.produits.size(), commandes.get(1).produits.size());
            assertEquals(commandes.get(0).produits.size(), commandes.get(1).produits.size());
        } catch (InvalidArgumentException e) {
            fail();
        }
    }
    // -------------------------------

    // ------------ enregistrerEtInvaldier ------------
    @Test
    public void testEnregistrerEtInvaldier_panierNull() throws MetierException, InvalidArgumentException {
        // Given
        Commande commande = CommandeService.creer(initPanierOk());
        try {
            // When
            CommandeService.enregistrerEtInvaldier(commande, null);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("Le panier ne peut être null"));
        }
    }

    @Test
    public void testEnregistrerEtInvaldier_commandeNull() throws MetierException, InvalidArgumentException {
        // Given
        Panier panier = initPanierOk();
        try {
            // When
            CommandeService.enregistrerEtInvaldier(null, panier);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("La commande ne peut être null"));
        }
    }
    @Test
    public void testEnregistrerEtInvaldier_allOk() throws MetierException, InvalidArgumentException {
        // Given
        Panier panierInit = initPanierOk();
        Commande commande = CommandeService.creer(panierInit);
        // When
        CommandeService.enregistrerEtInvaldier(commande, panierInit);
        Panier panier = PanierService.get().getPanier(commande.client);
        // Then
        assertNotEquals(panierInit, panier);
        assertNotEquals(panierInit.id, panier.id);
        assertEquals(0, panier.produits.size());
    }
    // -------------------------------
}
