#!/bin/bash

PROP_FILE_PATH=$HOME/Desktop/money/ejt2/money/src/main/mint3.properties

java -cp $HOME/Desktop/money/ejt2/money/target/money-1.0-SNAPSHOT.jar -Dprop_file_path=$PROP_FILE_PATH com.ejt.money.ProcessTransFiles $1
