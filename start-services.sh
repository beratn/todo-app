#!/bin/bash

set -e

echo "Starting Couchbase Server..."
/entrypoint.sh couchbase-server &
COUCHBASE_PID=$!

# Wait for Couchbase to be ready
echo "Waiting for Couchbase to be ready..."
for i in {1..30}; do
    if curl -s http://localhost:8091 > /dev/null; then
        break
    fi
    if [ $i -eq 30 ]; then
        echo "Couchbase failed to start"
        exit 1
    fi
    echo "Attempt $i: Waiting for Couchbase..."
    sleep 5
done

echo "Running Couchbase initialization..."
/opt/couchbase/init/init-couchbase.sh

# Verify Couchbase is fully initialized
echo "Verifying Couchbase initialization..."
for i in {1..30}; do
    if curl -s -u couchbaseuser9826:PassCouchbase!!9 http://localhost:8091/pools/default/buckets/todo > /dev/null; then
        echo "Couchbase initialization verified"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "Failed to verify Couchbase initialization"
        exit 1
    fi
    echo "Attempt $i: Waiting for Couchbase initialization..."
    sleep 5
done

# Start Nginx
echo "Starting Nginx..."
nginx

# Start Spring Boot application
echo "Starting Spring Boot application..."
MAX_RETRIES=5
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    echo "Starting Spring Boot application (attempt $(($RETRY_COUNT + 1)))"
    java -jar /opt/app/app.jar &
    SPRING_PID=$!
    
    # Wait for Spring Boot to start
    sleep 20
    
    if ps -p $SPRING_PID > /dev/null; then
        echo "Spring Boot application started successfully"
        break
    else
        echo "Spring Boot application failed to start, retrying..."
        RETRY_COUNT=$(($RETRY_COUNT + 1))
        if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
            echo "Failed to start Spring Boot application after $MAX_RETRIES attempts"
            exit 1
        fi
        sleep 5
    fi
done

echo "All services started successfully"

# Wait for any process to exit
wait -n

# Exit with status of process that exited first
exit $?
