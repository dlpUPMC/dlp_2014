/* Fichier compilé vers C */
#include <stdio.h>
#include <stdlib.h>

#include "ilp.h"

ILP_Object ilp_program ()
{
 if ( ILP_isEquivalentToTrue(  ILP_TRUE  ) ) {
{
return ILP_Float2ILP(1.5) ;
}
;
} else {
return ILP_FALSE;
};
}

int main (int argc, char *argv[]) {
  ILP_print(ilp_program());
  ILP_newline();
  return EXIT_SUCCESS;
}
/* fin */
