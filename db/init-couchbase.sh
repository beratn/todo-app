#!/bin/bash

echo "Initializing Couchbase cluster..."

# Setup initial cluster
couchbase-cli cluster-init -c localhost \
  --cluster-username couchbaseuser9826 \
  --cluster-password PassCouchbase!!9 \
  --services data,index,query \
  --cluster-ramsize 512 \
  --cluster-index-ramsize 256

echo "Cluster initialized. Creating bucket..."

# Create bucket
couchbase-cli bucket-create -c localhost \
  --username couchbaseuser9826 \
  --password PassCouchbase!!9 \
  --bucket todo \
  --bucket-type couchbase \
  --bucket-ramsize 256

echo "Bucket created. Assigning roles to the user..."

# Assign roles to the user
couchbase-cli user-manage -c localhost \
  --username couchbaseuser9826 \
  --password PassCouchbase!!9 \
  --set \
  --rbac-username couchbaseuser9826 \
  --rbac-password PassCouchbase!!9 \
  --roles bucket_full_access[todo] \
  --auth-domain local

echo "Roles assigned. Waiting for services to be ready..."

# Wait for services to be ready
sleep 10

echo "Checking if Query service is ready..."
until curl -s http://localhost:8093/admin/ping > /dev/null; do
    echo "Waiting for Query service..."
    sleep 2
done

echo "Query service is ready. Creating primary index..."

# Create primary index
cbq -e http://localhost:8093 -u couchbaseuser9826 -p PassCouchbase!!9 \
  --script="CREATE PRIMARY INDEX ON todo;"

echo "Primary index created. Setup complete."
