package pacman.Sistema;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
* Central de todas as estruturas de dados envolvidas na lógica do labirinto, definindo métodos de acesso e fazendo o fluxo de controle de variáveis.
* 
* A principal vantagem desta forma de definir as funções desta classe é a possibilidade de definição de um mapa genérico, com apenas a passagem de
* metadados em blueprint, sendo isso responsável entretanto por boa parte da complexidade dos métodos, pois deve-se ser suficientemente genérico 
* para possibilitar uma gama de mapas.
*
* @see pacman.Sistema.Blueprint
* @see pacman.Engine.Controlador
* @see pacman.Sistema.Caminho
* @see pacman.Sistema.NoGrafo
*
* @author diego-fleury
* @version v1.0
* @since Alpha v0.1
*/
public final class Mapa {
    
    private int NumVertices; //= 64
    private int NumArestas; // = 83;
    
    private final Caminho[] ArestasCaminhosIniciais = new Caminho[2];
    private int totalPacdots;
    
    private final Map<Set<Integer>, Integer> IndexadorDeArestas = new HashMap<>();
    private Set<Set<Integer>> ArestasKeySet;

    private NoGrafo[] ListaAdjascencia;
    private Caminho[] ListaCaminhos;

    /**
     * Construtor da classe que controla toda a interface com os elementos que constituem o
     * mapa do jogo (caminhos e nos).
     */    
    public Mapa(){ 
    }

    /**
     * Criacao do mapa, dada a blueprint de construcao.
     * @param blueprint metadados de construcao do mapa
     */
    public void MontaMapa(Blueprint blueprint){

      NumVertices = blueprint.GetNumVertices();
      MontaNos(blueprint);
      
      NumArestas = blueprint.GetNumArestas();
      ArestasKeySet = blueprint.GetArestasKeySet();
      MontaCaminhos(blueprint);
      
      SetTotalPacdots();

      ArestasCaminhosIniciais[0] = ListaCaminhos[IndexadorDeArestas.get(blueprint.GetCaminhoInicial(true))]; 
      ArestasCaminhosIniciais[1] = ListaCaminhos[IndexadorDeArestas.get(blueprint.GetCaminhoInicial(false))]; 

    }

    /**
     * Criacao do array de NoGrafos, dada a blueprint de construcao.
     * @param blueprint metadados de construcao do mapa
     */
    private void MontaNos(Blueprint blueprint){

      ListaAdjascencia = new NoGrafo[NumVertices];

      for(int i=0; i< NumVertices; i++){
        ListaAdjascencia[i] = new NoGrafo(i,blueprint.GetVizinhos(i),blueprint.GetX(i), blueprint.GetY(i));
      }

    }

    /**
     * Interface de controle de acesso de caminhos, a movimentacao das entidades depende do
     * acesso ao proximo caminho, dado o No em que esta e o index (que representa a direcao).
     * @param Atual NoGrafo em que a entidade atualmente se localiza
     * @param index direcao que deseja-se seguir naquele no (nota: processamento da validade ou nao desta
     * direcao e responsabilidade do controlador).
     * @return Caminho correspondente a aquela que fora solicitada
     */
    public Caminho GetProxCaminho(NoGrafo Atual, int index){

      Set<Integer> ArestaProx = new HashSet<>();
      ArestaProx.add(Atual.GetID());
      ArestaProx.add(Atual.GetVizinhos()[index]);

      return ListaCaminhos[IndexadorDeArestas.get(ArestaProx)];

    }

    /**
     * Acesso rapido quando necessario a um no, dado o seu ID.
     * @param ID identificação do nó desejado.
     * @return No solicitado.
     */
    public NoGrafo GetNoGrafo(int ID){
      return ListaAdjascencia[ID];
    }

    /**
     * Realiza a montagem dos caminhos, dada a descricao da montagem nos metadados de blueprint.
     * @param blueprint metadados de construcao do labirinto
     */
    private void MontaCaminhos(Blueprint blueprint){
      
      ListaCaminhos = new Caminho[NumArestas];
      
      int count = 0;
      for(Set<Integer> s : ArestasKeySet){
        
        Integer[] Aresta = s.toArray(new Integer[s.size()]);
        NoGrafo No1 = ListaAdjascencia[Aresta[0]]; 
        NoGrafo No2 = ListaAdjascencia[Aresta[1]];

        int deltaX = No1.GetX() - No2.GetX();
        int deltaY = No1.GetY() - No2.GetY();

        boolean CaminhoVertical  = (deltaX == 0);
        
        if(CaminhoVertical){
          
          ListaCaminhos[count] = (deltaY > 0)? new Caminho(No2,No1,deltaY+1,CaminhoVertical,blueprint.GetCaminhoConfig(s)): new Caminho(No1,No2,-(deltaY-1),CaminhoVertical,blueprint.GetCaminhoConfig(s));
        
        }else{
          
          ListaCaminhos[count] = (deltaX > 0)? new Caminho(No2,No1,deltaX+1,CaminhoVertical,blueprint.GetCaminhoConfig(s)): new Caminho(No1,No2,-(deltaX-1),CaminhoVertical,blueprint.GetCaminhoConfig(s));     
        
        }
        
        IndexadorDeArestas.put(s,count);
        count++;
      
      }
    }

    /**
     * Realiza a contabilizacao da totalidade dos pacdots que devem ser comidos de forma a passar de nivel.
     */
    private void SetTotalPacdots(){

      totalPacdots = 0;

      for(int i=0;i<NumVertices;i++){
        totalPacdots+= (ListaAdjascencia[i].GetSlot() == 1) ? 1 : 0;
      }

      for(int i=0;i<NumArestas;i++){
        totalPacdots += ListaCaminhos[i].GetTotalPacdotsIniciais();
      }

    }
    
    /**
     * Interface simples de retorno da totalidade de pacdots que devem ser comidos para passagem de nivel.
     * @return quantidade total de pacdots iniciais.
     */
    public int GetTotalPacdots(){
     return totalPacdots;
    }
    
    /**
     * Funcao que retorna o caminho inicial que uma entidade deve iniciar sua partida.
     * @param player indica se aquele que solicita o caminho inicial pede o do player ou dos fantasmas
     * @return caminho desejado.
     */
    public Caminho GetCaminhoInicial(boolean player){
        
        return player ? ArestasCaminhosIniciais[0] : ArestasCaminhosIniciais[1];
      
    }
}