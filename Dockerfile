FROM clojure
COPY . /usr/src/app
WORKDIR /usr/src/app
CMD ["clojure", "-M", "-m", "my-webapp.handler"]