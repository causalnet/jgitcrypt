FROM debian:11.10
WORKDIR /testrepo
RUN apt update && apt -y install git git-crypt

RUN git config --global user.email "auser@example.com" && git config --global user.name "A User"

RUN git init --initial-branch=main
RUN echo 'Hello, World!' > test.txt
RUN echo 'secrets.txt filter=git-crypt diff=git-crypt' > .gitattributes
RUN echo 'This is a secret' > secrets.txt

RUN mkdir /gitcryptkey
RUN git-crypt init
RUN git-crypt export-key /gitcryptkey/thekey

RUN mkdir /results
RUN echo "This is a result file" > /results/result.txt

CMD [ "/bin/bash" ]
