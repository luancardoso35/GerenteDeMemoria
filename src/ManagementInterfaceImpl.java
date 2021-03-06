import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author Luan Cesar Cardoso, 11340272
 * @author Matheo Bellini Marumo, 11315606
 * @author Matheus Oliveira Ribeiro da Silva, 11315096
 */

public class ManagementInterfaceImpl implements ManagementInterface {

    private short nroQuadros;
    private boolean[] mapaBits;
    private ArrayList<Processo> processoArrayList;
    private ArrayList<PageTable> pageTableArrayList;
    private BestFit bf;
    private int espacoLivre;

    /**
     * Construtor interface de gerenciamento de memoria
     *
     * @param nroQuadros o numero de quadros informado pelo usuario (32, 64 ou 128)
     */
    public ManagementInterfaceImpl(short nroQuadros) {
        bf = new BestFit();
        this.nroQuadros = nroQuadros;
        mapaBits = new boolean[nroQuadros];     //Inicializa mapa de bits para os quadros de memória (true = utilizado, false = não utilizado)
        espacoLivre = nroQuadros * 32;          //Calcula espaço livre (cada quadro tem 32 bytes)
        Arrays.fill(mapaBits, false);       // Preenche o mapa de bits com o valor false
        pageTableArrayList = new ArrayList<>();     //Instancia lista de páginas de tabela
        processoArrayList = new ArrayList<>();      //Instancia lista de processos
    }

