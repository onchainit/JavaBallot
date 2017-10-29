package it.onchain.javaballot;

import java.math.BigInteger;

import org.junit.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import it.onchain.javaballot.model.ReportBallot;

public class ReportBallotTest {

	@Test
	public void testDeploy() throws Exception {
		Web3j web3j = Web3j.build(new HttpService());  // defaults to http://localhost:8545/
//		Credentials credentials = WalletUtils.loadCredentials("password", "/path/to/walletfile");

//		ReportBallot contract = ReportBallot.deploy(web3j, credentials, BigInteger.valueOf(10000), BigInteger.valueOf(21000)).get();
	}
}
