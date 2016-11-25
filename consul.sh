#!/bin/sh
docker rm -f dev-consul && docker run -d -p 8500:8500 --name dev-consul consul && docker ps