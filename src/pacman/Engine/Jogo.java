package pacman.Engine;

import pacman.Sistema.Arquivo;
import pacman.Interface.Menu;
import pacman.Sistema.Mapa;
import pacman.Sistema.Blueprint;
import pacman.Sistema.Player;
import pacman.Sistema.Fantasma;
import pacman.Sistema.FantasmaAleatorio;
import pacman.Sistema.FantasmaPerseguidor;
import pacman.Interface.Cena;
import pacman.Sistema.Caminho;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
* Esta classe faz a função de fluxo de lógica da engine, funcionando como uma main (thread principal).
*
* @see pacman.Interface.PacMan
* 
* @author diego-fleury
* @version v1.0
* @since Alpha v0.1
*/
public class Jogo {

    // Variaveis de sistema
    private Player PacmanPlayer;
    private ArrayList<Fantasma> Fantasmas;
    private Menu menuInicial;
    private final String PlayerScoreFile;
    private ArrayList<Blueprint> listaBlueprints;
    private final Controlador joystick = new Controlador();
    private Mapa mapaJogo;
    private Cena cenario = null;

    // Variaveis de jogo
    private ArrayList<Integer> ThresholdsBonus;
    private int indexP;
    private int PacdotsComidos = 0;
    private int Nivel = 1;
    private int pontosAcumulados = 0;
    private int ContadorAceleracao = 1, FantasmasComidos = 0;
    private boolean frutaVisivel = false;
    private int CheckPointVida = 1;
    
    // Definicoes de constantes
    private static final int TempoBaseFuga = 158, MinimoTempoFuga = 30;
    private static final int opcaoContinuar = 0, opcaoMudarMapa = 1, opcaoSairDoJogo = -1;
    private static final int VAZIO = 0, PACDOT = 1, POWER_PILL = 2, FRUTA = 3;
    private static final int MORTO = 0, VIVO = 1, FUGA = 2;

    // Variaveis de fluxo de controle
    private boolean fimDoJogo = false;
    private final double FPS = 20;
    private Timer timer;
    private TimerTask timertask;
    private boolean pausado = false;

    /**
     * Construtor de classe jogo, responsavel por uma breve checagem dos
     * arquivos alem da inicialização da aplicação
     *
     * @param pArquivosMapas Pares de nomes de arquivos que contem os metadados
     * para geração dos mapas
     * @param pPlayerScoreFile Arquivo em que as pontuações dos jogadores devem
     * ser registradas
     * @param primaryStage Palco gerado pela chamada do método start da aplicação JavaFX.
     */
    public Jogo(String[] pArquivosMapas, String pPlayerScoreFile, Stage primaryStage) {

        PlayerScoreFile = pPlayerScoreFile;

        InicializaArrayMapas(pArquivosMapas);

        MenuInicial(listaBlueprints, primaryStage);

    }

    /**
     * Realiza a geração da lista de Blueprints de mapas do jogo.
     *
     * @param arquivosMapas Strings que contem os nomes dos arquivos passados ao
     * programa
     */
    private void InicializaArrayMapas(String[] arquivosMapas) {

        Arquivo Arq = new Arquivo();
        listaBlueprints = new ArrayList<>();

        for (int i = 0; i < arquivosMapas.length; i += 2) {

            // pares => MAPA.csv
            // impares => CAMINHO.txt
            String fileMapa = arquivosMapas[i];
            String fileCaminho = arquivosMapas[i + 1];

            listaBlueprints.add(new Blueprint(Arq.LeMapaConfigCSV(fileMapa), Arq.GetArestaConfig(fileCaminho)));

        }
    }

