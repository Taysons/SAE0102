String[] jouerIA2TIT(char couleur) {

  String[] bestAction = null;
  int bestEval = -1000000;
  String[] posCouleur = new String[13];
  addPosCouleurTIT(posCouleur, couleur);
  for (String src : posCouleur) {
    int[] srcID = recupereidTIT(src);
    String[] dsts = possibleDestsTIT(couleur, srcID[0], srcID[1]);
    for (String dst : dsts) {
      Result res = deplaceTIT(couleur, src, dst, ModeMvt.SIMU);
      if (res == Result.OK) {
        deplaceTIT(couleur, src, dst, ModeMvt.REAL);
        int eval = -evaluerTIT(couleur == 'B' ? 'R' : 'B', 3);
        if (eval > bestEval) {
          bestEval = eval;
          bestAction = new String[] { src, dst };
        }
        deplaceTIT(couleur, dst, src, ModeMvt.RETOUR);
      }
    }
  }
  return bestAction;
}


int evaluerTIT(char couleur, int depth) {

  char adv = couleur == 'B' ? 'R' : 'B';
  String[] posCouleur = new String[13];
  addPosCouleurTIT(posCouleur, couleur);
  String[] posAdv = new String[13];
  addPosCouleurTIT(posAdv, adv);
  int movesCurr = GetVerifPointTabTIT(posCouleur, couleur, null);
  int movesAdv = GetVerifPointTabTIT(posAdv, adv, null);
  if (depth == 0 || movesCurr == 0 || movesAdv == 0) {
    return movesAdv - movesCurr;
  }
  int bestEval = -1000000;
  for (String src : posCouleur) {
    int[] srcID = recupereidTIT(src);
    String[] dsts = possibleDestsTIT(couleur, srcID[0], srcID[1]);
    for (String dst : dsts) {
      Result res = deplaceTIT(couleur, src, dst, ModeMvt.SIMU);
      if (res == Result.OK) {
        deplaceTIT(couleur, src, dst, ModeMvt.REAL);
        int eval = -evaluerTIT(adv, depth - 1);
        if (eval > bestEval) {
          bestEval = eval;
        }
        deplaceTIT(couleur, dst, src, ModeMvt.RETOUR);
      }
    }
  }
  return bestEval;

}


void addPosCouleurTIT(String[] point, char couleur) {

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
        // si on a trouvé 13 pions, on sort de la boucle pour optimiser
        if (j == 13) {
          break;
        }
      }
    }

  }
  
  
  
  
  String[] possibleDestsTIT(char couleur, int idLettre, int idCol) {
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




Result deplaceTIT(char couleur, String lcSource, String lcDest, ModeMvt mode) {
    // votre code ici. Supprimer la ligne ci-dessous.
    int[] source = recupereidTIT(lcSource);
    int[] destination = recupereidTIT(lcDest);
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
    // est supérieure à 1

    if ((Math.abs(source[0] - destination[0]) > 1
        || Math.abs(source[1] - destination[1]) > 1)
        && mode != ModeMvt.RETOUR) {
      return Result.TOO_FAR;
    }
    // si le mode est réel alors on modifie la position du pion
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

 
  String[] possibleDestsTIT(char couleur, int idLettre, int idCol) {
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

  
int GetVerifPointTabTIT(String[] tab, char couleur, ModeMvt mode) {
    int indentation = 0;
    // pour chaque point de la liste

    for (int i = 0; i < tab.length; i++) {
      // on récupère les coordonnees du point

      int[] id = recupereidTIT(tab[i]);
      String[] possDest = possibleDestsTIT(couleur, id[0], id[1]);
      for (int j = 0; j < possDest.length; j++) {
        if (deplaceTIT(couleur, tab[i], possDest[j],
            ModeMvt.SIMU) == Result.OK) {
          indentation++;
          if (mode == ModeMvt.nbrPionJouable)
            break;
        }

      }

    }
    return indentation;
  }
  
  
  int[] recupereidTIT(String src) {

    int[] id = new int[2];
    // Convertit le caractère Unicode numérique à la
    // position spécifiée dans une chaîne spécifiée en un nombre
    // à virgule flottante double précision.
    if (src.length() < 2 || src.length() > 2) {
      id[0] = 0;
      id[1] = 0;
      return id;
    }
    src = tradIdLettre(src);
    id[0] = Character.getNumericValue(src.charAt(0));
    id[1] = Character.getNumericValue(src.charAt(1));
    return id;
  }
