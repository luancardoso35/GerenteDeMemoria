import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws MemoryOverflowException, FileFormatException, NoSuchFileException, InvalidProcessException, StackOverflowException, InvalidAddressException {
        Scanner sc = new Scanner(System.in);
        int quadros;
        while (true) {
            System.out.print("Insira a quantidade de quadros desejados (32, 64 ou 128): ");
            try {
                quadros = Integer.parseInt(sc.nextLine());
                if (quadros == 32 || quadros == 64 || quadros == 128) {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("ERRO: Insira um número de quadros válido!");
                continue;
            }
            System.out.println("ERRO: Insira um número de quadros válido!");
        }

        ManagementInterfaceImpl management = new ManagementInterfaceImpl((short) quadros);

        String opcao;
        do {
            System.out.println("----------Opções----------");
            System.out.println("1) Carregar processo para a memória");
            System.out.println("2) Alocar memória dinâmica para um processo");
            System.out.println("3) Liberar memória dinâmica ocupada por um processo");
            System.out.println("4) Excluir processo da memória");
            System.out.println("5) Excluir TODOS os processos da memória");
            System.out.println("6) Traduzir um endereço lógico de um processo para um endereço físico");
            System.out.println("7) Obter o mapa de bits");
            System.out.println("8) Obter a tabela de páginas de um processo");
            System.out.println("9) Obter a lista de processos");
            System.out.println("q() Sair do programa");

            System.out.print("Digite a opção: ");
            opcao = sc.nextLine();
            System.out.println();

            switch (opcao) {
                case "1" -> {
                    System.out.print("Insira o nome do arquivo: ");
                    String nomeArquivo = sc.nextLine();

                    int processId;
                    try {
                        processId = management.loadProcessToMemory(nomeArquivo);
                        if (processId == -1) {
                            System.out.println("ERRO: erro de leitura");
                            break;
                        }
                    } catch (NoSuchFileException | FileFormatException | MemoryOverflowException e) {
                        System.out.println(e.getMessage());
                        break;
                    }

                    System.out.println();
                    System.out.println("Processo carregado com sucesso na memória com identificador " + processId);
                }
                case "2" -> {
                    int processId;
                    System.out.print("Insira o identificador do processo: ");
                    try {
                        String processNumber = sc.nextLine();
                        if (!processNumber.equals("")) {
                            processId = Integer.parseInt(processNumber);
                        } else {
                            System.out.println("ERRO: número de processo inválido");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(e.getMessage());
                        break;
                    }

                    int tamanhoBloco;
                    System.out.print("Insira o tamanho do bloco de memória a ser alocado: ");
                    try {
                        tamanhoBloco = Integer.parseInt(sc.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println(e.getMessage());
                        break;
                    }

                    int memoriaAlocada;
                    try {
                        memoriaAlocada = management.allocateMemoryToProcess(processId, tamanhoBloco);
                    } catch (InvalidProcessException | StackOverflowException | MemoryOverflowException e) {
                        System.out.println(e.getMessage());
                        break;
                    }

                    System.out.println();
                    System.out.println(memoriaAlocada + " bytes alocados com sucesso para o processo com identificador " + processId);
                }
                case "3" -> {
                    int processId;
                    System.out.print("Insira o identificador do processo: ");
                    try {
                        processId = Integer.parseInt(sc.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println(e.getMessage());
                        break;
                    }

                    int tamanhoBloco;
                    System.out.print("Insira a quantidade de memória a ser liberada: ");
                    try {
                        tamanhoBloco = Integer.parseInt(sc.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                    if (tamanhoBloco <= 0) {
                        System.out.println("ERRO: A quantidade de memória deve ser maior que 0!");
                        break;
                    }

                    int memoriaLiberada;
                    try {
                        memoriaLiberada = management.freeMemoryFromProcess(processId, tamanhoBloco);
                    } catch (InvalidProcessException | NoSuchMemoryException e) {
                        System.out.println(e.getMessage());
                        break;
                    }

                    System.out.println();
                    System.out.println(memoriaLiberada + " bytes liberados com sucesso do processo" +
                            " com identificador " + processId);
                }
                case "4" -> {
                    int processId;
                    System.out.print("Insira o identificador do processo: ");
                    try {
                        processId = Integer.parseInt(sc.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println(e.getMessage());
                        break;
                    }

                    try {
                        management.excludeProcessFromMemory(processId);
                    } catch (InvalidProcessException e) {
                        System.out.println(e.getMessage());
                        break;
                    }

                    System.out.println();
                    System.out.println("Processo com identificador " + processId + " excluído da memória");
                }
                case "5" -> {
                    management.resetMemory();
                    System.out.println();
                    System.out.println("Todos os processos foram excluídos da memória!");
                }
                case "6" -> {
                    int processId;
                    System.out.print("Insira o identificador do processo: ");
                    try {
                        processId = Integer.parseInt(sc.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println(e.getMessage());
                        break;
                    }

                    int endLogico;
                    System.out.print("Insira o endereço lógico: ");
                    endLogico = Integer.parseInt(sc.nextLine());
                    int endFisico;
                    try {
                        endFisico = management.getPhysicalAddress(processId, endLogico);
                    } catch (InvalidProcessException | InvalidAddressException e) {
                        System.out.println(e.getMessage());
                        break;
                    }

                    System.out.println();
                    System.out.println("O endereço físico é " + endFisico);
                }
                case "7" -> {
                    String mapa = management.getBitMap();
                    System.out.println(mapa);
                }
                case "8" -> {
                    int processId;
                    System.out.print("Insira o identificador do processo: ");
                    try {
                        processId = Integer.parseInt(sc.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println(e.getMessage());
                        break;
                    }

                    String tabela;
                    try {
                        tabela = management.getPageTable(processId);
                    } catch (InvalidProcessException e) {
                        System.out.println(e.getMessage());
                        break;
                    }

                    System.out.println();
                    System.out.println(tabela);
                }
                case "9" -> {
                    String[] processos = management.getProcessList();
                    System.out.printf("%-30s %-2s%n", "Nome do programa", "Identificador do processo");
                    for (String processo : processos)
                        System.out.println(processo);
                }
                case "q()" -> System.out.println("Tchau!");
                default -> System.out.println("ERRO: Insira uma opção válida!");
            }

        } while (!opcao.equals("q()"));
    }
}