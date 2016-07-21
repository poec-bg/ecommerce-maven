/**
 * Created by formation on 21/07/2016.
 */
import exceptions.InvalidArgumentException;
import model.Commande;
import model.Panier;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import services.CommandeService;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CommandeServiceTest {

    @Test
    public void creerUneCommandePanierNull(){
       //Given
        Panier panier=null;
        Commande commande=null;
        //When
        try {
            commande = CommandeService.creerCommande(panier);

            fail();

        } catch (exceptions.InvalidArgumentException e) {
            //Then
            assertTrue(e.getRealMessage().contains("Le panier ne peut être null"));
        }


    }


}
