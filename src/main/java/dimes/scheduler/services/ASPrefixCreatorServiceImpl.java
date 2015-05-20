/**
 * 
 */
package dimes.scheduler.services;

import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Service for creating the WHO-IS / ROUTEVIEWS AS Prefix tables.<br>
 * Based on running Linux command line (apparently, it is the easiest and quickest way to do it).
 * 
 * @author Ido
 * @see {@link EntityCreatorService}
 */

//@Service("asPrefixCreatorService")
class ASPrefixCreatorServiceImpl implements EntityCreatorService {

	private final Logger logger = LoggerFactory
			.getLogger(ASPrefixCreatorServiceImpl.class);

	@Override
	public void createWeeklyEntity(int year, int week) {
		
		logger.info("Creating RV and Who-IS tables. Year -> {} Week -> {}", year, week);
		
		// Getting AS Prefix list from RouteViews: 
//		List<String> rvList = createRVList(year, week);	
//		logger.info("Number of RV Results : {} ", rvList.size());
		
		// For who-is : need to check if the date is the current one
		LocalDate now = new LocalDate();
		
		if(now.getYear() == year && now.getWeekOfWeekyear() == week) {
			
			// Downloading and unzipping WHOIS files
//			getWhoIsFiles();
		
			// GettingAS Prefix list from WHOIS files
			List<String> whoIsList = createWhoIsList();
			logger.info("Number of WHOIS Results : {} ", whoIsList.size());

		}
				
		
		//TODO - save results to DB
		
		
	}

	private List<String> createWhoIsList() throws IOException {
		
		try (DirectoryStream<Path> dbFilesStream = Files
				.newDirectoryStream(Paths.get("/tmp"), "*db*")) {
			for (Path dbFilePath : dbFilesStream) {
				Files.rea
			}
		}	
		return null;
	}

	/*
	 * Downloading and extracting who-is files into /tmp directory. 
	 */
	private void getWhoIsFiles() {

		ProcessBuilder radbDownloadPB = new ProcessBuilder("wget", "--quiet",
				"--directory-prefix=/tmp",
				"ftp://ftp.radb.net/radb/dbase/*.db.gz");
		ProcessBuilder ripeDownloadPB = new ProcessBuilder("wget", "--quiet",
				"--directory-prefix=/tmp",
				"ftp://ftp.ripe.net/ripe/dbase/split/ripe.db.route.gz");

		radbDownloadPB.inheritIO();
		ripeDownloadPB.inheritIO();

		try {

			int radbProcessExitValue = radbDownloadPB.start().waitFor();
			logger.info(
					"Finished downloading radb DB files. Process exit value : {}",
					radbProcessExitValue);

			int ripeProcessExitValue = ripeDownloadPB.start().waitFor();
			logger.info(
					"Finished downloading ripe DB file. Process exit value : {}",
					ripeProcessExitValue);

			// Unzipping the db files - need to process each file separately
			// since java can't do the globing of '*'
			try (DirectoryStream<Path> zippedFilesStream = Files
					.newDirectoryStream(Paths.get("/tmp"), "*db*.gz")) {
				for (Path zippedFilePath : zippedFilesStream) {
					ProcessBuilder gunzipPB = new ProcessBuilder("gunzip",
							zippedFilePath.toString());
					gunzipPB.inheritIO();
					int gunzipProcessExitValue = gunzipPB.start().waitFor();
					logger.debug(
							"Finished unzipping file {}. Process exit value : {}",
							zippedFilePath, gunzipProcessExitValue);
				}
			}

			logger.info("Finished unzipping ripe and radb DB file");

		} catch (InterruptedException | IOException e) {
			throw new RuntimeException("Service "
					+ this.getClass().getSimpleName()
					+ " could not finish creating WHOIS AS Prefix Table", e);
		}

	}

//	private void crateWhoIsList(int year, int week) {
//		// Creating the date id for the DB file to be download from RV
//		String dateOfFileStr = new LocalDate().withYear(year)
//				.withWeekOfWeekyear(week).withDayOfWeek(1)
//				.toString(ISODateTimeFormat.basicDate()).substring(2);
//		
//		// The DB file name in the RADB site standard
//		String radbFileNameUnzipped = "radb.db." + dateOfFileStr;
//		String radbFileNameZipped = radbFileNameUnzipped + ".gz";
//		// The full combined web address to download the relevant file from RADB
//		String radbFileFullAddress = "ftp://ftp.radb.net/radb/dbase/archive/" + radbFileNameZipped;
//		// Shell script for downloading and unzip file
//		String shellScriptStr = "curl " + radbFileFullAddress + " | gunzip";
//		// The file to which the final aggregated data will be saved. To be deleted in the end of process. 
//		File unzippedRadbFile = new File(radbFileNameUnzipped);
//		// The process builder to run the command through bash
//
//		ProcessBuilder radbFilePB = new ProcessBuilder("/bin/sh", "-c",
//				shellScriptStr);
//		// I/O redirection : Errors to the stdout. Data to the file.
//		radbFilePB.redirectErrorStream(true);
//		radbFilePB.redirectOutput(unzippedRadbFile);
//
//		try {
//			logger.info("Downloading and unzip RADB file -> {}",
//					radbFileFullAddress);
//			int processExitValue = radbFilePB.start().waitFor();
//			logger.info("Finished processing RADB file. Process exit value : {}", processExitValue);
//			
////			return Files.readAllLines(aggregatedRVFile.toPath(),
////					Charset.defaultCharset());
//			
//		} catch (IOException | InterruptedException e) {
//			throw new RuntimeException("Service " + this.getClass().getSimpleName() + " could not finish processing RADB file", e);
//		} finally {
//			try {
//				// Deleting the download file
// 				Files.deleteIfExists(unzippedRadbFile.toPath());
//			} catch (IOException e) {
//				throw new RuntimeException("File " + unzippedRadbFile + " caused I/O problem", e);
//			}
//		}
//
//	}

