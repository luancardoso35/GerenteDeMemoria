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
    /**
     *Define o quadro da página
     * @param quadro do quadro
     */
    public void setQuadro(int quadro) {
        this.quadro = quadro;
    }

    /**
     *Define o bit valido inválido
     * @param validoInvalido o valor do bit válido inválido
     */
    public void setValidoInvalido(boolean validoInvalido){
        this.validoInvalido = validoInvalido;
    }

    /**
     *Obtem o quadro da página
     * @return o valor do quadro
     */
    public int getQuadro(){
        return quadro;
    }

    /**
     *Obtem o bit válido-inválido
     * @return o valor do bit
     */
    public boolean getValidoInvalido(){
        return validoInvalido;
    }

    /**
     *Obtem valor do quadro e o bit em forma de texto
     * @return texto descrevendo o valor do quadro e do bit
     */
    public String toString(){
        return "{" + quadro + " ; " + validoInvalido + "}";
    }
}