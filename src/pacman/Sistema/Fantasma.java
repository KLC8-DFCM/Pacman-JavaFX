package pacman.Sistema;

/**
* Classe que define a interface do sistema para com a classe (genérica) dos fantasmas, com acesso aos estados, tempo de fuga, coordenadas e outras coisas.
* 
* @see pacman.Sistema.Entidade
*
* @author diego-fleury
* @version v1.0
* @since Alpha v0.1
*/
public abstract class Fantasma extends Entidade{

    int VEL_FUGA, VEL_MORTO, VEL_ATQ; 
    private static final int MORTO = 0, VIVO = 1, FUGA = 2;
    private int ContadorFuga = 0; 
    private static int tempoDeFuga;
    private int estado; // 0: morto, 1: ataque, 2: fuga
    A_Star guia;
    
    /**
     * Construtor da classe abstrata Fantasma, que encapsula os dois subtipos (aleatório e perseguidor). 
     * @param pCaminhoInicial Objeto caminho inicialmente ocupado pelo fantasma 
     * @param poffsetInicial Deslocamento inicial do fantasma
     * @param pmomentumInicial Momentum inicial do fantasma      
     */
    Fantasma(String pNome, Caminho pCaminhoInicial, int poffsetInicial, int pmomentumInicial, Integer[] CoordAlvoIniciais, int NoAlvoInicial, int ptempoDeFuga){
        
      super(pNome,pCaminhoInicial, poffsetInicial, pmomentumInicial);
      guia = new A_Star(CoordAlvoIniciais[0],CoordAlvoIniciais[1],NoAlvoInicial);
      estado = VIVO;
      tempoDeFuga = ptempoDeFuga;

    }

    public void SetContador(int pCont){
        Contador = pCont;
    }

    /**
     * Maneira rápida de modificar o estado do fantasma.
     * @param novoEstado padrão de comportamento que deve ser seguido pelo fantasma.
     */
    public void SetEstado(int novoEstado){  
        
        Contador = 0;
        if(novoEstado == MORTO){
            ContadorFuga = 0;
            Velocidade = VEL_MORTO;
        }else{
            if(novoEstado == VIVO){
                Velocidade = VEL_ATQ;
            }else{
                ContadorFuga = tempoDeFuga;
                Velocidade = VEL_FUGA;
            }
        }
        
        estado = novoEstado;
    }
    
    /**
    * Este método é chamado no contexto de verificação e contabilização de mudanças de velocidades, levando em
    * conta a noção das mudanças de estado.
    */
    @Override
    public void AddContador(){
        Contador = (Contador + 1 ) % Velocidade;
        if(estado == FUGA){
            ContadorFuga--;
            if(ContadorFuga == 0){
                SetEstado(VIVO);
            }
        }
    }
    
    /**
    * Este método muda a velocidade de ataque dinamicamente para fantasmas (aplicada apenas para Blinky).
    *@param vel velocidade nova 
    */
    public void SetVelocidadeAtaque(int vel){
        VEL_ATQ = vel;
        if(estado == VIVO)
            Velocidade = VEL_ATQ;
    }

    /**
     * Maneira rápida de recuperar a informação de qual é o estado do fantasma.
     * @return estado atual do fantasma
     */
    public int GetEstado(){
        return estado;
    }

    /**
     * Retorna a próxima direção que deve ser seguida pelo fantasma, dado o seu estado atual (variável interna)
     * que seleciona qual função chamar.
     * @param mapaJogo mapa usado para movimentacao
     * @param CoordPacman localizacao do jogador
     * @param IDnoPacman NoGrafo logo a frente do jogador
     * @return direção que deve ser seguida.
     */
    public int GetNextDirecao(Mapa mapaJogo, Integer[] CoordPacman, int IDnoPacman){
        return (estado == VIVO) ? DirecaoAtaque(mapaJogo,CoordPacman,IDnoPacman) : (estado == FUGA) ? DirecaoFuga(mapaJogo, CoordPacman, IDnoPacman) : DirecaoMorto(mapaJogo) ;
    }

    /**
     * Esta é a única forma de comportamento que é padronizada entre todas as classes que herdam de Fantasma,
     * sendo esta a direção que leva à origem (spawn) do fantasma, ativada quando o fantasma está morto.
     * (Por enquanto ainda não implementada).
     * @return direção que deve ser seguida para chegar à origem
     */
    private int DirecaoMorto(Mapa mapaJogo){
        // A* para a origem

        int direcao = -1;
        
        if(offset == 0 || offset == CaminhoAtual.GetTamanho() - 1){
            NoGrafo AlvoSpawn = CaminhoInicial.GetNosFronteira(0);
            NoGrafo Atual = CaminhoAtual.GetNosFronteira(offset);

            guia.SetTarget(AlvoSpawn.GetX(),AlvoSpawn.GetY(),AlvoSpawn.GetID());
            direcao = guia.GetNextDirecaoAStar(Atual, mapaJogo);
        }

        return direcao;
    }

    /**
     * Todas as classe que herdam de Fantasma devem implementar esta funcionalidade, uma estratégia de fuga
     * quando o jogador absorver uma pílula, sendo assim capaz de atacar os fantasmas.
     * @param mapaJogo usado para movimentacao no mapa 
     * @param CoordPacman usado para localizar destino (heuristica de A*)
     * @param IDnoPacman usado para localizar destino (direcao de A*)
     * @return direção que deve ser seguida para escapar do jogador.
     */
    abstract protected int DirecaoFuga(Mapa mapaJogo, Integer[] CoordPacman, int IDnoPacman);

    /**
     * Todas as classe que herdam de Fantasma devem implementar esta funcionalidade, uma estratégia de ataque.
     * @param mapaJogo usado para movimentacao no mapa 
     * @param CoordPacman usado para localizar destino (heuristica de A*)
     * @param IDnoPacman usado para localizar destino (direcao de A*)
     * @return direção que deve ser seguida para chegar ao jogador.
     */
    abstract protected int DirecaoAtaque(Mapa mapaJogo, Integer[] CoordPacman, int IDnoPacman);
}

