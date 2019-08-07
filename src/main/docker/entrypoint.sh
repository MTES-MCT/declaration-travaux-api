#!/bin/bash

java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Dserver.port=$SERVER_PORT -cp app:app/lib/* com.github.mtesmct.rieau.api.Application