	/*
	 * Downloading and processing the routeviews data. Returns a list with IP Prefix - AS number pair in each line.
	 */
	private List<String> createRVList(int year, int week) {
		
		// Creating the date id for the rib file to be download from RV
		String dateOfRIBFileStr = new LocalDate().withYear(year)
				.withWeekOfWeekyear(week).withDayOfWeek(1)
				.toString(ISODateTimeFormat.basicDate());
		// The rib file name is the RV site standard
		String ribFileNameUnzipped = "rib." + dateOfRIBFileStr + ".0000";
		String ribFileNameZipped = ribFileNameUnzipped + ".bz2";
		// The full combined web address to download the relevant file from RV
		String ribFileFullAddress = "ftp://archive.routeviews.org/bgpdata/" + year
				+ "." + dateOfRIBFileStr.substring(4, 6) + "/RIBS/"
				+ ribFileNameZipped;
		
		/*
		 *  The full command to be ran in order to download the file, extract him and run him through 2 RV processing perl scripts (which were download from RV web site):
		 *  1. zebra-dump-parser.pl : converting the binary rib file into text based file
		 *  2. aggregate-by-asn.pl : half baked script which make aggregation of the data. as preliminary the data in the file is being sorted and duplication are being removed (uniq)  
		 */
		String shellScriptStr = "curl "
				+ ribFileFullAddress
				+ " | bunzip2 -c | "
				+ getClass().getResource("/zebra-dump-parser.pl").toString()
						.substring(5)
				+ " | sort |"
				+ " uniq | "
				+ getClass().getResource("/aggregate-by-asn.pl").toString()
						.substring(5);
		// The file to which the final aggregated data will be saved. To be deleted in the end of process. 
		File aggregatedRVFile = new File(ribFileNameUnzipped + ".aggregated");
		// The process builder to run the command through bash
		ProcessBuilder rvFilePB = new ProcessBuilder("/bin/sh", "-c",
				shellScriptStr);
		// I/O redirection : Errors to the stdout. Data to the file.
		rvFilePB.redirectErrorStream();
		rvFilePB.redirectOutput(aggregatedRVFile);

		try {
			logger.info("Downloading and processing RouteView file -> {}",
					ribFileNameZipped);
			int rvProcessExitValue = rvFilePB.start().waitFor();
			logger.info("Finished processing RouteView file. Process exit value : {}", rvProcessExitValue);
			
			return Files.readAllLines(aggregatedRVFile.toPath(),
					Charset.defaultCharset());
			
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException("Service " + this.getClass().getSimpleName() + " could not finish processing RV file", e);
		} finally {
			try {
				// Deleting the download file
 				Files.deleteIfExists(aggregatedRVFile.toPath());
			} catch (IOException e) {
				throw new RuntimeException("File " + aggregatedRVFile + " caused I/O problem", e);
			}
		}

	}
	private void insertDataIntoDB(List<String> prefixesList) {
		// TODO Auto-generated method stub
		
	}
	


}