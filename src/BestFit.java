import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Luan Cesar Cardoso, 11340272
 * @author Matheo Bellini Marumo, 11315606
 * @author Matheus Oliveira Ribeiro da Silva, 11315096
 */

public class BestFit {
    /**
     * Obtem um ArrayList com os indices dos quadros a serem utilzados para alocar uma certa quantidade de bytes com base
     * no algoritmo Best-fit
     * @param bytes a quantidade de bytes a ser alocada
     * @param mapaBits o mapa de bits que corresponde aos quadros
     * @return uma lista com os indices dos quadros a serem alocados, ou null caso nao haja buracos
     * para alocacao
     */
    public ArrayList<Integer> allocate(int bytes, boolean[] mapaBits) {
        ArrayList<int[]> espacosContiguos = new ArrayList<>();

        //Procura por espaços contíguos e armazena o inicio e fim de cada um
        for (int i = 0; i < mapaBits.length; i++) {
            int fimEspaco = -1;
            int inicioEspaco = i;
            if (!mapaBits[i]) {
                while (i < mapaBits.length && !mapaBits[i]) {
                    i++;
                }
                fimEspaco = i;
                espacosContiguos.add(new int[]{inicioEspaco, fimEspaco});
            }
        }

        //Busca o menor buraco para alocar aquela quantidade de bytes
        int[] menorBuraco = buscaBuraco(true, espacosContiguos, bytes);
        int indexMenorBuraco = menorBuraco[0];  //indice do menor buraco
        int tamanhoMenorBuraco = menorBuraco[1];    //tamanho do menor buraco
        ArrayList<Integer> indexBuracosNaoContiguos = new ArrayList<>();
        if (tamanhoMenorBuraco == 0) {      //Caso não encontre um buraco contíguo grande o suficiente
            ArrayList<int[]> buracosNaoContiguos = new ArrayList<>();
            do{
                if (tamanhoMenorBuraco == 0) {      //Caso nao tenha encontrado um menor buraco para alocar a quantidade de bytes
                    //Busca o maior buraco contíguo que não irá alocar todos os bytes
                    int[] maiorBuraco = buscaBuraco(false, espacosContiguos, bytes);
                    int indexMaiorBuraco = maiorBuraco[0];
                    int tamanhoMaiorBuraco = maiorBuraco[1];
                    buracosNaoContiguos.add(maiorBuraco);  //Adiciona index
                    bytes -= tamanhoMaiorBuraco;    //Subtrai do total de bytes

                    //Caso não encontre mais buracos retorna nulo
                    if (indexMaiorBuraco == -1 && indexMenorBuraco == -1) {
                        return null;
                    }
                }
                //Busca novamente o menor buraco possível para alocar a nova quantidade de bytes
                menorBuraco = buscaBuraco(true, espacosContiguos, bytes);
                indexMenorBuraco = menorBuraco[0];
                tamanhoMenorBuraco = menorBuraco[1];
                bytes -= tamanhoMenorBuraco;
                if (bytes <= 0) {
                    buracosNaoContiguos.add(menorBuraco);
                }
            } while (bytes > 0);    //Enquanto não tiver encontrado espaços suficientes para alocar todos os bytes


            for (int[] item: buracosNaoContiguos) {
                int tamanho = item[1]/32;       //Divide o numero do quadro por 32 para encontrar o indice
                for (int i = item[0]; i < tamanho; i++){
                    indexBuracosNaoContiguos.add(i);    //Adiciona indice do quadro
                }
            }
            Collections.sort(indexBuracosNaoContiguos);     //Ordena
            return indexBuracosNaoContiguos;

        } else {    //Caso encontre um boraco contíguo grande o suficiente
            int quadrosNecessarios = (int) Math.ceil(bytes/32.0);   //Quantidade de quadros necessários para alocar os bytes
            ArrayList<Integer> buracosContiguos = new ArrayList<>();

            for (int i = indexMenorBuraco; i < indexMenorBuraco + quadrosNecessarios; i++) {
                buracosContiguos.add(i);    //Adiciona cada índice de cada quadro
            }
            return buracosContiguos;
        }
    }

    /**
     * Obtém o menor ou maior buraco
     * @param menor especifica se o metodo vai buscar o menor (true) ou o maior buraco (false)
     * @param espacosContiguos a lista com os espaços contíguos
     * @param bytes o tamanho do dado a ser alocado
     * @return array de inteiros onde a primeira posicaoo contem o indice do quadro e a segunda contem o tamanho
     */
    private int[] buscaBuraco(boolean menor, ArrayList<int[]> espacosContiguos, int bytes) {
        if (menor) {
            int menorBuraco = 0;
            int tamanho;
            int indexMenorBuraco = -1;
            //Para cada espaço contiguo confere o seu tamanho e busca o menor buraco que aloque a quantidade de bytes
            for (int[] i : espacosContiguos) {
                tamanho = (i[1] - i[0]) * 32;
                if (tamanho >= bytes) {
                    if (menorBuraco == 0) {
                        menorBuraco = tamanho;
                        indexMenorBuraco = i[0];
                    } else {
                        if (tamanho < menorBuraco) {
                            menorBuraco = tamanho;
                            indexMenorBuraco = i[0];
                        }
                    }
                }
            }
            return new int[]{indexMenorBuraco, menorBuraco};
        } else {    //Busca o maior buraco que não aloque a quantidade de bytes
            int maiorBuraco = 0;
            int tamanho;
            int indexMaiorBuraco = -1;
            for (int[] i : espacosContiguos) {
                tamanho = (i[1] - i[0]) * 32;
                if (maiorBuraco == 0) {
                    maiorBuraco = tamanho;
                    indexMaiorBuraco = i[0];
                }else if (tamanho > maiorBuraco) {
                        maiorBuraco = tamanho;
                        indexMaiorBuraco = i[0];
                }
                }
                return new int[]{indexMaiorBuraco, maiorBuraco};
            }
        }
    }


