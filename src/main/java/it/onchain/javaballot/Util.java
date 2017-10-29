package it.onchain.javaballot;

import java.math.BigInteger;

import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;

public class Util {

	public static Uint8 uint8(long val) {
		return new Uint8(val);
	}
	
	public static Uint256 uint256(BigInteger val) {
		return new Uint256(val);
	}
}
