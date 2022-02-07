package pacman.Sistema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
* Esta classe é a responsável pelo armazenamento (e solicitação de leitura) de quaisquer metadados dos mapas do jogo.
* 
* Essencialmente realiza a leitura e processamento da informação de conectividade (MapaConfig) do mapa e de elementos 
* do labirinto (CaminhoConfig).
*
* @see pacman.Engine.Jogo
* @see pacman.Interface.Menu
* @see pacman.Interface.Cena
* @see pacman.Sistema.Mapa
* 
* @author diego-fleury
* @version v1.0
* @since Alpha v0.1
*/
public class Blueprint {

  private final ArrayList<Integer[]> MapaConfig;
  private final HashMap<Set<Integer>,String> CaminhoConfig;
  private final int momentumInicialPlayer,momentumInicialFantasmas;
  private final int OffsetInicialPlayer,OffsetInicialFantasmas;
  private final Set<Integer> CaminhoInicialPlayer,CaminhoInicialFantasmas;
  private final ArrayList<Integer> Bonus = new ArrayList<>();
  
  /**
   * Construtor da classe responsavel pela manutencao das especificações dos metadados dos mapas em memória.
   * @param pMapaConfig Configurações dos nós.
   * @param pCaminhoConfig Configurações dos caminhos (incluindo condições iniciais das entidades, tratadas no contrutor).
   */
  public Blueprint(ArrayList<Integer[]> pMapaConfig, HashMap<Set<Integer>,String> pCaminhoConfig){
    
    Set<Integer> Player = new HashSet<>(), Fantasmas = new HashSet<>(), Thresholds = new HashSet<>();
    
    // Definicao dos valores nao relacionados aos caminhos em si a serem recuperados do arquivo
    Player.add(-1);
    Fantasmas.add(-2);
    Thresholds.add(-3);

    // Recuperacao dos valores de condicoes iniciais do jogador
    String[] CondicoesIniciaisPlayer = pCaminhoConfig.get(Player).split(",");
    
    CaminhoInicialPlayer = new HashSet<>();
    String[] ArestaInicialPlayer = CondicoesIniciaisPlayer[0].split("\\.");
    CaminhoInicialPlayer.add(Integer.parseInt(ArestaInicialPlayer[0]));
    CaminhoInicialPlayer.add(Integer.parseInt(ArestaInicialPlayer[1]));

    OffsetInicialPlayer = Integer.parseInt(CondicoesIniciaisPlayer[1]);    

    momentumInicialPlayer = Integer.parseInt(CondicoesIniciaisPlayer[2]);
    // ------------------------------------------------------------------
    
    // Recuperacao dos valores de condicoes iniciais dos fantasmas
    String[] CondicoesIniciaisFantasmas = pCaminhoConfig.get(Fantasmas).split(",");

    CaminhoInicialFantasmas = new HashSet<>();
    String[] ArestaInicialFantasmas = CondicoesIniciaisFantasmas[0].split("\\.");
    CaminhoInicialFantasmas.add(Integer.parseInt(ArestaInicialFantasmas[0]));
    CaminhoInicialFantasmas.add(Integer.parseInt(ArestaInicialFantasmas[1])); 

    OffsetInicialFantasmas = Integer.parseInt(CondicoesIniciaisFantasmas[1]); 

    momentumInicialFantasmas = Integer.parseInt(CondicoesIniciaisFantasmas[2]);
    // ------------------------------------------------------------------
    
    // Recuperacao dos valores de thresholds de pacdots que acarretam na aparicao dos bonus de pontos
    String[] ThresholdsDePontos = pCaminhoConfig.get(Thresholds).split(",");
    
    for (String ThresholdsDePonto : ThresholdsDePontos) {
        Bonus.add(Integer.parseInt(ThresholdsDePonto));
    }
    // ------------------------------------------------------------------

    pCaminhoConfig.remove(Player);
    pCaminhoConfig.remove(Fantasmas);
    pCaminhoConfig.remove(Thresholds);
    
    MapaConfig = pMapaConfig;
    CaminhoConfig = pCaminhoConfig;

  }
  
