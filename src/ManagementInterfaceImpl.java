import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ManagementInterfaceImpl implements ManagementInterface{

    private short nroQuadros;
    private boolean[] mapaBits;
    private int pid;
    private ArrayList<PageTable> pageTableArrayList;
    private BestFit bf;

    public ManagementInterfaceImpl (short nroQuadros) {
        bf = new BestFit();
        pid = 0;
        pageTableArrayList = new ArrayList<>();
        if (!(nroQuadros == 32 || nroQuadros == 64 || nroQuadros == 128)) {
            throw new IllegalArgumentException("Número de quadros inválido");
        }
        this.nroQuadros = nroQuadros;
        mapaBits = new boolean[nroQuadros];
        Arrays.fill(mapaBits, false);
    }

    @Override
    public int loadProcessToMemory(String processName) throws NoSuchFileException, FileFormatException, MemoryOverflowException {
        PageTable pt = new PageTable(processName);

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

        ArrayList<Integer> quadrosTexto = bf.allocate(tamanhoTexto, mapaBits);
        ArrayList<Integer> quadrosData = bf.allocate(tamanhoDados, mapaBits);
        if (quadrosTexto == null || quadrosData == null) {
            throw new MemoryOverflowException("Não há memória suficiente para alocar o processo");
        }

        pt.setTexto(quadrosTexto);
        pt.setDados(quadrosData, tamanhoDados);

        this.pageTableArrayList.add(pt);
        this.pid++;
        return this.pid - 1;
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

        try {
            tabelaDoProcesso.setHeap(alocacaoHeap);
        } catch (StackOverflowException soe){
            throw new StackOverflowException(soe.getMessage());
        }

        return realSize;
    }

    @Override
    public int freeMemoryFromProcess(int processId, int size) throws InvalidProcessException, NoSuchMemoryException {
        return 0;
    }

    @Override
    public void excludeProcessFromMemory(int processId) throws InvalidProcessException {

    }

    @Override
    public void resetMemory() {

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

        ItemTabelaDePagina itp = pageTableArrayList.get(processId).getLinha(paginaInt);
        int quadro = -1;
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
        String[] processList = new String[pageTableArrayList.size()];
        for (int processNumber = 0; processNumber < pageTableArrayList.size(); processNumber++) {
            processList[processNumber] = "Processo " + processNumber + " - " +
                    pageTableArrayList.get(processNumber).getProcessName();
        }
        return processList;
    }
}
