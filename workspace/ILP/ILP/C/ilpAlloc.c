/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ilpAlloc.c 768 2008-11-02 15:56:00Z queinnec $
 * GPL version>=2
 * ******************************************************************/

/** Ce fichier constitue la bibliothèque d'exécution d'ILP. */

#include <stdlib.h>
#include "ilp.h"
#include "ilpAlloc.h"
#include "ilpBasicError.h"

char *ilpAlloc_Id = "$Id: ilpAlloc.c 768 2008-11-02 15:56:00Z queinnec $";

/** Allouer un objet d'ILP avec une taille et une étiquette. */

ILP_Object 
ILP_malloc (int size, enum ILP_Kind kind)
{
     ILP_Object result = malloc(size);
     if ( result == NULL ) {
          return ILP_error("Memory exhaustion");
     };
     result->_kind = kind;
     return result;
}

/* end of ilpAlloc.c */
