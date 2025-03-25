FROM postgres:16

RUN apt-get update && apt-get install -y nano openjdk-17-jdk

COPY init.sql /docker-entrypoint-initdb.d/init.sql
COPY middleware_roles.sql /docker-entrypoint-initdb.d/middleware_roles.sql

# Create certificate
RUN openssl req -new -x509 -days 365 -nodes -text \
    -out /var/lib/postgresql/data/server.crt \
    -keyout /var/lib/postgresql/data/server.key \
    -subj "/CN=localhost"

# Set permissions
RUN chmod 600 /var/lib/postgresql/data/server.key

# PostgreSQL SSL
RUN echo "ssl = on" >> /var/lib/postgresql/data/postgresql.conf && \
    echo "ssl_cert_file = '/var/lib/postgresql/data/server.crt'" >> /var/lib/postgresql/data/postgresql.conf && \
    echo "ssl_key_file = '/var/lib/postgresql/data/server.key'" >> /var/lib/postgresql/data/postgresql.conf

# PostgreSQL pg_hba.conf
RUN echo "hostssl mydatabase middleware_user 10.0.0.0/24 cert" >> /var/lib/postgresql/data/pg_hba.conf

# Java Truststore creation and import
RUN keytool -import -trustcacerts -alias postgresCert \
    -file /var/lib/postgresql/data/server.crt \
    -keystore /etc/ssl/certs/java/cacerts \
    -storepass changeit -noprompt
