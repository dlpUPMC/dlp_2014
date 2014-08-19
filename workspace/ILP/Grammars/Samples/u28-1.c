/* Fichier compilé vers C */
#include <stdio.h>
#include <stdlib.h>

#include "ilp.h"

ILP_Object ilp_program ()
{
{
 ILP_Object  TEMP4  =  ILP_Integer2ILP(2) ;
 ILP_Object  x  =  TEMP4 ;
{
{
 ILP_Object  TEMP5  =  ILP_Integer2ILP(3) ;
 ILP_Object  y  =  TEMP5 ;
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
