import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.serial.SerialStruct;


public final class Arbre {
  String src;
  String dst;
  int couleur;
  boolean gagne;
  List<Arbre> fils;


  /**
   * 
   * @param src
   * @param dst
   * @param eval
   * @param gagne
   */
  public Arbre(String src, String dst, char couleur, boolean gagne) {
    
    
    
    this.src = src;
    this.dst = dst;
    this.couleur = couleur;
    this.gagne = gagne;
    this.fils = new ArrayList<>();
    


  }
  /**
   * 
   * @param fils
   */
  public void ajouteFils(Arbre fils) {
    this.fils.add(fils);
  }
/**
 * 
 * @param chemin
 * @throws IOException
 */
  public void ecritFichier(String chemin) throws IOException {
    FileWriter fw = new FileWriter(chemin);
    ecritFichier(fw, 0);
    fw.close();
  }
/**
 * 
 * @param fw
 * @param niveau
 * @throws IOException
 */
  private void ecritFichier(FileWriter fw, int niveau) throws IOException {
    for (int i = 0; i < niveau; i++) {
      fw.write("  ");
    }
    fw.write(src + " " + dst + " " + couleur + " " + gagne + "\n");
    for (Arbre fils : fils) {
      fils.ecritFichier(fw, niveau + 1);
    }
  }
}
