import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

import javax.lang.model.util.ElementScanner6;
import javax.swing.Renderer;
import javax.xml.stream.events.StartDocument;

public class StuckWinSTD {
  ///DEBUT_STD_DRAW

  void init()
  {
    StdDraw.setCanvasSize(1000,1000);
    StdDraw.setScale(-SIZE/2,SIZE/2);

    
  }

// Dessine des hexagones
void hexagone()
{
  StdDraw.setPenColor(StdDraw.BLACK);
  StdDraw.setPenRadius(0.005);
  
}




  ///FIN_STD_DRAW
  static final Scanner input = new Scanner(System.in);
  private static final double BOARD_SIZE = 7;

  enum Result {
    OK, BAD_COLOR, DEST_NOT_FREE, EMPTY_SRC, TOO_FAR, EXT_BOARD, EXIT
  }

  enum ModeMvt {
    REAL, SIMU
  }

  final char[] joueurs = { 'B', 'R' };
  final int SIZE = 8;
  final char VIDE = '.';
  // 'B'=bleu 'R'=rouge '.'=vide '-'=n'existe pas
  char[][] state = {
      { '-', '-', '-', '-', 'R', 'R', 'R', 'R' },
      { '-', '-', '-', '.', 'R', 'R', 'R', 'R' },
      { '-', '-', '.', '.', '.', 'R', 'R', 'R' },
      { '-', 'B', 'B', '.', '.', '.', 'R', 'R' },
      { '-', 'B', 'B', 'B', '.', '.', '.', '-' },
      { '-', 'B', 'B', 'B', 'B', '.', '-', '-' },
      { '-', 'B', 'B', 'B', 'B', '-', '-', '-' },
  };

  String[][] tableau = {
      { "", "", "", "07", "", "", "" },
      { "", "", "06", "", "17", "", "" },
      { "", "05", "", "16", "", "27", "" },
      { "04", "", "15", "", "26", "", "37" },
      { "", "14", "", "25", "", "36", "" },
      { "13", "", "24", "", "35", "", "46" },
      { "", "23", "", "34", "", "45", "" },
      { "22", "", "33", "", "44", "", "55" },
      { "", "32", "", "43", "", "54", "" },
      { "31", "", "42", "", "53", "", "64" },
      { "", "41", "", "52", "", "63", "" },
      { "", "", "51", "", "62", "", "" },
      { "", "", "", "61", "", "", "" }
  };

  String[] pointR = new String[13];
  String[] pointB = new String[13];

  Random random = new Random();

  /**
   * permet de recuperer un string pour renvoyer un string en mode Lettre Chiffre
   * 
   * @param id
   * @return
   */
  String getIdToLettre(String id) {
    //Renvoie 2 nouvelle chaîne (tmp et tmp2) contenant le caractère (ou, plus précisément, le point de code UTF-16) à la position indiquée en argument.
    int tmp = id.charAt(0);
    int tmp2 = id.charAt(1);
    if (tmp < 65) {
      tmp = tmp + 17;
    }
    // retourne la valeur de la lettre pour tmp et tmp2
    return "" + (char) tmp + (char) tmp2;
  }

  /**
   * ajoute les points de départ des pions
   * 
   * @param couleur
   * @param point
   * @return
   */
  void addPosCouleur(String[] point, char couleur) {

    int j = 0;
    // parcours le tableau
    for (int i = 0; i < state.length; i++) {
      for (int k = 0; k < state[i].length; k++) {
        // si la couleur correspond, on ajoute la position dans le tableau
        if (state[i][k] == couleur) {

          point[j] = "" + i + k;
          j++;
        }
      }
    }

  }
  

  /**
   * permet de modifié les tableaux de point rouge ou bleu
   * si la source correspond, cela modifiera la destination dans dans la liste
   * 
   * @param tab
   * @param source
   * @param dest
   */

