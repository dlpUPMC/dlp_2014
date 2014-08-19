/* Fichier compilé vers C */
#include <stdio.h>
#include <stdlib.h>

#include "ilp.h"

ILP_Object ilp_program ()
{
{
 ILP_Object  TEMP10  =  ILP_Integer2ILP(22) ;
 ILP_Object  i  =  TEMP10 ;
{
{
 ILP_Object  TEMP11  =  ILP_Float2ILP(6.3) ;
 ILP_Object  f  =  TEMP11 ;
{
return ILP_Plus( f ,  i ) ;
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
