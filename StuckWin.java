import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

import javax.lang.model.util.ElementScanner6;
import javax.swing.Renderer;

public class StuckWin {

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
          {'-', '-', '-', '-', 'R', 'R', 'R', 'R'},
          {'-', '-', '-', '.', 'R', 'R', 'R', 'R'},
          {'-', '-', '.', '.', '.', 'R', 'R', 'R'},
          {'-', 'B', 'B', '.', '.', '.', 'R', 'R'},
          {'-', 'B', 'B', 'B', '.', '.', '.', '-'},
          {'-', 'B', 'B', 'B', 'B', '.', '-', '-'},
          {'-', 'B', 'B', 'B', 'B', '-', '-', '-'},
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
   * ajoute les points de départ des pions
   * 
   * @param couleur
   * @return
   */
  void addPosCouleur(char couleur) {

    int j = 0;
    if (couleur == 'R') {

      for (int i = 0; i < state.length; i++) {
        for (int k = 0; k < state[i].length; k++) {
          if (state[i][k] == couleur) {

            pointR[j] = "" + i + k;
            j++;
          }
        }
      }
    } else {

      for (int i = 0; i < state.length; i++) {
        for (int k = 0; k < state[i].length; k++) {
          if (state[i][k] == couleur) {

            pointB[j] = "" + i + k;
            j++;
          }
        }
      }

    }
  }

