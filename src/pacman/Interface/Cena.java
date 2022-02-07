package pacman.Interface;

import pacman.Engine.Controlador;
import pacman.Sistema.Blueprint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
* Esta classe faz a função de controle daquilo que é exposto no palco principal da aplicação.
*   
* @see pacman.Engine.Jogo
* 
* @author diego-fleury
* @version v1.0
* @since Alpha v0.1
*/
public class Cena {
    
    // Variaveis de sistema
    private final Stage palco;
    private final Blueprint blueprintSelecionada;
    private final Controlador joystick;
    private static final int MORTO = 0, VIVO = 1, FUGA = 2;

    // Definicao de tamanhos usados para elementos fundamentais
    private final int SQUARE_SIDE = 20;
    private int COLS, ROWS;
    private final double TOP_SIDE = 100, BOTTOM_SIDE = 100, LEFT_SIDE = BorderWidths.DEFAULT.getLeft();
    private double HEIGHT, WIDTH;
    
    // Definicao da forma encontrada de rápido acesso aos elementos do tabuleiro
    private ObservableList<Node>[][] ElementosTabuleiro = null;

    // Definicao de cores
    private final Color BACKGROUND_COLOR = Color.AQUAMARINE,
            PACDOT_COLOR = Color.DARKORANGE,
            POWER_PILL_COLOR = Color.ORANGE,
            SPECIAL_BACKGROUND_COLOR = Color.WHITE,
            FOREGROUND_COLOR = Color.DARKBLUE;

    // Definicao de tamanhos de valores compostos
    private final double PACDOT_RADIUS = 2.0, POWER_PILL_RADIUS = 5.0;
    private final double PAD_BACKGROUND = 1, SPECIAL_PAD_BACKGROUND = 2;
    private final double BACKGROUND_SQUARE_SIDE = SQUARE_SIDE - PAD_BACKGROUND,
            SPECIAL_BACKGROUND_SQUARE_SIDE = SQUARE_SIDE - SPECIAL_PAD_BACKGROUND;

    // Variaveis de controle de movimentacao das entidades 
    private Integer[] UltimasCoordenadasPacman;
    private int UltimoAnguloPacman = 0;
    private final ArrayList<Integer> UltimosEstadosFantasmas = new ArrayList<>();
    private Integer[] frutaCoord;
    private boolean frutaVisivelAntes = false;

    // Labels usadas para mostrar valores relevantes
    private final Label DisplayVidas = new Label("vidas = 3");
    private final Label DisplayPontos = new Label("Pontuacao = 0");
    private final Label DisplayNivel = new Label("Nivel = ");
    
    // Definicao de arrays de imagens e retratos (ImageView)
    private final ArrayList<ImageView> Fantasmas = new ArrayList<>();
    private final ArrayList<Image> FantasmasImagens = new ArrayList<>();
    private final ArrayList<Image> FrutasImagens = new ArrayList<>();
    
    // Definicao da imagem e retrato do pacman 
    private final ImageView pacmanDireita = new ImageView(new Image(getClass().getResourceAsStream("Visual/PacmanDireita.gif")));

    // Definicao das imagens dos fantasmas (vivos)
    private final Image BlinkyVIVO = new Image(getClass().getResourceAsStream("Visual/Blinky.gif"));
    private final Image ClydeVIVO = new Image(getClass().getResourceAsStream("Visual/Clyde.gif"));
    private final Image InkyVIVO = new Image(getClass().getResourceAsStream("Visual/Inky.gif"));
    private final Image PinkyVIVO  = new Image(getClass().getResourceAsStream("Visual/Pinky.gif"));
    
    // Definicao dos retratos dos fantasmas (inicialmente vivos)
    private final ImageView Blinky = new ImageView(BlinkyVIVO);
    private final ImageView Clyde = new ImageView(ClydeVIVO);
    private final ImageView Inky = new ImageView(InkyVIVO);
    private final ImageView Pinky = new ImageView(PinkyVIVO);

