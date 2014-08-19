# -*- coding: utf-8 -*-
# ******************************************************************
# ILP -- Implantation d'un langage de programmation.
# Copyright (C) 2004-2007 <Christian.Queinnec@lip6.fr>
# $Id:Makefile 405 2006-09-13 17:21:53Z queinnec $
# GPL version>=2
# ******************************************************************

work :  create.rng.files \
		validate.xml.files
clean :: cleanMakefile
	-rm -f grammar*.rng

# Regenerer toutes les grammaires possibles.
all :
	for g in *.rnc ; do make $${g%c}g ; done

# 2007sep06: trang ne fonctionne pas avec le java d'Ubuntu (gij)! De
# plus, le code de trang n'est pas generique (a ne pas recompiler donc).

JAVA		=	java
TRANG		=	../Java/jars/trang.jar
JING		=	../Java/jars/jing.jar
#CODING		=	iso-8859-15
CODING		=	utf-8

.SUFFIXES: .rnc .rng .xsd .dtd
.rnc.rng :
	${JAVA} -jar ${TRANG} \
		-i encoding=${CODING} \
		-o encoding=${CODING} \
		 $*.rnc $*.rng
.rnc.xsd :
	${JAVA} -jar ${TRANG} \
		-i encoding=${CODING} \
		-o encoding=${CODING} \
		 $*.rnc $*.xsd
.rnc.dtd :
	${JAVA} -jar ${TRANG} \
		-i encoding=${CODING} \
		-o encoding=${CODING} \
		 $*.rnc $*.dtd

# NOTA: Valider un document XML d.xml avec un schéma f.rng et jing ainsi:
#  	${JAVA} -jar ${JING} f.rng d.xml

GRAMMARS	=	\
			grammar1.rnc \
			grammar2.rnc \
			grammar3.rnc \
			grammar4.rnc \
			grammar5.rnc \
			grammar6.rnc

# Créer les équivalents XML des schémas RelaxNG compacts:

create.rng.files : ${GRAMMARS:.rnc=.rng}

# Creer les équivalents XSD des schémas RelaxNG compacts:

create.xsd.files : ${GRAMMARS:.rnc=.xsd}

# Les grammaires s'incluent ce qui cree des dependances:
grammar2.rng : grammar1.rng
grammar3.rng : grammar2.rng
grammar4.rng : grammar3.rng
grammar5.rng : grammar4.rng
grammar6.rng : grammar4.rng

# Valider les exemples de programmes qui sont en Samples/
# Verifier que les grammaires sont bien incluses: tout ce que reconnait
# grammar1 doit etre reconnu par grammar2, etc.

validate.xml.files : ${GRAMMARS:.rnc=.rng}
	for i in 6 5 4 3 2 1 ; do \
	for p in Samples/?*-[1-$$i].xml ; do \
	echo Validating $$p with grammar$$i ; \
	${JAVA} -jar ${JING} grammar$$i.rng $$p ; \
	done ; done

# fin de Makefile
