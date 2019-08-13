#!/bin/bash

keytool -noprompt -importcert -alias rieau -keystore $JAVA_HOME/jre/lib/security/cacerts -storepass changeit -file $SERVER_CERT
java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Dserver.port=$SERVER_PORT -Djavax.net.debug=all -cp app:app/lib/* com.github.mtesmct.rieau.api.Application