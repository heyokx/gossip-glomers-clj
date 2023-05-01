#!/usr/bin/env fish

set -l challenge_bin dist/echo-challenge

../../../bin/maelstrom/maelstrom test -w echo --bin $challenge_bin  --node-count 1 --time-limit 10