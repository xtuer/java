#!/bin/bash
rm -rf ../exam-web-app/src/main/resources/page-p
yarn build

cd ../exam-vue-mobile

rm -rf ../exam-web-app/src/main/resources/page-m
yarn build