    @Override
    public int loadProcessToMemory(String processName) throws NoSuchFileException, FileFormatException,
            MemoryOverflowException {

        StringBuilder text = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(processName))){
            int value = 0;

            while ((value = br.read()) != -1) {
                char c = (char)value;
                text.append(c);
            }
        } catch (IOException e) {
            return -1;
        }

        // Verifica se o arquivo respeita o formato especificado (Considerando EOL = \n (Linux, MacOS) ou \r\n (Windows))
        if (text.charAt(text.length()-1) != '\n') {
            throw new FileFormatException("ERRO: O arquivo não respeita o formato especificado.");
        }

        String[] linhas = text.toString().split("\r?\n");

        if (!linhas[0].matches("program \\w+")) {
            throw new FileFormatException("ERRO: O nome do arquivo deve ser igual ao nome do processo");
        } else if (!(linhas[0].split(" ")[1] + ".txt").equals(processName)) {
            throw new FileFormatException("ERRO: O nome do arquivo deve ser igual ao nome do processo");
        } else if (!linhas[1].matches("text \\d+")) {
            throw new FileFormatException("ERRO: Tamanho do segmento de texto inválido");
        } else if (!linhas[2].matches("data \\d+")) {
            throw new FileFormatException("ERRO: Tamanho do sogmento de dados inválido");
        }

        //Extrai o tamanho dos textos e dos dados
        int tamanhoTexto = Integer.parseInt(linhas[1].split(" ")[1]);
        int tamanhoDados = Integer.parseInt(linhas[2].split(" ")[1]);

        // Confere o tamanho dos segmentos e se o programa pode ser alocado
        if (tamanhoTexto <= 1 || tamanhoTexto > 960)
            throw new FileFormatException("ERRO: Tamanho do segmento de texto inválido");
        if (tamanhoDados < 0 || tamanhoDados > 928)
            throw new FileFormatException("ERRO: Tamanho do segmento de dados inválido");
        if (tamanhoDados + tamanhoTexto + 64 > espacoLivre) {
            throw new MemoryOverflowException("ERRO: Não há memória suficiente para alocar o processo");
        }

        // Obtem um ID para o novo processo
        int idNovoProcesso = getIdNovoProcesso();
        if (idNovoProcesso == -1) {
            idNovoProcesso = pageTableArrayList.size();
        }

        //Cria um novo processo e uma nova tabela de página
        Processo p = new Processo(idNovoProcesso, processName, tamanhoTexto, tamanhoDados);
        p.createPageTable();
        PageTable pt = p.getTabelaPagina();     //Pega a tabela de página do processo

        ArrayList<Integer> quadrosTexto = new ArrayList<>();
        //Confere se o processo está sendo criado a partir de algum programa já utilizado
        Processo duplicatedProcess = searchDuplicatedProcessToAdd(processName, tamanhoTexto, tamanhoDados);
        if (duplicatedProcess != null) {    //Caso haja um processo repetido, utiliza os mesmos quadros de texto
            int nroQuadrosTexto = tamanhoTexto / 32;  // Quantidade de quadros necessários para alocar o segmento de texto
            if (tamanhoTexto % 32 != 0) {           //Caso o tamanho de texto não seja multiplo de 32 precisará de um quadro a mais
                nroQuadrosTexto++;
            }
            ArrayList<Integer> quadrosProcessoOriginal = new ArrayList<>();
            // Insere em quadrosProcessoOriginal todos os índices dos quadros do segmento de texto do processo original
            for (int i = 0; i < nroQuadrosTexto; i++) {
                quadrosProcessoOriginal.add(duplicatedProcess.getTabelaPagina().getLinha(i).getQuadro() / 32);
            }
            // Insere os quadros de texto do processo original na tabela de página do processo duplicado
            p.getTabelaPagina().setTexto(quadrosProcessoOriginal);
        } else {    //Caso não haja um processo repetido, aloca novos quadros de texto
            quadrosTexto = bf.allocate(tamanhoTexto, mapaBits);
            if (quadrosTexto == null || quadrosTexto.size() == 0) {
                throw new MemoryOverflowException("ERRO: Não há memória suficiente para alocar o processo");
            }
            editBitMap(quadrosTexto, true);   //Passa o estado dos quadros alocados para true
        }

        //Aloca os quadros para os dados estáticos e para a pilha
        ArrayList<Integer> quadrosData = bf.allocate(tamanhoDados, mapaBits);
        if (quadrosData == null) {
            throw new MemoryOverflowException("ERRO: Não há memória suficiente para alocar o processo");
        }
        editBitMap(quadrosData, true);
        ArrayList<Integer> quadrosPilha = bf.allocate(64, mapaBits);
        if (quadrosPilha == null) {
            throw new MemoryOverflowException("ERRO: Não há memória suficiente para alocar o processo");
        }
        editBitMap(quadrosPilha, true);

        //Adiciona as páginas na tabela de página
        pt.setTexto(quadrosTexto);
        pt.setDados(quadrosData, tamanhoDados);
        pt.setPilha(quadrosPilha);

        //Subtrai o espaço que será ocupado do espaço livre disponível
        int espacoOcupado = (quadrosData.size() + quadrosTexto.size() + quadrosPilha.size()) * 32;
        espacoLivre -= espacoOcupado;

        //Adiciona a nova tabela de página e o novo processo nas listas de tabela e de processos respectivamente
        if(idNovoProcesso == processoArrayList.size()){
            pageTableArrayList.add(idNovoProcesso, pt);
            processoArrayList.add(idNovoProcesso, p);
        }else{
            pageTableArrayList.set(idNovoProcesso, pt);
            processoArrayList.set(idNovoProcesso, p);
        }


        return idNovoProcesso;
    }

    @Override
    public int allocateMemoryToProcess(int processId, int size) throws InvalidProcessException, StackOverflowException,
            MemoryOverflowException, IllegalArgumentException {
        if (size < 0) {
            throw new IllegalArgumentException("ERRO: Tamanho do bloco de memória inválido");
        }

        // Confere se o ID do processo é válido
        if (processId < 0 || processId >= processoArrayList.size()) {
            throw new InvalidProcessException("ERRO: ID de processo inválido");
        }

        Processo p = processoArrayList.get(processId);
        if (p == null) {
            throw new InvalidProcessException("ERRO: ID de processo inválido");
        }

        //Confere se quantidade de heap está disponível
        int maxTamanhoHeap = 960 - p.getTamanhoSegmentoTexto() - p.getTamanhoSegmentoDados();
        if (size > maxTamanhoHeap) {
            throw new MemoryOverflowException("ERRO: Quantidade de memória maior que a disponível" +
                    " para o processo!");
        }
        // pega a tabela de página do processo
        PageTable tabelaDoProcesso = pageTableArrayList.get(processId);
        int realSize = tabelaDoProcesso.getRealSizeHeap(size, maxTamanhoHeap);    //Pega a quantidade de memória que efetivamente necessitará de novos quadros
        if (realSize == -1) {
            throw new MemoryOverflowException("ERRO: Não há memória disponível");
        }
        if (realSize > 0) {
            ArrayList<Integer> alocacaoHeap = bf.allocate(realSize, mapaBits);  //Aloca a quantidade de memória
            if (alocacaoHeap == null) {
                throw new MemoryOverflowException("ERRO: Não há memória disponível");
            }

            try {
                tabelaDoProcesso.setHeap(alocacaoHeap);     //Adiciona as páginas de heap na tabela do processo
            } catch (StackOverflowException soe) {
                throw new StackOverflowException(soe.getMessage());
            }
            editBitMap(alocacaoHeap, true);   //Passa o estado dos quadros utilizados para true
            espacoLivre -= alocacaoHeap.size() * 32;
        }

        return size;
    }

    @Override
    public int freeMemoryFromProcess(int processId, int size) throws InvalidProcessException, NoSuchMemoryException,
            IllegalArgumentException {
        if (size < 0) {
            throw new IllegalArgumentException("ERRO: Tamanho do bloco de memória inválido");
        }

        // Confere se o ID do processo é válido
        if (processId < 0 || processId >= pageTableArrayList.size()) {
            throw new InvalidProcessException("ERRO: Processo inválido");
        }
        Processo p = processoArrayList.get(processId);      //Pega o determinado processo
        if (p == null)
            throw new InvalidProcessException("ERRO: ID de processo inválido");

        //Libera as páginas de tabela e pega os quadros liberados
        ArrayList<Integer> quadrosParaLiberacao = p.getTabelaPagina().freeMemoryFromHeap(size);
        if (quadrosParaLiberacao == null) {
            throw new NoSuchMemoryException("ERRO: Quantidade de memória inválida");
        }

        // coloca os bits correspondentes aos quadros liberados como false no mapa de bits
        editBitMap(quadrosParaLiberacao, false);
        espacoLivre += quadrosParaLiberacao.size() * 32;

        return size;
    }

    @Override
    public void excludeProcessFromMemory(int processId) throws InvalidProcessException {
        if (processId < 0 || processId >= pageTableArrayList.size()) {
            throw new InvalidProcessException("ERRO: Processo inválido");
        }
        Processo p = processoArrayList.get(processId);
        if (p == null) {
            throw new InvalidProcessException("ERRO: ID de processo inválido");
        }

        PageTable pt = p.getTabelaPagina();
        ArrayList<Integer> quadrosParaLiberacao;

        //Confere se o processo que será excluido compartilha quadros de texto com outros processos
        if (searchDuplicatedProcessToRemove(p.getId(), p.getNome(), p.getTamanhoSegmentoTexto(),
                p.getTamanhoSegmentoDados()) != null) {
            quadrosParaLiberacao = pt.getQuadrosProcessoDuplicado(p.getTamanhoSegmentoTexto());
        } else {
            quadrosParaLiberacao = pt.getQuadros();
        }

        for (int i = 0; i < quadrosParaLiberacao.size(); i++) {
            quadrosParaLiberacao.set(i, quadrosParaLiberacao.get(i) / 32);
        }

        editBitMap(quadrosParaLiberacao, false);

        espacoLivre += quadrosParaLiberacao.size() * 32;
        processoArrayList.set(processId, null);
        pageTableArrayList.set(processId, null);
    }

    @Override
    public void resetMemory() {
        // Limpa a lista de tabelas de página e a lista de processos
        pageTableArrayList.clear();
        processoArrayList.clear();
        espacoLivre = nroQuadros * 32;
        // Preenche o mapa de bits com false (todos os quadros estão livres)
        Arrays.fill(mapaBits, false);
    }

    @Override
    public int getPhysicalAddress(int processId, int logicalAddress) throws InvalidProcessException, InvalidAddressException {
        if (processId < 0 || processId >= pageTableArrayList.size()) {
            throw new InvalidProcessException("ERRO: Número de processo inválido");
        }
        if (logicalAddress < 0 || logicalAddress > 1023) {
            throw new InvalidProcessException("ERRO: O endereco lógico deve ser um valor entre 0 e 1023");
        }
        //Converte endereço para binário e garante que o endereço terá 10 digitos
        String enderecoBin = Integer.toBinaryString(logicalAddress);
        while (enderecoBin.length() != 10) {
            enderecoBin = "0" + enderecoBin;
        }

        //Pega segmento de página e de deslocamento, e os converte para decimal
        String paginaStr = enderecoBin.substring(0, 5);
        int paginaInt = Integer.parseInt(paginaStr, 2);
        String deslocamentoStr = enderecoBin.substring(5, 10);
        int deslocamentoInt = Integer.parseInt(deslocamentoStr, 2);

        //Encontra a página na tabela de páginas
        ItemTabelaDePagina itp = processoArrayList.get(processId).getTabelaPagina().getLinha(paginaInt);
        int quadro;

        // Caso exista um quadro alocado para aquela página, pega o endereço inicial do quadro
        if (itp.getQuadro() == -1) {
            throw new InvalidAddressException("ERRO: Endereço lógico inválido");
        } else {
            quadro = itp.getQuadro();
        }

        // retorna o endereço inicial do quadro somado ao valor do deslocamento
        return quadro + deslocamentoInt;
    }

    @Override
    public String getBitMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < mapaBits.length; i++) {
            sb.append("(").append(i * 32).append(") ").append(mapaBits[i]);
            if (i != mapaBits.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String getPageTable(int processId) throws InvalidProcessException {
        if (processId < 0 || processId >= pageTableArrayList.size()) {
            throw new InvalidProcessException("ERRO: Processo inválido");
        }
        Processo p = processoArrayList.get(processId);
        //Confere se processo existe
        if (p == null) {
            throw new InvalidProcessException("ERRO: ID de processo inválido");
        }
        return p.getTabelaPagina().toString();
    }

    @Override
    public String[] getProcessList() {
        ArrayList<String> processList = new ArrayList<>();
        for (Processo p : processoArrayList) {
            if (p != null) {
                processList.add(String.format("%-30s %-2d", p.getNome(), p.getId()));
            }
        }
        return processList.toArray(new String[0]);
    }

    /**
     * Pega um identificador para o novo processo
     * Confere se tem algum identificador livre
     *
     * @return o identificador disponivel ou menos -1 caso seja necessario criar um novo identificador
     */
    private int getIdNovoProcesso() {
        for (int i = 0; i < processoArrayList.size(); i++) {
            if (processoArrayList.get(i) == null) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Confere se o processo esta sendo criado a partir de um programa repetido
     *
     * @param processName  nome do processo a ser adicionado
     * @param tamanhoTexto tamanho do segmento de texto do processo a ser adicionado
     * @param tamanhoDados tamanho do segmento de dados do processo a ser adicionado
     * @return o processo duplicado, caso exista
     */
    private Processo searchDuplicatedProcessToAdd(String processName, int tamanhoTexto, int tamanhoDados) {
        for (Processo processo : processoArrayList) {
            if (processo != null && processo.getNome().equals(processName) && processo.getTamanhoSegmentoTexto() == tamanhoTexto
                    && processo.getTamanhoSegmentoDados() == tamanhoDados) {
                return processo;
            }
        }
        return null;
    }

    /**
     * Confere, para remover um processo, se existe outro processo que compartilha o mesmo programa.
     *
     * @param processID    o ID do processo que sera removido
     * @param processName  o nome do processo a sera removido
     * @param tamanhoTexto o tamanho do segmento de texto do processo que sera removido
     * @param tamanhoDados o tamanho do segmento de dados do processo que sera removido
     * @return o processo duplicado, caso exista
     */
    private Processo searchDuplicatedProcessToRemove(int processID, String processName, int tamanhoTexto, int tamanhoDados) {
        for (Processo processo : processoArrayList) {
            if (processo != null && processo.getId() != processID && processo.getNome().equals(processName) &&
                    processo.getTamanhoSegmentoTexto() == tamanhoTexto &&
                    processo.getTamanhoSegmentoDados() == tamanhoDados) {
                return processo;
            }
        }
        return null;
    }

    /**
     * Define quadros como "utilizados" (true) ou "livres" (false)
     * @param quadros os index dos quadros que serao definidos como utilizados
     * @param value os novos valores para aqueles quadros
     */
    private void editBitMap(ArrayList<Integer> quadros, boolean value) {
        quadros.forEach((quadro) -> {
            mapaBits[quadro] = value;
        });
    }
}
