FROM node:latest

RUN npm install -g forever yarn

RUN mkdir /src

COPY index.js package.json /src/

WORKDIR /src

RUN yarn

EXPOSE 3000

ENTRYPOINT ["forever", "index.js"]