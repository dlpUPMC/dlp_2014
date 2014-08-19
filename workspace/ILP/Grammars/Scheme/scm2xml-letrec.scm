;;; $Id$
;;; Les programmes d'ilp5 se nomment ur*-5.scm

;;; Separer les definitions des expressions qui suivent.

(define (extract-definitions e*)
  (define (scan e* definitions)
    (cond ((null? e*)
           (cons definitions e*) )
          ((and (pair? (car e*))
                (eq? (caar e*) 'define) )
           (scan (cdr e*) (append definitions (list (car e*)))) )
          (else (cons definitions e*)) ) )
  (scan e* '()) )

;;; XMLisation d'un letrec pour ILP5. On ecrit:
;;;
;;; (letrec () 
;;;    (define (odd n) ...)
;;;    (define (even n) ...)
;;;    ... )
;;;
;;; et c'est traduit en:
;;;
;;;   <codefinitions>
;;;     <fonctions>
;;;       <definitionFonction ...>  comme pour les fonctions globales
;;;       </definitionFonction>
;;;     </fonctions>
;;;     <corps> ... </corps>
;;;   </codefinitions>

(set! *xml-transformers*
      (cons
       (list (lambda (e)
               ;; (letrec () (define (f ...) ...) ... ...)
               (set-program-level! 5)
               (if (not (null? (cadr e)))
                   (error (car e) "codefinitions incorrectes" (cadr e)) )
               (let ((fns+body (extract-definitions (cddr e))))
                 (tag "codefinitions"
                    ""
                    (tag "fonctions"
                         ""
                         (xmlize* (car fns+body)) )
                    (tag "corps"
                         ""
                         (xmlize* (cdr fns+body)) ) ) ) )
             'letrec )
       *xml-transformers* ) )

;;; end of scm2xml-letrec.scm
