#!/bin/sh

# 起動方法　「sh ./local.sh yokohama.yellow_man.sena.jobs.TestJob」
java -cp './target/universal/stage/lib/*' -Dconfig.file=./target/universal/stage/conf/application-local-dev.conf -Dlogger.file=./target/universal/stage/conf/logger-local.xml -Dfile.encoding=utf-8 yokohama.yellow_man.sena.jobs.JobExecutor $@
