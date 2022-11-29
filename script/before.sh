#!/bin/bash
export REPOSITORY=/home/ubuntu/deploy

if [ -d $REPOSITORY ]
then
 rm -rf $REPOSITORY
fi

mkdir -p $REPOSITORY