package it.onchain.javaballot;

import java.io.File;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.web3j.abi.datatypes.Address;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.RawTransaction;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import it.onchain.javaballot.model.ReportBallot;

import static it.onchain.javaballot.Util.*;

public class ReportBallotTest {
	
	public static final String WALLET_0 = "wallet-javaballot0-9f71f32ca402303718f3881f39a92cc73a2c1f057c5b19ef579fbf33bf98794c.json";
	public static final String PUBLIC_ADDRESS_0 = "0xa84e876bacafbb51cb4b4e3f2276535096d10b14";
	public static final String PUBLIC_ADDRESS_1 = "0xb1c0c1b000c03577102f1dc54933b4cde9b5729c";
	public static final String PUBLIC_ADDRESS_2 = "0xee9804eead6a12e4d855498d290da9352e2e2c40";

	private static ReportBallot rb;
	private static BigInteger cap = new BigInteger("10000000000000000000"); //10 ETH
	private static Web3j web3j;
	private static Credentials credentials;
	
	@BeforeClass
	public static void deployContract() throws Exception {
		web3j = Web3j.build(new HttpService());  // defaults to http://localhost:8545/
		credentials = WalletUtils.loadCredentials("javaballot0", new File(ReportBallotTest.class.getClassLoader().getResource(WALLET_0).toURI()));
		rb = deployContract(web3j, credentials, cap);
	}
	
	@Test
	public void testSendEthers() throws Exception {
		final BigInteger balance0 = getBalance(web3j, PUBLIC_ADDRESS_0);
		final BigInteger balance1 = getBalance(web3j, PUBLIC_ADDRESS_1);

		//get nonce
		EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(PUBLIC_ADDRESS_0, DefaultBlockParameterName.LATEST).sendAsync().get();
	    BigInteger nonce = ethGetTransactionCount.getTransactionCount();
		
	    //prepare raw transaction
	    final BigInteger value = new BigInteger("700000000000000000"); //0.7 ETH
	    final BigInteger gasPrice = BigInteger.valueOf(10000);
	    final BigInteger gasLimit = BigInteger.valueOf(21000);
	    final BigInteger maxGas = gasPrice.multiply(gasLimit);
	    RawTransaction rawTransaction  = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, PUBLIC_ADDRESS_1, value);
		
	    //sign transaction
	    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
	    String hexValue = Numeric.toHexString(signedMessage);
	    
	    //send transaction
	    EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
	    // poll for transaction response via org.web3j.protocol.Web3j.ethGetTransactionReceipt(<txHash>)
	    
//		System.out.println("transaction hash: " + ethSendTransaction.getTransactionHash());
		
		Assert.assertEquals(balance0.subtract(value).subtract(maxGas), getBalance(web3j, PUBLIC_ADDRESS_0));
		Assert.assertEquals(balance1.add(value), getBalance(web3j, PUBLIC_ADDRESS_1));
	}
	
	
	@Test
	public void testCap() throws Exception {
		Assert.assertEquals(cap, rb.cap().get().getValue());
	}
	
	private static ReportBallot deployContract(Web3j web3j, Credentials credentials, BigInteger cap) throws InterruptedException, ExecutionException {
		//prepare raw transaction
	    final BigInteger gasPrice = new BigInteger("2");
	    final BigInteger gasLimit = BigInteger.valueOf(4712000);
	    
	    ReportBallot rb = ReportBallot.deploy(web3j, credentials, gasPrice, gasLimit, BigInteger.valueOf(0), uint8(3), new Address(PUBLIC_ADDRESS_1), uint8(50), uint256(cap)).get();
	    return rb;
	}
	
	
	private BigInteger getBalance(Web3j web3j, String account) throws InterruptedException, ExecutionException
	{
		EthGetBalance egb0 = web3j.ethGetBalance(account, DefaultBlockParameterName.LATEST).sendAsync().get();
		BigInteger balance = egb0.getBalance();
//		System.out.println(account + " balance: " + balance);
		return balance;
	}
}
