FROM debian:11.10
WORKDIR /testrepo
RUN apt update && apt -y install git git-crypt

RUN git config --global user.email "auser@example.com" && git config --global user.name "A User"

RUN mkdir /gitcryptkey
#RUN git-crypt init
#RUN git-crypt export-key /gitcryptkey/thekey

COPY it-gitrepo /it-gitrepo

RUN mkdir /results
RUN echo "This is a result file" > /results/result.txt

CMD [ "/bin/bash" ]
