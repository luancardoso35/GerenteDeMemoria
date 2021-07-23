/**
 * @author Luan Cesar Cardoso, 11340272
 * @author Lucas Freitas Pinto Ferreira, 11340289
 * @author Matheo Bellini Marumo, 11315606
 * @author Matheus Oliveira Ribeiro da Silva, 11315096
 */

public class ItemTabelaDePagina {
    private int quadro;
    private boolean validoInvalido;

    /**
     * Construtor da classe ItemTabelaPagina, que inicializa o quadro como -1
     * e o bit válido inválido como false
     */
    public ItemTabelaDePagina(){
        this.quadro = -1;
        this.validoInvalido = false;
    }

    /**
     * Construtor da classe ItemTabelaPagina
     * @param quadro o valor do quadro
     * @param validoInvalido o valor do bit válido inválido
     */
    public ItemTabelaDePagina(int quadro, boolean validoInvalido){
        this.quadro = quadro;
        this.validoInvalido = validoInvalido;
    }
    /**
     *Define o quadro da página
     * @param quadro o valor do quadro
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