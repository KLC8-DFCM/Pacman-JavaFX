package pacman.Sistema;

/**
* Definição dos elementos de nós na estrutura interna de grafos do mapa, com métodos de acesso pensados para facilitar a movimentação de entidades,
* especialmente fantasmas (pela necessidade de extrapolação através do algorítimo A* interno).
* 
* @see pacman.Sistema.Mapa
*
* @see pacman.Sistema.Caminho
*
* @author diego-fleury
* @version v1.0
* @since Alpha v0.1
*/
public class NoGrafo {
    
    private final int ID;
    private final int[] Vizinhos;
    private final int x, y;
    private int Slot;
   
    /**
     * Construtor da classe responsavel pela manutencao da informacao de conectividade do mapa (e escala).
     * @param pID indice do no dentro da lista de adjascencia.
     * @param pVizinhos vizinhos do grafo, seguindo a orientacao convencional.
     * @param px coordenada X do no
     * @param py coordenada Y do no
     */ 
    NoGrafo(int pID,int[] pVizinhos, int px, int py){
      ID = pID;
      Vizinhos =  pVizinhos;
      x = px;
      y = py;
      Slot = -1;
    }
    
    /**
     * Retorna o ID do no.
     * @return ID do no
     */
    public int GetID(){
      return ID;
    }

    /**
     * Retorna o conjunto de vizinhos do no.
     * @return Vizinhos do no
     */    
    public int[] GetVizinhos(){
      return Vizinhos;
    }

    /**
     * Retorna a coordenada X do no.
     * @return localizacao do no relativo ao eixo X
     */
    public int GetX(){
      return x;
    }

    /**
     * Retorna a coordenada Y do no.
     * @return localizacao do no relativo ao eixo Y
     */
    public int GetY(){
      return y;
    }

    /**
     * Realiza a inicializacao do elemento de jogo presente naquela posicao, 
     * priorizando aqueles atributos de maior valor.
     * @param val valor passivel de ser atribuido aquela localizacao.
     */
    public void SetSlotInicial(int val){
      Slot = (val > Slot) ? val : Slot;
    }
    
    /**
     * Quando o jogador passar pelo no o valor deve ser retornado, para atualizar a
     * pontuacao atual, porem deve ser modificado o valor atual para vazio.
     * @return atributo coletado pelo jogador. 
     */
    public int GetSetSlot(){
      int atributo = Slot;
      if(atributo != 3)
        Slot = 0;
      return atributo;
    }    

    /**
     * Retorna o valor presente no slot do no.
     * @return atributo presente.
     */
    public int GetSlot(){
      return Slot;
    }
}