    // Definicao das imagens dos outros estados dos fantasmas
    private final Image FantasmaMorto = new Image(getClass().getResourceAsStream("Visual/FantasmaMorto.gif"));
    private final Image FantasmaFuga = new Image(getClass().getResourceAsStream("Visual/FantasmaFugaPOO.gif"));
    
    // Definicao das imagens (e retrato) das frutas
    private final Image Cherry = (new Image(getClass().getResourceAsStream("Visual/Cherry.png")));
    private final Image Strawberry = (new Image(getClass().getResourceAsStream("Visual/Strawberry.png")));
    private final Image Orange = (new Image(getClass().getResourceAsStream("Visual/Orange.png")));
    private final Image Apple = (new Image(getClass().getResourceAsStream("Visual/Apple.png")));
    private final Image Melon = (new Image(getClass().getResourceAsStream("Visual/Melon.png")));
    private final Image Galaxian = (new Image(getClass().getResourceAsStream("Visual/Galaxian.png")));
    private final Image Bell = (new Image(getClass().getResourceAsStream("Visual/Bell.png")));
    private final Image Key = (new Image(getClass().getResourceAsStream("Visual/Key.jpeg")));
    private final ImageView Fruta = new ImageView();

    /**
     * Pequena inicializacao de valores mostrados, de modo que os retratos possam ser adequados aos tamanhos definidos. 
     */
    private void initImagens() {

        pacmanDireita.setFitHeight(BACKGROUND_SQUARE_SIDE);
        pacmanDireita.setFitWidth(BACKGROUND_SQUARE_SIDE);

        Fantasmas.add(Clyde);
        Fantasmas.add(Inky);
        Fantasmas.add(Blinky);
        Fantasmas.add(Pinky);

        FantasmasImagens.add(ClydeVIVO);
        FantasmasImagens.add(InkyVIVO);
        FantasmasImagens.add(BlinkyVIVO);
        FantasmasImagens.add(PinkyVIVO);

        for (ImageView F : Fantasmas) {
            F.setFitHeight(BACKGROUND_SQUARE_SIDE);
            F.setFitWidth(BACKGROUND_SQUARE_SIDE);
        }

        FrutasImagens.add(Cherry);
        FrutasImagens.add(Strawberry);
        FrutasImagens.add(Orange);
        FrutasImagens.add(Apple);
        FrutasImagens.add(Melon);
        FrutasImagens.add(Galaxian);
        FrutasImagens.add(Bell);
        FrutasImagens.add(Key);

        
        Fruta.setFitHeight(BACKGROUND_SQUARE_SIDE);
        Fruta.setFitWidth(BACKGROUND_SQUARE_SIDE);
        Fruta.setImage(Cherry);

    }
    
    /**
     *Definicao do construtor da classe Cena recebe informações acerca de como desenhar, do que desenhar e de como integrar isso com a 
     *noção de EventHandling de JavaFX.
     *
     *@param pblueprintSelecionada Metadados do mapa usado.
     *@param primaryStage Palco criado pela aplicação JavaFX.
     *@param pjoystick Controlador do jogo.
     *@param CoordIniciaisPacman Valor inicial do pacman no mapa.
     */
    public Cena(Blueprint pblueprintSelecionada, Stage primaryStage, Controlador pjoystick, Integer[] CoordIniciaisPacman) {

        blueprintSelecionada = pblueprintSelecionada;
        palco = primaryStage;
        joystick = pjoystick;
        UltimasCoordenadasPacman = CoordIniciaisPacman;

        for(int i = 0;i<4;i++)
            UltimosEstadosFantasmas.add(VIVO);

        initImagens();

    }

    /**
     * Acesso simples ao fantasma de interesse dado o index.
      @param indexFantasma valor que indica qual é o fantasma a ser acessado.
        @return retrato deste fantasma
    */
    private ImageView GetFantasma(int indexFantasma) {
        return Fantasmas.get(indexFantasma);
    }
    
