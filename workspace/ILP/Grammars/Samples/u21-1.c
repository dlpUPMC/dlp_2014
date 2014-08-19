/* Fichier compilé vers C */
#include <stdio.h>
#include <stdlib.h>

#include "ilp.h"

ILP_Object ilp_program ()
{
{
(void) ILP_Integer2ILP(1) ;
(void) ILP_Float2ILP(2.2) ;
(void) ILP_TRUE ;
return ILP_String2ILP("foobar") ;
}
;
}

int main (int argc, char *argv[]) {
  ILP_print(ilp_program());
  ILP_newline();
  return EXIT_SUCCESS;
}
/* fin */
