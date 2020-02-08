import java.util.HashSet;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Actions;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainWebcrawlerProgram {
	// count the number of files it created
	public static int count = 0;
	public static ArrayList<String> noPDFList = new ArrayList<String>();
	public static ArrayList<String> PDFList = new ArrayList<String>();
	public static ArrayList<String> webAddress = new ArrayList<String>();

	// class variable
	final static String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
	final static java.util.Random rand = new java.util.Random();
	// consider using a Map<String,Boolean> to say whether the identifier is being
	// used or not
	final static Set<String> identifiers = new HashSet<String>();

	public static void main(String[] args) throws Exception {

		System.out.println("[DBG] Start MainWebcrawlerProgram..");

		MainWebcrawlerProgram prog = new MainWebcrawlerProgram();
		prog.turnOffWarning();

		/**
		 * 1. NCBI
		 */
		/*
		 * String[] keywordListNCBI = { "biofilm" // }; for (String keyword :
		 * keywordListNCBI) { prog.delayQueryExecution(); prog.procNCBI(keyword);
		 * Thread.sleep(1000); }
		 */
		/**
		 * 2. Google Scholar
		 */

		/**
		 * Step 1. find a list of keywords from each XML file
		 */
		// This is the first step
		// get all the file name in the array which names list of files.

		File folder = new File("Data/");
		File[] listOfFiles = folder.listFiles();
		// make it as a function
		ArrayList<String> keywords = saveKeywordsList(listOfFiles);

		String[] keywordList = keywords.toArray(new String[0]);
		String listofName = "listofAllKeywords.txt";
		saveList(keywords, listofName);
		// go through every step to collect information
		for (String keyword : keywordList) {
			try {
				System.out.printf("already handled %d paper names\n", count);
				// keyword = keyword.replaceAll("[^a-zA-Z0-9_-]", " ");
				/*
				 * if(keyword.length() >= 40) { keyword = keyword.substring(0, 20); }
				 */
				// keyword = keyword.substring(0, 15);
				// prog.delayQueryExecution();

				// for windows only
				/*
				 * keyword = keyword.replaceAll("[^a-zA-Z0-9_-]", "");
				 */
				// prog.googleScholar(keyword);
				prog.citeSeerX(keyword);
				// prog.procNCBI(keyword);
				Thread.sleep(1000);
				// this is used for google scholar search that delay 5 minutes for every 5
				// serach in order to avoid
				// google blocks
				/*
				 * if (count % 5 == 0) { System.out.println("[DBG] Take 5 minutes break!!!!!!");
				 * Thread.sleep(5 * 60 * 1000); }
				 */
			} catch (Exception e) {
				System.out.println("Paper name: " + keyword + " is invalid");
			}
		}

		// prog.citeSeerX("mocular");
		count = 0;
		String listofNameWithPDF = "listofNameWithPDF.txt";
		String listofNameWithNoPDF = "listofNameWithNoPDF.txt";
		String listofWebLink = "listofWebLink.txt";
		saveList(noPDFList, listofNameWithNoPDF);
		saveList(PDFList, listofNameWithPDF);
		saveList(webAddress, listofWebLink);
		System.out.println("[DBG] Finished collecting web links");
		// dealing with download
		
		try {
			BufferedReader reader;
			reader = new BufferedReader(new FileReader("listofWebLink.txt"));
			String line = reader.readLine();
			int number = 0;
			while (line != null) {
				System.out.printf("already download %d papers\n", number);
				System.out.println(line);
				String paperName = randomIdentifier();
				downLoadByUrl(line, paperName + ".pdf", "downloadPDF/"); // read next line
				line = reader.readLine();
				number++;
			}
			reader.close();
		} catch (Exception e) {
			System.out.println("download exception：" + e);
			e.printStackTrace();
		}

		System.out.println("[DBG] Done.");

	}

	// method that save the keyword that we didn't find the pdf into a file
	private static void saveList(ArrayList<String> List, String filename) throws IOException {
		FileWriter writer = new FileWriter(filename);
		for (String str : List) {
			writer.write(str + System.lineSeparator());
		}
		writer.close();
	}

	private void turnOffWarning() {
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
	}

	// this is different from the previous two methods since we only go to the first
	// link and download only one
	// pdf, the methods below is not suitable
	void citeSeerX(String keyword) throws IOException {
		try {
			
			WebDriver driver = new HtmlUnitDriver(); // new ChromeDriver();
			// driver.manage().window();
			driver.get("http://citeseer.ist.psu.edu/search?q=" + keyword);
			// we need to find the first href
			String name = driver.findElement(By.cssSelector(".remove.doc_details")).getAttribute("href");
			driver.get(name);
			driver.findElement(
					By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Cached'])[1]/following::img[1]"))
					.click();
			String dirOutputName = "outputCSX/";
			String url = driver.getCurrentUrl();
			System.out.println(url);
			String outputFileName = dirOutputName + keyword + ".pdf";
			webAddress.add(url);
			count++;
			// comment those out and save the list
			// downLoadByUrl(url, keyword + ".pdf", "downloadPDF/");
			/*
			 * ProcessBuilder pb = new ProcessBuilder(); String address = "\"" + url + "\"";
			 * pb.command("curl", "-c", "./cookie", "-s", "-L", address, ">", "/dev/null");
			 * pb.start(); pb.command("curl", "-Lb", "./cookie", url, "-o", outputFileName);
			 * pb.start();
			 */

			PDFList.add(keyword);
			// resetOutputDir("outputCSX/");
		} catch (Exception e) {
			System.out.println(keyword);
			noPDFList.add(keyword);
		}
	}

	// search ext:pdf
	void googleScholar(String keyword) throws IOException {
		/**
		 * Step 1. visit google scholar webpage.
		 */
		// System.setProperty("phantomjs.binary.path",
		// "phantomjs");//"webdriver.chrome.driver", "chromedriver");
		WebDriver driver = new HtmlUnitDriver(); // new PhantomJSDriver(); // new ChromeDriver();
		driver.get("https://scholar.google.com/scholar?q=" + "\"" + keyword + "\"");
		/**
		 * Case: visit and download multiple versions for the same document.
		 */
		// WebElement element = driver.findElement(By.partialLinkText(" versions"));
		// new Actions(driver).moveToElement(element).click().build().perform();

		// driver.findElement(By.partialLinkText(" versions")).click();
		String targetHtmlSource = driver.getPageSource();
		driver.close();

		// String dirOutputName = "outputGScholar/" + keyword.substring(0, 10) + "/";
		String dirOutputName = "outputGScholar/" + keyword;
		// use this so that all the pdf will appear in the same folder
		String fileTargetHtml = dirOutputName + "extractedFilePath.html";
		String fileUrlList = dirOutputName + "list_pdf_url.txt";
		resetOutputDir(dirOutputName);

		/**
		 * Step 2. save a target webpage html.
		 */
		File DestFile;
		FileWriter fileWriter;
		saveTargetHtml(fileTargetHtml, targetHtmlSource);

		/**
		 * Step 3. read a html file, collect pdf url list, and save them as a text file.
		 */
		File input = new File(fileTargetHtml);
		Document doc = Jsoup.parse(input, "UTF-8", "https://scholar.google.com/");
		DestFile = new File(fileUrlList);
		fileWriter = new FileWriter(DestFile);
		BufferedWriter out = new BufferedWriter(fileWriter);

		Elements links = doc.select("a[href]");
		for (Element link : links) {
			// notice that a file is end with the _pdf so that I add it
			if (link.attr("abs:href").contains(".pdf") || link.attr("abs:href").contains("_pdf")
					|| link.attr("abs:href").contains("pdf")) {
				String target = link.attr("abs:href");
				// out.write(target);
				webAddress.add(target);
				// out.newLine();
			}
		}
		out.close();
		fileWriter.close();
		// check if there is pdf link in there, if not save the keywords
		File newFile = new File(fileUrlList);
		if (newFile.length() == 0) {
			noPDFList.add(keyword);
		} else {
			PDFList.add(keyword);
		}
		/**
		 * Step 4. execute curl commands to download pdf files.
		 */
		// dirOutputName = dirOutputName.replaceAll("[^a-zA-Z0-9_-]", "_");
		// savePDF(dirOutputName, fileUrlList);
		count++;
	}

	/*
	 * void citeSeer(String keyword) throws IOException { WebDriver driver = new
	 * HtmlUnitDriver(); // new ChromeDriver(); // driver.manage().window();
	 * driver.get("http://citeseer.ist.psu.edu/search?q=" + keyword);
	 * 
	 * String targetHtmlSource = driver.getPageSource(); }
	 */
	void procNCBI(String keyword) throws IOException {
		String targetBaseURL = "https://www.ncbi.nlm.nih.gov/";
		String db = "pmc";
		String dirOutputName = "outputNCBI/" + keyword;
		String fileTargetHtml = dirOutputName + "extractedFilePath.html";
		String fileUrlList = dirOutputName + "list_pdf_url.txt";

		resetOutputDir(dirOutputName);

		/**
		 * Step 1. visit NCBI webpage.
		 */
		// System.setProperty("webdriver.chrome.driver", "chromedriver");
		WebDriver driver = new HtmlUnitDriver(); // new ChromeDriver();
		// driver.manage().window();
		driver.get(targetBaseURL + db + "/?term=" + keyword);
		String targetHtmlSource = driver.getPageSource();
		driver.close();

		/**
		 * Step 2. save a target webpage html.
		 */
		File DestFile;
		FileWriter fileWriter;
		saveTargetHtml(fileTargetHtml, targetHtmlSource);

		/**
		 * Step 3. read a html file, collect pdf url list, and save them as a text file.
		 */
		File input = new File(fileTargetHtml);
		Document doc = Jsoup.parse(input, "UTF-8", targetBaseURL);
		DestFile = new File(fileUrlList);
		fileWriter = new FileWriter(DestFile);
		BufferedWriter out = new BufferedWriter(fileWriter);

		Elements links = doc.select("a[href]");
		for (Element link : links) {
			if (link.attr("abs:href").contains(".pdf")) {
				String target = link.attr("abs:href");
				webAddress.add(target);
				// out.write(target);
				// out.newLine();
			}
		}
		out.close();
		fileWriter.close();
		// check if there is pdf link in there, if not save the keywords
		File newFile = new File(fileUrlList);
		if (newFile.length() == 0) {
			noPDFList.add(keyword);
		} else {
			PDFList.add(keyword);
		}
		/**
		 * Step 4. execute curl commands to download pdf files.
		 */
		// savePDF(dirOutputName, fileUrlList);
		count++;
	}

	private void resetOutputDir(String dirOutputName) throws IOException {
		Path pathOutput = Paths.get(dirOutputName);
		FileUtils.deleteDirectory(pathOutput.toFile());
		System.out.println("[DBG] " + pathOutput.toAbsolutePath());
		Files.createDirectories(pathOutput);
	}

	private void saveTargetHtml(String fileVisitHtml, String output) throws IOException {
		File DestFile = new File(fileVisitHtml);
		FileWriter fileWriter = new FileWriter(DestFile);
		fileWriter.write(output);
		fileWriter.close();
	}

	/**
	 * 
	 * 1) base url = https://www.ncbi.nlm.nih.gov/pmc/ 2) pdf url =
	 * articles/PMC4187679/pdf/zmr510.pdf 3) pdf file = zmr510.pdf
	 * 
	 * curl -c ./cookie -s -L
	 * "https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4187679/pdf/zmr510.pdf" >
	 * /dev/null curl -Lb ./cookie
	 * "https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4187679/pdf/zmr510.pdf" -o
	 * $PDF_FILE
	 */
	private void savePDF(String dirOutputName, String fileUrlList) throws FileNotFoundException, IOException {
		File input;
		ProcessBuilder pb = new ProcessBuilder();
		input = new File(fileUrlList);
		FileReader fileReader = new FileReader(input);

		BufferedReader bufferedReader = new BufferedReader(fileReader);
		List<String> lines = new ArrayList<String>();
		String line = null;

		while ((line = bufferedReader.readLine()) != null) {
			lines.add(line);
		}
		for (int i = 0; i < lines.size(); i++) {
			String pdfFileName = dirOutputName + "PDF_FILE" + Integer.toString(i) + ".pdf";
			pdfFileName = pdfFileName.replace(" ", "_");
			String address = "\"" + lines.get(i) + "\"";
			// count++;
			pb.command("curl", "-c", "./cookie", "-s", "-L", address, ">", "/dev/null");
			pb.start();
			pb.command("curl", "-Lb", "./cookie", lines.get(i), "-o", pdfFileName);
			pb.start();
			/*
			 * pb.command("curl", "-o", pdfFileName, lines.get(i)); pb.start();
			 */

		}
		bufferedReader.close();
	}

	private void delayQueryExecution() throws Exception {
		int[] intervalArray = { 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60 };
		int mili = 1000;
		Random rand = new Random();
		// get the random index number from the array
		int randNum = rand.nextInt(intervalArray.length);
		int delayInterval = intervalArray[randNum] * mili;
		System.out.println("[DBG] Delay Interval: " + (delayInterval / mili + " (sec)"));
		Thread.sleep(delayInterval);
	}

	private static ArrayList<String> saveKeywordsList(File[] listOfFiles) {
		ArrayList<String> keywords = new ArrayList<>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && !listOfFiles[i].getName().contentEquals(".DS_Store")) {
				// using try catch to skip the invalid xml.
				try {
					// First read a file for" test.
					File fXmlFile = new File("Data/" + listOfFiles[i].getName());
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					dbFactory.setNamespaceAware(true);
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
					// Either way will works
					// NodeList nList = doc.getElementsByTagName("Article");
					// System.out.println(nList.item(0).getFirstChild().getTextContent());
					NodeList nList = doc.getElementsByTagName("Title");
					String name = nList.item(0).getTextContent();
					keywords.add(name);
				} catch (Exception e) {

				}
			}
		}
		return keywords;
	}

	// new download engine that is different from curl
	private static void downLoadByUrl(String urlStr, String fileName, String savePath) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// set time limit as three seconds
		conn.setConnectTimeout(5 * 1000);
		// prevent error 403
		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		// get input stream
		InputStream inputStream = conn.getInputStream();
		// get array
		byte[] getData = readInputStream(inputStream);
		// save the position of file
		File saveDir = new File(savePath);
		if (!saveDir.exists()) {
			saveDir.mkdir();
		}
		File file = new File(saveDir + File.separator + fileName);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(getData);
		if (fos != null) {
			fos.close();
		}
		if (inputStream != null) {
			inputStream.close();
		}
		System.out.println("info:" + url + " download success");
	}

	private static byte[] readInputStream(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		bos.close();
		return bos.toByteArray();
	}

	// generate random name
	private static String randomIdentifier() {
		StringBuilder builder = new StringBuilder();
		while (builder.toString().length() == 0) {
			int length = rand.nextInt(5) + 5;
			for (int i = 0; i < length; i++) {
				builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
			}
			if (identifiers.contains(builder.toString())) {
				builder = new StringBuilder();
			}
		}
		return builder.toString();
	}
}
