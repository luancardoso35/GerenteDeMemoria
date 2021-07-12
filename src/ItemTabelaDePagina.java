public class ItemTabelaDePagina {
    private int frame;
    private boolean validoInvalido;
    public ItemTabelaDePagina(){
        frame = -1;
        validoInvalido = false;
    }
    public ItemTabelaDePagina(int frame, boolean validoInvalido){
        this.frame = frame;
        this.validoInvalido = validoInvalido;
    }
    public void setFrame(int frame){
        this.frame = frame;
    }
    public void getValidoInvalido(boolean validoInvalido){
        this.validoInvalido = validoInvalido;
    }
    public int getFrame(){
        return frame;
    }
    public boolean getValidoInvalido(){
        return validoInvalido;
    }
    public String toString(){
        return "{" + frame + " ; " + validoInvalido + "}";
    }
}