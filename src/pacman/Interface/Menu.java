package pacman.Interface;

import pacman.Sistema.Blueprint;
import pacman.Sistema.Mapa;

import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
* Esta classe faz a função de recuperação de dados do jogador através da geração de janelas gráficas que recuperam textp.
*   
* @see pacman.Engine.Jogo
* 
* @author diego-fleury
* @version v1.0
* @since Alpha v0.1
*/
public class Menu {

    private final ArrayList<Blueprint> listaBlueprints;
    private Blueprint blueprintSelecionada;
    private String inputUser = "";
    private int indexBlueprintSelecionada;

    /**
     * Na instanciacao da classe apenas é passada a lista das classes que contem
     * os metadados dos mapas
     * @param plistaBlueprints conjunto de metadados de mapas
     */
    public Menu(ArrayList<Blueprint> plistaBlueprints) {
        listaBlueprints = plistaBlueprints;
    }

    /**
     * Responsavel pela captura do
     * nome do jogador desta secção
     *
     * @return Nome informado pelo jogador, para registro no arquivo de
     * pontuações
     */
    public String getNome() {

        Stage promptNome = Prompt("Bem vindo ao Pacman !\nFavor inserir o seu nome para comecar: ",
                "");
        promptNome.showAndWait();

        return inputUser;
    }

    /**
     * Responsavel pela exposição ao
     * usuário dos mapas disponiveis, para escolha
     *
     * @return Indice do mapa escolhido pelo jogador
     */
    private int getMapa() {

        String selecao = "Selecionar numeros entre 0 e " + String.valueOf(listaBlueprints.size() - 1);
        Stage promptMapa = Prompt("Selecione um mapa dentre os detectados no jogo:", selecao);

        promptMapa.showAndWait();
        Integer MapaSelecionado = Integer.parseInt(inputUser);

        return MapaSelecionado;
    }

    /**
     * Responsavel pela captura da
     * opcao escolhida no menu de pausa.
     *
     * @return Optcao escolhida pelo jogador
     */
    public int getOption() {

        Integer Opcao = 0;

        boolean OpcaoValida = false;

        while (!OpcaoValida) {

            try {
                
                Stage promptOpcao = Prompt("Jogo pausado, selecione uma opcao para continuar:\n[-1] | Sair do jogo\n[0] | Continuar o jogo\n[1] | Mudar de mapa", "Selecione uma opção");

                promptOpcao.showAndWait();
                
                Opcao = Integer.parseInt(inputUser);

            } catch (Exception e) {
                OpcaoValida = true;
            }
            if (!(Opcao < -1 || Opcao > 1)) {
                OpcaoValida = !OpcaoValida;
            }
        }

        return Opcao;
    }
    
    /**
    * Retorna ao jogo principal o mapa escolhido já montado, além de armazenar a blueprint de metadados.
    *@return Mapa escolhido pelo jogador
    */
    public Mapa SelecionaMapa() {

        Mapa mapaJogo = new Mapa();
        int IndexMapaSelecionado = 0;
        boolean mapaValido = false;

        while (!mapaValido) {

            try {
                IndexMapaSelecionado = getMapa();

            } catch (Exception e) {
                mapaValido = true;
            }
            if (!(IndexMapaSelecionado < 0 || IndexMapaSelecionado >= listaBlueprints.size())) {
                mapaValido = !mapaValido;
            }
        }

        blueprintSelecionada = listaBlueprints.get(IndexMapaSelecionado);
        indexBlueprintSelecionada = IndexMapaSelecionado;
        mapaJogo.MontaMapa(blueprintSelecionada);
        return mapaJogo;
    }

    /**
    * Recuperação simples da última blueprint escolhida pelo jogador.
    *@return blueprintSelecionada.
    */
    public Blueprint GetblueprintSelecionada() {
        return blueprintSelecionada;
    }
    
    /**
    * Recuperação simples da numeração da blueprint selecionada.
    *@return posição dentre as outras blueprints selecionadas.
    */
    public int GetIndexMapa(){
        return indexBlueprintSelecionada;
    }

    /**
    * Gera um prompt genérico para ser mostrado na tela, de modo que as outras funções possam fazer uso desta para apenas a 
    * geração da tela, enquanto a lógica de validação de entrada é deixada para o corpo da função de solicitação.
    *@param mensagem string a ser mostrada para o usuário
    *@param dicaDeEntrada string a ser mostrada no campo de texto da entrada do prompt de modo a orientar o usuário sobre o tipo de entrada esperada.
    *@return Estágio principal de exibição do prompt.
    */
    private Stage Prompt(String mensagem, String dicaDeEntrada) {

        Stage prompt = new Stage();

        prompt.setTitle("Menu");

        VBox caixaVertical = new VBox();

        // Texto de prompt
        Label mensagemPrompt = new Label(mensagem);

        // Caixa de entrada
        TextField entrada = new TextField();
        entrada.setPromptText(dicaDeEntrada);
        entrada.setFocusTraversable(false);
        entrada.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                inputUser = entrada.getText();
                prompt.fireEvent(
                        new WindowEvent(
                                prompt,
                                WindowEvent.WINDOW_CLOSE_REQUEST
                        )
                );
            }

        });

        caixaVertical.getChildren().addAll(mensagemPrompt, entrada);

        Scene cenaPrompt = new Scene(caixaVertical);
        prompt.setScene(cenaPrompt);
        prompt.setResizable(false);

        return prompt;

    }
}
