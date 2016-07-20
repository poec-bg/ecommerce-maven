import exceptions.InvalidArgumentException;
import model.Client;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import services.ClientService;

import java.util.List;

import static org.junit.Assert.*;

public class ClientServiceTest {

    Client client;

    @BeforeClass
    public static void avantClass() {
        System.out.println("Avant la classe Client\n");
    }

    @Before
    public void avantToutTest() {
        ClientService.get().clear();
        try {
            client = ClientService.get().creer("luke.skywalker@gmail.com", "iamyourfather");
            ClientService.get().modifier(client, "Skywalker","Luke", "2 rue de Mos Eisley, Tatooine", "0123456789");
        } catch (Exception e) {
            fail();
        }
    }

    // creer
    @Test
    public void testCreer_everythingWrong() {
        // Given
        String email = null;
        String password = null;

        // When
        try {
            Client client = ClientService.get().creer(email, password);
            fail();
        } catch (Exception e) {
            // Then
            assertTrue(true);
        }
    }

    @Test
    public void testCreer_emailPasswordVide() {
        // Given
        String email = "";
        String password = "";

        // When
        try {
            Client client = ClientService.get().creer(email, password);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("Le email ne peut être null ou vide"));
            assertTrue(e.getRealMessage().contains("Le motDePasse ne peut être null ou vide"));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCreer_wrongEmail() {
        // Given
        String email = "luke.skywalker";
        String password = "iamyourfather";

        // When
        try {
            Client client = ClientService.get().creer(email, password);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("Le format de l'email est invalide"));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCreer_OK() {
        // Given
        String email = "anakin.skywalker@gmail.com";
        String password = "iamyourfather";

        // When
        try {
            Client client = ClientService.get().creer(email, password);
            // Then
            assertEquals("anakin.skywalker@gmail.com", client.email);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCreer_alreadyExists() {
        // Given
        ClientService.get().enregistrer(client);

        // When
        try {
            Client client2 = ClientService.get().creer(client.email, client.motDePasse);
            fail();
        } catch (Exception e) {
            // Then
            assertTrue(true);
        }
    }
    // ----------------------

    // modifier
    @Test
    public void testModifier_clientNull() {
        // Given
        Client client = null;
        String nom = null;
        String prenom = null;
        String adressePostale = null;
        String telephone = null;

        // When
        try {
            ClientService.get().modifier(client, nom, prenom, adressePostale, telephone);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("Le client ne peut être null"));
        }
    }

    @Test
    public void testModifier_everythingNull() {
        // Given
        String nom = null;
        String prenom = null;
        String adressePostale = null;
        String telephone = null;

        // When
        try {
            ClientService.get().modifier(client, nom, prenom, adressePostale, telephone);

            // Then
            assertEquals("Skywalker", client.nom);
        } catch (InvalidArgumentException e) {
            fail();
        }
    }

    @Test
    public void testModifier_OK() {
        // Given
        String nom = null;
        String prenom = "Anakin Junior";
        String adressePostale = null;
        String telephone = null;

        // When
        try {
            ClientService.get().modifier(client, nom, prenom, adressePostale, telephone);

            // Then
            assertEquals("Anakin Junior", client.prenom);
        } catch (InvalidArgumentException e) {
            fail();
        }

    }
    // ----------------------

    // supprimer
    @Test
    public void testSupprimer_clientNull() {
        // Given
        Client client1 = null;

        // When
        try {
            ClientService.get().supprimer(client1);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(true);
        }
    }

    @Test
    public void testSupprimer_clientOk() {
        // Given

        // When
        try {
            ClientService.get().supprimer(client);

            // Then
            assertEquals(true, client.isSupprime);

        } catch (InvalidArgumentException e) {
            fail();
        }
    }
    // ----------------------

    // lister
    @Test
    public void testLister_noClient() {
        // Given

        // When
        List<Client> clients = ClientService.get().lister();

        // Then
        assertEquals(0, clients.size());

    }

    @Test
    public void testLister_twoClients() throws Exception {
        // Given
        // enregistrer un premier Client
        ClientService.get().enregistrer(client);
        // enregistrer un deuxième Client
        Client client2 = ClientService.get().creer("han.solo@gmail.com", "0123456789");
        ClientService.get().enregistrer(client2);

        // When
        List<Client> clients = ClientService.get().lister();

        // Then
        assertEquals(2, clients.size());
    }
    // ----------------------

    // fusionner
    @Test
    public void testFusionner_noClients() {
        // Given
        Client client1 = null;
        Client client2 = null;

        // When
        try {
            Client client = ClientService.get().fusionner(client1, client2);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("Le client1 ne peut être null"));
            assertTrue(e.getRealMessage().contains("Le client2 ne peut être null"));
        }
    }

    @Test
    public void testFusionner_client1OKClient2OK() {
        // Given
        Client client2 = null;
        try {
            client2 = ClientService.get().creer("han.solo@gmail.com", "0123456789");
            ClientService.get().modifier(client2, "Solo", "Han", null, null);
        } catch (Exception e) {
            fail();
        }

        // When
        try {
            Client clientFusionne = ClientService.get().fusionner(client2, client);

            assertEquals(client2.nom, clientFusionne.nom);
            assertEquals(client2.prenom, clientFusionne.prenom);
            assertEquals(client.adressePostale, clientFusionne.adressePostale);
            assertEquals(client.telephone, clientFusionne.telephone);
        } catch (InvalidArgumentException e) {
            fail();
        }

        // Then
    }

    // ----------------------
    // authenticate
    @Test
    public void testAuthenticate_everythingWrong() {
        // Given
        String email = null;
        String motDePasse = null;

        // When
        try {
            boolean isAuthenticate = ClientService.get().authenticate(email, motDePasse);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("Le email ne peut être null ou vide"));
            assertTrue(e.getRealMessage().contains("Le motDePasse ne peut être null ou vide"));
        }
    }

    @Test
    public void testAuthenticate_wrongLogin() {
        // Given
        ClientService.get().enregistrer(client);
        String email = "wrongEmail@gmail.com";
        String motDePasse = "0123456789";

        // When
        try {
            boolean isAuthenticate = ClientService.get().authenticate(email, motDePasse);
            assertFalse(isAuthenticate);
        } catch (InvalidArgumentException e) {
            // Then
            fail();
        }
    }

    @Test
    public void testAuthenticate_goodLogin() {
        // Given
        ClientService.get().enregistrer(client);

        // When
        try {
            boolean isAuthenticate = ClientService.get().authenticate(client.email, client.motDePasse);
            assertTrue(isAuthenticate);
        } catch (InvalidArgumentException e) {
            // Then
            fail();
        }
    }
    // ----------------------

    // ----------------------
    // getClient
    @Test
    public void testGetClient_everythingWrong() {
        // Given
        String idClient = null;

        // When
        try {
            Client client = ClientService.get().getClient(idClient);
            fail();
        } catch (InvalidArgumentException e) {
            // Then
            assertTrue(e.getRealMessage().contains("L'idClient ne peut être null ou vide"));
        }
    }

    @Test
    public void testGetClient_unknownIdClient() {
        // Given
        ClientService.get().enregistrer(client);
        String idClient = "unknown";

        // When
        try {
            Client client = ClientService.get().getClient(idClient);
            // Then
            assertEquals(null, client);
        } catch (InvalidArgumentException e) {
            fail();
        }
    }

    @Test
    public void testGetClient_goodIdClient() {
        // Given
        ClientService.get().enregistrer(client);
        String idClient = client.id;

        // When
        try {
            Client client1 = ClientService.get().getClient(idClient);
            // Then
            assertEquals(client.nom, client1.nom);
        } catch (InvalidArgumentException e) {
            fail();
        }
    }
    // ----------------------

}
