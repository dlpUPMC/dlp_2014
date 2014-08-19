;;; ******************************************************************
;;; ILP -- Implantation d'un langage de programmation.
;;; Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
;;; $Id: scm2xml.scm 620 2007-01-07 14:24:50Z queinnec $
;;; GPL version>=2
;;; ******************************************************************

;;; Ce programme lit un fichier contenant des Sexpressions et les
;;; convertit dans le XML des grammaires RelaxNG associées au cours
;;; ILP. Ce programme est actuellement adapté à Bigloo que l'on peut
;;; récupérer en http://www-sop.inria.fr/mimosa/fp/Bigloo/

;;; {{{ Adaptations

;;; Utilitaires pour UMB-Scheme:
;(require 'string-port)
;(require 'format)

;;; Utilitaires pour Bigloo 2.6d: aucun

;;; }}}


;;; Lit un fichier et rend la liste de toutes les Sexpressions qui s'y
;;; trouvent.

(define (read-file filename)
  (call-with-input-file
      filename
      (lambda (in)
        (let loop ((e (read in)))
          (if (eof-object? e)
              '()
              (cons e (loop (read in))) ) ) ) ) )
;(display (read-file "scm2xml.scm"))

(define (read-file-as-string filename)
  (call-with-input-file
      filename
    (lambda (in)
      (let ((letters (let loop ((e (read-char in)))
                       (if (eof-object? e)
                           '()
                           (cons e (loop (read-char in))) ) )))
        (apply string letters) ) ) ) )            

;;; Utilitaires pour engendrer des balises XML

(define (tag name attributes . content)
  (if (pair? content)
      (format "<~a~a\n>~a</~a\n>" name attributes (gather content) name)
      (format "<~a~a\n/>" name attributes) ) )

;;; List[thing] -> chaine

(define (gather things)
  (if (pair? things)
      (format "~a~a" (car things) (gather (cdr things)))
      "" ) )

(define (attribute name value)
  (format " ~a='~a'" name value) )

(define (comment . texts) 
  (format "<!-- ~a \n-->" (gather texts)) )

;;; Conversion de Scheme en XML

(define (program-xmlize e*)
  (let ((xml (xmlize* e*)))
    (tag (gather (list "programme" *program-level*))
         ""
         xml ) ) )

(define (set-program-level! n)
  (if (> n *program-level*)
      (set! *program-level* n) ) )
  
(define *program-level* 1)
(define *input-filename* "???")

(define (xmlize* e*)
  (if (pair? e*)
      (format "~a~a" (xmlize (car e*)) (xmlize* (cdr e*)))
      "" ) )

;;; A reprendre pour utiliser des balises que doit ignorer jing:    TEMP
(define (test-name s)
  (comment "test:name"
           (attribute "description" s) ) )
(define (expected-result e)
  (comment "test:result"
           (attribute "value" e) ) )
(define *expected-value* #f)

(define (expected-printing e)
  (comment "test:printing"
           (attribute "value" "...a faire...") ) )
(define *expected-printing* #f)

(define *operators1* '((not !)))
(define (translate-op1 op)
  (translate op *operators1*) )

(define *operators2* '((divide /)
                       (modulo %)
                       (< "&lt;")
                       (<= "&lt;=")
                       (= "==")
                       (> "&gt;")
                       (>= "&gt;=")
                       (string-append "+")
                       ))
(define (translate-op2 op)
  (translate op *operators2*) )

(define *primitives* 
  '((display    print)
    (print      print)
    (newline    newline)
    (procedure? isFunction)
    (sinus      sinus) ; pour tme3
    (throw      throw)
    (memoryGet  memoryGet) ; pour partiel2013nov
    (memoryReset memoryReset)
 ))
(define sinus sin)
(define pi 3.1415926535)

(define (translate-primitive op)
  (translate op *primitives*) )

(define (translate op operators)
  (let ((p (assq op operators)))
    (if (pair? p)
        (cadr p)
        op ) ) )

(define *xml-transformers*
  (list 
   (list (lambda (e)
           (set-program-level! 2)
           (tag "definitionFonction"
                (attribute "nom" (car (cadr e)))
                (tag "variables"
                     ""
                     (gather (map xmlize 
                                  (cdr (cadr e)) )) )
                (tag "corps"
                     ""
                     (xmlize* (cddr e)) ) ) )
         'define )
   (list (lambda (e) 
           ;; (defclass c super (field ...) 
           ;;     (define (method var ...) ...) ... )
           (set-program-level! 6)
           (tag "definitionClasse"
                (gather (list (attribute "nom" (cadr e))
                              (attribute "parent" (caddr e)) ))
                (tag "champs"
                     ""
                     (gather (map (lambda (field)
                                    (tag "champ"
                                         (attribute "nom" field) ) )
                                  (cadddr e) )) )
                (tag "methodes"
                     ""
                     (gather (map (lambda (method)
                                    (tag "methode"
                                         (attribute "nom" (car (cadr method)))
                                         (tag "variables"
                                              ""
                                              (gather 
                                               (map xmlize 
                                                    (cdr (cadr method)) ) ) )
                                         (tag "corps"
                                              ""
                                              (xmlize* (cddr method)) ) ) )
                                  (cddddr e) )) ) ) )
         'defclass )
   (list (lambda (e)
         (set-program-level! 6)
         (tag "moi" "") )
         'self )
   (list (lambda (e)
         (set-program-level! 6)
         (tag "appelSuper" "") )
         'super )
   (list (lambda (e) 
         ;; (new class argument ...)
         (set-program-level! 6)
         (tag "creationObjet"
              (attribute "classe" (cadr e))
              (xmlize* (cddr e)) ) )
         'new )
   (list (lambda (e)
         ;; (send message receveur argument ...)
         (set-program-level! 6)
         (tag "envoiMessage"
              (attribute "message" (cadr e))
              (tag "receveur"
                   ""
                   (xmlize (caddr e)) )
              (tag "arguments"
                   "" 
                   (xmlize* (cdddr e)) ) ) )
         'send )
   (list (lambda (e)
         ;; (oget object "field")
         (set-program-level! 6)
         (tag "lectureChamp"
              (attribute "champ" (caddr e))
              (tag "cible"
                   ""
                   (xmlize (cadr e)) ) ) )
         'oget )
   (list (lambda (e)
         ;; (oset! object "field" value)
         (set-program-level! 6)
         (tag "ecritureChamp"
              (attribute "champ" (caddr e))
              (tag "cible"
                   ""
                   (xmlize (cadr e)) )
              (tag "valeur"
                   ""
                   (xmlize (cadddr e)) ) ) )
         'oset! )
   (list (lambda (e)
         ;; (comment "description")
         (test-name (cadr e)) )
         'comment )
   (list (lambda (e) 
         ;; (expected-result valeur-attendue)
         (set! *expected-value* (cadr e))
         (expected-result (cadr e)) )
         'expected-result )
   (list (lambda (e) 
         ;; (expected-printing "...")
         (set! *expected-printing* (cadr e))
         (expected-printing (cadr e)) )
         'expected-printing )
   (list (lambda (e)
         (tag "alternative"
              ""
              (tag "condition"
                   ""
                   (xmlize (cadr e)) )
              (tag "consequence"
                   ""
                   (xmlize (caddr e)) )
              (if (pair? (cdddr e))
                  (tag "alternant"
                       ""
                       (xmlize (cadddr e)) )
                  "" ) ) )
         'if )
   (list (lambda (e)
         (tag "sequence"
              ""
              (xmlize* (cdr e)) ) )
         'begin )
   (list (lambda (e)
         (if (= 1 (length (cadr e)))
             (tag "blocUnaire"
                  ""
                  (xmlize (car (car (cadr e))))
                  (tag "valeur"
                       ""
                       (xmlize (cadr (car (cadr e)))) )
                  (tag "corps"
                       ""
                       (xmlize* (cddr e)) ) )
             (begin 
               (set-program-level! 2)
               (tag "blocLocal"
                    ""
                    (tag "liaisons"
                         ""
                         (gather (map (lambda (binding)
                                        (tag "liaison"
                                             ""
                                             (xmlize (car binding))
                                             (tag "initialisation"
                                                  "" 
                                                  (xmlize (cadr binding)) ) ) )
                                      (cadr e) )) )
                    (tag "corps"
                         ""
                         (xmlize* (cddr e)) ) ) ) ) )
         'let  )
   (list (lambda (e)
         (set-program-level! 2)
         (tag "affectation"
              (attribute "nom" (cadr e))
              (tag "valeur"
                   ""
                   (xmlize (caddr e)) ) ) )
         'set! )
   (list (lambda (e)
         (set-program-level! 2)
         (tag "boucle"
              ""
              (tag "condition"
                   ""
                   (xmlize (cadr e)) )
              (tag "corps"
                   ""
                   (xmlize* (cddr e)) ) ) )
         'while )
   (list (lambda (e)
        ;;; (try-catch-finally body (lambda (e) catch) finally)
         (set-program-level! 3)
         (tag "try"
              ""
              (tag "corps"
                   ""
                   (xmlize (cadr e)) )
              (if (pair? (caddr e))
                  (let ((catcher (caddr e)))
                    (tag "catch"
                         (attribute "exception" (car (cadr catcher)))
                         (xmlize* (cddr catcher)) ) )
                  "" )
              (if (pair? (cadddr e))
                  (tag "finally"
                       ""
                       (xmlize (cadddr e)) )
                  "" ) ) )
         'try-catch-finally )
   (cons (lambda (e)  
           (tag "invocationPrimitive"
                (attribute "fonction" (translate-primitive (car e)))
                (xmlize* (cdr e)) ) )
         (map car *primitives*) )
   ;; Il y a deux sortes de -
   (list (lambda (e)
           (cond 
            ((= 3 (length e))
             (tag "operationBinaire"
                  (attribute "operateur" (translate-op2 (car e)))
                  (tag "operandeGauche" 
                       ""
                       (xmlize (cadr e)) )
                  (tag "operandeDroit"
                       ""
                       (xmlize (caddr e)) ) ) )
            ((= 2 (length e))
             (tag "operationUnaire"
                  (attribute "operateur" (translate-op1 (car e)))
                  (tag "operande"
                       ""
                       (xmlize (cadr e)) ) ) )
            (else
             (error (car e) "arite incorrecte" (cdr e)) ) ) )
         '- )
   (list (lambda (e)
           (if (= 2 (length e))
               (tag "operationUnaire"
                    (attribute "operateur" (translate-op1 (car e)))
                    (tag "operande"
                         ""
                         (xmlize (cadr e)) ) )
               (error (car e) "arite incorrecte" (cdr e)) ) )
         'not )
   (list (lambda (e)
           (if (= 3 (length e))
               (tag "operationBinaire"
                    (attribute "operateur" (translate-op2 (car e)))
                    (tag "operandeGauche" 
                         ""
                         (xmlize (cadr e)) )
                    (tag "operandeDroit"
                         ""
                         (xmlize (caddr e)) ) )
               (error (car e) "arite incorrecte" (cdr e)) ) )
         '+
         '*
         '/
         'divide
         '%
         'modulo
         '<
         '<=
         '=
         '>
         '>=
         '!=
         'string-append )
   ) )

(define (invocation-transformer e)
  (tag "invocation"
       ""
       (tag "fonction"
            ""
            (xmlize (car e)) )
       (tag "arguments"
            ""
            (xmlize* (cdr e)) ) ) )

(define (xmlize e)
  (if (pair? e)
      (let loop ((transformers *xml-transformers*))
        (if (pair? transformers)
            (if (memq (car e) (cdar transformers))
                (begin 
                  ;(display `(transforming ,(car e)))
                  ((caar transformers) e) )
                (loop (cdr transformers)) )
            (invocation-transformer e) ) )
      (cond
       ((symbol? e)
        (tag "variable" (attribute "nom" e)) )
       ((boolean? e) 
        (tag "booleen" (attribute "valeur" (if e "true" "false"))) )
       ((string? e) 
        (tag "chaine" "" e) )
       ((integer? e)
        (tag "entier" (attribute "valeur" e)) )
       ((inexact? e)
        (tag "flottant" (attribute "valeur" e)) )
       (else (comment e)) ) ) )

;(display (format "~a" (format "~a~a" 'symbole "string")))
;(display (tag "TAG" 
;              (attribute "NAME" 'value) 
;              "chaine"
;              'foobar
;              34
;              4.56))

;;; Traduire une valeur Scheme en la graphie que produit l'interprète ILP:

(define (transduce v)
  (cond 
   ((boolean? v) 
    (if v 'true 'false) )
   ((inexact? v)
    (let ((scale 10000))
      (/ (inexact->exact (round (* scale v))) scale) ) )
   (else v) ) )
;(transduce 0.1)
;(transduce 0.123)
;(transduce 0.12345)
;(transduce 0.1234567)
;(transduce 3.141592)
;(transduce 3.1415926535)    ; -> 3.1416

;;; Convertir un fichier en syntaxe Scheme en syntaxe XML pour ILP.

(define (convert-file filein fileout)
  (call-with-output-file
      fileout
    (lambda (out)
      (display "<?xml version='1.0' encoding='ISO-8859-15' ?>" out)
      (newline out)
      (display "<!-- " out)
      (newline out)
      (newline out)
      (display (read-file-as-string filein) out)
      (newline out)
      (display " -->" out)
      (newline out)
      (display (program-xmlize (read-file filein)) out)
      (newline out) ) ) )

;;; Evaluer un fichier en syntaxe Scheme et engendrer un fichier avec
;;; la valeur attendue et un autre fichier avec ce qui est imprime.
;;; La bibliotheque d'execution ne contient que print et newline.

(define (generate-result-files filein fileresult fileprint)
  (call-with-output-file
      fileresult
    (lambda (result-port)
      (call-with-output-file
          fileprint
        (lambda (print-port)
          (let ((result 
                 (eval `(let ((print (lambda (v) 
                                       (',display (',transduce v) ',print-port)
                                       #f ))
                              (newline (lambda ()
                                         (',newline ',print-port)
                                         #f ))
                              (throw (lambda (exception)
                                       (*the-catcher* exception) )) )
                          (try (call/cc
                                (lambda (exit-)
                                  (set! *the-catcher* exit-)
                                  ,@(read-file filein) ) )
                               (lambda (k proc msg obj)
                                 (print `(ERROR ,proc ,msg ,obj)) ; DEBUG
                                 (throw msg) ) ) )) ))
            (display (transduce result) result-port)
            (newline result-port) ) ) ) ) ) )

;;; traiter un fichier .scm et engendrer les 3 fichiers associes:
;;;  .xml      le programme en XML
;;;  .result   la valeur attendue
;;;  .print    les impressions attendues

;;; Pas le temps de simuler la semantique de grammar6 en Bigloo (les
;;; classes). Le résultat attendu est spécifié à la main avec la
;;; clause (expected-result ...) dans les fichiers u*-6.scm

(define (write-result-files fileresult fileprint)
  (call-with-output-file
      fileresult
    (lambda (result-port)
      (if *expected-value*
          (begin 
            (display (transduce *expected-value*) result-port)
            (newline result-port) ) ) ) )
  (call-with-output-file
      fileprint
    (lambda (print-port)
      (if *expected-printing*
          (begin 
            (display (transduce *expected-printing*) print-port)
            (newline print-port) ) ) ) ) )

;;; ATTENTION: il n'y a actuellement pas de programmes erronés!      FUTURE
;;; mais avec la clause (expected-result ...) cela devient plus envisageable.

(define (handle-file filein basefileout)
  (set! *program-level* 1)
  (set! *expected-value* #f)
  (set! *expected-printing* #f)
  (set! *input-filename* filein)
  (convert-file filein (string-append basefileout ".xml"))
  (let ((fileresult (string-append basefileout ".result"))
        (fileprint (string-append basefileout ".print")) )
    (if (or *expected-value* *expected-printing*)
        ;; Le résultat attendu a été spécifié à la main:
        (write-result-files fileresult fileprint)
        ;; Évaluer le programme:
        (generate-result-files filein fileresult fileprint) ) )
  'done )

;;; {{{ Utilitaires pour les adjonctions scm2xml-*.scm

(define (same-suffix? tail s)
  (and
   (> (string-length s) (string-length tail))
   (string=? tail (substring s 
                             (- (string-length s) 
                                (string-length tail) )
                             (string-length s) ) ) ) )

;;;(same-suffix? "-4tr.scm" "u40-4tr.scm")
;;;(not (same-suffix? "-4tr.scm" "u40-4.scm"))
;;;(not (same-suffix? "-4tr.scm" "u40-4trc.scm"))

;;; }}}
;;; {{{ Complément pour evaluer les programmes d'ILP:

;;; Les programmes d'ILP sont évalués en Scheme et un fichier .result
;;; est produit. Mais pour cela, il faut que tous les traits d'ILP
;;; soient présents. C'est fait jusqu'à grammar4 seulement.

;;; Voir tests pour ces ajouts dans scm2xmlTest.scm

(define-macro (while condition . body)
  (let ((w (gensym "while")))
    `(letrec ((,w (lambda ()
                    (if ,condition
                        (begin ,@body (,w)) ) )))
       (,w) ) ) )

(define (*the-catcher* e)
  (error '*the-catcher* 
         "default catcher"
         e ) )
(define (reset-the-catcher!)
  (set! *the-catcher*
        (lambda (e)
          (error '*the-catcher* 
                 "default catcher"
                 e ) ) ) )

(define (throw exception)
  ;(display `(throwing ,exception)) ; DEBUG
  (*the-catcher* exception) )

(define-macro (try-catch-finally body catcher finally)
  (let ((oldc      (gensym "old"))
        (exit      (gensym "exit"))
        (result    (gensym "result"))
        (exception (gensym "exception")) )
    `(let ((,oldc      *the-catcher*)
           (,exception #f)
           (,result    #f) )
       (set! ,result
             (call/cc
              (lambda (,exit)
                (set! *the-catcher*
                      (lambda (exception)
                        (set! ,exception #t)
                        (,exit exception) ) )
                ,body ) ) )
       (set! *the-catcher* ,oldc)
       (if ,exception
           ,(if (pair? catcher)
                `(call/cc
                  (lambda (,exit)
                    (set! ,exception #f)
                    (set! *the-catcher*
                          (lambda (exception)
                            (set! ,exception #t)
                            (set! ,result exception)
                            (,exit 'go) ) )
                    (,catcher ,result) ) )
                `#f ) )
       (set! *the-catcher* ,oldc)
       ,finally
       (if ,exception
           (,oldc ,result)
           ,result ) ) ) )

(define (divide o1 o2)
  (if (and (integer? o1) (integer? o2))
      (quotient o1 o2)
      (/ o1 o2) ) )

;;; Nécessaire pour u59-2.scm
(define g 'void)
;;; necessaire pour u92-4js.scm
(define globalcount 'void)

;;; }}}

;;; end of scm2xml.scm
