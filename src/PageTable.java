import java.util.ArrayList;
import java.util.Arrays;

public class PageTable {
    private String processName;
    private ItemTabelaDePagina[] linhas;
    private short finalDoHeap;
    private int ocupacaoUltimaPagina;
    private short topoDaPilha;

    public PageTable(String processName){
        this.processName = processName;
        linhas = new ItemTabelaDePagina[32];
        finalDoHeap = -1;
        ocupacaoUltimaPagina = -1;  //Quantos bytes são utilizados na última página alocada
        topoDaPilha = 31;
    }


    public void setTexto(ArrayList<Integer> quadros){
        for(int i : quadros){
            setLinha(finalDoHeap +1, new ItemTabelaDePagina((i*32), true));
            finalDoHeap++;
        }
    }

    /*
     *Adiciona as paginas dos dados estáticos e verifica se a última página
     * poderá ser utilizada para armazenar o heap
     */
    public void setDados(ArrayList<Integer> quadros, int tamanhoDados){
        for(int i : quadros){
            setLinha(finalDoHeap +1, new ItemTabelaDePagina((i*32), true));
            finalDoHeap++;
        }
        ocupacaoUltimaPagina = (tamanhoDados % 32);
    }

    /*
     *Recebe o tamanho do heap e retorna o tamanho que efetivamente será alocado
     */
    public short getRealSizeHeap(int size) {
        if (ocupacaoUltimaPagina != 0) {
            int realHeap = (size - (32 - ocupacaoUltimaPagina));
            ocupacaoUltimaPagina = realHeap % 32;
            return (short) realHeap;
        } else {
            ocupacaoUltimaPagina = size % 32;
            return (short) size;
        }
    }

    /*
     *Adiciona as páginas de heap
     */
    public void setHeap(ArrayList<Integer>quadros) throws StackOverflowException {
        if(topoDaPilha == finalDoHeap){
            throw new StackOverflowException("Memória requisitada marior do que a disponível");
        }
        for(int i : quadros){
            setLinha(finalDoHeap +1, new ItemTabelaDePagina((i*32), true));
            finalDoHeap++;
        }
    }

    /*
     *Adiciona as páginas de pilha
     */
    public void setPilha(ArrayList<Integer>quadros){
        for(int i : quadros){
            setLinha(topoDaPilha, new ItemTabelaDePagina((i*32), true));
            topoDaPilha--;
        }
    }

    /*
     * Adiciona uma nova linha ao array de linhas
     */
    private void setLinha(int index, ItemTabelaDePagina item){
        linhas[index] = item;
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
                count++;
            }
        }
        return sb.toString();
    }

    public String getProcessName() {
        return processName;
    }

    public ItemTabelaDePagina getLinha(int pagina){
        return linhas[pagina];
    }

    /*
     * Adiciona uma nova linha ao array de linhas
     */
    private void setLinha(int index, ItemTabelaDePagina item){
        linhas[index] = item;
    }
}
