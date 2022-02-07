package pacman.Sistema;

/**
* Esta classe abstrata define a base de movimentos comum para fantasmas e jogador.
* 
* @see pacman.Engine.Controlador
*
* @author diego-fleury
* @version v1.0
* @since Alpha v0.1
*/
public abstract class Entidade {
    
    String Nome;
    private int momentum;
    int offset;
    Caminho CaminhoAtual;
    
    int Velocidade, Contador = 0;
    private final int momentumInicial;
    private final int offsetInicial;
    final Caminho CaminhoInicial;
 
    /**
     * Construtor da classe abstrata de qualquer entidade que pode-se mover pelo mapa.
     * Embora poderia ter sido implementada uma interface "movable" optou-se por uma classe
     * abstrata pela possibilidade de definição de corpo de funções.
     * @param pCaminhoInicial aresta inicial da entidade  
     * @param poffsetInicial deslocamento inicial da entidade
     * @param pmomentumInicial momentum inicial da entidade
     */
    Entidade(String pNome,Caminho pCaminhoInicial, int poffsetInicial, int pmomentumInicial){

      Nome = pNome;

      momentum = pmomentumInicial;
      momentumInicial= pmomentumInicial;

      offset=poffsetInicial;
      offsetInicial=poffsetInicial;

      CaminhoAtual=pCaminhoInicial;
      CaminhoInicial=pCaminhoInicial;

    }
    
    /**
     * Retorna o nome do jogador.
     * @return Nome
     */  
    public String GetNome(){
        return Nome;
    }

    /**
     * Acesso rápido à informação do caminho atual ocupado pela entidade.
     * @return Objeto caminho atualmente ocupado.
     */
    public Caminho GetCaminho(){
      return CaminhoAtual;
    }

    public int GetContador(){
        return Contador;
    }

    abstract public void AddContador();

    /**
     * Modificação do caminho atual ocupado pela entidade.
     * @param caminhoNovo caminho que deve ser ocupado a partir deste instante
     */
    public void SetCaminho(Caminho caminhoNovo){
      CaminhoAtual = caminhoNovo;
    }

    /**
     * Acesso rápido à informação do momentum atual da entidade.
     * @return Momentum atual da entidade.
     */
    public int GetMomentum(){
      return momentum;
    }

    /**
     * Modificação do momentum atual da entidade.
     * @param pmomentum atualizado da entidade.
     */
    public void SetMomentum(int pmomentum){
      momentum = pmomentum;
    }

    /**
     * Acesso rápido à informação do offset atual da entidade.
     * @return Offset atual da entidade.
     */
    public int GetOffset(){
      return offset;
    }

    /**
     * Modificação do offset atual da entidade.
     * @param poffset atualizado da entidade.
     */
    public void SetOffset(int poffset){
      offset = poffset;
    }

    public Integer[] GetCoord(){
    
        Integer[] Coord = new Integer[2];

        if(CaminhoAtual.GetVertical()){

            Coord[0] = CaminhoAtual.GetNosFronteira(0).GetX();

            Coord[1] = CaminhoAtual.GetNosFronteira(0).GetY() + offset;

        }else{

            Coord[0] = CaminhoAtual.GetNosFronteira(0).GetX() + offset;

            Coord[1] = CaminhoAtual.GetNosFronteira(0).GetY();

        }

        return Coord;
    
    }
}