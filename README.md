
Solr Index Change Alert Version 1.0 
===================================


Author: Michael E. Weigel
-------------------------


Purpose
-------
   Solr Index Change Alert is a WebSocket application developed using Spring Boot, that
is used to monitor Apache Solr Index files for any type of alterations/changes and the
creation of new index files and the deletion of existing index files. The web application
utilizes WebSocket protocol communication between the client browser and the service 
deployed and running in Apache Tomcat.


Activity
--------
   When the Solr Index Change Alert Web application war file is deployed in Apache Tomcat,
the user can view the front end application running at http://hostName:portNum. They
will be able to connect to the service via a WebSocket. They then can start the Solr Index
Change Montitor background thread. This thread is running a FileAlterationMonitor that
has a reference to a FileAlterationObserver. The FileAlterationObserver will observe file
alterations and then invoke a registered IndexChangeListenerImpl event listener. The
IndexChangeListenerImpl has a reference to an instance of a StompMessageClient that it
uses to send AlertMessages to the  Websocket application endpoint /app/alertMessage. The
incoming AlertMessages are then received by the contoller of the Solr Index Change Alert
service and are processed by its onMessageReceived method, which then routes ResponseMessages
to the subscribed topic endpoint /topic/responseMessage which the web browser client is
subscribed to. The messages will then be displayed in a table for review.


   When something is added/removed/changed to the Solr indexs, a message is sent to the web 
browser, so the client is updated in real time and the user knows instantly when things are
added/removed/changed without having to refresh the page or do some kind of polling.



IMPORTANT BEFORE COMPILING
--------------------------

   The file com.prototype.utils.AppConstants contains the PROXY_HOST and the PROXY_PORT
constants that have to be changed before compiling. You must also change the constant
INDEX_FOLDER and add the actual full path to your Solr core index files directory before
compiling. This constants file will be replaced later with a properties file.

* PROXY_HOST = "127.0.0.1";
* PROXY_PORT = "8080";
* INDEX_FOLDER = "/cots/solr-6.4.0/collection1/data/index";


RUN Solr Index Change Alert
---------------------------
1) gradle bootRun

2) start browser with 127.0.0.1:8080 (use your values for PROXY_HOST and PROXY_PORT)


TODO:
-----
   Possibly add a properties file to replace com.prototype.utils.AppConstants so that recompliling
will not be necessary.



