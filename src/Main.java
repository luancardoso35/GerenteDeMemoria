import java.util.Scanner;

/**
 * @author Luan Cesar Cardoso, 11340272
 * @author Matheo Bellini Marumo, 11315606
 * @author Matheus Oliveira Ribeiro da Silva, 11315096
 */

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int quadros;
        // O usuário insere o número de quadros (só aceita 32, 64 ou 128 quadros)
        while (true) {
            System.out.print("Insira a quantidade de quadros desejados (32, 64 ou 128): ");
            try {
                quadros = Integer.parseInt(sc.nextLine());
                if (quadros == 32 || quadros == 64 || quadros == 128) {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("ERRO: Insira um numero de quadros válido!");
                continue;
            }
            System.out.println("ERRO: Insira um numero de quadros válido!");
        }

        ManagementInterfaceImpl management = new ManagementInterfaceImpl((short) quadros);

        // Capta o comando inserido pelo usuário e verifica qual método a ser invocado, dado o comando
        String opcao;
        do {
            System.out.print("> ");
            opcao = sc.nextLine();
            System.out.println();

            if (opcao.matches("loadProcessToMemory\\s\\w+\\.\\w+")) {
                String processName = opcao.split(" ")[1];
                int processId;

                try {
                    processId = management.loadProcessToMemory(processName);
                    System.out.println("Processo carregado com sucesso! ID do processo: " + processId);
                } catch (NoSuchFileException | FileFormatException | MemoryOverflowException e) {
                    System.out.println(e.getMessage());
                }
            } else if (opcao.matches("allocateMemoryToProcess\\s\\d+\\s\\d+")) {
                int processId = Integer.parseInt(opcao.split(" ")[1]);
                int size = Integer.parseInt(opcao.split(" ")[2]);

                try {
                    int allocated = management.allocateMemoryToProcess(processId, size);
                    System.out.println(allocated + " bytes alocados para o processo " + processId);
                } catch (InvalidProcessException | StackOverflowException | MemoryOverflowException e) {
                    System.out.println(e.getMessage());
                }
            } else if (opcao.matches("freeMemoryFromProcess\\s\\d+\\s\\d+")) {
                int processId = Integer.parseInt(opcao.split(" ")[1]);
                int size = Integer.parseInt(opcao.split(" ")[2]);

                try {
                    int released = management.freeMemoryFromProcess(processId, size);
                    System.out.println(released + " bytes de memória liberados");
                } catch (InvalidProcessException | NoSuchMemoryException e) {
                    System.out.println(e.getMessage());
                }
            } else if (opcao.matches("excludeProcessFromMemory\\s\\d+")) {
                int processId = Integer.parseInt(opcao.split(" ")[1]);

                try {
                    management.excludeProcessFromMemory(processId);
                    System.out.println("Processo " + processId + " excluido com sucesso!");
                } catch (InvalidProcessException e) {
                    System.out.println(e.getMessage());
                }
            } else if (opcao.matches("resetMemory")) {
                    management.resetMemory();
                    System.out.println("Memória resetada!");
            } else if (opcao.matches("getPhysicalAddress\\s\\d+\\s\\d+")) {
                int processId = Integer.parseInt(opcao.split(" ")[1]);
                int logicalAddress = Integer.parseInt(opcao.split(" ")[2]);

                try {
                    int physicalAddress = management.getPhysicalAddress(processId, logicalAddress);
                    System.out.println("Endereço físico referente ao endereço " + logicalAddress + " = " + physicalAddress);
                } catch (InvalidProcessException | InvalidAddressException e) {
                    System.out.println(e.getMessage());
                }
            } else if (opcao.equals("getBitMap")) {
                String mapa = management.getBitMap();
                System.out.println("Mapa de bits:");
                System.out.println(mapa);
            } else if (opcao.matches("getPageTable\\s\\d+")) {
                int processId = Integer.parseInt(opcao.split(" ")[1]);

                try {
                    String tabela = management.getPageTable(processId);
                    System.out.println("Tabela de página do processo " + processId + ":");
                    System.out.println(tabela);
                } catch (InvalidProcessException e) {
                    System.out.println(e.getMessage());
                }
            } else if (opcao.equals("getProcessList")) {
                String[] processList = management.getProcessList();
                System.out.printf("%-30s %-2s%n", "Nome do programa", "Identificador do processo");
                for (String processo : processList)
                    System.out.println(processo);
            } else if (opcao.equals("q()")) {
                System.out.println("Finalizando..");
                System.exit(0);
            } else if (opcao.equals("help")) {
                showCommands();
            } else {
                System.out.println("Comando inválido.\n");
            }
        } while (!opcao.equals("q()"));
    }

    private static void showCommands() {
        System.out.println("---------- Comandos disponíveis ----------");
        System.out.println("> loadProcessToMemory <nome_arquivo> -> carrega um processo para " +
                "memória de acordo com a especificação do arquivo");
        System.out.println("> allocateMemoryToProcess <IdProcesso> <tamanho> -> Aloca o tamanho requisitado de " +
                "bytes de memória para o processo com o ID especificado");
        System.out.println("> freeMemoryFromProcess <IdProcesso> <tamanho> -> Libera o tamanho requisitado de" +
                "bytes de memória para o processo com o ID especificado");
        System.out.println("> excludeProcessFromMemory <IdProcesso> -> Exclui o processo informado da memória");
        System.out.println("> resetMemory -> Reestabelece a memória ao seu estado inicial");
        System.out.println("> getPhysicalAddress <IdProcesso> <endereço> -> " +
                " Traduz o endereço lógico requisitado, do processo especificado, para um endereço físico");
        System.out.println("> getBitMap -> Mostra o mapa de bits da memória");
        System.out.println("> getPageTable <IdProcesso> -> Mostra a tabela de página do processo especificado");
        System.out.println("> getProcessList -> Mostra todos os processos carregados, junto com seus " +
                "identificadores");
    }
}