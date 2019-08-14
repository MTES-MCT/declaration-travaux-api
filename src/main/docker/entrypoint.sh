#!/bin/bash

if [[ -z "$SERVER_CERT" ]]; then
    keytool -noprompt -importcert -alias rieau -keystore $JAVA_HOME/jre/lib/security/cacerts -storepass changeit -file $SERVER_CERT
fi
java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Dserver.port=$SERVER_PORT -cp app:app/lib/* com.github.mtesmct.rieau.api.Application