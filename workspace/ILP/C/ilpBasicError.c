/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ilpBasicError.c 822 2009-10-07 08:04:40Z queinnec $
 * GPL version>=2
 * ******************************************************************/

/** Ce fichier constitue la bibliothèque d'exécution d'ILP. */

#include <stdlib.h>
#include <stdio.h>
#include "ilp.h"
#include "ilpBasicError.h"

char *ilpBasicError_Id = "$Id: ilpBasicError.c 822 2009-10-07 08:04:40Z queinnec $";

/** 
 * Signalement de problèmes.
 *
 * Cette fonction ne renvoit rien mais le typage est meilleur si on
 * l'utilise après un return (cf. exemples plus bas).
 */

ILP_Object
ILP_error (char *message)
{
     ILP_die(message);
     /** NOT REACHED */
     return NULL;
}

#define BUFFER_LENGTH 1000

/** Une fonction pour signaler qu'un argument n'est pas du type attendu. */

ILP_Object
ILP_domain_error (char *message, ILP_Object o)
{
     char buffer[BUFFER_LENGTH];
     snprintf(buffer, BUFFER_LENGTH, "%s\nCulprit: 0x%p\n", 
              message, (void*) o);
     ILP_die(buffer);
     /** NOT REACHED */
     return NULL;
}

/* end of ilpBasicError.c */
