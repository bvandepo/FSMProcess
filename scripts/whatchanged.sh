#!/bin/sh

git status | grep modified  | sed ':a;{N;s/\n/ /};ba'   | sed 's/[[:space:]]*#[[:blank:]]*modified:[[:blank:]]*/ /g'
