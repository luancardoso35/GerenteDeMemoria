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
     *Instancia a tabela de página do processo
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
     *Obtem a tabela de página do processo
     * @return tabela de página do processo
     */
    public PageTable getTabelaPagina() {
        return tabelaPagina;
    }

    /**
     *Confere se o processo é igual a outro
     * @param o o outro processo
     * @return true se for igual false se não
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null) {
            return false;
        }

        if (getClass() != o.getClass()) {
            return false;
        }

        //Confere se os processos tem o mesmo nome, tamanho de segmento de texto e dados
        Processo p = (Processo) o;
        return this.nome.equals(p.getNome()) && this.tamanhoSegmentoTexto == p.getTamanhoSegmentoTexto()
                && this.tamanhoSegmentoDados == p.getTamanhoSegmentoDados();

    }

}
