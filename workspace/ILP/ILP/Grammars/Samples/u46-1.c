/* Fichier compilé vers C */
#include <stdio.h>
#include <stdlib.h>

#include "ilp.h"

ILP_Object ilp_program ()
{
{
(void)  ILP_print ( ILP_String2ILP("Un, ") );
(void)  ILP_print ( ILP_String2ILP("deux et ") );
return  ILP_print ( ILP_String2ILP("trois.") );
}
;
}

int main (int argc, char *argv[]) {
  ILP_print(ilp_program());
  ILP_newline();
  return EXIT_SUCCESS;
}
/* fin */
