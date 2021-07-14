import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws MemoryOverflowException, FileFormatException, NoSuchFileException, InvalidProcessException, StackOverflowException, InvalidAddressException {
        Scanner sc = new Scanner(System.in);
        int quadros;
        while (true) {
            System.out.println("Insira a quantidade de quadros desejados (32, 64 ou 128");
            quadros = Integer.parseInt(sc.nextLine());
            if (quadros == 32 || quadros == 64 || quadros == 128) {
                break;
            }
            System.out.println("Insira um número de quadros válido");
            quadros = Integer.parseInt(sc.nextLine());
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
                    } catch (NoSuchFileException e) {
                        System.out.println("ERRO: Arquivo não encontrado!");
                        break;
                    } catch (FileFormatException e) {
                        System.out.println("ERRO: Arquivo não tem o formato adequado!");
                        break;
                    } catch (MemoryOverflowException e) {
                        System.out.println("ERRO: Não há memória suficiente para carregar o processo!");
                        break;
                    }

                    System.out.println();
                    System.out.println("Processo carregado com sucesso na memória com identificador " + processId);
                }
                case "2" -> {
                    int processId;
                    System.out.print("Insira o identificador do processo: ");
                    try {
                        processId = Integer.parseInt(sc.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("ERRO: O identificador do processo deve ser um número inteiro!");
                        break;
                    }

                    int tamanhoBloco;
                    System.out.print("Insira o tamanho do bloco de memória a ser alocado: ");
                    try {
                        tamanhoBloco = Integer.parseInt(sc.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("ERRO: O tamanho do bloco de memória deve ser um número inteiro!");
                        break;
                    }

                    int memoriaAlocada;
                    try {
                        memoriaAlocada = management.allocateMemoryToProcess(processId, tamanhoBloco);
                    } catch (InvalidProcessException e) {
                        System.out.println("ERRO: Identificador do processo inválido!");
                        break;
                    } catch (StackOverflowException e) {
                        System.out.println("ERRO: Tamanho do bloco de memória maior do que" +
                                " a quantidade de memória disponível para o processo!");
                        break;
                    } catch (MemoryOverflowException e) {
                        System.out.println("ERRO: Não há memória suficiente para atender a solicitação!");
                        break;
                    }

                    System.out.println();
                    System.out.println(memoriaAlocada + " byte(s) alocado(s) com sucesso para o processo com identificador " + processId);
                }
                case "3" -> {
                    int processId;
                    System.out.print("Insira o identificador do processo: ");
                    try {
                        processId = Integer.parseInt(sc.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("ERRO: O identificador do processo deve ser um número inteiro!");
                        break;
                    }

                    int tamanhoBloco;
                    System.out.print("Insira a quantidade de memória a ser liberado: ");
                    try {
                        tamanhoBloco = Integer.parseInt(sc.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("ERRO: A quantidade de memória deve ser um número inteiro!");
                        break;
                    }
                    if (tamanhoBloco <= 0) {
                        System.out.println("ERRO: A quantidade de memória deve ser maior que 0!");
                        break;
                    }

                    int memoriaLiberada;
                    try {
                        memoriaLiberada = management.freeMemoryFromProcess(processId, tamanhoBloco);
                    } catch (InvalidProcessException e) {
                        System.out.println("ERRO: Identificador do processo inválido!");
                        break;
                    } catch (NoSuchMemoryException e) {
                        System.out.println("ERRO: A quantidade de memória é maior do que a quantidade" +
                                " de memória dinâmica alocada para o processo!");
                        break;
                    }

                    System.out.println();
                    System.out.println(memoriaLiberada + " byte(s) liberado(s) com sucesso do processo" +
                            " com identificador " + processId);
                }
                case "4" -> {
                    int processId;
                    System.out.print("Insira o identificador do processo: ");
                    try {
                        processId = Integer.parseInt(sc.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("ERRO: O identificador do processo deve ser um número inteiro!");
                        break;
                    }

                    try {
                        management.excludeProcessFromMemory(processId);
                    } catch (InvalidProcessException e) {
                        System.out.println("ERRO: Identificador do processo inválido!");
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
                        System.out.println("ERRO: O identificador do processo deve ser um número inteiro!");
                        break;
                    }

                    int endLogico;
                    System.out.print("Insira o endereço lógico: ");
                    endLogico = Integer.parseInt(sc.nextLine());
                    int endFisico;
                    try {
                        endFisico = management.getPhysicalAddress(processId, endLogico);
                    } catch (InvalidProcessException e) {
                        System.out.println("ERRO: Identificador do processo inválido!");
                        break;
                    } catch (InvalidAddressException e) {
                        System.out.println("ERRO: O endereço lógico inserido é inválido!");
                        break;
                    }

                    System.out.println();
                    System.out.println("O endereço físico é " + endFisico);
                }
                case "7" -> {
                    String mapa = management.getBitMap();
                    System.out.print(mapa);
                }
                case "8" -> {
                    int processId;
                    System.out.print("Insira o identificador do processo: ");
                    try {
                        processId = Integer.parseInt(sc.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("ERRO: Identificador do processo inválido!");
                        break;
                    }

                    String tabela;
                    try {
                        tabela = management.getPageTable(processId);
                    } catch (InvalidProcessException e) {
                        System.out.println("ERRO: Identificador do processo inválido!");
                        break;
                    }

                    System.out.println();
                    System.out.print(tabela);
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
