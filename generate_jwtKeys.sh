mkdir jwt
openssl genrsa -out jwt/rsaPrivateKey.pem 2048 -ii
openssl rsa -pubout -in jwt/rsaPrivateKey.pem -out jwt/publicKey.pem
openssl pkcs8 -topk8 -nocrypt -inform pem -in jwt/rsaPrivateKey.pem -outform pem -out jwt/privateKey.pem
chmod 600 jwt/rsaPrivateKey.pem
chmod 600 jwt/privateKey.pem