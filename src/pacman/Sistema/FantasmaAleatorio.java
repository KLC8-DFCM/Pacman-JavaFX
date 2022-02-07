package pacman.Sistema;

import java.util.concurrent.ThreadLocalRandom;

/**
* Define a noção de um fantasma aleatório, aproveitando métodos genéricos de fantasma e implementando as formas de ataque novas.
* 
* @see pacman.Sistema.Fantasma
*
* @author diego-fleury
* @version v1.0
* @since Alpha v0.1
*/
public class FantasmaAleatorio extends Fantasma {
    
    /**
     * Construtor do fantasma cuja definição de ataque e fuga são dadas por um algorítmo aleatório.
     * @param pNome nome do fantasma
     * @param pCaminhoInicial Objeto caminho inicial do fantasma
     * @param poffsetInicial deslocamento inicial do fantasma
     * @param pmomentumInicial momentum inicial do fantasma
     * @param CoordAlvoIniciais local inicial do fantasma
     * @param NoAlvoInicial No inicial em que o fantasma é inicializado
     * @param pTempoDeFuga Quantidade de ciclos que o fantasma deve aguardar até retornar ao estado pa
     */
    public FantasmaAleatorio(String pNome,Caminho pCaminhoInicial, int poffsetInicial, int pmomentumInicial, Integer[] CoordAlvoIniciais, int NoAlvoInicial, int pTempoDeFuga){
        super(pNome,pCaminhoInicial, poffsetInicial, pmomentumInicial, CoordAlvoIniciais, NoAlvoInicial,pTempoDeFuga);
        VEL_FUGA = 4; 
        VEL_MORTO = 1; 
        VEL_ATQ = 3;
        
        this.Velocidade = VEL_ATQ;
    }

    /**
     * Fora pensada em um algorítmo que é aleatório, porém com maior probabilidade (na fuga)
     * de seguir uma direção que é oposta àquela que o jogador está.(Funcionalidade ainda não implementada).
     * @return Direção que deve ser seguida para fugir do jogador.
     */
    @Override
    protected int DirecaoFuga(Mapa mapaJogo, Integer[] CoordPacman, int IDnoPacman){
        return ThreadLocalRandom.current().nextInt(0,4);
    }

    /**
     * Fora pensada em um algorítmo que é aleatório, porém com maior probabilidade (no ataque)
     * de seguir uma direção que leva aonde o jogador está.
     * (Funcionalidade ainda não implementada).
     * @return Direção que deve ser seguida para chegar ao jogador.
     */
    @Override
    protected int DirecaoAtaque(Mapa mapaJogo, Integer[] CoordPacman, int IDnoPacman){
        return ThreadLocalRandom.current().nextInt(0,4);
    }

}