package model.types;

public enum EUserRole {

    ADMIN("Administrateur"),CLIENT("Client Normal");

    private String label;

    EUserRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
