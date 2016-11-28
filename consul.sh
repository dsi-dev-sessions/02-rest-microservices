#!/bin/sh
docker rm -f service-discovery && docker run -d -p 8500:8500 --name service-discovery consul && docker ps