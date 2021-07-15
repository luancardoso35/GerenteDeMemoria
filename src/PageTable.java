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
        for (int i = 0; i < linhas.length; i++) {
            linhas[i] = new ItemTabelaDePagina(-1, false);
        }
        contadorPaginas = -1;
        inicioDaPilha = 31;
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
    public int getRealSizeHeap(int size) {
        if (tamanhoHeap == 0) {
            tamanhoHeap += size;
            if (size <= (32 - ocupacaoUltimaPaginaDados)) {     //Caso caiba tudo na última página do segmento de dados
                ocupacaoUltimaPaginaHeap = ocupacaoUltimaPaginaDados - size;
                return 0;
            } else if (ocupacaoUltimaPaginaDados != 0) {        //Caso a sobra na última página seja maior que o quanto se quer alocar
                int realHeap = size - (32 - ocupacaoUltimaPaginaDados);
                ocupacaoUltimaPaginaHeap = realHeap % 32;
                return realHeap;
            } else {    //Caso a última página de dados esteja toda ocupada
                ocupacaoUltimaPaginaHeap = size % 32;
                return size;
            }
        } else {
            if (size <= (32-ocupacaoUltimaPaginaHeap)) {
                ocupacaoUltimaPaginaHeap = ocupacaoUltimaPaginaHeap - size;
                return 0;
            } else if (ocupacaoUltimaPaginaHeap != 0) {
                int realHeap = size - (32 - ocupacaoUltimaPaginaHeap);
                ocupacaoUltimaPaginaHeap = realHeap % 32;
                return realHeap;
            } else {
                ocupacaoUltimaPaginaHeap = size % 32;
                return size;
            }
        }
    }

    /*
     *Adiciona as páginas de heap
     */
    public void setHeap(ArrayList<Integer>quadros) throws StackOverflowException {
        if (contadorPaginas + quadros.size() >= inicioDaPilha) {
            throw new StackOverflowException("ERRO: Memória requisitada maior do que a disponível");
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
        for (int i = quadros.size() - 1; i >= 0; i--){
            setLinha(inicioDaPilha, new ItemTabelaDePagina((quadros.get(i)*32), true));
            inicioDaPilha--;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (ItemTabelaDePagina item: linhas) {
            if (item.getQuadro() != -1) {
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
            quadros.add(linha.getQuadro());

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
        linhas[index].setQuadro(-1);
        linhas[index].setValidoInvalido(false);
        contadorPaginas--;
    }
}
