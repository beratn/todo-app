# Stage 1: Build Frontend
FROM node:18 AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# Stage 2: Build Backend
FROM maven:3.8.4-openjdk-17 AS backend-build
WORKDIR /app/backend
COPY backend/app/pom.xml ./
COPY backend/app/src ./src
RUN mvn clean package -DskipTests

# Stage 3: Final image with Couchbase
FROM couchbase/server:7.1.1

# Install necessary packages
RUN apt-get update && apt-get install -y \
    nginx \
    openjdk-17-jre \
    && rm -rf /var/lib/apt/lists/*

# Copy Couchbase initialization scripts
COPY db/init-couchbase.sh /opt/couchbase/init/
COPY db/start.sh /opt/couchbase/init/
RUN chmod +x /opt/couchbase/init/*.sh

# Copy frontend build and configure nginx
COPY --from=frontend-build /app/frontend/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy backend jar
COPY --from=backend-build /app/backend/target/*.jar /opt/app/app.jar

# Copy startup script
COPY start-services.sh /opt/app/
RUN chmod +x /opt/app/start-services.sh

EXPOSE 3000 8091-8094 8080 11210

CMD ["/opt/app/start-services.sh"]
