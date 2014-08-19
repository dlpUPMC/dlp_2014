/* Fichier compilé vers C */
#include <stdio.h>
#include <stdlib.h>

#include "ilp.h"

ILP_Object ilp_program ()
{
{
 if ( ILP_isEquivalentToTrue(  ILP_TRUE  ) ) {
{
(void)  ILP_print ( ILP_String2ILP("invisible") );
}
;
} else {
(void) ILP_FALSE;
};
return ILP_Integer2ILP(48) ;
}
;
}

int main (int argc, char *argv[]) {
  ILP_print(ilp_program());
  ILP_newline();
  return EXIT_SUCCESS;
}
/* fin */
