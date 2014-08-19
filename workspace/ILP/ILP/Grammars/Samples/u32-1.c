/* Fichier compilé vers C */
#include <stdio.h>
#include <stdlib.h>

#include "ilp.h"

ILP_Object ilp_program ()
{
{
 ILP_Object  TEMP12  =  ILP_Float2ILP(2.2) ;
 ILP_Object  f1  =  TEMP12 ;
{
{
 ILP_Object  TEMP13  =  ILP_Float2ILP(6.3) ;
 ILP_Object  f2  =  TEMP13 ;
{
return ILP_Plus( f1 ,  f2 ) ;
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
