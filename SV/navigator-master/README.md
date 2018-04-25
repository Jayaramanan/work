0. reference system info
uname -a
>Linux src-test 3.16.0-4-amd64 #1 SMP Debian 3.16.7-ckt20-1+deb8u3 (2016-01-17) x86_64 GNU/Linux

1. Install JDK. (JRE is not enough)
aptitude install openjdk-7-jdk

2. install the tools
aptitude install vim git maven2 libprotoc9 protobuf-compiler libprotobuf-java sudo wget postgresql tomcat8

3. get the source code
git clone https://github.com/Social-Vision-GmbH/navigator.git navigator

4. install project specific libs
cd navigator
install code related libs
./install_ext_lib.sh 
./install_ext_lib_ac.sh 

5. compile the code
switch to branch
git checkout development
compile the code
mvn -P managed-packaging -DconfigName=default -DskipTests=true clean install

6. configure the database
configure the database
echo "listen_addresses = '*'" >> /etc/postgresql/9.4/main/postgresql.conf
echo "host all all 127.0.0.1/24 trust" >>  /etc/postgresql/9.4/main/pg_hba.conf
/etc/init.d/postgresql restart

restore the dump
su - postgres
assume you have db.dump provided
pg_restore -d postgres -C db.dump

su - root

7. configure tomcat
cd /usr/share/tomcat8/lib/
copy JDBC driver to %TOMCAT_HOME%/lib (the driver can be downloaded from Postgres web page)
 
setup the datasource. Local database is assumed
cd /etc/tomcat8/
add the following into context.xml
<Resource name="jdbc/hcl_db" auth="Container"
          type="javax.sql.DataSource" driverClassName="org.postgresql.Driver"
          url="jdbc:postgresql://127.0.0.1:5432/db"
          username="postgres" password="" maxActive="20" maxIdle="10"
 		maxWait="-1"/>
		

copy postgis jar from your local maven repository ( m2_repository\org\postgis\postgis-jdbc\ )

8. deploy the backend
cd /var/lib/tomcat8/webapps
copy Ni3Web.war here

9. deploy the frontend
cd /var/lib/tomcat8/webapps/ROOT/
copy the Ni3.jnlp file here
copy Ni3.jar file here
 
10. start the application
/etc/init.d/tomcat8 restart
after the restart the application should be available on the url like http://your_host_and_port/Ni3.jnlp
