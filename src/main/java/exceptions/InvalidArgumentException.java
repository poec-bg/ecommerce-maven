package exceptions;


public class InvalidArgumentException extends Exception {

    private String[] _arguments;

    public InvalidArgumentException(String[] var1) {
        this._arguments = var1;
    }

    public String getRealMessage() {
        StringBuffer var1 = new StringBuffer("{");

        for(int var2 = 0; var2 < this._arguments.length; ++var2) {
            var1.append(this._arguments[var2]);
            if(var2 < this._arguments.length - 1) {
                var1.append(", ");
            }
        }

        var1.append(" }");
        return "Arguments fournis non valides : " + var1.toString();
    }

    public String getField() {
        return this.getMessage();
    }

    public String toString() {
        return "InvalidArgumentException[ " + this.getRealMessage() + "]";
    }
}