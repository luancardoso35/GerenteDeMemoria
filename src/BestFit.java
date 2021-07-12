import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class BestFit {

    public BestFit() {

    }

    /*
    Retorna um ArrayList com os index dos quadros a serem utilzados para alocar uma certa quantidade de bytes
     */
    public ArrayList<Integer> allocate(int bytes, boolean[] mapaBits) {
        ArrayList<int[]> espacosContiguos = new ArrayList<>();

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

        int[] menorBuraco = buscaBuraco(true, espacosContiguos, bytes);
        int indexMenorBuraco = menorBuraco[0];
        int tamanhoMenorBuraco = menorBuraco[1];
        ArrayList<Integer> indexBuracosNaoContiguos = new ArrayList<>();
        if (tamanhoMenorBuraco == 0) {      //Caso não encontre um buraco contíguo grande o suficiente
            ArrayList<int[]> buracosNaoContiguos = new ArrayList<>();
            do{
                if (tamanhoMenorBuraco == 0) {
                    int[] maiorBuraco = buscaBuraco(false, espacosContiguos, bytes);
                    int indexMaiorBuraco = maiorBuraco[0];
                    int tamanhoMaiorBuraco = maiorBuraco[1];
                    buracosNaoContiguos.add(maiorBuraco);  //Adiciona index
                    bytes -= tamanhoMaiorBuraco;

                    //Caso não encontre mais buracos retorna nulo
                    if (indexMaiorBuraco == -1 && indexMenorBuraco == -1) {
                        return null;
                    }
                }

                menorBuraco = buscaBuraco(true, espacosContiguos, bytes);
                indexMenorBuraco = menorBuraco[0];
                tamanhoMenorBuraco = menorBuraco[1];
                bytes -= tamanhoMenorBuraco;
                if (bytes <= 0) {
                    buracosNaoContiguos.add(menorBuraco);
                }
            }while (bytes > 0);

            for (int[] item: buracosNaoContiguos) {
                int tamanho = item[1]/32;
                for (int i = item[0]; i < tamanho; i++){
                    indexBuracosNaoContiguos.add(i);
                    mapaBits[i] = true;
                }
            }
            Collections.sort(indexBuracosNaoContiguos);
            return indexBuracosNaoContiguos;
        } else {    //Caso encontre um boraco contíguo grande o suficiente
            int quadrosNecessarios = (int) Math.ceil(bytes/32.0);
            ArrayList<Integer> buracosContiguos = new ArrayList<>();

            for (int i = indexMenorBuraco; i < indexMenorBuraco + quadrosNecessarios; i++) {
                buracosContiguos.add(i);
                mapaBits[i] = true;
            }
            return buracosContiguos;
        }
    }

    /*
     * Busca o menor ou maior buraco
     * @param menor espeficica se o método vai buscar o menor (true) ou o maior buraco (false)
     */
    private int[] buscaBuraco(boolean menor, ArrayList<int[]> espacosContiguos, int bytes) {
        if (menor) {
            int menorBuraco = 0;
            int tamanho;
            int indexMenorBuraco = -1;
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
        } else {
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


