package services;

import model.Commande;

public class CommandeService {

    /**
     * Singleton
     */
    private static CommandeService instance;

    /**
     * Constructeur privé = personne ne peut faire de new ComandeService()
     */
    private CommandeService() {

    }

    /**
     * Seule méthode pour récupérer une instance (toujours la même) de CommandeService
     *
     * @return toujours la même instance de CommandeService
     */
    public static CommandeService get() {
        if (instance == null) {
            instance = new CommandeService();
        }
        return instance;
    }

    

}
