package model;

import model.types.EUserRole;

public class Client {

    public String id;
    public String email;
    public String motDePasse;
    public String nom;
    public String prenom;
    public String adressePostale;
    public String telephone;
    public EUserRole role;
    public boolean isSupprime = false;

}
