public class ItemTabelaDePagina {
    private int quadro;
    private boolean validoInvalido;
    public ItemTabelaDePagina(){
        quadro = -1;
        validoInvalido = false;
    }
    public ItemTabelaDePagina(int quadro, boolean validoInvalido){
        this.quadro = quadro;
        this.validoInvalido = validoInvalido;
    }
    public void setQuadro(int quadro) {
        this.quadro = quadro;
    }
    public void getValidoInvalido(boolean validoInvalido){
        this.validoInvalido = validoInvalido;
    }
    public int getQuadro(){
        return quadro;
    }
    public boolean getValidoInvalido(){
        return validoInvalido;
    }
    public String toString(){
        return "{" + quadro + " ; " + validoInvalido + "}";
    }
}