    /**
     * Esta funcao realiza a troca de imagem de cada fantasma baseando-se no estado que é solicitado.
     *
     *@param indexFantasma fantasma cujo estado deve ser trocado 
     *@param Estado indica qual imagem deve ser usada (vivo, morto, fuga)
     */
    public void SetEstado(int indexFantasma, int Estado){
        Image FantasmaEstado = (Estado == MORTO) ? FantasmaMorto : ((Estado == VIVO) ? FantasmasImagens.get(indexFantasma) : FantasmaFuga);
        Fantasmas.get(indexFantasma).setImage(FantasmaEstado);
    }

   /**
    * Realiza a rotacao do pacman, de modo que a boca esteja sempre na orientacao correta.
    * Faz isso através de uma análise de orientação.
    *@param CoordNovas Coordenadas atualizadas do pacman
    *@param CoordAntigas Coordenadas antigas do pacman
    *@return Angulo de rotação do retrato do pacman.
    */
    private int RotacaoEntidade(Integer[] CoordNovas, Integer[] CoordAntigas) {

        int Xn = CoordNovas[0], Yn = CoordNovas[1];

        return (Xn == CoordAntigas[0]) ? ((Yn > CoordAntigas[1]) ? 90 : ((Yn == CoordAntigas[1]) ? UltimoAnguloPacman : 270)) : (((Xn > CoordAntigas[0]) ? 0 : 180));
    }

    /**
     * Lógica de como a fruta deve ser mostrada é tratada aqui.
     *@param Nivel Indica qual fruta deve ser adicionada
     */
    private void SetFruta(int Nivel) {

        ObservableList<Node> frutaSlot = GetElementoTabuleiro(frutaCoord[1], frutaCoord[0]);
        if (!frutaVisivelAntes) {
            //Colocar Fruta 
            frutaSlot.remove(1);
            frutaSlot.add(1, Fruta(Nivel));
        } else {

            //  Tirar Fruta
            frutaSlot.remove(1);
            frutaSlot.add(1, new Rectangle(SPECIAL_BACKGROUND_SQUARE_SIDE, SPECIAL_BACKGROUND_SQUARE_SIDE, FOREGROUND_COLOR));

        }

    }
    
    /**
     * Retorna a fruta dado o nível.
     *@param Nivel Indica qual imagem deve ser aproveitada
     *@return Fruta para aquela ocasião
     */
    private ImageView Fruta(int Nivel) {
        Fruta.setImage(FrutasImagens.get((Nivel < 8) ? Nivel - 1 : 7));
        return Fruta;
    }

    /**
    * Realiza a rotina de atualização constante de posicao de cada uma das entidades, inclusive com chamada às mudanças de estado.
    *@param CoordPacman Localização do jogador.
    *@param CoordFantasmas Localização de cada um dos fantasmas.
    *@param EstadosFantasmas Estados de cada um dos fantasmas.
    *@param Vidas Usada no display de vidas.
    *@param Pontos Usada no display de pontos. 
    *@param frutaVisivel Indica se a fruta deve ou nao aparecer naquele instante.
    *@param Nivel Usada no display de Nivel, alem de indicar qual fruta mostrar.
    */
    public void Update(Integer[] CoordPacman, ArrayList<Integer[]> CoordFantasmas, ArrayList<Integer> EstadosFantasmas, int Vidas, int Pontos, boolean frutaVisivel, int Nivel) {

        ObservableList<Node> pacmanSlotAtual = GetElementoTabuleiro(CoordPacman[1], CoordPacman[0]);
        
        if (pacmanSlotAtual.size() >= 3) {

            if (pacmanSlotAtual.get(2).getClass().equals((new Circle()).getClass())) {
                pacmanSlotAtual.remove(2);
            }
        }

        UltimoAnguloPacman = RotacaoEntidade(CoordPacman, UltimasCoordenadasPacman);
        pacmanDireita.setRotate(UltimoAnguloPacman);

        if (!pacmanSlotAtual.contains(pacmanDireita)) {
            pacmanSlotAtual.add(pacmanDireita);
        }

        UltimasCoordenadasPacman = CoordPacman;

        // Ordem: Clyde, Inky, Blinky, Pinky
        for (int i = 0; i < CoordFantasmas.size(); i++) {

            ObservableList<Node> FSlotAtual = GetElementoTabuleiro(CoordFantasmas.get(i)[1], CoordFantasmas.get(i)[0]);

            ImageView Fantasma = GetFantasma(i);
            if (!FSlotAtual.contains(Fantasma)) {
                FSlotAtual.add(Fantasma);
            }
            
            if(!UltimosEstadosFantasmas.get(i).equals(EstadosFantasmas.get(i))){
                SetEstado(i,EstadosFantasmas.get(i));
                UltimosEstadosFantasmas.set(i, EstadosFantasmas.get(i));
            }

        }

        if (frutaVisivel == !frutaVisivelAntes) {
            SetFruta(Nivel);
            frutaVisivelAntes = !frutaVisivelAntes;
        }

        DisplayVidas.setText("Vidas = " + Vidas);
        DisplayPontos.setText("Pontuacao = " + Pontos);
        DisplayNivel.setText("Nivel = " + Nivel);

    }

