#!/bin/bash

mvn deploy
mvn deploy -P external
mvn deploy -P sfdc
