#!/bin/sh

threads=${1:-30}
requests=${2:-100000}

echo "Sending $requests using $threads threads\n"
ab -k -n $requests -c $threads -p plainText http://localhost:8080/hello