    /**
    * Forma de acesso constante de elementos do tabuleiro é inicializada através desta função.
    *@param tabuleiro Estrutura hierárquica do mapa.
    */
    private void SetElementosTabuleiro(GridPane tabuleiro) {

        this.ElementosTabuleiro = new ObservableList[ROWS][COLS];
        for (Node node : tabuleiro.getChildren()) {
            int col = GridPane.getColumnIndex(node);
            int row = GridPane.getRowIndex(node);
            Pane Stack = (Pane) node;
            ElementosTabuleiro[row][col] = Stack.getChildren();
        }
    }

    /**
    * Acesso constante e modular ao tabuleiro.
    *@param x Abcissa.
    *@param y Ordenada.
    *@return Hierarquia de nodes para aquela localização.
    */
    private ObservableList<Node> GetElementoTabuleiro(int x, int y) {
        return ElementosTabuleiro[x][y];
    }

    /**
    * Acesso ao fundo de cada quadrado do tabuleiro.
    *@param temFruta indica se este deve receber a coloração típica do quadrado com a fruta.
    *@return Stackpane com o fundo vazio.
    */
    private StackPane FundoVazio(boolean temFruta) {

        StackPane Fundo = new StackPane();

        Color corDeFundo = (!temFruta) ? BACKGROUND_COLOR : SPECIAL_BACKGROUND_COLOR;
        Rectangle fundo = new Rectangle(SQUARE_SIDE, SQUARE_SIDE, corDeFundo);

        double tamanhoDeFrente = (!temFruta) ? BACKGROUND_SQUARE_SIDE : SPECIAL_BACKGROUND_SQUARE_SIDE;
        Rectangle frente = new Rectangle(tamanhoDeFrente, tamanhoDeFrente, FOREGROUND_COLOR);

        Fundo.setAlignment(Pos.CENTER);
        Fundo.getChildren().addAll(fundo, frente);

        return Fundo;
    }

    /**
    * Acesso a imagem do pacdot.
    *@return pacdot
    */
    private Circle Pacdot() {
        return new Circle(PACDOT_RADIUS, PACDOT_COLOR);
    }
    
    /**
    * Acesso a imagem da power pill.
    *@return power pill.
    */
    private Circle Pilula() {
        return new Circle(POWER_PILL_RADIUS, POWER_PILL_COLOR);
    }

