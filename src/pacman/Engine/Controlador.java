package pacman.Engine;

import pacman.Sistema.Caminho;
import pacman.Sistema.Mapa;
import pacman.Sistema.NoGrafo;
import pacman.Sistema.Entidade;
import pacman.Sistema.Player;
import pacman.Sistema.Fantasma;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
* Esta classe faz referência à noção clássica de um joystick, sendo responsável pela movimentação do pacman e dos fantasmas.
* A forma com que isto é realizado é através da definição de uma classe abstrata Entidade, em que tem como principal objetivo
* definir métodos e variáveis que permita o tratamento igual da movimentação em si de todas as entidades no mapa.
* Esta classe serve de EventHandler (forma com que JavaFX organiza eventos assíncronos de GUI) para o pacman, implementando não
* só movimento, mas também o mecanismo de pausa (ENTER).
*
* @see pacman.Interface.Cena
* 
* @author diego-fleury
* @version v1.0
* @since Alpha v0.1
*/
public class Controlador implements EventHandler<KeyEvent> {
    
    //Variáveis imutáveis (definições de constantes)
    private static final KeyCode SOBE = KeyCode.UP, ESQU = KeyCode.LEFT, DESC = KeyCode.DOWN, DIRE = KeyCode.RIGHT;
    private static final KeyCode PAUSA = KeyCode.ENTER, INVALIDA = KeyCode.SPACE;
    
    //Ferramentas usadas para movimentação e controle de fluxo
    private static final HashMap<KeyCode, Integer> MapaTecla = new HashMap<>();
    private KeyCode teclaPressionada = INVALIDA;
    private boolean estaPausado = false;

    /**
     * Construtor da classe controlador apenas inicializa algumas variaveis
     * uteis, classe como um todo é responsavel pela lógica de movimentacao das
     * entidades
     */
    Controlador() {
        MapaTecla.put(SOBE, 0);
        MapaTecla.put(ESQU, 1);
        MapaTecla.put(DESC, 2);
        MapaTecla.put(DIRE, 3);
        MapaTecla.put(INVALIDA, -1);
    }
    
    /**
    * Esta função implementa o método handle (obrigatório para herança de EventHandler).
    * Este método define o que será realizado a cada vez que uma ação de tecla for detectada,
    * no caso, é definido que deve-se capturar a tecla pressionada e salva-la.
    * Caso a excessão de pausa (tecla pressionada = ENTER) seja detectada, isso é anotado.
    *@param NovaTeclaPressionada tecla detectada pela Aplicação
    */
    @Override
    public void handle(KeyEvent NovaTeclaPressionada) {
        
        try {
            CapturaTecla(NovaTeclaPressionada);
        } catch (PauseException p) {
            FlipPausa();
            teclaPressionada = INVALIDA;
        }

    }
    
    /**
    * Esta função apenas muda o estado da pausa, sem que seja necessário acessar a variável diretamente.
    */
    private void FlipPausa() {
        estaPausado = !estaPausado;
    }

    /**
     * Funcao que indica se a direcao é vertical ou nao
     *
     * @param c direcao
     * @return Validade da expressao
     */
    private boolean Vertical(KeyCode c) {
        return (c == SOBE || c == DESC);
    }

    /**
     * Funcao que indica se a direcao é horizontal ou nao
     *
     * @param c direcao
     * @return Validade da expressao
     */
    private boolean Horizontal(KeyCode c) {
        return (c == ESQU || c == DIRE);
    }

    /**
     * Funcao que indica se a direcao corresponde a um movimento ou nao
     *
     * @param c direcao
     * @return Validade da expressao
     */
    private boolean Movimento(KeyCode c) {
        return (Vertical(c) || Horizontal(c));
    }

    /**
     * Esta função é responsável pela captura do teclado da direção do jogador.
     * Realiza também o lançamento de excessão caso uma pausa seja detectada.
     * 
     * @param NovaTeclaPressionada tecla detectada pelo método handle, passada para variável de classe.
     * @exception PauseException Indica se a tecla definida como pausa fora
     * pressionada ou não
     * @return Direção escolhida pelo jogador
     */
    private void CapturaTecla(KeyEvent NovaTeclaPressionada) throws PauseException {

        //Le teclado
        teclaPressionada = NovaTeclaPressionada.getCode();

        //Caso nao seja um movimento valido
        if (!Movimento(teclaPressionada)) {

            NovaTeclaPressionada.consume();
            if (teclaPressionada != PAUSA) {
                teclaPressionada = INVALIDA;

            } else {
                throw new PauseException();
            }

        } else {
            NovaTeclaPressionada.consume();
        }

    }

