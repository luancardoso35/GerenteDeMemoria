import java.lang.reflect.Array;
import java.util.ArrayList;

public class PageTable {
    private ItemTabelaDePagina[] linhas;
    private short contadorPaginas;
    private int ultimaPaginaDados;
    private int ultimaPaginaHeap;
    private int ocupacaoUltimaPaginaDados;
    private int ocupacaoUltimaPaginaHeap;
    private int tamanhoHeap;
    private short inicioDaPilha;

    public PageTable(){
        linhas = new ItemTabelaDePagina[32];
        contadorPaginas = -1;
        inicioDaPilha = 30;
        tamanhoHeap = 0;
        ultimaPaginaHeap = -1;
    }

    /*
    * Adiciona as páginas de texto
    *
     */
    public void setTexto(ArrayList<Integer> quadros){
        for(int i : quadros){
            setLinha(contadorPaginas +1, new ItemTabelaDePagina((i*32), true));
            contadorPaginas++;
        }
    }

    /*
     *Adiciona as paginas dos dados estáticos e verifica se a última página
     * poderá ser utilizada para armazenar o heap
     */
    public void setDados(ArrayList<Integer> quadros, int tamanhoDados){
        for(int i : quadros){
            setLinha(contadorPaginas +1, new ItemTabelaDePagina((i*32), true));
            contadorPaginas++;
        }
        ocupacaoUltimaPaginaDados = (tamanhoDados % 32);
        ultimaPaginaDados = contadorPaginas;
    }

    /*
     *Recebe o tamanho do heap e retorna o tamanho que efetivamente será alocado
     */
    public short getRealSizeHeap(int size) {
        tamanhoHeap += size;
        if (ocupacaoUltimaPaginaDados != 0) {
            int realHeap = (size - (32 - ocupacaoUltimaPaginaDados));
            ocupacaoUltimaPaginaHeap = realHeap % 32;
            return (short) realHeap;
        } else {
            ocupacaoUltimaPaginaHeap = size % 32;
            return (short) size;
        }
    }

    /*
     *Adiciona as páginas de heap
     */
    public void setHeap(ArrayList<Integer>quadros) throws StackOverflowException {
        if(inicioDaPilha == contadorPaginas){
            throw new StackOverflowException("Memória requisitada marior do que a disponível");
        }
        for(int i : quadros){
            setLinha(contadorPaginas +1, new ItemTabelaDePagina((i*32), true));
            contadorPaginas++;
        }
        ultimaPaginaHeap = contadorPaginas;
    }

    public ArrayList<Integer> freeMemoryFromHeap(int size) {
        if (ultimaPaginaHeap == -1 || size > tamanhoHeap) {
            return null;
        }

        ArrayList<Integer> quadrosParaLiberacao = new ArrayList<>();

        //Se a memoria a ser liberada for maior que zero e a ocupação a última página também
        if (size > 0 && ocupacaoUltimaPaginaHeap != 0) {
            size -= ocupacaoUltimaPaginaHeap;           //subtrai a quantidade de memória liberada
            excludeLinha(ultimaPaginaHeap);             //exclui última página
            quadrosParaLiberacao.add(linhas[ultimaPaginaHeap].getQuadro());     //adiciona o quadro da última página na lista de quadros a serem liberados
            ultimaPaginaHeap--;             //atualiza a última página do heap agora
        }

        //Retira as páginas da tabela até que o total liberado seja menor que 32 bytes
        while (size / 32 != 0) {
            if (ultimaPaginaHeap != ultimaPaginaDados) {
                excludeLinha(ultimaPaginaHeap);
                quadrosParaLiberacao.add(linhas[ultimaPaginaHeap].getQuadro());
                ultimaPaginaHeap--;
                size -= 32;
            }
        }

        if (ultimaPaginaHeap == ultimaPaginaDados) {
            size -= (32 - ocupacaoUltimaPaginaDados);
        }

        return size == 0 ? quadrosParaLiberacao: null;
    }

    /*
     *Adiciona as páginas de pilha
     */
    public void setPilha(ArrayList<Integer>quadros){
        for(int i : quadros){
            setLinha(inicioDaPilha, new ItemTabelaDePagina((i*32), true));
            inicioDaPilha++;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (ItemTabelaDePagina item: linhas) {
            if (item != null) {
                sb.append(count);
                sb.append(" ").append(item);
                sb.append("\n");
            }else{
                sb.append(count);
                sb.append(" {").append(" - ; false}");
                sb.append("\n");
            }
            count++;
        }
        return sb.toString();
    }

    public ItemTabelaDePagina getLinha(int pagina){
        return linhas[pagina];
    }

    public ArrayList<Integer> getQuadros() {
        ArrayList<Integer> quadros = new ArrayList<>();
        for (ItemTabelaDePagina linha: linhas) {
            if (linha != null) {
                quadros.add(linha.getQuadro());
            }
        }
        return quadros;
    }

    /*
     * Adiciona uma nova linha ao array de linhas
     */
    private void setLinha(int index, ItemTabelaDePagina item){
        linhas[index] = item;
    }

    /*
    *Exclui uma linha
    * @param index index da linha
     */
    private void excludeLinha(int index){
        linhas[index] = null;
        contadorPaginas--;
    }
}
