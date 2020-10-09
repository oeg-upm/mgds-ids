#!/bin/bash

cd connector;
docker-compose stop;
docker-compose rm -f;
docker-compose build --parallel;
docker-compose up -d;

cd ..

#cd broker;
#docker-compose stop;
#docker-compose rm -f;
#docker-compose build --parallel;
#docker-compose up -d;

#cd ..
