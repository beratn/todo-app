#!/bin/bash

# Start Couchbase server in the background
/entrypoint.sh couchbase-server &

# Wait for Couchbase server to be ready
until curl -s http://localhost:8091/pools > /dev/null; do
    echo "Waiting for Couchbase server to start..."
    sleep 3
done

# Run initialization script
/opt/couchbase/init/init-couchbase.sh

# Keep container running by waiting for the Couchbase process
wait $(jobs -p)
