#!/bin/bash

for fileFrom in $(find ${1} -type f -regex '.*_framed.png'); do \
fileTo=$(echo "$fileFrom" | sed 's|\(.*\)\(_framed\)\(.*\)|\1\3|g');  \
echo "From " $fileFrom ; \
echo "To" $fileTo ; \
mv $fileFrom $fileTo ; \
done
