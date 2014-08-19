/* -*- coding: utf-8 -*- 
 * ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ilpError.c 929 2010-08-20 18:00:59Z queinnec $
 * GPL version>=2
 * ******************************************************************/

/** Ce fichier constitue la bibliothèque d'exécution d'ILP. Il
 * implante ilpBasicError.h avec des fonctions qui signalent
 * maintenant des exceptions rattrapables par ILP.
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "ilp.h"
#include "ilpBasicError.h"
#include "ilpException.h"

char *ilpError_Id = "$Id: ilpError.c 929 2010-08-20 18:00:59Z queinnec $";

/** Allocation statique d'une instance d'exception. Sa définition fait
 * qu'elle peut être prise pour un ILP_Object. Par contre sa grosse 
 * taille n'influence pas la taille des ILP_Object. */

enum ILP_ExceptionKind {
     ILP_EXCEPTION_KIND     = 0xab060ba
};

#define ILP_EXCEPTION_BUFFER_LENGTH    1000
#define ILP_EXCEPTION_CULPRIT_LENGTH     10

struct ILP_exception {
     enum ILP_ExceptionKind    _kind;
     union {
          struct asException {
               char message[ILP_EXCEPTION_BUFFER_LENGTH];
               ILP_Object culprit[ILP_EXCEPTION_CULPRIT_LENGTH];
          } asException;
     }                         _content;
};
static struct ILP_exception ILP_the_exception =  {
     ILP_EXCEPTION_KIND, { { "", { NULL } } }
};

/** 
 * Signalement d'une exception
 */

ILP_Object
ILP_error (char *message)
{
     snprintf(ILP_the_exception._content.asException.message,
              ILP_EXCEPTION_BUFFER_LENGTH,
              "Error: %s\n",
              message);
     fprintf(stderr, "%s", ILP_the_exception._content.asException.message);
     ILP_the_exception._content.asException.culprit[0] = NULL;
     return ILP_throw((ILP_Object) &ILP_the_exception);
}

/** Une fonction pour signaler qu'un argument n'est pas du type attendu. */

ILP_Object
ILP_domain_error (char *message, ILP_Object o)
{
     snprintf(ILP_the_exception._content.asException.message,
              ILP_EXCEPTION_BUFFER_LENGTH,
              "Domain error: %s\nCulprit: 0x%p\n",
              message, (void*) o);
     fprintf(stderr, "%s", ILP_the_exception._content.asException.message);
     ILP_the_exception._content.asException.culprit[0] = o;
     ILP_the_exception._content.asException.culprit[1] = NULL;
     return ILP_throw((ILP_Object) &ILP_the_exception);
}

/* end of ilpError.c */
