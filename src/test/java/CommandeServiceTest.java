import exceptions.InvalidArgumentException;
import exceptions.MetierException;
import model.Commande;
import model.Panier;
import model.Produit;
import org.junit.Test;
import services.ClientService;
import services.CommandeService;
import services.PanierService;
import services.ProduitService;
import services.date.DateService;

import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by formation on 21/07/2016.
 */
public class CommandeServiceTest {

    @Test
    public void testCreer_panierNull(){
        // Given
        Panier panier = null;

        // When
        try {
            Commande commande = CommandeService.get().creer(panier);
            fail();
        } catch (InvalidArgumentException e) {
            assertTrue(true);
        } catch (MetierException e) {
            fail();
        }
    }

    @Test
    public void testCreer_PanierOK()throws exceptions.InvalidArgumentException, MetierException{
        // Given
        Panier panier = new Panier();
        panier.id = UUID.randomUUID().toString();
        panier.client = ClientService.get().creer("lenaickchartrain@gmail.com","lenaick");
        Produit produit1 = ProduitService.get().creer("Classeur","Joli Classeur",5.5f);
        panier = PanierService.get().ajouterProduit(panier,produit1);

        // When
        try {
            Commande commande = CommandeService.get().creer(panier);
            assertTrue(true);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
            fail();
        } catch (MetierException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetCommande_idCommandeVide() {
        // Given
        String idCommande = "";
        // When
        try {
            Commande commande = CommandeService.get().getCommande("");
            fail();
        } catch (InvalidArgumentException e) {
            assertTrue(true);
        }
    }
    @Test
    public void testGetCommande_idCommandeNull() {
        // Given
        String idCommande = null;
        // When
        try {
            Commande commande = CommandeService.get().getCommande("");
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(true);
        }
    }

    @Test
    public void testEnregistrer_CommandeNull(){
        // Given
        Commande commande = null;

        // When
        try {
            CommandeService.get().enregistrer(commande);
            fail();
        } catch (InvalidArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testEnregistrer_CommandeOK() throws InvalidArgumentException, MetierException, exceptions.InvalidArgumentException{
        // Given
        Panier panier = new Panier();
        panier.id = UUID.randomUUID().toString();
        panier.client = ClientService.get().creer("lenaickchartrain@gmail.com","lenaick");
        Produit produit1 = ProduitService.get().creer("Classeur","Joli Classeur",5.5f);
        panier = PanierService.get().ajouterProduit(panier,produit1);
        Commande commande = CommandeService.get().creer(panier);
    }
}
