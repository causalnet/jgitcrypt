FROM ubuntu:22.04
WORKDIR /repo
RUN apt update && apt -y install git git-crypt

RUN git config --global user.email "auser@example.com" && git config --global user.name "A User"

RUN git init --initial-branch=main
RUN echo 'Hello, World!' > test.txt
RUN echo 'secrets.txt filter=git-crypt diff=git-crypt' > .gitattributes
RUN echo 'This is a secret' > secrets.txt

RUN mkdir /gitcryptkey
RUN git-crypt init
RUN git-crypt export-key /gitcryptkey/thekey

RUN git add * && git add .gitattributes
RUN git commit -m "Initial commit"
RUN git-crypt lock

RUN mkdir /export
VOLUME /export

RUN echo 'tar cf /export/gitrepo.tar /repo && cp /gitcryptkey/thekey /export/ && /bin/bash' > /startup.sh && chmod 0755 /startup.sh

CMD [ "/bin/bash", "-c", "/startup.sh" ]
