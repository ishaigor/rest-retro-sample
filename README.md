# rest-retro-sample
Retro-fitting Security into REST web services sample is part of the talk presented at Java One 2014.
For more information about the talk visit [session page](https://oracleus.activeevents.com/2014/connect/sessionDetail.ww?SESSION_ID=1765&amp;tclass=popup).

To deploy run 

mvn clean install
cp */target/*.war $CATALINA_HOME/webapps/.

To re-deploy clean first

rm -rf $CATALINA_HOME/webapps/secure#*; rm -rf $CATALINA_HOME/webapps/insecure#*