  void changePointTab(String[] point, String source, String dest) {
    // parcours le tableau
    for (int i = 0; i < point.length; i++) {
      // si la source correspond, on modifie la destination
      if (point[i].equals(source)) {
        point[i] = dest;
        break;
      }
    }
  }

  /**
   * Retour Faux si la position est hors du plateau
   * 
   * @param pos
   * @return
   */
  boolean verifTaille(int[] pos) {

    return (pos[0] >= 0 && pos[0] < 7 && pos[1] >= 0 && pos[1] < 8);

  }

  /**
   * Traducteur de l'idLettre, entre String valeur forme "E2" sortie String "42"
   * 
   * @param valeur
   * @return
   */
  String tradIdLettre(String valeur) {
    String retour;
    // si la valeur = 2 charactere
    char tmp = valeur.charAt(0);
    char tmp1 = valeur.charAt(1);
    tmp = Character.toUpperCase(tmp);
    String valideL = "ABCDEFG";
    // si la valeur est dans le tableau valideL
    if (valideL.contains(String.valueOf(tmp))) {
      int val = tmp;
      tmp = (char) (val - 17);
    }
    // retourne la valeur de tmp et tmp1
    retour = "" + tmp + tmp1;
    return retour;

  }

  /**
   * Déplace un pion ou simule son déplacement
   * 
   * @param couleur  couleur du pion à déplacer
   * @param lcSource case source Lc
   * @param lcDest   case destination Lc
   * @param mode     ModeMVT.REAL/SIMU selon qu'on réalise effectivement le
   *                 déplacement ou qu'on le simule seulement.
   * @return enum {OK, BAD_COLOR, DEST_NOT_FREE, EMPTY_SRC, TOO_FAR, EXT_BOARD,
   *         EXIT} selon le déplacement
   */
  Result deplace(char couleur, String lcSource, String lcDest, ModeMvt mode) {
    int[] source = recupereid(lcSource);
    int[] destination = recupereid(lcDest);
    // si la source ou la destination est hors du plateau
    if (!verifTaille(source) || !verifTaille(destination))
      return Result.EXT_BOARD;
    // si la destination est -
    if (state[destination[0]][destination[1]] == '-')
      return Result.EXT_BOARD;
    // si la source est vide
    if (state[source[0]][source[1]] == VIDE)
      return Result.EMPTY_SRC;
    // si la couleur ne correspond pas
    if (state[source[0]][source[1]] != couleur)
      return Result.BAD_COLOR;
    // si la destination n'est pas vide
    if (state[destination[0]][destination[1]] != VIDE) {
      return Result.DEST_NOT_FREE;
    }
    // si la distance est supérieur à 1
    if (Math.abs(source[0] - destination[0]) > 1 || Math.abs(source[1] - destination[1]) > 1) {
      return Result.TOO_FAR;
    }
    if (mode == ModeMvt.REAL) {
      state[source[0]][source[1]] = VIDE;
      state[destination[0]][destination[1]] = couleur;
      if (couleur == 'R') {
        changePointTab(pointR, lcSource, lcDest);

      } else {
        changePointTab(pointB, lcSource, lcDest);

      }

    }
    return Result.OK;
  }

  /**
   * Construit les trois chaînes représentant les positions accessibles
   * à partir de la position de départ [idLettre][idCol].
   * 
   * @param couleur  couleur du pion à jouer
   * @param idLettre id de la ligne du pion à jouer
   * @param idCol    id de la colonne du pion à jouer
   * @return tableau des trois positions jouables par le pion (redondance possible
   *         sur les bords)
   */
  String[] possibleDests(char couleur, int idLettre, int idCol) {
    // si la couleur est rouge
    if (couleur == 'R') {
      // retourne les 3 positions
      return new String[] { "" + idLettre + (idCol - 1),
          "" + (idLettre + 1) + (idCol),
          "" + (idLettre + 1) + (idCol - 1) };

    // si la couleur est bleu
    } else if (couleur == 'B') {
      // retourne les 3 positions
      return new String[] { "" + idLettre + (idCol + 1),
          "" + (idLettre - 1) + (idCol),
          "" + (idLettre - 1) + (idCol + 1) };

    } else {
      // sinon retourne false
      return new String[] { "False" };
    }

  }

