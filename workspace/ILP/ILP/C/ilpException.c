/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ilpException.c 768 2008-11-02 15:56:00Z queinnec $
 * GPL version>=2
 * ******************************************************************/

/** Ce fichier constitue un complément à la bibliothèque d'exécution
 * d'ILP notamment les primitives manipulant les exceptions. */

#include <stdlib.h>
#include <stdio.h>
#include "ilpException.h"

/** Ces variables globales contiennent:
 *  -- le rattrapeur d'erreur courant
 *  -- l'exception courante (lorsque signalée)
 */

static struct ILP_catcher ILP_the_original_catcher = {
     NULL
};
struct ILP_catcher *ILP_current_catcher = &ILP_the_original_catcher;

ILP_Object ILP_current_exception = NULL;

/** Signaler une exception. */

ILP_Object
ILP_throw (ILP_Object exception)
{
     ILP_current_exception = exception;
     if ( ILP_current_catcher == &ILP_the_original_catcher ) {
          ILP_die("No current catcher!");
     };
     longjmp(ILP_current_catcher->_jmp_buf, 1);
     /** UNREACHABLE */
     return NULL;
}

/** Chaîner le nouveau rattrapeur courant avec l'ancien. */

void
ILP_establish_catcher (struct ILP_catcher *new_catcher)
{
     new_catcher->previous = ILP_current_catcher;
     ILP_current_catcher = new_catcher;
}

/** Remettre en place un rattrapeur. */

void
ILP_reset_catcher (struct ILP_catcher *catcher)
{
     ILP_current_catcher = catcher;
}

/* end of ilpException.c */