  /**
   *
   */
  String getIdToLettre(String id) {
    int tmp = id.charAt(0);
    int tmp2 = id.charAt(1);
    if (tmp < 65) {
      tmp = tmp + 17;
    }

    return "" + (char) tmp + (char) tmp2;
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
    // votre code ici. Supprimer la ligne ci-dessous.

    if (couleur == 'B' || couleur == 'R') {
      int[] source = recupereid(lcSource);
      int[] destination = recupereid(lcDest);

      String[] posDest = possibleDests(couleur, source[0], source[1]);
      Boolean erreurDest = false;
      Boolean erreurOut = false;

      if (!(verifTaille(source)) || !(verifTaille(destination))) {
        return Result.EXT_BOARD;
      }

      if (state[source[0]][source[1]] == couleur) {
        for (int i = 0; i < posDest.length; i++) {

          if (posDest[i].equals(lcDest)) {
            if (state[destination[0]][destination[1]] == VIDE) {

              if (mode == ModeMvt.REAL) {
                state[source[0]][source[1]] = VIDE;
                state[destination[0]][destination[1]] = couleur;
                if (couleur == 'R') {
                  for (int g = 0; g < pointR.length; g++) {
                    if (pointR[g].equals(lcSource)) {
                      pointR[g] = lcDest;
                      break;
                    }
                  }
                } else {
                  for (int g = 0; g < pointR.length; g++) {
                    if (pointB[g].equals(lcSource)) {
                      pointB[g] = lcDest;
                      break;
                    }
                  }
                }
              }
              return Result.OK;

            } else if (state[destination[0]][destination[1]] == 'B' || state[destination[0]][destination[1]] == 'R') {
              erreurDest = true;
            }

          } else if ((state[destination[0]][destination[1]] == '-')) {
            erreurOut = true;
          }

        }
      } else {
        return Result.EMPTY_SRC;
      }

      if (Boolean.TRUE.equals(erreurDest)) {
        return Result.DEST_NOT_FREE;
      } else if (Boolean.TRUE.equals(erreurOut)) {
        return Result.EXT_BOARD;
      } else {
        return Result.TOO_FAR;
      }

    } else {
      return Result.BAD_COLOR;

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

    if (couleur == 'R') {

      return new String[] { "" + idLettre + (idCol - 1),
          "" + (idLettre + 1) + (idCol),
          "" + (idLettre + 1) + (idCol - 1) };

    } else if (couleur == 'B') {

      return new String[] { "" + idLettre + (idCol + 1),
          "" + (idLettre - 1) + (idCol),
          "" + (idLettre - 1) + (idCol + 1) };

    } else {

      return new String[] { "False" };
    }

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

    if (valideL.contains(String.valueOf(tmp))) {

      int val = tmp;
      tmp = (char) (val - 17);
    }

    retour = "" + tmp + tmp1;
    return retour;

  }

  /**
   * Affiche le plateau de jeu dans la configuration portée par
   * l'attribut d'état "state"
   */
  void affiche() {

    int lettre;
    int chiffre;
    char tmp;
    for (int i = 0; i < tableau.length; i++) {
      for (int j = 0; j < tableau[i].length; j++) {

        if (tableau[i][j].equals("")) {
          System.out.print("  ");
        } else {
          tmp = tableau[i][j].charAt(0);
          lettre = Character.getNumericValue(tmp);
          tmp = tableau[i][j].charAt(1);
          chiffre = Character.getNumericValue(tmp);
          if (state[lettre][chiffre] == 'R') {

            System.out.print(ConsoleColors.RED_BACKGROUND + (char) (65 + lettre) + chiffre + ConsoleColors.RESET);

          } else if (state[lettre][chiffre] == 'B') {

            System.out.print(ConsoleColors.BLUE_BACKGROUND + (char) (65 + lettre) + chiffre + ConsoleColors.RESET);

          } else {

            System.out.print(ConsoleColors.BLACK + ConsoleColors.WHITE_BACKGROUND + (char) (65 + lettre) + chiffre
                + ConsoleColors.RESET);

          }

        }

      }
      System.out.println("");
    }

  }

  String[] jouerIAHumain() {

    String src = input.next();
    String dst = input.next();
    return new String[] { src, dst };
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
      if (couleur == 'R') {
        int rand = random.nextInt(pointR.length);
        src = pointR[rand];
        int[] source = { Character.getNumericValue(src.charAt(0)), Character.getNumericValue(src.charAt(1)) };

        String[] posPossible = possibleDests(couleur, source[0], source[1]);
        int rand2 = random.nextInt(posPossible.length);
        dst = posPossible[rand2];
      } else if (couleur == 'B') {
        int rand = random.nextInt(pointB.length);

        src = pointB[rand];
        int[] source = { Character.getNumericValue(src.charAt(0)), Character.getNumericValue(src.charAt(1)) };

        String[] posPossible = possibleDests(couleur, source[0], source[1]);
        int rand2 = random.nextInt(posPossible.length);
        dst = posPossible[rand2];
      }
    } while (deplace(couleur, src, dst, ModeMvt.SIMU) != Result.OK);
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
        src = input.next();
        dst = input.next();
        // mvtIa = jouerIA(couleur);
        // src = mvtIa[0];
        // dst = mvtIa[1];
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
   * retourne un tableau de String contenant les id possibles pour une position
   * 
   *
   * @param src
   * @return int
   */

  int[] recupereid(String src) {
    int[] id = new int[2];
    id[0] = Character.getNumericValue(src.charAt(0));
    id[1] = Character.getNumericValue(src.charAt(1));
    return id;
  }

  /**
   * retourne 'R' ou 'B' si vainqueur, 'N' si partie pas finie
   * 
   * @param couleur
   * @return
   */
  char finPartie(char couleur) {

    if (couleur == 'R') {
      for (int i = 0; i < pointR.length; i++) {
        int[] id = recupereid(pointR[i]);
        String[] possDest = possibleDests(couleur, id[0], id[1]);
        for (int j = 0; j < possDest.length; j++) {
          if (deplace(couleur, pointR[i], possDest[j], ModeMvt.SIMU) == Result.OK) {

            return 'N';
          }
        }
      }
      return 'R';
    } else {
      for (int i = 0; i < pointB.length; i++) {
        int[] id = recupereid(pointB[i]);
        String[] possDest = possibleDests(couleur, id[0], id[1]);
        for (int j = 0; j < possDest.length; j++) {
          if (deplace(couleur, pointB[i], possDest[j], ModeMvt.SIMU) == Result.OK) {
            return 'N';
          }
        }
      }
      return 'B';
    }
  }

  public static void main(String[] args) {

    StuckWin jeu = new StuckWin();
    jeu.addPosCouleur('R');
    jeu.addPosCouleur('B');
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
    System.out.printf("Victoire : " + partie + " (" + (cpt / 2) + " coups)");
  }

}