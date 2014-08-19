/* Fichier compilé vers C */
#include <stdio.h>
#include <stdlib.h>

#include "ilp.h"

ILP_Object ilp_program ()
{
{
 ILP_Object  TEMP8  =  ILP_Integer2ILP(22) ;
 ILP_Object  i  =  TEMP8 ;
{
{
 ILP_Object  TEMP9  =  ILP_Float2ILP(3.3) ;
 ILP_Object  f  =  TEMP9 ;
{
return ILP_Plus( i ,  f ) ;
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
