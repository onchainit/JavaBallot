package it.onchain.javaballot.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path("/api")
public class BallotResource {

	@POST
	@Path("/loadWallet")
	@Consumes("multipart/form-data")
	public Response loadWallet(MultipartFormDataInput input) throws Exception {
		Map<String, List<InputPart>> formParts = input.getFormDataMap();
		File f = storeWalletFile(formParts.get("file").iterator().next());
		String password = formParts.get("password").iterator().next().getBodyAsString();
		BallotService service = new BallotService(f, password);
		
//		System.out.println("pwd: " + formParts.get("password").iterator().next().getBodyAsString());
		return Response.status(200).entity("Read wallet of public address " + service.getWalletPublicAddress()).build();
	}
	
	@POST
	@Path("/deploy")
	@Consumes("multipart/form-data")
	public Response deploy(MultipartFormDataInput input) throws Exception {
		Map<String, List<InputPart>> formParts = input.getFormDataMap();
		File f = storeWalletFile(formParts.get("file").iterator().next());
		String password = formParts.get("password").iterator().next().getBodyAsString();
		BallotService service = new BallotService(f, password);
		
		int proposals = Integer.valueOf(formParts.get("proposals").iterator().next().getBodyAsString());
		String secondBeneficiary = formParts.get("secondBeneficiary").iterator().next().getBodyAsString();
		int perc = Integer.valueOf(formParts.get("perc").iterator().next().getBodyAsString());
		long capEth = Integer.valueOf(formParts.get("capEth").iterator().next().getBodyAsString());
		BigInteger cap = new BigInteger("1000000000000000000").multiply(BigInteger.valueOf(capEth));
		String contractAddress = service.deploy(proposals, secondBeneficiary, perc, cap);
		return Response.status(200).entity("Contract deployed at address " + contractAddress).build();
	}
	
	@POST
	@Path("/vote")
	@Consumes("multipart/form-data")
	public Response vote(MultipartFormDataInput input) throws Exception {
		Map<String, List<InputPart>> formParts = input.getFormDataMap();
		File f = storeWalletFile(formParts.get("file").iterator().next());
		String password = formParts.get("password").iterator().next().getBodyAsString();
		BallotService service = new BallotService(f, password);
		
		String contractAddress = formParts.get("contractAddress").iterator().next().getBodyAsString();
		int proposal = Integer.valueOf(formParts.get("proposal").iterator().next().getBodyAsString());
		String amount = formParts.get("amount").iterator().next().getBodyAsString();
		
		service.load(contractAddress);
		BigInteger gasUsed = service.vote(proposal, new BigInteger(amount));
		
		return Response.status(200).entity("Vote, used gas: " + gasUsed).build();
	}
	
	private File storeWalletFile(InputPart inputPart) throws IOException {
		MultivaluedMap<String, String> headers = inputPart.getHeaders();
		String fileName = parseFileName(headers);
		InputStream istream = inputPart.getBody(InputStream.class, null);
		File f = new File(System.getProperty("java.io.tmpdir"), fileName);
		saveFile(istream, f);
		return f;
	}

	// Parse Content-Disposition header to get the original file name
	private String parseFileName(MultivaluedMap<String, String> headers) {
		String[] contentDispositionHeader = headers.getFirst("Content-Disposition").split(";");
		for (String name : contentDispositionHeader) {
			if ((name.trim().startsWith("filename"))) {
				String[] tmp = name.split("=");
				String fileName = tmp[1].trim().replaceAll("\"", "");
				return fileName;
			}
		}
		return "randomName";
	}

	private void saveFile(InputStream uploadedInputStream, File serverLocation) {
		try {
			OutputStream outpuStream = new FileOutputStream(serverLocation);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				outpuStream.write(bytes, 0, read);
			}
			outpuStream.flush();
			outpuStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
