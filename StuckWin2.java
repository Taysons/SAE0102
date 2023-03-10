import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

import javax.lang.model.util.ElementScanner6;
import javax.swing.Renderer;

public class StuckWin2 {

  static final Scanner input = new Scanner(System.in);
  private static final double BOARD_SIZE = 7;

  enum Result {
    OK, BAD_COLOR, DEST_NOT_FREE, EMPTY_SRC, TOO_FAR, EXT_BOARD, EXIT
  }

  enum ModeMvt {
    REAL, SIMU, RETOUR, nbrPionJouable
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
  // char[][] state = {
  // { '-', '-', '-', '-', 'R', 'R', 'R', '.' },
  // { '-', '-', '-', '.', 'R', 'R', 'R', 'R' },
  // { '-', '-', '.', 'R', '.', 'R', 'R', 'R' },
  // { '-', 'B', 'B', 'B', 'B', '.', 'R', 'R' },
  // { '-', 'B', 'B', '.', '.', 'B', 'B', '-' },
  // { '-', '.', 'B', 'B', 'B', '.', '-', '-' },
  // { '-', '.', '.', 'B', 'B', '-', '-', '-' },
  // };

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

    if (id.length() < 2 || id.length() > 2) {
      return "Erreur id";
    }
    // Renvoie 2 nouveaux int (tmp et tmp2)
    // contenant les valeurs des 2 premiers caract??res de l'id
    int tmp = id.charAt(0);
    int tmp2 = id.charAt(1);
    // Si tmp est plus petit que 65 (caract??re A) alors il le transforme en lettre

    if (tmp < 65) {
      tmp = tmp + 17;
    }
    // retourne l'id en format lettre chiffre
    return "" + (char) tmp + (char) tmp2;
  }

  /**
   * ajoute les points de d??part des pions
   * 
   * @param couleur
   * @param point
   * @return
   */
  void addPosCouleur(String[] point, char couleur) {

    int j = 0;
    // parcours le tableau State pour trouver les pions de la couleur
    for (int i = 0; i < state.length; i++) {
      for (int k = 0; k < state[i].length; k++) {
        // si la couleur correspond, on ajoute la position
        // dans le tableau de point de la couleur correspondante
        if (state[i][k] == couleur) {

          point[j] = "" + i + k;
          j++;
        }
      }
    }

  }

  /**
   * permet de modifier les tableaux de point rouge ou bleu
   * si la source correspond, cela modifiera la destination dans la liste
   *
   * @param point
   * @param source
   * @param dest
   */

