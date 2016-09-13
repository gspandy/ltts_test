@echo off 

cd %TEST_HOME%

set toolpath=%TEST_HOME%
set libs=%toolpath%/lib
set class_path=.;%toolpath%;%toolpath%/ltts-test-0.8.0.jar;%libs%/commons-lang3-3.1.jar;%libs%/commons-pool2-2.3.jar;%libs%/dmb-api-1.0.0-SNAPSHOT.jar;%libs%/fastjson-1.1.40.jar;%libs%/hamcrest-core-1.3.jar;%libs%/jedis-2.7.0.jar;%libs%/junit-4.12.jar;%libs%/log4j-1.2.17.jar;%libs%/logback-classic-1.1.7.jar;%libs%/logback-core-1.1.7.jar;%libs%/slf4j-api-1.7.21.jar;

java -classpath %class_path% cn.sunline.lttsts.LttsTest %1 %2 %3
