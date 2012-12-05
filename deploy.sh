#!/bin/bash

cd "./src"
node="node"
paxos="paxos"
tpc="tpc"
acceptor="Acceptor"
leader="PaxosLeader"
coord="TPCCoordinator"

if [[ $1 == $acceptor ]]
 then 
   java Deploy $node$2 $acceptor 
