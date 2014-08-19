/* Fichier compilé vers C */
#include <stdio.h>
#include <stdlib.h>

#include "ilp.h"

ILP_Object ilp_program ()
{
return  ILP_print ( ILP_Minus( ILP_Float2ILP(2.5) ,  ILP_Integer2ILP(1) ) );
}

int main (int argc, char *argv[]) {
  ILP_print(ilp_program());
  ILP_newline();
  return EXIT_SUCCESS;
}
/* fin */