  /**
   * Affiche le plateau de jeu dans la configuration portée par
   * l'attribut d'état "state"
   */
  void affiche() {

    int lettre;
    int chiffre;
    char tmp;
    // pour chaque ligne
    for (int i = 0; i < tableau.length; i++) {
      // pour chaque colonne
      for (int j = 0; j < tableau[i].length; j++) {
        // si la case est vide
        if (tableau[i][j].equals("")) {
          System.out.print("  ");
        } 
        else {
          tmp = tableau[i][j].charAt(0);
          lettre = Character.getNumericValue(tmp);
          tmp = tableau[i][j].charAt(1);
          chiffre = Character.getNumericValue(tmp);
          // si la case est rouge
          if (state[lettre][chiffre] == 'R') {
            // affiche la case en rouge 
            System.out.print(ConsoleColors.RED_BACKGROUND + (char) (65 + lettre) + chiffre + ConsoleColors.RESET);

          } 
          // si la case est bleu
          else if (state[lettre][chiffre] == 'B') {
            // affiche la case en bleu
            System.out.print(ConsoleColors.BLUE_BACKGROUND + (char) (65 + lettre) + chiffre + ConsoleColors.RESET);

          } 
          // si la case est vide ou incorect
          else {
            // affiche la case en noir
            System.out.print(ConsoleColors.BLACK + ConsoleColors.WHITE_BACKGROUND + (char) (65 + lettre) + chiffre
                + ConsoleColors.RESET);

          }

        }

      }
      System.out.println("");
    }

  }

  /**
   * Fonction qui permet de remplacer la fonction joueIA en mode humain
   * 
   * @return
   */
  String[] jouerIAHumain() {

    String src = input.next();
    String dst = input.next();
    return new String[] { src, dst };
  }

  /**
   * retourne un tableau de String contenant les id possibles pour une position
   *
   * @param src
   * @return int
   */

  int[] recupereid(String src) {
    int[] id = new int[2];
    // Convertit le caractère Unicode numérique à la position spécifiée dans une chaîne spécifiée en un nombre à virgule flottante double précision.
    id[0] = Character.getNumericValue(src.charAt(0));
    id[1] = Character.getNumericValue(src.charAt(1));
    return id;
  }

  /**
   * Joue un tour aleatoire grace a pointR et pointB avec un rand
   * et retourne un tableau de deux String contenant la position de depart et la
   * position d'arrivee
   * 
   * @param couleur couleur du pion à jouer
   * @return tableau contenant la position de départ et la destination du pion à
   *         jouer.
   */
  String[] jouerIA(char couleur) {
    String src = "";
    String dst = "";

    do {
      // si la couleur est rouge
      if (couleur == 'R') {
        // genere un nombre aleatoire entre 0 et la taille du tableau pointR
        int rand = random.nextInt(pointR.length);
        src = pointR[rand];
        int[] source = { Character.getNumericValue(src.charAt(0)), Character.getNumericValue(src.charAt(1)) };

        // genere un nombre aleatoire entre 0 et la taille du tableau posPossible
        String[] posPossible = possibleDests(couleur, source[0], source[1]);
        int rand2 = random.nextInt(posPossible.length);
        dst = posPossible[rand2];
      // si la couleur est bleu
      } else if (couleur == 'B') {
        // genere un nombre aleatoire entre 0 et la taille du tableau pointB
        int rand = random.nextInt(pointB.length);
      
        src = pointB[rand];
        int[] source = { Character.getNumericValue(src.charAt(0)), Character.getNumericValue(src.charAt(1)) };
        // genere un nombre aleatoire entre 0 et la taille du tableau posPossible
        String[] posPossible = possibleDests(couleur, source[0], source[1]);
        int rand2 = random.nextInt(posPossible.length);
        dst = posPossible[rand2];
      }
    }
    // tant que le deplacement n'est pas possible  
    while (deplace(couleur, src, dst, ModeMvt.SIMU) != Result.OK);
    return new String[] { src, dst };

  }

