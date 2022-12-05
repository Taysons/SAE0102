import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.lang.model.util.ElementScanner6;

public class StuckWin {
    static final Scanner input = new Scanner(System.in);
    private static final double BOARD_SIZE = 7;

    enum Result {OK, BAD_COLOR, DEST_NOT_FREE, EMPTY_SRC, TOO_FAR, EXT_BOARD, EXIT}
    enum ModeMvt {REAL, SIMU}
    final char[] joueurs = {'B', 'R'};
    final int SIZE = 8;
    final char VIDE = '.';
    // 'B'=bleu 'R'=rouge '.'=vide '-'=n'existe pas
    char[][] state = {
            {'-', '-', '-', '-', 'R', 'R', 'R', 'R'},
            {'-', '-', '-', '.', 'R', 'R', 'R', 'R'},
            {'-', '-', '.', '.', '.', 'R', 'R', 'R'},
            {'-', 'B', 'B', '.', '.', '.', 'R', 'B'},
            {'-', 'B', 'B', 'B', '.', '.', 'B', '-'},
            {'-', 'B', 'B', 'B', 'B', '.', '-', '-'},
            {'-', 'B', '.', 'R', 'B', '-', '-', '-'},
    };

    String [][] Tableau = {
      {"","","","07","","",""},
      {"","","06","","17","",""},
      {"","05","","16","","27",""},
      {"04","","15","","26","","37"},
      {"","14","","25","","36",""},
      {"13","","24","","35","","46"},
      {"","23","","34","","45",""},
      {"22","","33","","44","","55"},
      {"","32","","43","","54",""},
      {"31","","42","","53","","64"},
      {"","41","","52","","63",""},
      {"","","51","","62","",""},
      {"","","","61","","",""}
    };
    /**
     * Déplace un pion ou simule son déplacement
     * @param couleur couleur du pion à déplacer
     * @param lcSource case source Lc
     * @param lcDest case destination Lc
     * @param mode ModeMVT.REAL/SIMU selon qu'on réalise effectivement le déplacement ou qu'on le simule seulement.
     * @return enum {OK, BAD_COLOR, DEST_NOT_FREE, EMPTY_SRC, TOO_FAR, EXT_BOARD, EXIT} selon le déplacement
     */
    Result deplace(char couleur, String lcSource, String lcDest,  ModeMvt mode) {
      // votre code ici. Supprimer la ligne ci-dessous.
      throw new java.lang.UnsupportedOperationException("à compléter");
    }



    /**
     * Construit les trois chaînes représentant les positions accessibles
     * à partir de la position de départ [idLettre][idCol].
     * @param couleur couleur du pion à jouer
     * @param idLettre id de la ligne du pion à jouer
     * @param idCol id de la colonne du pion à jouer
     * @return tableau des trois positions jouables par le pion (redondance possible sur les bords)
     */
    String[] possibleDests(char couleur, int idLettre, int idCol){
      
      if (couleur == 'R')
      {
        String [] posPossible  = { "" + idLettre + (idCol-1) ,"" + (idLettre+1) + (idCol), "" + (idLettre+1) + (idCol-1) };
      
        for (int i =0;i<posPossible.length;i++)
        {
          char tmp = posPossible[i].charAt(0);
          int lettre = Character.getNumericValue(tmp);
          tmp = posPossible[i].charAt(1);
          int chiffre = Character.getNumericValue(tmp);


          if ((lettre>=0 && lettre<7) && (chiffre>=0 && chiffre<8))
          {
            if( state[lettre][chiffre] != '.' )
            {
                posPossible[i] = "-";
            }
          }
          else
          {
              posPossible[i] = "Out";
          }
            
          

        }
      return posPossible;
      
      }
      else if(couleur == 'B')
      {
        String [] posPossible  = { "" + idLettre + (idCol+1) ,"" + (idLettre-1) + (idCol), "" + (idLettre-1) + (idCol-1) };
      
        for (int i =0;i<posPossible.length;i++)
        {
          char tmp = posPossible[i].charAt(0);
          int lettre = Character.getNumericValue(tmp);
          tmp = posPossible[i].charAt(1);
          int chiffre = Character.getNumericValue(tmp);

          
          if ((lettre>=0 && lettre<7) && (chiffre>=0 && chiffre<8))
          {
            if( state[lettre][chiffre] != '.' )
            {
                posPossible[i] = "-";
            }
          }
          else
          {
              posPossible[i] = "Out";
          }

        }
        return posPossible;
      
      }
      else
      {
        String posPossible [] = {"False"};
        return posPossible;
      }


    }

/**
 * Traducteur de l'idLettre, entre String valeur forme "E2" sortie String "42"
 * @param valeur
 * @return
 */
    String TradIdLettre(String  valeur )
    {
      char tmp = valeur.charAt(0);
      char tmp1 = valeur.charAt(1);
      tmp = Character.toUpperCase(tmp);
      
      String valide = "ABCDEFG";
      if (valide.contains(String.valueOf(tmp))) {
        valeur = "Out";
        return valeur;
      }



      int val = tmp;
      val = val-65;
      tmp =(char)val;
      return ""+tmp+tmp1;
      
    }
    



