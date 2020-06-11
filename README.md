#Vaadin 14 Websocket PushHandler#connectionLost demo
This is a demonstration project of how Vaadin's PushHandler is leaving CurrentInstances in the connectionLost method when 
the UI is reloaded in a Chromium based browser and push transport is WEBSOCKET(+XHR).

Using
- Vaadin v14.2.0 and also v14.1.x with automatic PUSH mode and WEBSOCKET+XHR PUSH transport (no issues with LONG_POLLING)
- Spring-Boot v2.3.0-RELEASE and also v2.2.x-RELEASE
- Tomcat 9.0.35 (from Boot's BOM)

Build and run with maven, or your favourite IDE as usual. Load up http://localhost:8080/ in a Chromium based browser 
then hit F5. Check the application log for LoggingTomcatEndpointThreadPoolExecutor messages like this:
```
2020-06-11 11:57:11.468  WARN 6940 --- [    demo-exec-8] $LoggingTomcatEndpointThreadPoolExecutor : Vaadin CurrentInstance is not empty after execution, clearing now. UI: A57D87D7B3AF3EB1DBF696F2457DC1C9/0
2020-06-11 11:57:16.265  WARN 6940 --- [    demo-exec-2] $LoggingTomcatEndpointThreadPoolExecutor : Vaadin CurrentInstance is not empty after execution, clearing now. UI: A57D87D7B3AF3EB1DBF696F2457DC1C9/1
```

Repeat this in Firefox, or old MS Edge, and you will not see these warnings.

Got log warnings on:
 - Chrome v83.0.4103.97 (64 bit)
 - Microsoft Edge v84.0.522.9 dev (64 bit)
 
No issues on:
 - Firefox v76.0.1 (64-bit)
 - Microsoft Edge 44.18362.449.0 (the old one)

---

Reloading Vaadin UI-s with **Websocket Push** enabled in **Chromium based browsers** works differently than 
eg. Firefox/old MS Edge. The **PushHandler#connectionLost** method is not executed by VaadinService#handleRequest
but Tomcat's UpgradeProcessorInternal and Atmosphere's JSR356Endpoint and PushAtmosphereHandler classes, like this:
```
connectionLost:294, PushHandler (com.vaadin.flow.server.communication)
onStateChange:60, PushAtmosphereHandler (com.vaadin.flow.server.communication)
invokeAtmosphereHandler:551, AsynchronousProcessor (org.atmosphere.cpr)
completeLifecycle:493, AsynchronousProcessor (org.atmosphere.cpr)
endRequest:604, AsynchronousProcessor (org.atmosphere.cpr)
executeClose:710, DefaultWebSocketProcessor (org.atmosphere.websocket)
close:658, DefaultWebSocketProcessor (org.atmosphere.websocket)
onClose:310, JSR356Endpoint (org.atmosphere.container)
fireEndpointOnClose:555, WsSession (org.apache.tomcat.websocket)
onClose:533, WsSession (org.apache.tomcat.websocket)
processDataControl:347, WsFrameBase (org.apache.tomcat.websocket)
processData:289, WsFrameBase (org.apache.tomcat.websocket)
processInputBuffer:133, WsFrameBase (org.apache.tomcat.websocket)
onDataAvailable:82, WsFrameServer (org.apache.tomcat.websocket.server)
doOnDataAvailable:171, WsFrameServer (org.apache.tomcat.websocket.server)
notifyDataAvailable:151, WsFrameServer (org.apache.tomcat.websocket.server)
upgradeDispatch:148, WsHttpUpgradeHandler (org.apache.tomcat.websocket.server)
dispatch:54, UpgradeProcessorInternal (org.apache.coyote.http11.upgrade)
process:59, AbstractProcessorLight (org.apache.coyote)
process:868, AbstractProtocol$ConnectionHandler (org.apache.coyote)
doRun:1590, NioEndpoint$SocketProcessor (org.apache.tomcat.util.net)
run:49, SocketProcessorBase (org.apache.tomcat.util.net)
runWorker:1128, ThreadPoolExecutor (java.util.concurrent)
run:628, ThreadPoolExecutor$Worker (java.util.concurrent)
run:61, TaskThread$WrappingRunnable (org.apache.tomcat.util.threads)
run:834, Thread (java.lang)
```

The same use-case with Firefox (via VaadinService#handleRequest):
```
connectionLost:294, PushHandler (com.vaadin.flow.server.communication)
onDisconnect:111, PushAtmosphereHandler$AtmosphereResourceListener (com.vaadin.flow.server.communication)
onDisconnect:759, AtmosphereResourceImpl (org.atmosphere.cpr)
notifyListeners:650, AtmosphereResourceImpl (org.atmosphere.cpr)
notifyListeners:630, AtmosphereResourceImpl (org.atmosphere.cpr)
completeLifecycle:499, AsynchronousProcessor (org.atmosphere.cpr)
inspect:77, OnDisconnectInterceptor (org.atmosphere.interceptor)
invokeInterceptors:343, AsynchronousProcessor (org.atmosphere.cpr)
action:200, AsynchronousProcessor (org.atmosphere.cpr)
suspended:114, AsynchronousProcessor (org.atmosphere.cpr)
service:67, Servlet30CometSupport (org.atmosphere.container)
doCometSupport:2297, AtmosphereFramework (org.atmosphere.cpr)
handleRequest:249, PushRequestHandler (com.vaadin.flow.server.communication)
handleRequest:1545, VaadinService (com.vaadin.flow.server)
service:247, VaadinServlet (com.vaadin.flow.server)
service:120, SpringServlet (com.vaadin.flow.spring)
service:741, HttpServlet (javax.servlet.http)
internalDoFilter:231, ApplicationFilterChain (org.apache.catalina.core)
doFilter:166, ApplicationFilterChain (org.apache.catalina.core)
doFilter:53, WsFilter (org.apache.tomcat.websocket.server)
internalDoFilter:193, ApplicationFilterChain (org.apache.catalina.core)
doFilter:166, ApplicationFilterChain (org.apache.catalina.core)
doFilterInternal:100, RequestContextFilter (org.springframework.web.filter)
doFilter:119, OncePerRequestFilter (org.springframework.web.filter)
internalDoFilter:193, ApplicationFilterChain (org.apache.catalina.core)
doFilter:166, ApplicationFilterChain (org.apache.catalina.core)
doFilterInternal:93, FormContentFilter (org.springframework.web.filter)
doFilter:119, OncePerRequestFilter (org.springframework.web.filter)
internalDoFilter:193, ApplicationFilterChain (org.apache.catalina.core)
doFilter:166, ApplicationFilterChain (org.apache.catalina.core)
doFilterInternal:201, CharacterEncodingFilter (org.springframework.web.filter)
doFilter:119, OncePerRequestFilter (org.springframework.web.filter)
internalDoFilter:193, ApplicationFilterChain (org.apache.catalina.core)
doFilter:166, ApplicationFilterChain (org.apache.catalina.core)
invoke:202, StandardWrapperValve (org.apache.catalina.core)
invoke:96, StandardContextValve (org.apache.catalina.core)
invoke:541, AuthenticatorBase (org.apache.catalina.authenticator)
invoke:139, StandardHostValve (org.apache.catalina.core)
invoke:92, ErrorReportValve (org.apache.catalina.valves)
invoke:74, StandardEngineValve (org.apache.catalina.core)
service:343, CoyoteAdapter (org.apache.catalina.connector)
service:373, Http11Processor (org.apache.coyote.http11)
process:65, AbstractProcessorLight (org.apache.coyote)
process:868, AbstractProtocol$ConnectionHandler (org.apache.coyote)
doRun:1590, NioEndpoint$SocketProcessor (org.apache.tomcat.util.net)
run:49, SocketProcessorBase (org.apache.tomcat.util.net)
runWorker:1128, ThreadPoolExecutor (java.util.concurrent)
run:628, ThreadPoolExecutor$Worker (java.util.concurrent)
run:61, TaskThread$WrappingRunnable (org.apache.tomcat.util.threads)
run:834, Thread (java.lang)
```

The `com.vaadin.flow.server.communication.PushHandler.connectionLost` executes `service.findVaadinSession(vaadinRequest)` 
and `service.findUI(vaadinRequest)` and this populates the `com.vaadin.flow.internal.CurrentInstance.instances` ThreadLocal. 
This Session/UI context holder is not cleared properly when the caller is `PushAtmosphereHandler` (no one calls 
`com.vaadin.flow.internal.CurrentInstance.clearAll`). This leads to leftover Vaadin CurrentInstance state on Tomcat 
managed (pooled) execution threads.

This could be a serious problem if the application also uses "non Vaadin" code like Spring RestControllers, or custom Servlets, 
etc.

In our application we are using a generic Aspect that uses an optional UI scoped Bean that stores a user selected 
entity filter, and applies the selection to JPA queries. When no such Bean exists, or it doesn't contain selection, 
the aspect doesn't touch the queries.
We also have "non Vaadin" code (like a Spring Security UserDetailsService implementation) that calls some otherwise 
aspect enveloped method. Normally one would expect that in "non Vaadin-pure Spring" code no UI-s exist, hence the 
aspect won't do anything to the queries. 
Unfortunately when leftover CurrentInstances remain from a former PushHandler thread this is not the case. :( 

There was a similar problem not too long ago with PushHandler, please check https://github.com/vaadin/flow/issues/7116 .

P.S. AFAIK LONG_POLLING PUSH request are unaffected by this because they reach PushHandler via `VaadinService#handleRequest`,
which cleans CurrentInstance ThreadLocals in the `requestEnd` method.