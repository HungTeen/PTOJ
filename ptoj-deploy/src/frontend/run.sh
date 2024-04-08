#!/bin/sh
set -eu 
if [ "$USE_HTTPS" == "true" ]; then
	envsubst '${SERVER_NAME} ${SERVER_HOST} ${SERVER_PORT}' < /etc/nginx/conf.d/default.conf.ssl.template > /etc/nginx/conf.d/default.conf
else
	envsubst '${SERVER_NAME} ${SERVER_HOST} ${SERVER_PORT}' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf
fi
exec "$@"
