package pacman.Interface;

import pacman.Engine.Jogo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
* Função principal do programa que gera a thread primária de controle, herda características de Application para a integração
* correta com o restante do módulo javafx.
* 
* Além da implementação do módulo main, temos também a implementação (obrigatória) do método start de JavaFX, que é responsável 
* por iniciar a thread principal com a instanciação do jogo.
* 
* @see javafx.application.Application
* @see javafx.application.Platform
* 
* @author diego-fleury
* @version v1.0
* @since Alpha v0.1
*/
public class PacMan extends Application {
    
    /**
     * Funcao principal do programa
     * @param args padrao de aplicacoes javafx
     */
    public static void main(String[] args) {
      launch(args);        
    }

    /**
     * Método start gera o estágio principal da aplicação, sendo que este é o realmente invocado no início.
     * 
     * Realiza a instanciação do jogo, passa o estágio principal e o nome dos arquivos em que os mapas estão localizados.
     * @param primaryStage Estagio principal gerado pela aplicação JavaFX.
     */
    @Override
    public void start(Stage primaryStage){
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent t) {
            Platform.exit();
            System.exit(0);
        }
        });

      String[] mapas = {"ArquivosJogo/MapaConfig1.csv","ArquivosJogo/CaminhoConfig1.txt","ArquivosJogo/MapaConfig2.csv","ArquivosJogo/CaminhoConfig2.txt"};    
      
      String placar = "ArquivosJogo/PontuacaoPlayers.txt";
      
     Jogo Game = new Jogo(mapas,placar,primaryStage);
     
    }
}
