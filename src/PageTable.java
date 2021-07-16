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
        ultimaPaginaHeap = 0;
    }

    /*
    * Adiciona as páginas de texto
    *@param quadros os indices dos quadros que serão utilizados
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
     * @param quadros os indices dos quadros que serão utilizados
     * @param tamanhoDados o tamanho do segmento de dados
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
     * @param size o tamanho do heap que será
     * @return o quantidade do heap que precisará de novos quadros
     */
    public int getRealSizeHeap(int size) {
        if (tamanhoHeap == 0) {     //Caso nãp haja memória dinâmica alocada
            tamanhoHeap += size;
            if (size <= (32 - ocupacaoUltimaPaginaDados)) {     //Caso caiba tudo na última página do segmento de dados
                ocupacaoUltimaPaginaHeap = size % 32;
                return 0;
            } else if (ocupacaoUltimaPaginaDados != 0) {        //Caso não tenha sobra na última página
                int realHeap = size - (32 - ocupacaoUltimaPaginaDados);
                ocupacaoUltimaPaginaHeap = realHeap % 32;
                return realHeap;
            } else {    //Caso a última página de dados esteja toda ocupada
                ocupacaoUltimaPaginaHeap = size % 32;
                return size;
            }
        } else {        //Caso haja memória dinâmica alocada
            if (size <= (32 - ocupacaoUltimaPaginaHeap)) {//Caso caiba tudo na última página do heap
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
     * @param quadros os indices dos quadros que serão alocados
     */
    public void setHeap(ArrayList<Integer>quadros) throws StackOverflowException {
        //Confere se exite espaço para as novas páginas
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
        if (ultimaPaginaHeap == 0 || size > tamanhoHeap) {
            return null;
        }

        ArrayList<Integer> quadrosParaLiberacao = new ArrayList<>();

        //Se a memoria a ser liberada for maior que zero e a ocupação a última página também
        if (size > 0 && ocupacaoUltimaPaginaHeap != 0) {
            size -= ocupacaoUltimaPaginaHeap;           //subtrai a quantidade de memória liberada
            quadrosParaLiberacao.add(linhas[ultimaPaginaHeap].getQuadro() / 32);     //adiciona o quadro da última página na lista de quadros a serem liberados
            excludeLinha(ultimaPaginaHeap);             //exclui última página
            ultimaPaginaHeap--;             //atualiza a última página do heap agora
            ocupacaoUltimaPaginaHeap = 0;
        }

        //Retira as páginas da tabela até que o total liberado seja menor que 32 bytes
        while (size / 32 != 0) {
            if (ultimaPaginaHeap != ultimaPaginaDados) {
                excludeLinha(ultimaPaginaHeap);
                quadrosParaLiberacao.add(linhas[ultimaPaginaHeap].getQuadro() / 32);
                ultimaPaginaHeap--;
                size -= 32;
            }
        }

        if (size > 0) {
            if (ultimaPaginaHeap == ultimaPaginaDados) {
                size -= (32 - ocupacaoUltimaPaginaDados);
            } else {
                ocupacaoUltimaPaginaHeap = (32 - size);
            }
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

    public ArrayList<Integer> getQuadrosProcessoDuplicado(int tamanhoSegmentoTexto) {
        int nroQuadros = tamanhoSegmentoTexto/32;

        if (tamanhoSegmentoTexto % 32 != 0) {
            nroQuadros++;
        }

        ArrayList<Integer> quadrosProcessoDuplicado = new ArrayList<>();

        for (int i = nroQuadros; i < linhas.length; i++) {
            if (linhas[i].getQuadro() != -1) {
                quadrosProcessoDuplicado.add(linhas[i].getQuadro());
            }
        }

        return quadrosProcessoDuplicado;
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
