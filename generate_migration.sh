#/bin/sh

echo "Type file name:"
read fileName

touch migrations/`date +%s`__$fileName.sql