  void changePointTab(String[] point, String source, String dest) {
    // parcours le tableau
    for (int i = 0; i < point.length; i++) {
      // si la source correspond, on modifie la valeur par la destination
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

    return (pos[0] >= 0 && pos[0] < BOARD_SIZE && pos[1] >= 0 && pos[1] < SIZE);

  }

  /**
   * Traducteur de l'idLettre, entre String valeur forme "E2" sortie String "42"
   * 
   * @param valeur
   * @return
   */
  String tradIdLettre(String valeur) {
    String retour;

    if (valeur.length() < 2 || valeur.length() > 2) {
      return "00";
    }

    char tmp = valeur.charAt(0);
    char tmp1 = valeur.charAt(1);
    tmp = Character.toUpperCase(tmp);
    String valideL = "ABCDEFG";
    // si le premier charact??re est dans le tableau valideL
    // alors on retourne la valeur en int correspondante
    if (valideL.contains(String.valueOf(tmp))) {

      int val = tmp;
      tmp = (char) (val - 17);
    }

    retour = "" + tmp + tmp1;
    return retour;

  }

  /**
   * D??place un pion ou simule son d??placement
   * 
   * @param couleur  couleur du pion ?? d??placer
   * @param lcSource case source Lc
   * @param lcDest   case destination Lc
   * @param mode     ModeMVT.REAL/SIMU selon qu'on r??alise effectivement le
   *                 d??placement ou qu'on le simule seulement.
   * @return enum {OK, BAD_COLOR, DEST_NOT_FREE, EMPTY_SRC, TOO_FAR, EXT_BOARD,
   *         EXIT} selon le d??placement
   */
  Result deplace(char couleur, String lcSource, String lcDest, ModeMvt mode) {
    // votre code ici. Supprimer la ligne ci-dessous.
    int[] source = recupereid(lcSource);
    int[] destination = recupereid(lcDest);
    // si la source ou la destination est hors du plateau
    if (!verifTaille(source) || !verifTaille(destination))
      return Result.EXT_BOARD;
    // si la destination dans State est '-'
    if (state[destination[0]][destination[1]] == '-' || state[source[0]][source[1]] == '-')
      return Result.EXT_BOARD;
    // si la source dans State est vide
    if (state[source[0]][source[1]] == VIDE)
      return Result.EMPTY_SRC;
    // si la couleur dans State ne correspond pas
    if (state[source[0]][source[1]] != couleur)
      return Result.BAD_COLOR;
    // si la destination dans State n'est pas vide
    if (state[destination[0]][destination[1]] != VIDE) {
      return Result.DEST_NOT_FREE;
    }
    if (source == destination) {
      return Result.DEST_NOT_FREE;
    }
    // si la distance entre la Source et la destination dans State
    // est sup??rieure ?? 1

    if ((Math.abs(source[0] - destination[0]) > 1
        || Math.abs(source[1] - destination[1]) > 1)
        && mode != ModeMvt.RETOUR) {
      return Result.TOO_FAR;
    }
    // si le mode est r??el alors on modifie la position du pion
    if (mode != ModeMvt.SIMU) {
      state[source[0]][source[1]] = VIDE;
      state[destination[0]][destination[1]] = couleur;
      // et on modifie la position du pion dans le tableau
      // pointR ou pointB selon la couleur
      if (couleur == 'R') {
        changePointTab(pointR, lcSource, lcDest);

      } else {
        changePointTab(pointB, lcSource, lcDest);

      }

    }
    return Result.OK;
  }

  /**
   * Construit les trois cha??nes repr??sentant les positions accessibles
   * ?? partir de la position de d??part [idLettre][idCol].
   * 
   * @param couleur  couleur du pion ?? jouer
   * @param idLettre id de la ligne du pion ?? jouer
   * @param idCol    id de la colonne du pion ?? jouer
   * @return tableau des trois positions jouables par le pion (redondance possible
   *         sur les bords)
   */
  String[] possibleDests(char couleur, int idLettre, int idCol) {
    // si la couleur est rouge
    if (couleur == 'R') {
      // retourne les 3 positions possibles
      return new String[] { "" + idLettre + (idCol - 1),
          "" + (idLettre + 1) + (idCol),
          "" + (idLettre + 1) + (idCol - 1) };
      // si la couleur est bleue
    } else if (couleur == 'B') {
      // retourne les 3 positions possibles
      return new String[] { "" + idLettre + (idCol + 1),
          "" + (idLettre - 1) + (idCol),
          "" + (idLettre - 1) + (idCol + 1) };
      // sinon retourne false
    } else {

      return new String[] { "False" };
    }

  }

  /**
   * Affiche le plateau de jeu dans la configuration port??e par
   * l'attribut d'??tat "state"
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
          // si la valeur est rouge
          if (state[lettre][chiffre] == 'R') {
            // affiche la case en rouge
            System.out.print(ConsoleColors.RED_BACKGROUND +
                (char) (65 + lettre) + chiffre + ConsoleColors.RESET);
            // si la valeur est bleue
          } else if (state[lettre][chiffre] == 'B') {
            // affiche la case en bleu
            System.out.print(ConsoleColors.BLUE_BACKGROUND +
                (char) (65 + lettre) + chiffre + ConsoleColors.RESET);
            // si la case est "." (tous les autres cas en soi)
          } else {
            // affiche la case en white
            System.out.print(ConsoleColors.BLACK +
                ConsoleColors.WHITE_BACKGROUND + (char) (65 + lettre) + chiffre +
                ConsoleColors.RESET);

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
   * retourne un tableau de Int contenant les charact??res
   * de la position de depart
   *
   * 
   * @param src
   * @return int
   */

  int[] recupereid(String src) {

    int[] id = new int[2];
    // Convertit le caract??re Unicode num??rique ?? la
    // position sp??cifi??e dans une cha??ne sp??cifi??e en un nombre
    // ?? virgule flottante double pr??cision.
    if (src.length() < 2 || src.length() > 2) {
      id[0] = 0;
      id[1] = 0;
      return id;
    }
    id[0] = Character.getNumericValue(src.charAt(0));
    id[1] = Character.getNumericValue(src.charAt(1));
    return id;
  }

  /**
   * Joue un tour aleatoire grace ?? pointR et pointB avec un rand
   * et retourne un tableau de deux Strings contenant la position de depart
   * et la position d'arrivee
   * 
   * @param couleur couleur du pion ?? jouer
   * @return tableau contenant la position de d??part et la destination du pion ??
   *         jouer.
   */
  String[] jouerIAaleatoire(char couleur) {
    String src = "";
    String dst = "";

    do {
      // si la couleur est rouge
      if (couleur == 'R') {
        // genere un nombre aleatoire entre 0
        // et la taille du tableau pointR
        int rand = random.nextInt(pointR.length);
        src = pointR[rand];
        int[] source = { Character.getNumericValue(src.charAt(0)),
            Character.getNumericValue(src.charAt(1)) };

        String[] posPossible = possibleDests(couleur, source[0],
            source[1]);
        // genere un nombre aleatoire entre 0 et la taille du tableau posPossible
        int rand2 = random.nextInt(posPossible.length);
        dst = posPossible[rand2];

      } else if (couleur == 'B') {
        int rand = random.nextInt(pointB.length);

        src = pointB[rand];
        int[] source = { Character.getNumericValue(src.charAt(0)),
            Character.getNumericValue(src.charAt(1)) };

        String[] posPossible = possibleDests(couleur, source[0],
            source[1]);
        int rand2 = random.nextInt(posPossible.length);
        dst = posPossible[rand2];
      }
      // tant que le deplacement n'est pas possible
    } while (deplace(couleur, src, dst, ModeMvt.SIMU) != Result.OK);

    return new String[] { src, dst };

  }

  /**
   * Joue un tour aleatoire grace ?? pointR et pointB avec un rand
   * et retourne un tableau de deux Strings contenant la position de depart
   * et la position d'arrivee
   * 
   * @param couleur couleur du pion ?? jouer
   * @return tableau contenant la position de d??part et la destination du pion ??
   *         jouer.
   */
  String[] jouerIA(char couleur) {
    String src = "";
    String dst = "";
    ArrayList<Integer> nbrsJetonJouableC = new ArrayList<>();
    ArrayList<Integer> nbrsJetonJouableNonC = new ArrayList<>();
    ArrayList<String> infoPosition = new ArrayList<>();
    String[] point;
    String[] pointAdv;
    String[] TabPointCClone;
    String[] TabPointCAdvClone;
    char Ncouleur;

    if (couleur == 'R') {
      Ncouleur = 'B';
      point = pointR;
      pointAdv = pointB;
      TabPointCClone = point.clone();
      TabPointCAdvClone = pointAdv.clone();
    } else {
      Ncouleur = 'R';
      point = pointB;
      pointAdv = pointR;
      TabPointCClone = point.clone();
      TabPointCAdvClone = pointAdv.clone();
    }

    test(couleur, Ncouleur, point, pointAdv, TabPointCClone, TabPointCAdvClone, nbrsJetonJouableC, nbrsJetonJouableNonC,
        infoPosition, 3);
    System.out.println("partie 'for' fini");
    System.out.println("");
    System.out.println(" src  |  dst  |  PointC  |  PointAdv  |  Difference");

    for (int i = 0; i < infoPosition.size(); i++) {
      String src2 = infoPosition.get(i).charAt(0) + "" + infoPosition.get(i).charAt(1);
      String dst2 = infoPosition.get(i).charAt(2) + "" + infoPosition.get(i).charAt(3);
      System.out.print(getIdToLettre(src2) + " | " + getIdToLettre(dst2) + " | ");
      System.out.print(nbrsJetonJouableC.get(i) + " | ");
      System.out.print(nbrsJetonJouableNonC.get(i) + " | ");
      System.out.println((nbrsJetonJouableNonC.get(i)) - (nbrsJetonJouableC.get(i)));
    }

    triBulle(nbrsJetonJouableC, nbrsJetonJouableNonC, infoPosition);

    System.out.println("");
    System.out.println("TriBulle");
    for (int i = 0; i < infoPosition.size(); i++) {
      String src2 = infoPosition.get(i).charAt(0) + "" + infoPosition.get(i).charAt(1);
      String dst2 = infoPosition.get(i).charAt(2) + "" + infoPosition.get(i).charAt(3);
      System.out.print(getIdToLettre(src2) + " | " + getIdToLettre(dst2) + " | ");
      System.out.print(nbrsJetonJouableC.get(i) + " | ");
      System.out.print(nbrsJetonJouableNonC.get(i) + " | ");
      System.out.println((nbrsJetonJouableNonC.get(i)) - (nbrsJetonJouableC.get(i)));
    }

    triBulle2(nbrsJetonJouableC, nbrsJetonJouableNonC, infoPosition);

    System.out.println("");
    System.out.println("TriBulle2");
    for (int i = 0; i < infoPosition.size(); i++) {
      String src2 = infoPosition.get(i).charAt(0) + "" + infoPosition.get(i).charAt(1);
      String dst2 = infoPosition.get(i).charAt(2) + "" + infoPosition.get(i).charAt(3);
      System.out.print(getIdToLettre(src2) + " | " + getIdToLettre(dst2) + " | ");
      System.out.print(nbrsJetonJouableC.get(i) + " | ");
      System.out.print(nbrsJetonJouableNonC.get(i) + " | ");
      System.out.println((nbrsJetonJouableNonC.get(i)) - (nbrsJetonJouableC.get(i)));
    }

    int rand = random.nextInt(infoPosition.size());

    src = infoPosition.get(rand).charAt(0) + "" + infoPosition.get(rand).charAt(1);
    dst = infoPosition.get(rand).charAt(2) + "" + infoPosition.get(rand).charAt(3);
    return new String[] { src, dst };

  }

  /**
   * 
   * @param pointC
   * @param pointAdv
   * @param infoPosition
   */
  void triBulle(ArrayList<Integer> pointC,
      ArrayList<Integer> pointAdv,
      ArrayList<String> infoPosition) {
    int taille = infoPosition.size();
    int tmp;
    int limite;
    for (limite = -1; limite <= taille - 2; limite++) {

      for (int i = taille - 1; i > limite + 1; i--) {
        if (pointC.get(i) < pointC.get(i - 1)) {
          tmp = pointC.get(i);
          pointC.set(i, pointC.get(i - 1));
          pointC.set(i - 1, tmp);

          tmp = pointAdv.get(i);
          pointAdv.set(i, pointAdv.get(i - 1));
          pointAdv.set(i - 1, tmp);

          String tmp2 = infoPosition.get(i);
          infoPosition.set(i, infoPosition.get(i - 1));
          infoPosition.set(i - 1, tmp2);

        }
      }

    }

    int val = pointC.get(0);
    for (int i = taille - 1; i > 0; i--) {
      if (pointC.get(i) != val) {
        pointAdv.remove(i);
        pointC.remove(i);
        infoPosition.remove(i);
      }
    }

  }

  /**
   * 
   * @param pointC
   * @param pointAdv
   * @param infoPosition
   */
  void triBulle2(ArrayList<Integer> pointC,
      ArrayList<Integer> pointAdv,
      ArrayList<String> infoPosition) {
    int taille = infoPosition.size();
    int tmp;
    int limite;

    for (limite = -1; limite <= taille - 2; limite++) {

      for (int i = taille - 1; i > limite + 1; i--) {

        if ((pointC.get(i) - pointAdv.get(i)) < (pointC.get(i - 1) - pointAdv.get(i - 1))) {
          tmp = pointC.get(i);
          pointC.set(i, pointC.get(i - 1));
          pointC.set(i - 1, tmp);

          tmp = pointAdv.get(i);
          pointAdv.set(i, pointAdv.get(i - 1));
          pointAdv.set(i - 1, tmp);

          String tmp2 = infoPosition.get(i);
          infoPosition.set(i, infoPosition.get(i - 1));
          infoPosition.set(i - 1, tmp2);

        }
      }

    }

    int val = (pointC.get(0) - pointAdv.get(0));
    for (int i = taille - 1; i > 0; i--) {
      if ((pointC.get(i) - pointAdv.get(i)) != val) {
        pointAdv.remove(i);
        pointC.remove(i);
        infoPosition.remove(i);
      }
    }
  }

  /**
   * 
   * @param couleur
   * @param Ncouleur
   * @param point
   * @param pointAdv
   * @param TabPointCClone
   * @param TabPointCAdvClone
   * @param nbrsJetonJouableC
   * @param nbrsJetonJouableNonC
   * @param infoPosition
   */
  void test(char couleur,
      char Ncouleur,
      String[] point,
      String[] pointAdv,
      String[] TabPointCClone,
      String[] TabPointCAdvClone,
      ArrayList<Integer> nbrsJetonJouableC,
      ArrayList<Integer> nbrsJetonJouableNonC,
      ArrayList<String> infoPosition,
      int depth) {

    

    /// fonction a sortir pour recursivit??
    for (int i = 0; i < point.length; i++) {

      int[] id = recupereid(point[i]);

      String[] possible = possibleDests(couleur, id[0], id[1]);
      for (int j = 0; j < possible.length; j++) {

        if (deplace(couleur, point[i], possible[j], ModeMvt.REAL) == Result.OK) {

          
          nbrsJetonJouableC.add(Integer.valueOf(GetVerifPointTab(point, couleur, ModeMvt.RETOUR)));
          nbrsJetonJouableNonC.add(Integer.valueOf(GetVerifPointTab(pointAdv, Ncouleur, ModeMvt.RETOUR)));
          
          point = TabPointCClone.clone();
          pointAdv = TabPointCAdvClone.clone();
          deplace(couleur, possible[j], point[i], ModeMvt.RETOUR);
          infoPosition.add(point[i] + "" + possible[j]);

         

        }

      }
    }
  }

  /**
   * g??re le jeu en fonction du joueur/couleur
   * 
   * @param couleur
   * @return tableau de deux cha??nes {source, destination} du pion ?? jouer
   */
  String[] jouer(char couleur) {
    String src = "";
    String dst = "";
    String[] mvtIa;
    switch (couleur) {
      case 'B':
        System.out.println("Mouvement " + couleur);
        mvtIa = jouerIA(couleur);
        src = mvtIa[0];
        dst = mvtIa[1];
        // src = input.next();
        // dst = input.next();
        System.out.println(getIdToLettre(src) + "->" + getIdToLettre(dst));
        break;
      case 'R':
        System.out.println("Mouvement " + couleur);
        mvtIa = jouerIAaleatoire(couleur);
        src = mvtIa[0];
        dst = mvtIa[1];
        // src = input.next();
        // dst = input.next();
        System.out.println(getIdToLettre(src) + "->" + getIdToLettre(dst));
        break;
      default:
        break;
    }
    return new String[] { src, dst };
  }

  /**
   * 
   * Fonction qui renvoie la valeur du nombre de jeton jouable
   * 
   * (fonction utilis??e dans la fonction finPartie et IA)
   * 
   * @param tab
   * @param couleur
   * @return
   */
  int GetVerifPointTab(String[] tab, char couleur, ModeMvt mode) {
    int indentation = 0;
    // pour chaque point de la liste

    for (int i = 0; i < tab.length; i++) {
      // on r??cup??re les coordonnees du point

      int[] id = recupereid(tab[i]);
      String[] possDest = possibleDests(couleur, id[0], id[1]);
      for (int j = 0; j < possDest.length; j++) {
        if (deplace(couleur, tab[i], possDest[j],
            ModeMvt.SIMU) == Result.OK) {
          indentation++;
          if (mode == ModeMvt.nbrPionJouable)
            break;
        }

      }

    }
    return indentation;
  }

  /**
   * retourne 'R' ou 'B' si vainqueur, 'N' si parti pas fini
   * 
   * @param couleur
   * @return
   */
  char finPartie(char couleur) {
    int indentation = 0;
    if (couleur == 'R') {
      indentation = GetVerifPointTab(pointR, couleur, ModeMvt.nbrPionJouable);
    } else {
      indentation = GetVerifPointTab(pointB, couleur, ModeMvt.nbrPionJouable);
    }
    if (indentation > 0) {
      System.out.println("Il y a " + indentation + " pion de couleur " + couleur + " jouable");
      return 'N';
    }
    return couleur;
  }

  public static void main(String[] args) {

    StuckWin2 jeu = new StuckWin2();
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
      // s??quence pour Bleu ou rouge
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
    } while (partie == 'N'); // tant que la partie n'est pas finie
    jeu.affiche();
    System.out.println("Victoire : " + partie + " (" + (cpt / 2) + " coups)");
  }

}
