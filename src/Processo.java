/**
 * @author Luan Cesar Cardoso, 11340272
 * @author Matheo Bellini Marumo, 11315606
 * @author Matheus Oliveira Ribeiro da Silva, 11315096
 */

public class Processo {
    private int id;
    private String nome;
    private int tamanhoSegmentoTexto;
    private int tamanhoSegmentoDados;
    private PageTable tabelaPagina;

    /**
     * Construtor do processo
     * @param id o id do processo
     * @param nome o nome do processo
     * @param tamanhoSegmentoTexto o tamanho do segmento de texto daquele processo
     * @param tamanhoSegmentoDados o tamanho do segmento de dados daquele processo
     */
    public Processo(int id, String nome, int tamanhoSegmentoTexto, int tamanhoSegmentoDados) {
        this.id = id;
        this.nome = nome;
        this.tamanhoSegmentoTexto = tamanhoSegmentoTexto;
        this.tamanhoSegmentoDados = tamanhoSegmentoDados;
    }

    /**
     *Instancia a tabela de pagina do processo
     */
    public void createPageTable() {
        this.tabelaPagina = new PageTable();
    }

    /**
     *Obtem o id do processo
     * @return id do processo
     */
    public int getId() {
        return id;
    }

    /**
     *Obtem o nome do processo
     * @return nome do processo
     */
    public String getNome() {
        return nome;
    }


    /**
     *Obtem o tamanho em bytes do segmento de texto
     * @return tamanho do segmento de texto
     */
    public int getTamanhoSegmentoTexto() {
        return tamanhoSegmentoTexto;
    }


    /**
     *Obtem tamanho do segmento de dados
     * @return tamanho do segmento de dados
     */
    public int getTamanhoSegmentoDados() {
        return tamanhoSegmentoDados;
    }


    /**
     *Obtem a tabela de pagina do processo
     * @return tabela de pagina do processo
     */
    public PageTable getTabelaPagina() {
        return tabelaPagina;
    }
}
