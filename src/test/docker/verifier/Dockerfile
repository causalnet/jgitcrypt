FROM debian:11.10

RUN apt update && apt -y install git git-crypt file

RUN git config --global user.email "auser@example.com" && git config --global user.name "A User"

RUN mkdir /gitcryptkey
RUN mkdir /results

COPY thekey /gitcryptkey
COPY it-gitrepo /it-gitrepo
WORKDIR /it-gitrepo

SHELL ["/bin/bash", "-c"]

#Verify we have an encrypted file in the repo
RUN [[ `file -b --mime newsecrets.txt` == application/octet-stream* ]] || (echo File was not encrypted but should be && exit 1)

#Decrypt
RUN git-crypt unlock /gitcryptkey/thekey

#Verify file is now decrypted
RUN [[ `file -b --mime newsecrets.txt` == text/plain* ]] || (echo File was not decrypted by git-crypt && exit 1)

#Copy file for verification
RUN cp /it-gitrepo/newsecrets.txt /results/newsecrets.txt

CMD [ "/bin/bash" ]
