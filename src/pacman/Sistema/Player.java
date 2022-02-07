package pacman.Sistema;

/**
* Classe que define a interface do sistema para com a classe do jogador, com acesso à pontuação, vidas e outras coisas.
* 
* @see pacman.Sistema.Entidade
*
* @see pacman.Engine.Jogo
*
* @author diego-fleury
* @version v1.0
* @since Alpha v0.1
*/
public class Player extends Entidade {

    private int pontos = 0;
    private int vidas = 3;
    private int Nivel = 1;
    private static final int VAZIO = 0, PACDOT = 1, POWER_PILL = 2, FRUTA = 3;
    private boolean podeFruta = false;
    /**
     * Construtor da classe que representa o jogador, recebe os valores iniciais, incluindo neste caso
     * o nome, que sera salvo ao final no arquivo de pontuacoes.
     * @param pNome Nome do jogador
     * @param pCaminhoInicial Aresta inicial do jogador
     * @param poffsetInicial Localização dentro da aresta inicial
     * @param pmomentumInicial 
     * @param PontosIniciais Pontuação inicial do jogador, invocada pela necessidade de transmissão de informação entre níveis.
     */
    public Player(String pNome, Caminho pCaminhoInicial, int poffsetInicial, int pmomentumInicial, int PontosIniciais){
        super(pNome,pCaminhoInicial, poffsetInicial, pmomentumInicial);
        this.Velocidade = 2;
        pontos = PontosIniciais;
        // pacdots para serem comidos
    }

    /** 
     * Retorna a pontuacao do jogador.
     * @return pontos
     */
    public int GetPontos(){
        return pontos;
    }

    /**
     * Define a pontuacao do jogador como zero caso mude de mapa.
     */
    public void ResetPontos(){
        pontos = 0;
    }

    /**
    * Atualiza a pontução dado o contexto da quantidade de fantasmas comidos.
    *@param nComidos Numeros de fantasmas comidos pelo jogador desde a última vez que comeu uma pacdot.
    */
    public void FantasmaComido(int nComidos){
        pontos += (Math.pow(2, nComidos - 1) * 200);
    }


    /**
     * Retorna a quantidade de vidas restantes.
     * @return quantidade de vidas que o jogador tem
     */
    public int GetVidas(){
        return vidas;
    }

    /**
     * Modifica a quantidade de vidas quando o juiz determinar.
     * @param pVidas vidas a serem atualizadas para o jogador
     */
    public void SetVida(int pVidas){
        vidas =pVidas ;
    }

    /**
     * Mantem atualizado o nivel em que o jogo esta para que a quantidade
     * de pontos seja adequadamente contabilizada. 
     * @param pNivel Nivel em que esta a partida
     */
    public void SetNivel(int pNivel){
        Nivel = pNivel;
    }

    /**
    * Permite ou bloqueia o acesso à fruta pelo jogador.
    *@param pPodeFruta permissão (ou não) de captura da fruta
    */
    public void SetFruta(boolean pPodeFruta){
        podeFruta = pPodeFruta;
    }

    /**
    * Forma modular de recuperar a informação de se a fruta esta disponível ao jogador ou não.
    *@return valor-verdade da possibilidade do jogador de capturar a fruta.
    */
    public boolean GetFruta(){
        return podeFruta;
    }

    /**
     * Atualiza a pontuacao do jogador dado o atributo coletado no mapa, 
     * retorna o valor verdade de se o pacman esta em modo de ataque.
     * @param atributo atributo coletado
     */
    public void Update(int atributo){
       
       pontos+= (atributo == PACDOT) ? 10 : (atributo == POWER_PILL) ? 50 : (atributo == FRUTA) ? ((podeFruta) ? (2*(Nivel) -1)*100 : 0) : 0;
    }
    
    /**
    * Método usado para controlar/simular a noção de velocidade 
    */
    @Override
    public void AddContador(){
        Contador = (Contador + 1 ) % Velocidade;
    }
}