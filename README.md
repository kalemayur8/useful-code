# useful-code
Important Piece of code, I come across my developer phase.

# Important Tools and Plungins we used during development phase:
1. Sonar Qube for Code Qality gate check 
Link : https://dzone.com/articles/how-quickly-get-started-sonar

2. Best tool  UCDetector to remove dead code from any existing code.
Link : http://www.ucdetector.org/

3. Walk Mode, to remove, unnecessary variables, imports from Java code.

Link : https://github.com/walkmod/walkmod-dead-code-cleaner-plugin

4. For Regular expression creation and debug :

Link : https://regex101.com/

5. MDC Logger with TaskExecutor, Logback.xml, Jboss

Link : https://stackoverflow.com/questions/6073019/how-to-use-mdc-with-thread-pools
Link : https://logback.qos.ch/manual/mdc.html
Link : for Jboss changes should be in settings.xml

While using completableFuture pass existing taskexecutor threadpool to it, so that existing MDC logger logic should work.

6. Multiple application Context path for Jboss application (Generally useful for mock url, during deployment).
Link : https://stackoverflow.com/questions/30875657/multiple-context-root-for-single-war-deployment-jboss-eap-6-1

7. Splunk Configuration
 1. Extraction of Fields 
 2. Creating Dashboard.
 
8. Appdynamics
