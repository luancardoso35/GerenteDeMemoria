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
            linhas[i] = new ItemTabelaDePagina();
        }
        contadorPaginas = -1;
        inicioDaPilha = 31;
        tamanhoHeap = 0;
        ultimaPaginaHeap = 0;
    }

    /**
    * Adiciona as páginas de texto
    *@param quadros os indices dos quadros que serão utilizados
     */
    public void setTexto(ArrayList<Integer> quadros){
        for(int i : quadros){
            setLinha(contadorPaginas +1, new ItemTabelaDePagina((i*32), true));
            contadorPaginas++;
        }
    }

    /**
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

    /**
     * Recebe o número de bytes que deseja se alocar, e retorna a quantidade de bytes
     * que precisará ser alocada em novos quadros
     * @param size número de bytes que se deseja alocar
     * @return o quantidade do heap que precisará de novos quadros
     */
    public int getRealSizeHeap(int size) {
        if (tamanhoHeap == 0) {     //Caso nãp haja memória dinâmica alocada
            tamanhoHeap += size;
            if (size <= (32 - ocupacaoUltimaPaginaDados)) {     //Caso caiba tudo na última página do segmento de dados
                ocupacaoUltimaPaginaHeap = size % 32;
                return 0;
            } else if (ocupacaoUltimaPaginaDados != 0) {        //Caso caiba apenas uma parte na última página do segmento de dados
                int realHeap = size - (32 - ocupacaoUltimaPaginaDados);
                ocupacaoUltimaPaginaHeap = realHeap % 32;
                return realHeap;
            } else {    //Caso a última página de dados esteja toda ocupada
                ocupacaoUltimaPaginaHeap = size % 32;
                return size;
            }
        } else {        //Caso haja memória dinâmica alocada
            if (size <= (32 - ocupacaoUltimaPaginaHeap)) {//Caso caiba tudo na última página que o heap ocupa
                ocupacaoUltimaPaginaHeap = ocupacaoUltimaPaginaHeap - size;
                return 0;
            } else if (ocupacaoUltimaPaginaHeap != 0) { // Caso caiba apenas uma parte na última página que o heap ocupa
                int realHeap = size - (32 - ocupacaoUltimaPaginaHeap);
                ocupacaoUltimaPaginaHeap = realHeap % 32;
                return realHeap;
            } else { // Caso essa nova memória será alocada apenas em páginas novas
                ocupacaoUltimaPaginaHeap = size % 32;
                return size;
            }
        }
    }

    /**
     *Adiciona as páginas de heap
     * @param quadros os indices dos quadros que serão alocados
     */
    public void setHeap(ArrayList<Integer>quadros) throws StackOverflowException {
        //Confere se exite espaço para as novas páginas
        if (contadorPaginas + quadros.size() >= inicioDaPilha) {
            throw new StackOverflowException("ERRO: Memória requisitada maior do que a disponível");
        }
        // Adiciona cada um dos quadros na tabela de página
        for(int i : quadros){
            setLinha(contadorPaginas +1, new ItemTabelaDePagina((i*32), true));
            contadorPaginas++;
        }
        // Atualiza a última página ocupada pelo heap
        ultimaPaginaHeap = contadorPaginas;
    }

    /**
     *Libera heap da memória
     * @param size tamanho da memória que será liberada
     * @return Lista de indices dos quadros que foram liberados, ou nulo caso a memória requisitada para
     * liberação seja maior que a existente no heap
     */
    public ArrayList<Integer> freeMemoryFromHeap(int size) {
        // variável que armazena o valor inicial de memória que deseja-se liberar
        int auxSize = size;

        // Caso o heap esteja vazio ou se queira liberar mais memória do que há no heap
        if (tamanhoHeap == 0 || size > tamanhoHeap) {
            return null;
        }

        // ArrayList que guarda os quadros que serão liberados
        ArrayList<Integer> quadrosParaLiberacao = new ArrayList<>();

        //Se a memoria a ser liberada for maior que zero e o heap ocupar uma parte de uma página
        if (size > 0 && ocupacaoUltimaPaginaHeap != 0) {
            size -= ocupacaoUltimaPaginaHeap;           //subtrai da quantidade de memória a ser liberada a ocupação da última página do heap
            quadrosParaLiberacao.add(linhas[ultimaPaginaHeap].getQuadro() / 32);     //adiciona o quadro da última página na lista de quadros a serem liberados
            excludeLinha(ultimaPaginaHeap);             //exclui última página que o heap ocupava
            ultimaPaginaHeap--;             //atualiza o valor da última página do heap
            ocupacaoUltimaPaginaHeap = 0; // o heap ocupa apenas páginas inteiras
        }

        //Retira as páginas da tabela até que o total liberado seja menor que 32 bytes
        while (size / 32 != 0) {
            // confere se a página atual do heap é diferente da última página de dados
            if (ultimaPaginaHeap != ultimaPaginaDados) {
                excludeLinha(ultimaPaginaHeap); // exclui a última página que o heap ocupa
                // adiciona o quadro que foi esvaziado na lista de quadros para liberação
                quadrosParaLiberacao.add(linhas[ultimaPaginaHeap].getQuadro() / 32);
                ultimaPaginaHeap--; //atualiza o valor da última página do heap
                size -= 32; // retira o valor de um quadro da quantidade de memória a ser liberada
            }
        }

        // caso após liberar todas as páginas inteiras, ainda exista memória para liberar
        if (size > 0) {
            // caso o heap esteja na página de dados
            if (ultimaPaginaHeap == ultimaPaginaDados) {
                ocupacaoUltimaPaginaHeap = 32 - ocupacaoUltimaPaginaDados - size;
            } else {
                ocupacaoUltimaPaginaHeap = (32 - size);
            }
        }

        // atualiza a quantidade de memória armazenada no heap, retirando Size bytes
        tamanhoHeap -= auxSize;
        return quadrosParaLiberacao;
    }

    /**
     *Adiciona as páginas de pilha
     * @param quadros indices dos quadros a serem alocados
     */
    public void setPilha(ArrayList<Integer>quadros){
        for (int i = quadros.size() - 1; i >= 0; i--){
            setLinha(inicioDaPilha, new ItemTabelaDePagina((quadros.get(i)*32), true));
            inicioDaPilha--;
        }
    }
    /**
     *Pega uma string contendo informações das paginas da tabela
     * @return string com páginas da tabela (número, quadro e bit válido-inválido)
     */
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

    /**
     *Obtem uma determinada página da tabela
     * @param pagina o número da página
     * @return página
     */
    public ItemTabelaDePagina getLinha(int pagina){
        return linhas[pagina];
    }

    /**
     *Obtem os quadros utilizados da tabela de página
     * @return uma lista com os quadros da tabela
     */
    public ArrayList<Integer> getQuadros() {
        ArrayList<Integer> quadros = new ArrayList<>();
        for (ItemTabelaDePagina linha: linhas) {
            quadros.add(linha.getQuadro());
        }
        return quadros;
    }

    /**
     *Obtem os quadros utilizados da tabela que não são dedicados a texto
     * Será utilizado na hora de excluir um processo duplicado
     * @param tamanhoSegmentoTexto tamanho do segmento de texto
     * @return uma lista com os quadros que não são de texto
     */
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

    /**
     * Adiciona uma nova página ao array "linhas"
     * @param index número da pagina/linha
     * @param item nova página
     */
    private void setLinha(int index, ItemTabelaDePagina item){
        linhas[index] = item;
    }

    /**
    *Exclui uma linha
    * @param index index da linha
     */
    private void excludeLinha(int index){
        linhas[index].setQuadro(-1);
        linhas[index].setValidoInvalido(false);
        contadorPaginas--;
    }
}
