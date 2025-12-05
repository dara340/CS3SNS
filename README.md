# CS3SNS
A multi-user chat service using socket programming. Consisting of a chat server that hosts a group chat; and a chat client that can be executed by multiple users to interact with the group chat.

Part of setup initialization code for creating serverkeystore, run it in src

keytool -genkeypair -alias chatserver -keyalg RSA -keysize 2048 `
  -keystore ServerKeyStore.jks -validity 365 `
  -storepass serverpass -keypass serverpass
(Create serverkeystore)


keytool -exportcert -alias chatserver -keystore ServerKeyStore.jks -file chatserver.cer -storepass serverpass
(Exporting public cert)

keytool -importcert -alias chatserver -file chatserver.cer -keystore ClientTrustStoreServer.jks -storepass clientpass

(Give permission to store cert in client trust store)