    /**
    * Acesso à estrutura completa de um slot do tabuleiro dado o atributo presente naquele ponto.
    *@param AtributoDoSlot 0: vazio, 1: pacdot, 2: powerpill, 3: slot de fruta
    *@return configuração total do slot.
    */
    private StackPane GetPaneConfig(Integer AtributoDoSlot) {

        StackPane Pane = FundoVazio(AtributoDoSlot == 3);

        if (AtributoDoSlot == 1 || AtributoDoSlot == 2) {

            Circle Sobreposto = (AtributoDoSlot == 1) ? Pacdot() : Pilula();
            Pane.getChildren().addAll(Sobreposto);

        }

        return Pane;

    }

    /**
    *Preprocessamento dos metadados dos caminhos do mapa de modo a permitir o assembly correto do tabuleiro.
    *@param config Configuracao daquele caminho
    *@param delta forma de controle de indexação.
    *@return Array de atributos do caminho dada a configuração.
    */
    private Integer[] GetAtributosCaminho(String config, int delta) {

        Integer[] CaminhoAttr = new Integer[delta + 1];

        String[] Comms = config.split(",");

        int fill = Integer.parseInt(Comms[0].split("\\.")[1]);

        for (int i = 0; i <= delta; i++) {
            CaminhoAttr[i] = fill;
        }

        for (int i = 1; i < Comms.length; i++) {

            String[] PutString = Comms[i].split("\\.");

            int atributoPuts = Integer.parseInt(PutString[1]);
            int indexPuts = Integer.parseInt(PutString[2]);

            CaminhoAttr[indexPuts] = atributoPuts;
        }

        return CaminhoAttr;
    }

