
Solr Index Change Alert Version 1.5 
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
Change Monitor background thread. This thread is running a FileAlterationMonitor that
has a reference to a FileAlterationObserver. The FileAlterationObserver will observe file
alterations and then invoke a registered IndexChangeListenerImpl event listener. The
IndexChangeListenerImpl has a reference to an instance of a StompMessageClient that it
uses to send AlertMessages to the  Websocket application endpoint /app/alertMessage. The
incoming AlertMessages are then received by the controller of the Solr Index Change Alert
service and are processed by its onMessageReceived method, which then routes ResponseMessages
to the subscribed topic endpoint /topic/responseMessage which the web browser client is
subscribed to. The messages will then be displayed in a table for review.


   When something is added/removed/changed to the Solr indexs, a message is sent to the web 
browser, so the client is updated in real time and the user knows instantly when things are
added/removed/changed without having to refresh the page or do some kind of polling.


PROPERTIES FILE - IMPORTANT BEFORE RUNNING
------------------------------------------

   The config properties file (SolrIndexChangeAlert/conf/config.properties) contains the 
PROXY_HOST, the PROXY_PORT and the INDEX_FOLDER_ROOT constants that should be changed 
before running. The constant INDEX_FOLDER_ROOT is the actual root path to your Solr core 
index files directory which is typically COLLECTION_shard#_replica1/data/index. The COLLECTION 
and shard # are specified in the UI. 

Config File Examples:
* INDEX_FOLDER_ROOT=/opt/lucidworks/fusion/3.1.5/data/solr/
* PROXY_HOST=127.0.0.1
* PROXY_PORT=8080


To Build and Run Solr Index Change Alert
----------------------------------------
1) gradle clean build

2) gradle bootRun

3) start browser with 127.0.0.1:8080 (use your values for PROXY_HOST and PROXY_PORT)


RECENT UPGRADE CHANGES 8-31-18
------------------------------
1) Added a properties file so that recompliling will not be necessary.

2) Added multiple column data output instead of just the date time, message.

3) Added export/save of table data to a JSON file.

4) Added chart.js plotting of the table data.

5) Added a HTML5 Web Worker for the construction of chart.js data sets.

6) The HTML5 Web Worker utilizes Transferrable objects for speed.


RECENT UPGRADE CHANGES 5-03-20
------------------------------
1) Upgraded dependency versions

2) Added auto-detection of index directories from INDEX_FOLDER_ROOT (config.properties)

3) Multiple FileAlterationMonitor threads started for each index dir found.



Future goals:
-------------
* Store collected data in MongoDB