  /**
   * gère le jeu en fonction du joueur/couleur
   * 
   * @param couleur
   * @return tableau de deux chaînes {source,destination} du pion à jouer
   */
  String[] jouer(char couleur) {
    String src = "";
    String dst = "";
    String[] mvtIa;
    switch (couleur) {
      case 'B':
        System.out.println("Mouvement " + couleur);
        // src = input.next();
        // dst = input.next();
        mvtIa = jouerIA(couleur);
        src = mvtIa[0];
        dst = mvtIa[1];
        System.out.println(getIdToLettre(src) + "->" + getIdToLettre(dst));
        break;
      case 'R':
        System.out.println("Mouvement " + couleur);
        mvtIa = jouerIA(couleur);
        src = mvtIa[0];
        dst = mvtIa[1];
        getIdToLettre(src);
        getIdToLettre(dst);
        System.out.println(getIdToLettre(src) + "->" + getIdToLettre(dst));
        break;
      default:
        break;
    }
    return new String[] { src, dst };
  }

  /**
   * Fonction qui verifie la liste de points de rouge ou bleu
   * si un des points a la possibilite de se déplacer alors il renvoie 'N'
   * Sinon il renvoie la couleur pour dire qui gagnera
   * 
   * (fonction utilisé dans la fonction finPartie)
   * 
   * @param tab
   * @param couleur
   * @return
   */
  char verifPointTab(String[] tab, char couleur) {
    // pour chaque point de la liste
    for (int i = 0; i < tab.length; i++) {
      // on recupere les coordonnees du point
      int[] id = recupereid(tab[i]);
      String[] possDest = possibleDests(couleur, id[0], id[1]);
      // pour chaque destination possible
      for (int j = 0; j < possDest.length; j++) {
        // si le deplacement est possible
        if (deplace(couleur, tab[i], possDest[j], ModeMvt.SIMU) == Result.OK) {

          return 'N';
        }
      }
    }
    return couleur;
  }

  /**
   * retourne 'R' ou 'B' si vainqueur, 'N' si partie pas finie
   * 
   * @param couleur
   * @return
   */
  char finPartie(char couleur) {
    // si la couleur est rouge
    if (couleur == 'R') {
      return verifPointTab(pointR, couleur);
    } 
    // si la couleur est bleu
    else {
      return verifPointTab(pointB, couleur);
    }
  }

  public static void main(String[] args) {

    StuckWinSTD jeu = new StuckWinSTD();
    jeu.addPosCouleur(jeu.pointR, 'R');
    jeu.addPosCouleur(jeu.pointB, 'B');
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

      do {
        status = Result.EXIT;
        reponse = jeu.jouer(curCouleur);

        src = reponse[0];
        dest = reponse[1];
        if ("q".equals(src))
          return;
        src = jeu.tradIdLettre(src);
        dest = jeu.tradIdLettre(dest);
        status = jeu.deplace(curCouleur, src, dest, ModeMvt.REAL);

        partie = jeu.finPartie(nextCouleur);

        System.out.println("status : " + status + " partie : " + partie);
      } while (status != Result.OK && partie == 'N');

      tmp = curCouleur;
      curCouleur = nextCouleur;
      nextCouleur = tmp;
      cpt++;
    } while (partie == 'N'); // TODO affiche vainqueur
    jeu.affiche();
    System.out.println("Victoire : " + partie + " (" + (cpt / 2) + " coups)");
  }

}