    /**
    *Preprocessamento dos metadados do mapa de modo a permitir o assembly correto do tabuleiro e do palco, junto de inicialização de valores
    * e variáveis cruciais à seção de display.
    *@param nome Nome do jogador
    *@param pausa Estabelece link de comunicação do botão de pausa com a ação dentro do jogo.
    */
    public void Monta(String nome, EventHandler<ActionEvent> pausa) {

        // Para montar o mapa em javafx
        // Pegar dimensoes
        Integer[] CoordMax = blueprintSelecionada.GetDimensoes();
        int Xmax = CoordMax[0], Ymax = CoordMax[1];

        BorderPane root = new BorderPane();
        root.setOnKeyPressed(joystick);
        GridPane tabuleiro = new GridPane();

        COLS = Xmax + 1;
        ROWS = Ymax + 1;

        for (int i = 0; i < ROWS; i++) {
            ColumnConstraints coluna = new ColumnConstraints(SQUARE_SIDE);
            tabuleiro.getColumnConstraints().add(coluna);
        }

        for (int i = 0; i < COLS; i++) {
            RowConstraints linha = new RowConstraints(SQUARE_SIDE);
            tabuleiro.getRowConstraints().add(linha);
        }

        Map<Integer, Integer> AtributosDeFronteira = new HashMap<>();

        // inicializacao
        for (int i = 0; i < blueprintSelecionada.GetNumVertices(); i++) {
            AtributosDeFronteira.put(i, 0);
        }

        // definir aqui logica de montar
        for (Set<Integer> s : blueprintSelecionada.GetArestasKeySet()) {

            Integer[] Aresta = s.toArray(new Integer[2]);
            int No1 = Aresta[0];
            int No2 = Aresta[1];

            int X1 = blueprintSelecionada.GetX(No1), X2 = blueprintSelecionada.GetX(No2);
            int Y1 = blueprintSelecionada.GetY(No1), Y2 = blueprintSelecionada.GetY(No2);

            int deltaX = X1 - X2;
            int deltaY = Y1 - Y2;

            String config = blueprintSelecionada.GetCaminhoConfig(s);

            int atual1 = AtributosDeFronteira.get(No1);
            int atual2 = AtributosDeFronteira.get(No2);

            int maior, menor;
            boolean No1Maior;

            Integer[] AtributosCaminho;

            if (deltaX == 0) { // vertical

                No1Maior = deltaY > 0;

                menor = (No1Maior) ? Y2 : Y1;
                maior = (No1Maior) ? Y1 : Y2;

                // aqui gerar o vetor de atributos
                AtributosCaminho = GetAtributosCaminho(config, maior - menor);
                for (int i = 1; i < maior - menor; i++) {

                    StackPane p = GetPaneConfig(AtributosCaminho[i]);
                    if (AtributosCaminho[i] == 3) {

                        frutaCoord = new Integer[2];
                        frutaCoord[0] = (X1);
                        frutaCoord[1] = (menor + i);

                    }
                    tabuleiro.add(p, X1, menor + i);
                }

            } else { // horizontal

                No1Maior = deltaX > 0;

                menor = (No1Maior) ? X2 : X1;
                maior = (No1Maior) ? X1 : X2;

                AtributosCaminho = GetAtributosCaminho(config, maior - menor);
                for (int i = 1; i < maior - menor; i++) {

                    StackPane p = GetPaneConfig(AtributosCaminho[i]);
                    if (AtributosCaminho[i] == 3) {

                        frutaCoord = new Integer[2];
                        frutaCoord[0] = (menor + i);
                        frutaCoord[1] = (Y1);

                    }
                    tabuleiro.add(p, menor + i, Y1);
                }

            }

            int novo1 = (No1Maior) ? AtributosCaminho[maior - menor] : AtributosCaminho[0];
            int novo2 = (No1Maior) ? AtributosCaminho[0] : AtributosCaminho[maior - menor];

            if (novo1 > atual1) {
                AtributosDeFronteira.replace(No1, novo1);
            }

            if (novo2 > atual2) {
                AtributosDeFronteira.replace(No2, novo2);
            }

        }

        for (int i = 0; i < blueprintSelecionada.GetNumVertices(); i++) {
            StackPane p = GetPaneConfig(AtributosDeFronteira.get(i));
            if (AtributosDeFronteira.get(i) == 3) {

                frutaCoord = new Integer[2];
                frutaCoord[0] = blueprintSelecionada.GetX(i);
                frutaCoord[1] = blueprintSelecionada.GetY(i);

            }
            tabuleiro.add(p, blueprintSelecionada.GetX(i), blueprintSelecionada.GetY(i));
        }

        // inicializar estrutura de acesso constante
        SetElementosTabuleiro(tabuleiro);

        tabuleiro.setAlignment(Pos.CENTER);
        tabuleiro.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        root.setCenter(tabuleiro);

        HBox superior = new HBox(100);
        superior.setAlignment(Pos.CENTER);
        root.setTop(superior);
        BorderPane.setAlignment(superior, Pos.CENTER);

        superior.getChildren().addAll(DisplayVidas, DisplayNivel, DisplayPontos);

        HBox inferior = new HBox(100);
        inferior.setAlignment(Pos.CENTER);
        root.setBottom(inferior);
        BorderPane.setAlignment(inferior, Pos.CENTER);

        Button BotaoMenu = new Button("Menu");
        BotaoMenu.setOnAction(pausa);
        inferior.getChildren().addAll(new Label("Trabalho POO"), BotaoMenu, new Label(nome));

        HEIGHT = (ROWS * SQUARE_SIDE) + TOP_SIDE + BOTTOM_SIDE;
        WIDTH = (COLS * SQUARE_SIDE) + 2 * LEFT_SIDE;

        Scene cenario = new Scene(root);

        palco.setTitle("Pacman");
        palco.setScene(cenario);
        palco.centerOnScreen();
        palco.setWidth(WIDTH);
        palco.setHeight(HEIGHT);
        palco.setResizable(false);
        palco.show();
        root.requestFocus();

        ObservableList<Node> pacmanSlotInicial = GetElementoTabuleiro(UltimasCoordenadasPacman[1], UltimasCoordenadasPacman[0]);
        pacmanSlotInicial.add(pacmanDireita);

    }

    /**
    * Fecha o palco e o retorna para a aplicação principal reutiliza-lo.
    *@return palco principal.
    */
    public Stage Desmonta(){
        palco.close();
        return palco;
    }
}
