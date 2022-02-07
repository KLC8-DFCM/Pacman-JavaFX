package pacman.Sistema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* Esta classe é a responsável pela lógica interna de quaisquer percursos que devem ser seguidos pelos fantasmas.
* 
* O funcionamento essencialmente é dado pela definição de uma coordenada alvo, que é então tomada pelo algorítmo
* A estrela, que utiliza-a junto da heurística da distância Manhattan (e do mapa do jogo) para definir um conjunto
* de nós que deve ser seguido que minimiza o comprimento do trajeto.
*
* @see pacman.Sistema.Fantasma
* @see pacman.Engine.Jogo
* @see pacman.Engine.Controlador
* 
* @author diego-fleury
* @version v1.0
* @since Alpha v0.1
*/
public class A_Star{ 

    private int X_target, Y_target;
    private int IDNotarget;
    private ArrayList<Integer> SequenciaDeNosEncontrada = new ArrayList<>();
    private static final int INFINITO = 1000000;

    /**
     * Na inicializacao sao apenas passados valores de busca iniciais, valores X e Y sao usados para
     * calculo do custo heuristico do algoritmo A*, alvo do algoritmo em si é o nó representado por IDNotarget,
     * sendo este o ID do nó para o qual o pacman esta se direcionando.
     * @param X_targetInicial Coordenada X do alvo buscado inicialmente
     * @param Y_targetInicial Coordenada Y do alvo buscado inicialmente
     * @param IDNotargetInicial ID do No do alvo buscado inicialmente
     */
    public A_Star(int X_targetInicial,int Y_targetInicial,int IDNotargetInicial){
        X_target = X_targetInicial;
        Y_target = Y_targetInicial;
        IDNotarget = IDNotargetInicial;
    }
    
    /**
     * Funcao utilizada para depuracao do algoritmo.
     */
    public void PrintSequencia(){
      
      System.out.print("Fim");
      for(Integer I : SequenciaDeNosEncontrada){
        System.out.print("<-" + I.toString());
      }

      System.out.println("\n");
      
    }

    /**
     * Passagem de parametros de busca para o algoritmo.
     * @param pX_target Coordenada X do alvo buscado 
     * @param pY_target Coordenada Y do alvo buscado 
     * @param pIDNotarget ID do No do alvo buscado 
     */
    public void SetTarget(int pX_target,int pY_target,int pIDNotarget){
        X_target = pX_target; 
        Y_target = pY_target;
        IDNotarget = pIDNotarget; 
    }

    /**
     * Calculo da heuristica de distancia utilizada em A*, optou-se pelo uso da distancia manhattan, 
     * por conta da natureza estritamente vertical ou horizontal dos caminhos do mapa.
     * @param NoAtual No pelo qual deseja-se calcular a heuristica de distancia sobre
     */
    private int DistanciaManhattan(NoGrafo NoAtual){
        return (Math.abs(X_target - NoAtual.GetX()) + Math.abs(Y_target - NoAtual.GetY()));
    }

    /**
    * Implementação do algorítmo de busca A*, a sequencia de nós final é armazenada em uma lista para depuração
    * e exposição no terminal.
    * @param NoInicial Como só faz sentido chamar esta funcao quando o fantasma esta em um No, temos de passa-lo
    * @param mapaJogo Utilizada para interface com mapa, de modo a ter acesso a caminhos novos
    * @return ID do proximo no que o fantasma deve seguir. Caso ocorra algum erro, -1 é retornado
    */
    public int GetNextDirecaoAStar(NoGrafo NoInicial, Mapa mapaJogo){
        
        if(NoInicial.GetID() == IDNotarget)
            return 0;

        ArrayList<Integer> NosAbertos = new ArrayList<>();
        
        Map<Integer,Integer> IDAnterior = new HashMap<>();
        Map<Integer,Integer> G = new HashMap<>();
        Map<Integer,Integer> H = new HashMap<>();
        Map<Integer,Integer> F = new HashMap<>();

        int IDinicial  = NoInicial.GetID();
        NosAbertos.add(IDinicial);

        IDAnterior.put(IDinicial,IDinicial);
        G.put(IDinicial,0);
        H.put(IDinicial,DistanciaManhattan(NoInicial));
        F.put(IDinicial, G.get(IDinicial) + H.get(IDinicial) );

        while(!NosAbertos.isEmpty()){
            
            int indexMenorValor = 0;
            for( int i = 0; i< NosAbertos.size(); i++){
                indexMenorValor = ( F.get(NosAbertos.get(i)) < F.get(NosAbertos.get(indexMenorValor)) ) ? i : indexMenorValor; 
            }

            NoGrafo NoAtual = mapaJogo.GetNoGrafo(NosAbertos.get(indexMenorValor));          

            if( NosAbertos.get(indexMenorValor) == IDNotarget ){

                SequenciaDeNosEncontrada = new ArrayList<>();

                int index = IDNotarget;
                while(IDAnterior.get(index)!= index){
                    SequenciaDeNosEncontrada.add(index);
                    index = IDAnterior.get(index);
                }
                
                SequenciaDeNosEncontrada.add(NoInicial.GetID()); 
                
                int IDNoVizinho = SequenciaDeNosEncontrada.get(SequenciaDeNosEncontrada.size() - 2);

                int direcao = -1;
                for(int i =0;i<4;i++){
                    if(NoInicial.GetVizinhos()[i] == IDNoVizinho)
                    direcao = i;
                }

                if(direcao == -1)
                    System.out.println("ERRO, AStar falhou em encontrar valor correto");

                return direcao;
            }

            NosAbertos.remove(Integer.valueOf(NoAtual.GetID()));

            // adicionar todos os Nos que estao adjascentes ao No atual (escolhido) na lista de abertos (se ja nao estao la)
            // calcular valor G de cada no processo (distancia ao original INICIAL)

            for(int i = 0; i< 4; i++){
                int IDSucessor = NoAtual.GetVizinhos()[i];

                if(!G.containsKey(IDSucessor)){
                  G.put(IDSucessor,INFINITO);
                }

                if( IDSucessor!= -1){
                    int custo_atual_sucessor = G.get(NoAtual.GetID()) + (mapaJogo.GetProxCaminho(NoAtual,i).GetTamanho() - 1);
                    
                    if(custo_atual_sucessor < G.get(IDSucessor)){
                       IDAnterior.put(IDSucessor, NoAtual.GetID());
                       G.put(IDSucessor,custo_atual_sucessor);
                       F.put(IDSucessor, custo_atual_sucessor + DistanciaManhattan(mapaJogo.GetNoGrafo(IDSucessor)));
                       if(!NosAbertos.contains(IDSucessor))
                       NosAbertos.add(IDSucessor);
                    }

                }
            }
        }

        // se chegou aqui ERRO: NosAbertos esta vazia !
       System.out.println("Erro, lista vazia nos");
       return -1;
    }
}