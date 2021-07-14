import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class ManagementInterfaceImpl implements ManagementInterface{

    private short nroQuadros;
    private boolean[] mapaBits;
    private ArrayList<Processo> processoArrayList;
    private ArrayList<PageTable> pageTableArrayList;
    private BestFit bf;

    public ManagementInterfaceImpl (short nroQuadros) {
        bf = new BestFit();

        if (!(nroQuadros == 32 || nroQuadros == 64 || nroQuadros == 128)) {
            throw new IllegalArgumentException("Número de quadros inválido");
        }

        this.nroQuadros = nroQuadros;
        mapaBits = new boolean[nroQuadros];
        Arrays.fill(mapaBits, false);
        pageTableArrayList = new ArrayList<>();
        processoArrayList = new ArrayList<>();
    }

    @Override
    public int loadProcessToMemory(String processName) throws NoSuchFileException, FileFormatException, MemoryOverflowException {
        final ArrayList<String> text = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") +
                "\\src\\" + processName))) {
            br.lines().forEach(text::add);

            //Confere a formatação do arquivo
            if(text.size() != 3){
                throw new FileFormatException("Formatação do arquivo errada");
            }else{
                if (!text.get(0).matches("program \\w+")) {
                    throw new FileFormatException("O nome do arquivo deve ser igual ao nome do processo");
                } else if (!(text.get(0).split(" ")[1] + ".txt").equals(processName) ){
                    throw new FileFormatException("O nome do arquivo deve ser igual ao nome do processo");
                } else if (!text.get(1).matches("text \\d+")) {
                    throw new FileFormatException("Tamanho do segmento de texto inválido");
                }else if(!text.get(2).matches("data \\d+")) {
                    throw new FileFormatException("Tamanho do sogmento de dados inválido");
                }
            }

        } catch (FileNotFoundException fnfe) {
            throw new NoSuchFileException("Arquivo não encontrado");
        } catch (IOException ioe) {
            System.out.println("Erro de leitura.");
        }

        //Extrai o tamanho dos textos e dos dados
        int tamanhoTexto = Integer.parseInt(text.get(1).split(" ")[1]);
        int tamanhoDados = Integer.parseInt(text.get(2).split(" ")[1]);

        if(tamanhoTexto <= 1 || tamanhoTexto > 960)
            throw new FileFormatException("Tamanho do segmento de texto inválido");
        if(tamanhoDados < 0 || tamanhoDados > 928)
            throw new FileFormatException("Tamanho do segmento de dados inválido");

        int idNovoProcesso = getIdNovoProcesso();
        if (idNovoProcesso == -1) {
            idNovoProcesso = pageTableArrayList.size();
        }

        //Cria um novo processo e uma nova tabela de página
        Processo p = new Processo(idNovoProcesso, processName, tamanhoTexto, tamanhoDados);
        p.createPageTable();
        PageTable pt = p.getTabelaPagina();

        ArrayList<Integer> quadrosTexto = new ArrayList<>();
        //Confere se o processo está sendo criado a partir de algum programa já utilizado
        Processo duplicatedProcess = duplicatedProcess(processName, tamanhoTexto, tamanhoDados);
        if (duplicatedProcess != null) {    //Caso haja um processo repetido, utiliza os mesmos quadros de texto
            int nroQuadrosTexto = tamanhoTexto/32;
            if (tamanhoTexto % 32 != 0) {
                nroQuadrosTexto++;
            }
            ArrayList<Integer> quadrosProcessoOriginal = new ArrayList<>();
            for (int i = 0; i < nroQuadrosTexto; i++) {
                quadrosProcessoOriginal.add(duplicatedProcess.getTabelaPagina().getLinha(i).getQuadro());
            }
            p.getTabelaPagina().setTexto(quadrosProcessoOriginal);
        } else {    //Caso não haja um processo repetido, aloca novos quadros
            quadrosTexto = bf.allocate(tamanhoTexto, mapaBits);
        }

        //Aloca os quadros para os dados estáticos e para a pilha
        ArrayList<Integer> quadrosData = bf.allocate(tamanhoDados, mapaBits);
        ArrayList<Integer> quadrosPilha = bf.allocate(64, mapaBits);
        if (quadrosTexto.size() == 0 || quadrosData == null) {
            throw new MemoryOverflowException("Não há memória suficiente para alocar o processo");
        }

        pt.setTexto(quadrosTexto);
        pt.setDados(quadrosData, tamanhoDados);
        pt.setPilha(quadrosPilha);

        //Adiciona a nova tabela de página e o novo processo nas listas de tabela e de processos respectivamente
        pageTableArrayList.add(idNovoProcesso, pt);
        processoArrayList.add(idNovoProcesso, p);

        return idNovoProcesso;
    }

    @Override
    public int allocateMemoryToProcess(int processId, int size) throws InvalidProcessException, StackOverflowException, MemoryOverflowException {
        if(processId < 0 || processId >= pageTableArrayList.size()){
            throw new InvalidProcessException("Id de processo inválido");
        }
        PageTable tabelaDoProcesso = pageTableArrayList.get(processId);
        short realSize = tabelaDoProcesso.getRealSizeHeap(size);
        ArrayList<Integer> alocacaoHeap = bf.allocate(realSize, mapaBits);
        if (alocacaoHeap == null) {
            throw new MemoryOverflowException("Não há memória disponível");
        }

        int quadrosAlocados = alocacaoHeap.size();

        try {
            tabelaDoProcesso.setHeap(alocacaoHeap);
        } catch (StackOverflowException soe){
            throw new StackOverflowException(soe.getMessage());
        }

        return quadrosAlocados;
    }

    @Override
    public int freeMemoryFromProcess(int processId, int size) throws InvalidProcessException, NoSuchMemoryException {
        Processo p = processoArrayList.get(processId);
        ArrayList<Integer> quadrosParaLiberacao = p.getTabelaPagina().freeMemoryFromHeap(size);

        for (int quadro: quadrosParaLiberacao) {
            int nroQuadro = quadro/32;
            mapaBits[nroQuadro] = false;
        }

        return quadrosParaLiberacao.size();
    }

    @Override
    public void excludeProcessFromMemory(int processId) throws InvalidProcessException {

    }

    @Override
    public void resetMemory() {
        pageTableArrayList.clear();
        processoArrayList.clear();
        Arrays.fill(mapaBits, false);
    }

    @Override
    public int getPhysicalAddress(int processId, int logicalAddress) throws InvalidProcessException, InvalidAddressException {
        if (processId < 0 || processId >= pageTableArrayList.size()) {
            throw new InvalidProcessException("Número de processo inválido");
        }
        if(logicalAddress < 0 || logicalAddress > 1023){
           throw new InvalidProcessException("O endereco lógico deve ser um valor entre 0 e 1023");
        }
        String enderecoBin = Integer.toBinaryString(logicalAddress);
        String paginaStr = enderecoBin.substring(0,5);
        int paginaInt = Integer.parseInt(paginaStr, 2);
        String deslocamentoStr = enderecoBin.substring(6,10);
        int deslocamentoInt = Integer.parseInt(deslocamentoStr, 2);

        ItemTabelaDePagina itp = processoArrayList.get(processId).getTabelaPagina().getLinha(paginaInt);
        int quadro;
        if (itp == null) {
            throw new InvalidAddressException("Endereço lógico inválido");
        } else {
            quadro = itp.getQuadro();
        }

        return quadro + deslocamentoInt;
    }

    @Override
    public String getBitMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (boolean b: mapaBits) {
            sb.append(b);
            sb.append(" ");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String getPageTable(int processId) throws InvalidProcessException {
        if (processId < 0 || processId >= pageTableArrayList.size()) {
            throw new InvalidProcessException("Processo inválido");
        }
        return pageTableArrayList.get(processId).toString();
    }

    @Override
    public String[] getProcessList() {
        String[] processList = new String[processoArrayList.size()];
        int i = 0;
        for (Processo p: processoArrayList) {
            processList[i] = "{Processo " + p.getNome() + "; id: " + p.getId();
            i++;
        }
        return processList;
    }

    private int getIdNovoProcesso() {
        for (int i = 0; i < processoArrayList.size(); i++) {
            if (processoArrayList.get(i) == null) {
                return i;
            }
        }
        return -1;
    }

    /*
    *Confere se o processo está sendo criado a partir de um programa repetido e retorna o processo que compartilha o mesmo programa
    * @param processName nome do processo
    * @param tamamnhoTexto tamanho do segmento de texto
    * @param tamanhoDados tamanho do segmento de dados
     */
    private Processo duplicatedProcess(String processName, int tamanhoTexto, int tamanhoDados) {
        Processo p = new Processo(-1, processName, tamanhoTexto, tamanhoDados);
        for (Processo processo: processoArrayList) {
            if (processo.equals(p)) {
                return processo;
            }
        }
        return null;
    }
}
