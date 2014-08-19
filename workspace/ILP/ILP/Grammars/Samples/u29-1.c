/* Fichier compilé vers C */
#include <stdio.h>
#include <stdlib.h>

#include "ilp.h"

ILP_Object ilp_program ()
{
{
 ILP_Object  TEMP6  =  ILP_Integer2ILP(3) ;
 ILP_Object  x  =  TEMP6 ;
{
{
 ILP_Object  TEMP7  =  ILP_Plus( x ,  x ) ;
 ILP_Object  x  =  TEMP7 ;
{
return ILP_Times( x ,  x ) ;
}
}
;
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