    /**
     * Retorna o indice (dentro do campo Vizinhos em NoGrafo) correspondente à
     * direção escolhida pelo jogador
     *
     * @param c direcao
     * @return Indice correspondente àquele valor no campo Vizinhos de NoGrafo
     */
    private int TeclaDecoderNo(KeyCode c) {
        return MapaTecla.get(c);
    }

    /**
     * Responsável pela atualização do campo momentum, baseia-se na ideia de que
     * caso uma direcao seja inadequada em um dado contexto (ex: subir em um
     * caminho horizontal) deve-se optar por seguir o momentum atual (ultimo
     * movimento válido)
     *
     * @param direcao direcao tomada pela entidade
     * @param momentumAtual ultimo momentum valido registrado
     * @param caminhoVertical booleano que possui a informacao da orientacao do
     * caminho atual
     * @return Momentum atualizado para o contexto
     */
    private int MomentumUpdate(KeyCode direcao, int momentumAtual, boolean caminhoVertical) {

        if (Movimento(direcao)) {
            if (Vertical(direcao) && caminhoVertical) {

                if ((momentumAtual == -1 && direcao == DESC) || momentumAtual == 1 && direcao == SOBE) {
                    momentumAtual *= (-1);
                }

            } else {

                if (Horizontal(direcao) && (!caminhoVertical)) {

                    if ((momentumAtual == -1 && direcao == DIRE) || momentumAtual == 1 && direcao == ESQU) {
                        momentumAtual *= (-1);
                    }
                }
            }
        }

        return momentumAtual;
    }

    /**
     * Invocada dentro de MoveEntidade, contendo a indicação se o nó atual
     * contém um caminho na direção escolhida
     *
     * @param sentidoPositivo indica, atraves da convencao de que valores
     * visualmente mais a direita e abaixo são maiores (informalmente "eixo Y
     * apontando para baixo e X para a direita"), parte relevante da informacao
     * da direcao seguida
     * @param caminhoBuscadoVertical indica a orientacao do candidato a caminho
     * buscado, completando toda a informação necessaria para descobrir a tecla
     * @param vizinhos passado para a verificacao da existencia ou nao de um
     * outro no naquela direcao escolhida
     * @return Valor verdade da existencia de um caminho naquela direcao
     */
    private boolean ExisteCaminho(boolean sentidoPositivo, boolean caminhoBuscadoVertical, int[] vizinhos) {

        if (caminhoBuscadoVertical) {

            return ((!sentidoPositivo) && (vizinhos[TeclaDecoderNo(SOBE)] != -1)) || ((sentidoPositivo) && (vizinhos[TeclaDecoderNo(DESC)] != -1));

        }

        return ((!sentidoPositivo) && (vizinhos[TeclaDecoderNo(ESQU)] != -1)) || ((sentidoPositivo) && (vizinhos[TeclaDecoderNo(DIRE)] != -1));

    }

    /**
     * Executa a logica da movimentacao dos fantasmas, utilizando conceitos de
     * polimorfismo
     *
     * @param Fantasmas Conjunto de fantasmas a serem movimentados
     * @param CoordPacman Localização do pacman a ser passada para cada fantasma para que possam segui-lo.
     * @param IDnoPacman Informação crucial acerca de qual nó o jogador esta se direcionando para, de modo a ter uma direção de ataque coerente.
     * @param mapaJogo Mapa do jogo utilizado é passado de modo a permitir
     * movimentação dos fantasmas
     */
    public void MoveFantasmas(ArrayList<Fantasma> Fantasmas, Mapa mapaJogo, Integer[] CoordPacman, int IDnoPacman) {

        for (Fantasma f : Fantasmas) {
            
            if (f.GetContador() == 0) {
                int direcaoIndex = f.GetNextDirecao(mapaJogo, CoordPacman, IDnoPacman);
                KeyCode direcao = (direcaoIndex == 0) ? SOBE : ((direcaoIndex == 1) ? ESQU : ((direcaoIndex == 2) ? DESC : ((direcaoIndex == 3) ? DIRE : INVALIDA)));
                MoveEntidade(direcao, mapaJogo, f);
            }
            f.AddContador();
        }

    }

