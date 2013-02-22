#!/bin/bash
#requirements:
# Jargon
# Java SDK 1.6+
# Maven 3+
# EPM packager

SCRIPTNAME=`basename $0`

# in case we need to download maven
MAVENVER=3.0.4
MAVENFILE=apache-maven-$MAVENVER
MAVENDOWNLOAD=http://apache.cs.utah.edu/maven/maven-3/$MAVENVER/binaries/$MAVENFILE-bin.zip

# define usage
USAGE="

Usage: $SCRIPTNAME [<proxy hostname> <proxy portnum>]

Example:
$SCRIPTNAME www.myhost.com 80
"

# check for correct num of args
if [[ $# -gt 0  &&  $# -lt 2 || $# -gt 2 ]]; then
        echo $USAGE
        exit 1
fi
PROXYHOST=$1
PROXYPORT=$2

# setup MAVEN settings file
UGLYSETTINGSFILESTRING='
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <proxies>
    <proxy>
      <active>true</active>
      <protocol>http</protocol>
      <host>'$PROXYHOST'</host>
      <port>'$PROXYPORT'</port>
    </proxy>
   </proxies>
</settings>'

# get into the correct directory
PACKAGEDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $PACKAGEDIR
cd ..
BUILDDIR=`pwd`
cd $PACKAGEDIR

# check to make sure mvn and epm commands are in path
MAVEN=`which mvn`
if [[ "$?" != "0" || `echo $MAVEN | awk '{print $1}'` == "no" ]] ; then
        echo "Apache Maven required to build project - downloading from $MAVENDOWNLOAD"

        # clean up any old one first
        rm -rf $MAVENFILE*

        # download maven
        wget $MAVENDOWNLOAD

        # install and setup environment
        unzip $MAVENFILE-bin.zip
        export M2_HOME=$PACKAGEDIR/$MAVENFILE
        export M2=$M2_HOME/bin
        export PATH=$M2:$PATH

        # create the .m2 dir
        mvn --version > /dev/null 2>&1

        # if proxy specified set it up in settings.xml
        if [[ $PROXYHOST ]]; then
                # save old maven settings file if one exists
                mv ~/.m2/settings.xml save_settings.xml > /dev/null 2>&1
                echo $UGLYSETTINGSFILESTRING > ~/.m2/settings.xml
        fi
else
        MAVENVERSION=`mvn --version`
        echo "Detected maven [$MAVEN] version[$MAVENVERSION]"
fi

# now get our version of EPM
cd $PACKAGEDIR
RENCIEPM="epm42-renci.tar.gz"
rm -rf epm
rm -f $RENCIEPM
wget ftp://ftp.renci.org/pub/eirods/build/$RENCIEPM
tar -xf $RENCIEPM
cd $PACKAGEDIR/epm
echo "Configuring EPM"
./configure > /dev/null
if [ "$?" != "0" ]; then
        exit 1
fi
echo "Building EPM"
if [ "$?" != "0" ]; then
        exit 1
fi
make > /dev/null

# build Scotty
echo "Building Scotty ..."
cd $BUILDDIR
mvn clean package
RETVAL=$?
if [ $RETVAL -eq 1 ]; then
  echo "Scotty Build Failed - Exiting"
  echo "Packaging Failed"
  exit 1
fi

echo "Creating Package ..."
cd $PACKAGEDIR
if [ -d $PACKAGEDIR/scotty ]; then
  rm -rf $PACKAGEDIR/scotty
fi

if [ -f $PACKAGEDIR/scotty.list ]; then
  rm $PACKAGEDIR/scotty.list
fi

mkdir $PACKAGEDIR/scotty
cd $PACKAGEDIR/scotty
cp $BUILDDIR/target/scotty.war .
unzip scotty.war
rm scotty.war

cd $PACKAGEDIR
$PACKAGEDIR/epm/mkepmlist -u scotty -g scotty --prefix /var/lib/scotty scotty > scotty.list
sed 's/\$/$$/g' scotty.list > tmp.list
cat scotty.list.template tmp.list > scotty.list
rm tmp.list

if [ -f "/etc/redhat-release" ]; then # CentOS and RHEL and Fedora
  echo "Running EPM :: Generating RPM"
  $PACKAGEDIR/epm/epm -f rpm scotty RPM=true scotty.list
elif [ -f "/etc/SuSE-release" ]; then # SuSE
  echo "Running EPM :: Generating RPM"
  $PACKAGEDIR/epm/epm -f rpm scotty RPM=true scotty.list
elif [ -f "/etc/lsb-release" ]; then  # Ubuntu
  echo "Running EPM :: Generating DEB"
  $PACKAGEDIR/epm/epm -a amd64 -f deb scotty DEB=true scotty.list
elif [ -f "/usr/bin/sw_vers" ]; then  # MacOSX
  echo "Running EPM :: Generating MacOSX DMG"
  $PACKAGEDIR/epm/epm -f osx scotty DEB=true scotty.list
fi
