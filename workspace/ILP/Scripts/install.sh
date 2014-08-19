#! /bin/bash
# Installe ILP

YEAR=2013
INFODIR=/Infos/lmd/$YEAR/master/ue/ilp-${YEAR}oct
HGURL=${HGURL:-${INFODIR}/Mercurial}
SHAREDSRCDIR=${SHAREDSRCDIR:-${INFODIR}}
#TAG=ilp1

cd
if [ ! -f .hgrc ]
then 
    {
        echo '[ui]'
        echo "username = $USER <$USER@etu.upmc.fr>"
    } > .hgrc
fi

if ! grep -q 'trusted' < .hgrc
then { 
        echo '[trusted]'
        echo 'users = queinnec'
    } >> .hgrc
fi

mkdir -p workspace/
if [[ -d workspace/ILP/.hg/ ]]
then ( 
        cd workspace/ILP
        hg update )
else (
        cd workspace
        hg clone $HGURL ILP )
fi

if [[ ! -d workspace/ILP/Java ]]
then 
    echo "Hg n'a pas fonctionne, je copie sans Hg!"
    rsync -avu $HGURL/ILP workspace/
fi

# Pour gagner de la place dans les HOME des etudiants, les jars sont partages:
( cd workspace/ILP/Java/jars/ && \
  ln -sf ${SHAREDSRCDIR}/Java/jars/*jar . )

# et les sources du gc de Boehm aussi
( cd workspace/ILP/C/ && \
  ln -sf ${SHAREDSRCDIR}/C/gc*.tgz . )

# On precompile les fichiers C
( cd workspace/ILP/C/ && \
  make )

# end of install.sh
