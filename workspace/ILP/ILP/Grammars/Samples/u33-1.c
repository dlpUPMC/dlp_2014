/* Fichier compilé vers C */
#include <stdio.h>
#include <stdlib.h>

#include "ilp.h"

ILP_Object ilp_program ()
{
{
 ILP_Object  TEMP14  =  ILP_Integer2ILP(33) ;
 ILP_Object  u  =  TEMP14 ;
{
(void) ILP_String2ILP("foobar") ;
return ILP_Plus( u ,  ILP_Integer2ILP(22) ) ;
}
}
;
}

int main (int argc, char *argv[]) {
  ILP_print(ilp_program());
  ILP_newline();
  return EXIT_SUCCESS;
}
/* fin */
