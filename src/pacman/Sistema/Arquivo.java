package pacman.Sistema;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
* Esta classe é a responsável pelo manejamento de todos os acessos a disco em si, seja para leitura ou escrita.
* 
* @see pacman.Sistema.Blueprint
* @see pacman.Engine.Jogo
* 
* @author diego-fleury
* @version v1.0
* @since Alpha v0.1
*/
public class Arquivo {

    private String filepath;

    /**
     * Construtor vazio de uma classe cuja unica função é a interface com
     * arquivos.
     */
    public Arquivo() {

    }

    /**
     * Maneira simples de passar o nome do arquivo a ser trabalhado no momento.
     *
     * @param pfilepath nome/caminho do arquivo.
     */
    private void SetFile(String pfilepath) {
        String currentDirectory = System.getProperty("user.dir");
        System.out.println("Diretorio atual: " + currentDirectory);
        filepath = pfilepath;
    }

    /**
     * Funcao responsavel pela leitura do arquivo CSV que contem metadados da
     * configuracao dos Nos de um mapa. A informação está organizada no formato
     * de 6 numeros inteiros linha a linha, de modo que o ID atribuido a cada nó
     * será a linha em que foi lido. As 4 primeiras posições indicam os vizinhos
     * deste nó, na convenção de ordenação acima, esquerda, embaixo e direita.
     * As duas ultimas posicoes indicam a noção de escala do mapa, com
     * coordenadas x,y do nó, sendo que fora tomado como referencial positivo em
     * X o mais à direita, e o positivo em Y mais abaixo, de modo que seja
     * similar à progressão de escrita de um texto.
     *
     * @param configFilePath nome/path do arquivo que contem os metadados
     * @return Lista que contem toda a informação necessária para o assembly do
     * labirinto
     */
    public ArrayList<Integer[]> LeMapaConfigCSV(String configFilePath) {

        // armazena o valor do arquivo
        SetFile(configFilePath);

        // inicializa leitora e linha
        BufferedReader Leitora = null;
        String lido = "";

        // variavel de retorno
        ArrayList<Integer[]> MapaConfig = new ArrayList<>();

        try {

            Leitora = new BufferedReader(new FileReader(filepath));

            while ((lido = Leitora.readLine()) != null) {

                String[] LinhaNo = lido.split(",");

                Integer[] ValsNo = new Integer[LinhaNo.length];

                for (int i = 0; i < LinhaNo.length; i++) {
                    ValsNo[i] = Integer.parseInt(LinhaNo[i]);
                }

                MapaConfig.add(ValsNo);
            }

        } catch (Exception e) {

            //Mostra mensagem de erro
            e.printStackTrace();

        } finally {

            //Fecha o arquivo
            try {
                Leitora.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return MapaConfig;

    }

    /**
     * Funcao responsavel pela leitura do arquivo txt que contem metadados, de
     * elementos do jogo, em cada caminho, incluindo condicoes iniciais
     * associadas as posicoes de cada entidade inicialmente no jogo, o padrao
     * utilizado é o seguinte: cada linha possui um conjunto de letras e
     * numeros, seguido de um character ':', seguido de um conjunto de inteiros
     * que representam os IDs dos nos utilizados, indicando a existencia de uma
     * aresta entre eles. A letra F significa "Fill", P significa "Put", S
     * indica uma condicao Inicial, comumente chamada de "Spawn", seguida de "P"
     * (player) ou F (Fantasma). Algo como F.0: 10.20 indica que a aresta que
     * liga os nos 10 e 20 deve ser preenchida por zeros, F.0,P.1.2: 20.21
     * indica que a aresta que liga os nos 20 ao 21 deve ser preenchida de zeros
     * e depois um valor de 1 deve ser colocado no offset 2. Para as condicoes
     * iniciais, S.P: 12.13, 3, -1 indica que o jogador deve ser posicionado
     * inicialmente na aresta 12.13, no offset 3, com momentum negativo. O
     * formato nao é perfeito e por partes confuso, possíveis alterações podem
     * ser feitas para aperfeiçoamento.
     *
     * @param configFilePath nome/path do arquivo que contem os metadados
     * @return Mapeamento de arestas que contem toda a informação necessária
     * para o assembly do labirinto
     */
    public HashMap<Set<Integer>, String> GetArestaConfig(String configFilePath) {

        // armazena o valor do arquivo
        SetFile(configFilePath);

        // inicializa leitora e linha
        BufferedReader Leitora = null;
        String lido = "";

        HashMap<Set<Integer>, String> ArestaConfig = new HashMap<>();

        try {

            Leitora = new BufferedReader(new FileReader(filepath));

            while ((lido = Leitora.readLine()) != null) { // para cada configuracao

                String[] Linha = lido.split(":"); //separar entre a configuracao e as arestas equivalentes

                if (Linha[0].charAt(0) == 'S' || Linha[0].charAt(0) == 'T') { //spawn (caminho inicial, offset, momentumInicial

                    if (Linha[0].charAt(0) == 'S') {
                        Set<Integer> NovoSpawn = new HashSet<>();
                        if (Linha[0].charAt(2) == 'P') { // player
                            NovoSpawn.add(-1);
                        } else { //fantasmas
                            NovoSpawn.add(-2);
                        }
                        String CondicoesIniciais = Linha[1];
                        ArestaConfig.put(NovoSpawn, CondicoesIniciais);
                    } else {
                        Set<Integer> Threshold = new HashSet<>();
                        Threshold.add(-3);
                        ArestaConfig.put(Threshold, Linha[1]);
                    }

                } else {
                    String[] Arestas = Linha[1].split(","); // separar em cada aresta

                    for (int i = 0; i < Arestas.length; i++) {
                        String[] ParNos = Arestas[i].split("\\."); // para cada aresta, recuperar os nos que a compoem
                        Set<Integer> NovaAresta = new HashSet<>(); // armazenar em um set
                        NovaAresta.add(Integer.parseInt(ParNos[0]));
                        NovaAresta.add(Integer.parseInt(ParNos[1]));
                        ArestaConfig.put(NovaAresta, Linha[0]); // mapear a aresta para a configuracao
                    }
                }
            }

        } catch (Exception e) {

            //Mostra mensagem de erro
            e.printStackTrace();

        } finally {

            //Fecha o arquivo
            try {
                Leitora.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ArestaConfig;
    }

    /**
    *Este método é responsável pelo armazenamento de informações de jogos anteriormente jogados em um arquivo.
    *
    *@param playerFilePath Endereco do arquivo em que deve-se armazenar (append) a informação
    *@param Nome do jogador
    *@param score pontuação conseguida
    *@param Nivel Nivel chegado
    *@param Mapa Index do mapa que fora jogado
    *@throws IOException Pois temos acessos a disco que podem gerar este erro
    */
    public void RegistraPontuacaoJogador(String playerFilePath, String Nome, int score, int Nivel, int Mapa) throws IOException {
        SetFile(playerFilePath);

        String scoreSalvo = "Jogador(a) : " + Nome + " fez " + score + " pontos, chegou ate nivel " + Nivel + " no mapa " + Mapa + "\n";
        FileWriter fw = new FileWriter(filepath, true);
        BufferedWriter escritora = new BufferedWriter(fw);
        escritora.write(scoreSalvo);
        escritora.close();
    }
}
