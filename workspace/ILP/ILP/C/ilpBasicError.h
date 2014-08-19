/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ilpBasicError.h 1053 2011-08-18 18:13:02Z queinnec $
 * GPL version>=2
 * ******************************************************************/

#ifndef ILP_BASIC_ERROR_H
#define ILP_BASIC_ERROR_H

extern ILP_Object ILP_error (char *message);
extern ILP_Object ILP_domain_error (char *message, ILP_Object o);

#endif /* ILP_BASIC_ERROR_H */
/* end of ilpBasicError.h */
