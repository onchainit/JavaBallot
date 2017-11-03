package it.onchain.javaballot.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

	private BallotService service;

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
