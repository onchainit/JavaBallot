# JavaBallot

Password encrypted wallets for test accounts available in 'src/test/resources' with wallet-\<password\>-\<privatekey\>.json file format.

To run tests, start a local blockchain and load those accounts with some ethers. For example, using testrpc, that's achieved with:

> testrpc --account="0x9f71f32ca402303718f3881f39a92cc73a2c1f057c5b19ef579fbf33bf98794c,50000000000000000000" --account="0x477f5283fefc7d3bd0aa02e21ba840984f4540da82c2d22e005fb3980a158a6c,50000000000000000000" --account="0x05e2a207bad2e82367350bf8143e3db55fcc4a0ec5ea4f4273611c5f7717ca51,50000000000000000000"

# Running the application

You can deploy the application ('target/java-ballot.war') on a recent web container (Tomcat) and browse to 'http://localhost:8080/java-ballot'.
Alternatively, you can run the application fat jar with 'java -jar target/webapp.jar' and browse to 'http://localhost:8080/'.
