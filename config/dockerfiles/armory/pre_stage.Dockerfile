FROM node:16.15.0-slim

COPY services/armory /armory

WORKDIR /armory

RUN npm install --registry={{env.NODE_PRIVATE_REGISTRY}} --_auth={{env.NODE_PRIVATE_AUTH}}
RUN npm run build
