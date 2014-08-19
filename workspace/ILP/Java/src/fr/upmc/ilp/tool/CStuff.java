/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id: CStuff.java 735 2008-09-26 16:38:19Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.tool;

/** Conversion de suites de lettres du monde ILP vers le monde C. */

public class CStuff {

  /** Transformer un identificateur ILP en un identificateur C. */

  public static String mangle (final String s) {
    assert(s != null);
    final StringBuffer sb = new StringBuffer(s.length());
    for ( int i=0 ; i<s.length() ; i++ ) {
      final char c = s.charAt(i);
      if (   ( 'a' <= c && c <= 'z' )
          || ( 'A' <= c && c <= 'Z' )
          || ( '0' <= c && c <= '9' )
          || ( '_' == c ) ) {
        sb.append(c);
      } else {
          sb.append("_");
          sb.append(Integer.toHexString(c));
      }
    }
    return sb.toString();
  }

  /** Transformer une chaine ILP en une chaine C. */

  public static String protect (String value) {
      final int n = value.length();
      final StringBuffer result = new StringBuffer(n);
      for ( int i = 0 ; i<n ; i++ ) {
        char c = value.charAt(i);
        switch ( c ) {
        case '\\':
        case '"': {
          result.append("\\");
        }
        //$FALL-THROUGH$
        default: {
          result.append(c);
        }
        }
      }
      return result.toString();
  }

}

// end of CStuff.java
