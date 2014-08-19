/* Fichier compilé vers C */
#include <stdio.h>
#include <stdlib.h>

#include "ilp.h"

ILP_Object ilp_program ()
{
{
 ILP_Object  TEMP2  =  ILP_Integer2ILP(1) ;
 ILP_Object  x  =  TEMP2 ;
{
{
 ILP_Object  TEMP3  =  ILP_Integer2ILP(2) ;
 ILP_Object  x  =  TEMP3 ;
{
return x ;
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
