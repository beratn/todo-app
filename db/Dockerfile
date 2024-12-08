FROM couchbase/server:7.1.1

COPY init-couchbase.sh /opt/couchbase/init/
RUN chmod +x /opt/couchbase/init/init-couchbase.sh

# Create a wrapper script that will start Couchbase and run initialization
COPY start.sh /opt/couchbase/init/
RUN chmod +x /opt/couchbase/init/start.sh

CMD ["/opt/couchbase/init/start.sh"]
