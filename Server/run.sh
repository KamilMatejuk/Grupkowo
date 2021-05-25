#!/bin/bash

curr_date=$(date +'%d.%m.%Y')
logs_dir="/home/kamil/Desktop/Grupkowo-Server/logs/"
log_file="${logs_dir}log_${curr_date}"

rm -f $log_file

/usr/bin/python3 -m uvicorn --host 192.168.2.114 --app-dir /home/kamil/Desktop/Grupkowo-Server main:app >> $log_file 2>&1 &
server_pid=$!
echo "Started server for Grupkowo (pid = ${server_pid})"

echo "kill ${server_pid}" > kill.sh
echo "Created kill file for this process (kill.sh)"
