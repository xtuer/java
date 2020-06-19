rd /q /s ..\exam-web-app\src\main\resources\page-p
call yarn build

cd ../exam-vue-mobile

rd /q /s ..\exam-web-app\src\main\resources\page-m
call yarn build