    /**
     * Affiche le plateau de jeu dans la configuration portée par
     * l'attribut d'état "state"
     */
    void affiche() {

     

      int lettre;
      int chiffre;
      char tmp;
      for (int i = 0; i < Tableau.length;i++)
      {
        for (int j = 0; j < Tableau[i].length; j++) {
          
          if (Tableau[i][j] == "")
          {
            System.out.print("  ");
          }
          else
          {
            tmp = Tableau[i][j].charAt(0);
            lettre = Character.getNumericValue(tmp);
            tmp = Tableau[i][j].charAt(1);
            chiffre = Character.getNumericValue(tmp);
            if(state[lettre][chiffre] == 'R')
            {

              System.out.print(ConsoleColors.RED_BACKGROUND + (char)(65+lettre) + chiffre + ConsoleColors.RESET);

            }
            else if(state[lettre][chiffre] == 'B')
            {

              System.out.print(ConsoleColors.BLUE_BACKGROUND + (char)(65+lettre) + chiffre + ConsoleColors.RESET);

            }
            else 
            {

              System.out.print(ConsoleColors.BLACK+ConsoleColors.WHITE_BACKGROUND + (char)(65+lettre) + chiffre + ConsoleColors.RESET);

            }
            

          }
          

        }
        System.out.println("");
      }




      




    }

    /**
     * Joue un tour
     * @param couleur couleur du pion à jouer
     * @return tableau contenant la position de départ et la destination du pion à jouer.
     */
    String[] jouerIA(char couleur) {
      // votre code ici. Supprimer la ligne ci-dessous.
      throw new java.lang.UnsupportedOperationException("à compléter");
    }

    /**
     * gère le jeu en fonction du joueur/couleur
     * @param couleur
     * @return tableau de deux chaînes {source,destination} du pion à jouer
     */
    String[] jouer(char couleur){
        String src = "";
        String dst = "";
        String[] mvtIa;
        switch(couleur) {
            case 'B':
                System.out.println("Mouvement " + couleur);
                src = input.next();
                dst = input.next();
                System.out.println(src + "->" + dst);

                //Test TradIdLettre
                src = TradIdLettre(src);
                dst = TradIdLettre(dst);
                System.out.println(src + "->" + dst);
                //Fin de Test

                break;
            case 'R':
                System.out.println("Mouvement " + couleur);
                mvtIa = jouerIA(couleur);
                src = mvtIa[0];
                dst = mvtIa[1];
                System.out.println(src + "->" + dst);
                break;
        }
        return new String[]{src, dst};
    }

    /**
     * retourne 'R' ou 'B' si vainqueur, 'N' si partie pas finie
     * @param couleur
     * @return
     */
    char finPartie(char couleur){
      // votre code ici. Supprimer la ligne ci-dessous.
      throw new java.lang.UnsupportedOperationException("à compléter");
    }


    public static void main(String[] args) {
        StuckWin jeu = new StuckWin();
        String src = "";
        String dest;
        String[] reponse;
        Result status;
        char partie = 'N';
        char curCouleur = jeu.joueurs[0];
        char nextCouleur = jeu.joueurs[1];
        char tmp;
        int cpt = 0;

        // version console
        do {
              // séquence pour Bleu ou rouge
              jeu.affiche();


              //Test Pour Possible dest
              String [] pos  = jeu.possibleDests('R',6,3);
              for (int i = 0;i<pos.length;i++)
              {
                System.out.println(pos[i]);

              }






              do {
                  status = Result.EXIT;
                  reponse = jeu.jouer(curCouleur);
                  
                  src = reponse[0];
                  dest = reponse[1];
                  if("q".equals(src))
                      return;
                  status = jeu.deplace(curCouleur, src, dest, ModeMvt.REAL);
                  partie = jeu.finPartie(nextCouleur);
                  System.out.println("status : "+status + " partie : " + partie);
              } while(status != Result.OK && partie=='N');
              tmp = curCouleur;
              curCouleur = nextCouleur;
              nextCouleur = tmp;
              cpt ++;
        } while(partie =='N'); // TODO affiche vainqueur
        System.out.printf("Victoire : " + partie + " (" + (cpt/2) + " coups)");
    }
}
