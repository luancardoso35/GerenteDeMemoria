import java.util.ArrayList;

public class Processo {
    private int id;
    private String nome;
    private int tamanhoSegmentoTexto;
    private int tamanhoSegmentoDados;
    private PageTable tabelaPagina;

    public Processo(int id, String nome, int tamanhoSegmentoTexto, int tamanhoSegmentoDados) {
        this.id = id;
        this.nome = nome;
        this.tamanhoSegmentoTexto = tamanhoSegmentoTexto;
        this.tamanhoSegmentoDados = tamanhoSegmentoDados;
    }

    public void createPageTable() {
        this.tabelaPagina = new PageTable();
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getTamanhoSegmentoTexto() {
        return tamanhoSegmentoTexto;
    }

    public int getTamanhoSegmentoDados() {
        return tamanhoSegmentoDados;
    }

    public PageTable getTabelaPagina() {
        return tabelaPagina;
    }

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

        Processo p = (Processo) o;
        return this.nome.equals(p.getNome()) && this.tamanhoSegmentoTexto == p.getTamanhoSegmentoTexto()
                && this.tamanhoSegmentoDados == p.getTamanhoSegmentoDados();

    }

}