    /**
     * Executa a lógica de movimentação do jogador (pacman)
     *
     * @param pacman instancia da classe jogador a ser movimentada
     * @param mapaJogo Mapa do jogo utilizado é passado de modo a permitir
     * movimentação do jogador4
     * @exception PauseException Indica a possibilidade de lancamento da excecao
     * de pausa do jogo
     * @exception IndexOutOfBoundsException Pelo uso constante de acessos em
     * arrays na logica foi adicionada por seguranca
     * @return retorna o atributo capturado pelo pacman em sua movimentacao
     */
    public int MovePacman(Player pacman, Mapa mapaJogo) throws PauseException, IndexOutOfBoundsException {
    
        if (estaPausado) {
            FlipPausa();
            throw new PauseException();
        }

        if (pacman.GetContador() == 0) {
            MoveEntidade(teclaPressionada, mapaJogo, pacman);

            // agora tem de retornar aquilo que estava naquela posicao, de modo que possa ser tratado pelo juiz
            int offset = pacman.GetOffset();
            Caminho caminhoAtualPlayer = pacman.GetCaminho();

            int atributo;
            if (offset == 0 || offset == (caminhoAtualPlayer.GetTamanho() - 1)) {
                atributo = caminhoAtualPlayer.GetNosFronteira(offset).GetSetSlot();
            } else {
                atributo = caminhoAtualPlayer.GetSetSlot(offset);
                if (atributo == -1) {
                    throw new IndexOutOfBoundsException();
                }
            }
            
            pacman.AddContador();
            return atributo;

        } else {
            pacman.AddContador();
            return 0;
        }
    }


    /**
    * Esta função é adicionada devido ao fato de existirem entidades com movimentos estocásticos (fantasmas aleatórios), que
    * podem submeter um movimento inválido. 
    * Caso a direcao para a qual o fantasma está seguindo seja inválida, ele apenas permanece no mesmo caminho.
    *
    * @param sentidoPositivo Indica se o sentido buscado é aquele convencionado como positivo.
    * @param caminhoBuscadoVertical Indica se o caminho é vertical.
    * @return Index do nó na convenção utilizada.
    */
    private int CorrecaoIndexInvalido(boolean sentidoPositivo, boolean caminhoBuscadoVertical) {
        return (sentidoPositivo) ? ((caminhoBuscadoVertical) ? 2 : 3) : ((caminhoBuscadoVertical) ? 0 : 1);
    }

    /**
     * Executa a parte genérica da lógica de movimentação das entidades
     *
     * @param direcao direcao em que a entidade escolheu seguir (seja pela
     * escolha do jogador ou do algoritmo particular do fantasma naquele
     * instante)
     * @param mapaJogo passado para realizar as trocas de caminho
     * @param E entidade passada para ser movimentada
     */
    private void MoveEntidade(KeyCode direcao, Mapa mapaJogo, Entidade E) {

        Caminho caminhoAtual = E.GetCaminho();
        int offset = E.GetOffset();
        int momentumAtual = E.GetMomentum();

        int offsetNovo;
        int momentumNovo;

        // temos 2 casos: EM caminho ou EM no
        if (offset == 0 || offset == (caminhoAtual.GetTamanho() - 1)) { // Em NO

            int index = TeclaDecoderNo(direcao);

            boolean caminhoBuscadoVertical;
            boolean sentidoPositivo;

            if (index == -1) { // direcao invalida equivale a permanecer nesta trajetoria (direcao e sentido)
                caminhoBuscadoVertical = caminhoAtual.GetVertical();
                sentidoPositivo = (momentumAtual > 0);
                offsetNovo = offset;
                momentumNovo = momentumAtual;
            } else {
                caminhoBuscadoVertical = Vertical(direcao);
                sentidoPositivo = (direcao == DESC) || (direcao == DIRE);
            }

            NoGrafo NoAtual = caminhoAtual.GetNosFronteira(offset);

            if (ExisteCaminho(sentidoPositivo, caminhoBuscadoVertical, NoAtual.GetVizinhos())) {

                Caminho novoCaminho = (index != -1) ? mapaJogo.GetProxCaminho(NoAtual, index) : mapaJogo.GetProxCaminho(NoAtual, CorrecaoIndexInvalido(sentidoPositivo, caminhoBuscadoVertical));

                E.SetCaminho(novoCaminho);

                offsetNovo = (sentidoPositivo) ? 1 : novoCaminho.GetTamanho() - 2;
                momentumNovo = (sentidoPositivo) ? 1 : -1;
            } else {
                offsetNovo = offset;
                momentumNovo = momentumAtual;
            }

        } else { // Em caminho

            momentumNovo = MomentumUpdate(direcao, momentumAtual, caminhoAtual.GetVertical());

            offsetNovo = offset + (momentumAtual + momentumNovo) / 2;

        }

        E.SetOffset(offsetNovo);
        E.SetMomentum(momentumNovo);

    }

}
