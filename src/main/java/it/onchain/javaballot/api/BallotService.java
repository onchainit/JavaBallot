package it.onchain.javaballot.api;

import java.io.File;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import it.onchain.javaballot.model.ReportBallot;

public class BallotService {

	private ReportBallot rb;
	private final Web3j web3j;
	private final Credentials credentials;

	public BallotService(String wallet, String pwd) throws Exception {
		this.web3j = Web3j.build(new HttpService());
		this.credentials = WalletUtils.loadCredentials(pwd, new File(wallet));
	}
	
	public BallotService(File wallet, String pwd) throws Exception {
		this.web3j = Web3j.build(new HttpService());
		this.credentials = WalletUtils.loadCredentials(pwd, wallet);
	}
	
	public void load(String contractAddress) {
		final BigInteger gasPrice = BigInteger.valueOf(2);
		final BigInteger gasLimit = BigInteger.valueOf(4712000);
		this.rb = ReportBallot.load(contractAddress, web3j, credentials, gasPrice, gasLimit);
	}
	
	public String deploy(int proposals, String secondBeneficiary, int perc, BigInteger cap) throws Exception {
		final BigInteger gasPrice = BigInteger.valueOf(2);
		final BigInteger gasLimit = BigInteger.valueOf(4712000);
		this.rb = ReportBallot.deploy(web3j, credentials, gasPrice, gasLimit, BigInteger.valueOf(proposals),
				secondBeneficiary, BigInteger.valueOf(perc), cap).send();
		return this.rb.getContractAddress();
	}
	
	public BigInteger vote(int proposal, BigInteger amount) throws Exception {
		return this.rb.vote(BigInteger.valueOf(proposal), amount).send().getGasUsed();
	}
	
	public int getWinningProposal() throws Exception {
		return this.rb.winningProposal().send().intValue();
	}
	
	public String getWalletPublicAddress() {
		return this.credentials.getAddress();
	}
	
	public static List<String> getWalletFiles() {
		return new LinkedList<String>(); //TODO
	}
}