  /**
   * Acesso rápido ao momentum inicial das entidades
   * @param Player indica se deve retornar a condicao do jogador ou nao
   * @return MomentumInicial de alguma entidade
   */
  public int GetMomentumInicial(boolean Player){
    return (Player) ? momentumInicialPlayer: momentumInicialFantasmas;
  }  

  /**
   * Acesso rápido ao offset inicial das entidades
   * @param Player indica se deve retornar a condicao do jogador ou nao
   * @return OffsetInicial de alguma entidade
   */
  public int GetOffsetInicial(boolean Player){
    return (Player) ? OffsetInicialPlayer: OffsetInicialFantasmas;
  }  

  /**
   * Acesso rápido ao Caminho inicial das entidades
   * @param Player indica se deve retornar a condicao do jogador ou nao
   * @return (ID da Fronteira do) CaminhoInicial de alguma entidade
   */
  public Set<Integer> GetCaminhoInicial(boolean Player){
    return (Player) ? CaminhoInicialPlayer: CaminhoInicialFantasmas;
  }
  
  /**
   * Acesso simples e modular das partições de pacdots capturados em que deve-se tornar disponível a fruta.
   *@return ArrayList dos intervalos lidos do arquivo
   */
  public ArrayList<Integer> GetBonus(){
    return Bonus;
  }
  
  /**
   * Acesso rápido a coordenada X do nó especificado.
   * @param index ID do nó em questao
   * @return valor da coordenada X
   */
  public Integer GetX(int index){
    return MapaConfig.get(index)[4];
  }

  /**
   * Acesso rápido a coordenada Y do nó especificado.
   * @param index ID do nó em questao
   * @return valor da coordenada Y
   */
  public Integer GetY(int index){
    return MapaConfig.get(index)[5];
  }

  /**
   * Acesso rápido ao conjunto de vizinhos do nó especificado.
   * @param index ID do nó em questao
   * @return array que contem todos os indices dos vizinhos do nó
   */
  public int[] GetVizinhos(int index){
    int[] Viz = new int[4];

    for(int i=0;i<4;i++)
    Viz[i] = MapaConfig.get(index)[i];

   return Viz;
  }

  /**
   * Acesso rápido à quantidade de nós no mapa.
   * @return quantidade de nós.
   */
  public int GetNumVertices(){
    return MapaConfig.size();
  }

  /**
   * Acesso rápido à quantidade de arestas no mapa.
   * @return quantidade de arestas.
   */
  public int GetNumArestas(){
    return CaminhoConfig.size();
  }

  /**
   * Acesso rápido à configuração de um caminho em específico dados os IDs dos nós que compoem a sua fronteira.
     * @param Aresta set que contém os IDs que definem a aresta
   * @return String de configuração do caminho.
   */
  public String GetCaminhoConfig(Set<Integer> Aresta){
    return CaminhoConfig.get(Aresta);
  }
  
  /**
   * Acesso rápido ao conjunto de arestas do mapa.
   * @return listagem das arestas.
   */
  public Set<Set<Integer>> GetArestasKeySet(){
    return CaminhoConfig.keySet();
  }

  /**
   * Retorna de modo simples e modular as dimensões do tabuleiro, para a montagem na interface gráfica.
   * @return Tamanho do mapa em coordenadas clássicas (Xmax, Ymax)
   */
  public Integer[] GetDimensoes(){
    
    // nos arquivos de configuracao devemos ter coordenadas sempre positivas
    int Xmax = 0, Ymax = 0;
    
    for (int i = 0; i< this.GetNumVertices(); i++){
        
        int X = MapaConfig.get(i)[4];
        Xmax = (X > Xmax) ? X : Xmax;
        
        int Y = MapaConfig.get(i)[5];
        Ymax = (Y > Ymax) ? Y : Ymax;

    }

    Integer[] Coord = new Integer[2];
    Coord[0] = Xmax;
    Coord[1] = Ymax;

    return Coord;
    
  }
}