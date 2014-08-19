/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ilpException.h 768 2008-11-02 15:56:00Z queinnec $
 * GPL version>=2
 * ******************************************************************/

#ifndef ILP_EXCEPTION_H
#define ILP_EXCEPTION_H

/** Les entêtes des ressources manipulant les exceptions.
 * Voici comment utiliser cette bibliothèque:
 *
 * À tout instant, ILP_current_catcher contient le rattrapeur
 * d'exceptions. Lorsqu'une exception est signalée par ILP_throw, elle
 * est envoyée au rattrapeur courant. Pour établir un nouveau
 * rattrapeur d'erreur, utilisez le motif suivant:
 *
 *        { struct ILP_catcher *current_catcher = ILP_current_catcher;
 *          struct ILP_catcher new_catcher;
 *          if ( 0 == setjmp(new_catcher->_jmpbuf) ) {
 *              ILP_establish_catcher(&new_catcher);
 *              ...
 *              ILP_current_exception = NULL;
 *          };
 *          ILP_reset_catcher(current_catcher);
 *          if ( NULL != ILP_current_exception ) {
 *              if ( 0 == setjmp(new_catcher->_jmpbuf) ) {
 *                  ILP_establish_catcher(&new_catcher);
 *                  ... = ILP_current_exception;
 *                  ILP_current_exception = NULL;
 *                  ...
 *              };
 *          };
 *          ILP_reset_catcher(current_catcher);
 *          ...
 *          if ( NULL != ILP_current_exception ) {
 *             ILP_throw(ILP_current_exception);
 *          };
 *        }
*/

#include <setjmp.h>
#include "ilp.h"

struct ILP_catcher {
     struct ILP_catcher *previous;
     jmp_buf _jmp_buf;
};

extern struct ILP_catcher *ILP_current_catcher;
extern ILP_Object ILP_current_exception;
extern ILP_Object ILP_throw (ILP_Object exception);
extern void ILP_establish_catcher (struct ILP_catcher *new_catcher);
extern void ILP_reset_catcher (struct ILP_catcher *catcher);

#endif /* ILP_EXCEPTION_H */

/* end of ilpException.h */
