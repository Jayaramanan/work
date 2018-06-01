mvn install:install-file -DgroupId=javax.transaction -DartifactId=jta -Dversion=1.0.1B -Dpackaging=jar -Dfile=./ext_lib/jta-1.0.1B.jar

mvn install:install-file -DgroupId=jexcelapi -DartifactId=jxl -Dversion=2.6.12 -Dpackaging=jar -Dfile=./ext_lib/jxl-2.6.12.jar

mvn install:install-file -DgroupId=com.smardec -DartifactId=license4j -Dversion=1.6 -Dpackaging=jar -Dfile=./ext_lib/license4j-1.6.jar

mvn install:install-file -DgroupId=jasperreports -DartifactId=jasperreports -Dversion=3.7.4 -Dpackaging=jar -Dfile=./ext_lib/jasperreports-3.7.4.jar

mvn install:install-file -DgroupId=com.sun.java.jnlp -DartifactId=jnlp-servlet -Dversion=6.0 -Dpackaging=jar -Dfile=./ext_lib/jnlp-servlet-6.0.jar
