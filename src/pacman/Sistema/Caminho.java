package pacman.Sistema;

import java.util.Arrays;

/**
* Definição dos elementos de arestas na estrutura interna de grafos do mapa, com métodos de acesso pensados para facilitar a movimentação de entidades,
* especialmente player (pela necessidade de implementação dos métodos de atributos internos para pontuação e mudança de estados).
* 
* @see pacman.Sistema.Mapa
*
* @see pacman.Sistema.NoGrafo
*
* @author diego-fleury
* @version v1.0
* @since Alpha v0.1
*/
public class Caminho {
    
  //Nocoes da topologia do caminho
  private final NoGrafo Inicial;
  private final NoGrafo Final;
  private final int Tam;
  private final boolean Vertical;    
  private Integer[] Elementos;
  private final String Config;
  
  /**
   * Construtor do caminho recebe toda a informação da localização, tamanho e configuração deste elemento.
   * Tem-se que o tamanho é definido como incluindo as fronteiras (os nós) para facilitar o processamento
   * da próxima posição, portanto, o tamanho real dos elementos contidos neste caminho é pTam - 2.
   * @param pVertical indica a orientação do caminho
   * @param pInicial NoGrafo de menor valor em uma das coordenadas
   * @param pFinal NoGrafo de maior valor em uma das coordenadas
   * @param pConfig String de configuração de preenchimento, seguindo o padrão explícito em blueprint,
   * lembrando-se da definição de tamanho convencionada.
   * @param pTam Tamanho virtual do caminho, incluindo a fronteira (NoGrafo Inicial e Final)
   */
  Caminho(NoGrafo pInicial,NoGrafo pFinal, int pTam, boolean pVertical, String pConfig){
    Inicial = pInicial;
    Final = pFinal;
    Tam = pTam;
    Vertical = pVertical;
    Config = pConfig;
    InicializaCaminho();
  }
  /**
   * Acesso rápido à informação da orientação do caminho.
   * @return Booleano da verticalidade do caminho
   */
  public boolean GetVertical(){
    return Vertical;
  }
  
  /**
   * Acesso rápido aos nós de fronteira do caminho, dado o offset passado.
   * @param offset deslocamento relativo ao ponto inicial
   * @return NoGrafo que faz parte da fronteira
   */
  public NoGrafo GetNosFronteira(int offset){
    return (offset == 0) ? Inicial : Final ;
  }

  /**
   * Acesso rápido à informação do tamanho virtual do caminho.
   * @return Tamanho convencional
   */
  public int GetTamanho(){
    return Tam;
  }

  /**
   * Inicialização dos elementos do caminho, dada uma configuração inicial.
   * Convenção de valores:
   * 0: Espaço vazio
   * 1: Pacdot presente na localização
   * 2: Pílula presenta na localização
   * 3: Fruta presenta na localização 
   * Convenção de configuração:
   * F.num: preencher o caminho com atributo num
   * P.num.offset : substituir atributo em offset por num
   * Nota: na intersecção de dois caminhos (nó) a convenção tomada é
   * a da prevalencia do atributo de maior valor
   */ 
  private void InicializaCaminho(){ 

    Elementos = new Integer[Tam-2];      
    String[] Comandos = Config.split(",");

    int fill = Integer.parseInt(Comandos[0].split("\\.")[1]);
    Arrays.fill(Elementos,fill);

    Inicial.SetSlotInicial(fill);
    Final.SetSlotInicial(fill);

    for(int i = 1; i < Comandos.length; i++){
    
        String[] PutString = Comandos[i].split("\\.");
        int atributoPuts = Integer.parseInt(PutString[1]); 
        int indexPuts = Integer.parseInt(PutString[2]);

        if(indexPuts > 0 && indexPuts <= Tam - 2){
            Elementos[indexPuts-1] = atributoPuts;
        }else{
            ((indexPuts == 0) ? Inicial : Final).SetSlotInicial(atributoPuts);
        }

    }

  }

  /**
   * Esta função é chamada quando o pacman estiver passando por um offset de caminho,
   * de modo que o atributo deve ser recuperado e logo após removido do caminho.
   * @param offset deslocamento em que deve ser recuperado (e atualizado) o atributo
   * @return atributo recuperado ou -1 em caso de erro
   */
  public int GetSetSlot(int offset){
    
    if(offset > 0 && offset < Tam -1){
        int atributo = Elementos[offset-1];
        
        if(atributo != 3)
            Elementos[offset-1] = 0;

        return atributo; 
    }
    
    System.out.println("Indice fora da fronteira de caminho");
    return -1;
  }

  /**
   * Esta função é chamada apenas uma vez, no início do mapa, com a finalidade de
   * recuperar a informação da totalidade de pacdots que devem ser comidos para finalizar o nível.
   * @return Total de pacdots no caminho real (não virtual, tratado em outro local).
   */
  public int GetTotalPacdotsIniciais(){
    int total = 0 ;
    for(int i = 0; i< Tam - 2; i++){
        total+= (Elementos[i] == 1) ? 1 : 0 ;
    }
    return total;
  }

}