     /**
     * Funcao que inicializa o menuPrincipal do jogom além de inicializar as mais diversas variáveis.
     * Através da chamada dos métodos de SetGame e initGame pede-se informação ao usuário de como quer jogar.
     *
     * @param listaBlueprints Conjunto de classes que contem os metadados para
     * cada mapa disponivel
     * @param primaryStage Palco gerado pela chamada do método start da aplicação JavaFX.
     */
    private void MenuInicial(ArrayList<Blueprint> listaBlueprints, Stage primaryStage) {

        menuInicial = new Menu(listaBlueprints);

        SetGame(primaryStage);
        
        initGame(primaryStage);

    }

    /**
    * Esta função é responsável pela cuidadosa inicialização de parâmetros a CADA vez que um novo mapa é solicitado pelo jogador.
    * Chama métodos do menuInicial para carregar interfaces gráficas que são reponsáveis por pegar diversos valores, inicializa outros
    * com valores-padrão a cada inicio de mapa.
    *
    *@param primaryStage Palco gerado pela chamada do método start da aplicação JavaFX.
    */
    private void SetGame(Stage primaryStage) {

        Nivel = 1;
        CheckPointVida = 1;
        PacdotsComidos = 0;
        String nome = menuInicial.getNome();
        mapaJogo = menuInicial.SelecionaMapa();
        Blueprint blueprintSelecionada = menuInicial.GetblueprintSelecionada();

        initEntidades(mapaJogo, nome, 0, blueprintSelecionada);

        ThresholdsBonus = blueprintSelecionada.GetBonus();
        ThresholdsBonus.add(mapaJogo.GetTotalPacdots());
        indexP = 0;

        cenario = new Cena(blueprintSelecionada, primaryStage, joystick, PacmanPlayer.GetCoord());
        EventHandler<ActionEvent> pausa = new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                pausado = true;
                MenuPausa(primaryStage);
                pausado = false;
            }

        };

        cenario.Monta(nome, pausa);
        ContadorAceleracao = 1;
        FantasmasComidos = 0;
        frutaVisivel = false;
        
    }
    
    /**
     * Este método realiza a inicialização dos valores de todas as entidades, de modo a prepará-las para o jogo.
     * Devido à noção de um coletor de lixo do java, optou-se pela simplificação do código pela simples instanciação de
     * novas instâncias da mesma classe das entidades a cada vez que faz-se necessário, porém com valores diferentes solicitados
     * nos argumentos (pois se estamos apenas passando de nível devemos salvar os valores até então acumulados, enquanto que se estamos
     * iniciando um novo mapa todos os valores devem ser tomados como específicos).
     *
     * @param mapaJogo intância de classe ativa de mapa.
     * @param nome Nome do jogador.
     * @param PontosIniciais Pontuação que deseja-se atribuir ao jogador, diferente se esta função é chamada através de Set ou Reset Game.
     * @param blueprintSelecionada Metadados do mapa selecionada. 
     */
    private void initEntidades(Mapa mapaJogo, String nome, int PontosIniciais,Blueprint blueprintSelecionada) {

        PacmanPlayer = new Player(nome, mapaJogo.GetCaminhoInicial(true), blueprintSelecionada.GetOffsetInicial(true), blueprintSelecionada.GetMomentumInicial(true),PontosIniciais);

        Fantasmas = new ArrayList<>();

        Caminho caminhoInicialFantasmas = mapaJogo.GetCaminhoInicial(false);
        int offsetInicialFantasmas = blueprintSelecionada.GetOffsetInicial(false);
        int momentumInicialFantasmas = blueprintSelecionada.GetMomentumInicial(false);

        ArrayList<String> NomesFantasmas = new ArrayList<>();
        NomesFantasmas.add("Clyde");
        NomesFantasmas.add("Inky");
        NomesFantasmas.add("Blinky");
        NomesFantasmas.add("Pinky");

        int tFuga = (TempoBaseFuga - 2*Nivel >= MinimoTempoFuga) ? TempoBaseFuga - 2*Nivel : MinimoTempoFuga;
        for (int i = 0; i < 4; i++) {
            int IdNoAleatorio = ThreadLocalRandom.current().nextInt(0, blueprintSelecionada.GetNumVertices() - 1);
            Integer[] AlvoFantasma = new Integer[2];
            AlvoFantasma[0] = mapaJogo.GetNoGrafo(IdNoAleatorio).GetX();
            AlvoFantasma[1] = mapaJogo.GetNoGrafo(IdNoAleatorio).GetY();

            if (i < 2) {
                Fantasmas.add(
                        new FantasmaAleatorio(NomesFantasmas.get(i), caminhoInicialFantasmas, offsetInicialFantasmas, momentumInicialFantasmas, AlvoFantasma, IdNoAleatorio,tFuga));
            } else {
                Fantasmas.add(
                        new FantasmaPerseguidor(NomesFantasmas.get(i), caminhoInicialFantasmas, offsetInicialFantasmas, momentumInicialFantasmas, AlvoFantasma, IdNoAleatorio,tFuga));
            }

        }

    }

    /**
     * Esta função é responsável pela lógica do que é executado a cada definido intervalo de tempo (tomado como 0,05 segundos por testes)
     * de modo a gerar a experiência do fluxo de ações do jogo.
     * 
     * Possivelmente a função mais importante do código, a cada iteração o timer realiza uma timertask, que é definida pela sobrecarga do
     * método run. Esta 'task' é a execução do loop de eventos do jogo, que implica na atualização de valores na tela e internamente a cada
     * iteração do clock. 
     * 
     * O que é implementado é uma simples execução da função GameLoop, que caso retorne na iteração anterior fimDoJogo, resulta
     * no ato de salvar os valores no arquivo e fechar toda a aplicação. 
     * 
     * Caso uma interrupção de pausa seja detectada, a variável pausado assume o valor de true, o que bloqueia a execução da lógica do jogo
     * pelas próximas threads criadas, até que a atual termine a ação desejada pelo jogador ao pausar.
     *
     *@param primaryStage Palco gerado pela chamada do método start da aplicação JavaFX.
     */
    private void initGame(Stage primaryStage) {

        timer = new Timer();
        timertask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            if (!fimDoJogo) {
                                if (!pausado) {
                                    fimDoJogo = GameLoop();
                                }
                            } else {
                                // Salvar o jogo
                                Arquivo Arq = new Arquivo();
                                try{
                                    Arq.RegistraPontuacaoJogador(PlayerScoreFile, PacmanPlayer.GetNome(),PacmanPlayer.GetPontos(),Nivel,menuInicial.GetIndexMapa());
                                }catch(IOException e){
                                    System.out.println("Infelizmente nao foi possivel salvar o score");
                                    e.printStackTrace();
                                }
                                
                                primaryStage.fireEvent(
                                        new WindowEvent(
                                                primaryStage,
                                                WindowEvent.WINDOW_CLOSE_REQUEST
                                        )
                                );

                                System.out.println("Saindo do programa ... ");
                                System.exit(0);
                            }
                        } catch (PauseException p) {
                            pausado = true;
                            MenuPausa(primaryStage);
                            pausado = false;
                        }
                    }
                });
            }
        };

        long PeriodoFundamental = (long) (1000.0 / FPS);
        timer.schedule(timertask, 0, PeriodoFundamental);

    }

    /**
     * Este método implementa uma funcionalidade similar àquela presente em SetGame, porém é chamada no contexto de passagem de
     * um nível ao outro. 
     * 
     * Por conta desta diferença, os valores a serem passados e setados são diferentes, e portanto integrados em outra função.
     * @param primaryStage Palco gerado pela chamada do método start da aplicação JavaFX.
     */
    private void ResetGame(Stage primaryStage){

        pontosAcumulados = PacmanPlayer.GetPontos();
        int vidas = PacmanPlayer.GetVidas();
        PacdotsComidos = 0;
        Nivel++;
        mapaJogo = new Mapa();
        mapaJogo.MontaMapa(menuInicial.GetblueprintSelecionada());
        indexP = 0;
        initEntidades(mapaJogo, PacmanPlayer.GetNome(), pontosAcumulados,menuInicial.GetblueprintSelecionada());
        cenario = new Cena( menuInicial.GetblueprintSelecionada(), primaryStage, joystick, PacmanPlayer.GetCoord());
        EventHandler<ActionEvent> pausa = new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                pausado = true;
                MenuPausa(primaryStage);
                pausado = false;
            }

        };

        cenario.Monta(PacmanPlayer.GetNome(), pausa);
        ContadorAceleracao = 1;
        FantasmasComidos = 0;
        frutaVisivel = false;
        PacmanPlayer.SetVida(vidas);
        PacmanPlayer.SetNivel(Nivel);
    }

    /**
     * Este método apenas faz o fluxo de controle do menu de pausa, solicitando input do player e direcionando a 
     * opcao escolhida para as funções desejadas.
     * @param primaryStage Palco gerado pela chamada do método start da aplicação JavaFX. 
     */
    private void MenuPausa(Stage primaryStage) {

        int opcao = menuInicial.getOption();

        switch (opcao) {
            case opcaoContinuar:
                break;

            case opcaoMudarMapa:
                SetGame(primaryStage);
                break;

            case opcaoSairDoJogo:
                fimDoJogo = true;
                break;

            default:
                break;

        }

    }

    /**
     * Funcao que é responsavel pela logica do jogo em si.
     *
     * Inicialmente movimenta todas as entidades e depois retorna o valor verdade do Juiz acerca
     * do estado atual do jogo.
     *
     * @exception PauseException Excecao lancada quando a tecla de pause for
     * pressionada pelo jogador, causando a aparicao do menu com opcoes
     * @exception IndexOutOfBoundsException Excecao provavelmente nao sera
     * lancada, porem pelo uso constante de acessos em arrays na logica foi
     * adicionada
     * @return Retorna o valor verdade do final do jogo, sendo true quando o
     * player morrer.
     */
    private boolean GameLoop() throws PauseException, IndexOutOfBoundsException {

        int atributo = joystick.MovePacman(PacmanPlayer, mapaJogo);
        PacdotsComidos += (atributo == PACDOT) ? 1 : 0;

        // atualiza os pontos
        PacmanPlayer.Update(atributo);

        // Sera passada uma instancia de AStar (pois temos de saber as coordenadas x e y do player)
        joystick.MoveFantasmas(Fantasmas, mapaJogo,
                PacmanPlayer.GetCoord(),
                PacmanPlayer.GetCaminho().GetNosFronteira((PacmanPlayer.GetMomentum() == -1) ? 0 : 1).GetID());

        return Juiz(atributo);
    }

    /**
     * Esta função realiza a manipulação das variáveis de jogo, incluindo o estado dos fantasmas, pontuação do jogador,
     * direcionamento da lógica das vidas, solicitação de updates na imagem mostrada pela aplicação JavaFX, entre outras coisas.
     * 
     * Inicialmente buscou-se a implementação baseada em máquinas de estado, inclusive com a consideração da implementação deste
     * paradigma de design de classe. Porém devido a várias razões (incluindo complexibilidade, interpretabilidade e tempo) não
     * seguiu-se com esta ideia, sendo assim o uso de muitas variáveis privadas adotadas em seu lugar.
     *
     *@param atributo valor capturado pelo jogador no passo atual andado.
     *@return valor-verdade acerca de se o fimDoJogo fora ou não atingido.
     */
    private boolean Juiz(int atributo) {
        
        Integer[] CoordPacman = PacmanPlayer.GetCoord();

        ArrayList<Integer[]> CoordFantasmas = new ArrayList<>();
        ArrayList<Integer> EstadosFantasmas = new ArrayList<>();

        for (Fantasma f : Fantasmas) {
            CoordFantasmas.add(f.GetCoord());
            EstadosFantasmas.add(f.GetEstado());
        }
        
        if(PacmanPlayer.GetPontos() > 10000*CheckPointVida){
            PacmanPlayer.SetVida(PacmanPlayer.GetVidas()+1);
            CheckPointVida++;
        }
        
        if (PacdotsComidos >= ThresholdsBonus.get(indexP)) {
            indexP++;
            if (indexP == ThresholdsBonus.size()) {
                
                pausado = true;
                ResetGame(cenario.Desmonta());
                pausado = false;
                return false;

            } else {
                frutaVisivel = true;
                PacmanPlayer.SetFruta(true);
            }
        }

        if(ContadorAceleracao * mapaJogo.GetTotalPacdots()/3 < PacdotsComidos){
            Fantasmas.get(2).SetVelocidadeAtaque(5 - ContadorAceleracao);
            ContadorAceleracao++;
        }

        for (Fantasma f : Fantasmas) {
            if (Arrays.equals(CoordPacman, f.GetCoord())) {
                if (f.GetEstado() == VIVO) {
                    FantasmasComidos = 0;
                    PacmanPlayer.SetVida(PacmanPlayer.GetVidas() - 1);

                    if (PacmanPlayer.GetVidas() == 0) {
                        return true;
                    } else {
                        frutaVisivel = false;
                        PacmanPlayer.SetFruta(false);
                        PacmanPlayer.SetCaminho(mapaJogo.GetCaminhoInicial(true));
                        PacmanPlayer.SetMomentum(menuInicial.GetblueprintSelecionada().GetMomentumInicial(true));
                        PacmanPlayer.SetOffset(menuInicial.GetblueprintSelecionada().GetOffsetInicial(true));

                        for (Fantasma g : Fantasmas) {
                            g.SetCaminho(mapaJogo.GetCaminhoInicial(false));
                            g.SetMomentum(menuInicial.GetblueprintSelecionada().GetMomentumInicial(false));
                            g.SetOffset(menuInicial.GetblueprintSelecionada().GetOffsetInicial(false));
                            g.SetEstado(VIVO);
                        }

                        break;
                    }
                } else {
                    if (f.GetEstado() == FUGA) {
                        FantasmasComidos++;
                        PacmanPlayer.FantasmaComido(FantasmasComidos);
                        f.SetEstado(MORTO);
                        cenario.SetEstado(Fantasmas.indexOf(f),MORTO);
                    }
                }
            }
            if ( f.GetEstado() == MORTO 
                && ((f.GetCaminho().GetNosFronteira(0) == mapaJogo.GetCaminhoInicial(false).GetNosFronteira(0) || f.GetCaminho().GetNosFronteira(0) == mapaJogo.GetCaminhoInicial(false).GetNosFronteira(1))
                || (f.GetCaminho().GetNosFronteira(1) == mapaJogo.GetCaminhoInicial(false).GetNosFronteira(0) || f.GetCaminho().GetNosFronteira(1) == mapaJogo.GetCaminhoInicial(false).GetNosFronteira(1))
                )) { // Ja esta na origem
                
                f.SetEstado(VIVO);
                cenario.SetEstado(Fantasmas.indexOf(f),VIVO);
            }
        }

        if (atributo == POWER_PILL) {
            FantasmasComidos = 0;
            for (Fantasma f : Fantasmas) {
                f.SetEstado(FUGA);
                cenario.SetEstado(Fantasmas.indexOf(f),FUGA);
            }

        } else {

            if (atributo == FRUTA && PacmanPlayer.GetFruta()) {
                PacmanPlayer.SetFruta(false);
                frutaVisivel = false;
            }

        }

        cenario.Update(
                CoordPacman,
                CoordFantasmas,
                EstadosFantasmas,
                PacmanPlayer.GetVidas(),
                PacmanPlayer.GetPontos(),
                frutaVisivel,
                Nivel
        );

        return false;
    }

}
