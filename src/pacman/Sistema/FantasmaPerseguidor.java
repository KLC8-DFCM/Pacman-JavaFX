package pacman.Sistema;

/**
* Define a noção de um fantasma perseguidor, aproveitando métodos genéricos de fantasma e implementando as formas de ataque novas.
*
* @see pacman.Sistema.Fantasma 
*
* @author diego-fleury
* @version v1.0
* @since Alpha v0.1
*/
public class FantasmaPerseguidor extends Fantasma {
    
    /**
     * Construtor do fantasma cuja definição de ataque e fuga são dadas pelo
     * algorítmo A*.
     *
     * @param pNome nome do fantasma
     * @param pCaminhoInicial Objeto caminho inicial do fantasma
     * @param poffsetInicial deslocamento inicial do fantasma
     * @param pmomentumInicial momentum inicial do fantasma
     * @param CoordAlvoIniciais para heuristica do alvo inicial do fantasma
     * @param NoAlvoInicial alvo inicial do fantasma
     * @param pTempoDeFuga quantidade de ciclos que devem passar antes de  sair do estado de fuga
     */
    public FantasmaPerseguidor(String pNome, Caminho pCaminhoInicial, int poffsetInicial, int pmomentumInicial, Integer[] CoordAlvoIniciais, int NoAlvoInicial, int pTempoDeFuga) {
        super(pNome, pCaminhoInicial, poffsetInicial, pmomentumInicial, CoordAlvoIniciais, NoAlvoInicial,pTempoDeFuga);
        
        if(this.Nome.equals("Blinky")){
            VEL_ATQ = 5; 
        }else{
            VEL_ATQ = 4;
        }

        VEL_FUGA = 7;
        VEL_MORTO = 1;
        
        this.Velocidade = VEL_ATQ;
    }

    /**
     * Fora pensada em um algorítmo que é completamente aleatório (na fuga).
     * (Funcionalidade ainda não implementada).
     *
     * @return Direção aleatória que deve ser seguida para fugir do jogador.
     */
    @Override
    protected int DirecaoFuga(Mapa mapaJogo, Integer[] CoordPacman, int IDnoPacman) {

        int direcaoAtaque = DirecaoAtaque(mapaJogo, CoordPacman, IDnoPacman);

        int direcaoEscolhida = -1;

        if (direcaoAtaque != -1) {
            int[] Viz = CaminhoAtual.GetNosFronteira(offset).GetVizinhos();
            for (int i = 0; i < 4; i++) {
                if (!(Viz[i] == -1 || i == direcaoAtaque)) {
                    direcaoEscolhida = i;
                    break;
                }
            }
        }

        return (direcaoEscolhida);
    }

    /**
     * Segue o jogador através de uma sequência de nós trasversada que segue o
     * algorítmo A*. (Funcionalidade ainda não implementada).
     *
     * @return Direção que deve ser seguida para chegar ao jogador.
     */
    @Override
    protected int DirecaoAtaque(Mapa mapaJogo, Integer[] CoordPacman, int IDnoPacman) {

        int direcao = -1;

        if (offset == 0 || offset == CaminhoAtual.GetTamanho() - 1) {
            NoGrafo Atual = CaminhoAtual.GetNosFronteira(offset);
            guia.SetTarget(CoordPacman[0], CoordPacman[1], IDnoPacman);
            direcao = guia.GetNextDirecaoAStar(Atual, mapaJogo);
        }

        return direcao;
    }

}
