package org.tldgen.writers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;

import org.apache.commons.io.IOUtils;
import org.tldgen.model.Library;
import org.tldgen.model.Tag;
import org.tldgen.util.ClasspathFileUtils;
import org.tldgen.util.DirectoryUtils;

/**
 * Create HTML Documentation
 * @author carlos
 *
 */
public class HtmlLibraryWriter {

	/** version of the YUI grid CSS file */
	public static final String YUI_VERSION = "2.7.0";
	
	/** the location of the folder where HTML output will be written */
	private String htmlFolder;
	
	private boolean formatOutput = true;
	
	//private static Log log = LogFactory.getLog(HtmlLibraryWriter.class);
	
	/**
	 * 
	 * @param library the library to create HTML documentation
	 * @throws IOException if an error occurred writing HTML file
	 */
	public void writeHtml(Library library) throws IOException {
		DirectoryUtils.createHtmlFolder(htmlFolder);
		createCss(library);
		HtmlIndexWriter indexWriter = new HtmlIndexWriter(htmlFolder + "/index.html");
		indexWriter.setFormatOutput(formatOutput);
		indexWriter.write(library);
		indexWriter.close();
		for (Tag tag: library.getTags()) {
			HtmlTagWriter tagWriter = new HtmlTagWriter(htmlFolder + "/" + tag.getName() + ".html");
			tagWriter.setFormatOutput(formatOutput);
			tagWriter.write(tag, library);
			tagWriter.close();
		}

		if (!library.getFunctions().isEmpty()) {
			HtmlFunctionWriter functionWriter = new HtmlFunctionWriter(htmlFolder + "/functions.html");
			functionWriter.setFormatOutput(formatOutput);
			functionWriter.write(library);
			functionWriter.close();		
		}
	}

	/**
	 * Create the CSS files
	 */
	private void createCss(Library library) throws MalformedURLException, IOException {
		
		// copy YUI CSS file
		String yuiFolder = htmlFolder + "/yui-" + YUI_VERSION;
		new File(yuiFolder).mkdir();
		InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("css/yui-" + YUI_VERSION + "/reset-fonts-grids.css");
		OutputStream output = new FileOutputStream(yuiFolder + "/reset-fonts-grids.css");
		try {
			IOUtils.copy(input, output);
		} finally {
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(input);
		}
		
		// Css file path 
		copy("css/styles.css", htmlFolder + "/styles.css");
		copy("css/required.png", htmlFolder + "/required.png");
		copy("css/rtexprvalue.png", htmlFolder + "/rtexprvalue.png");
		copy("css/deprecated.png", htmlFolder + "/deprecated.png");
		
	}

	private void copy(String source, String dest) throws IOException {
		InputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			inputStream = ClasspathFileUtils.getInputStream(source);
			outputStream = new FileOutputStream(dest);
			IOUtils.copy(inputStream, outputStream);
		} finally {
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(outputStream);
		}
	}

	public void setHtmlFolder(String htmlFolder) {
		this.htmlFolder = htmlFolder;
	}

	public void setFormatOutput(boolean formatOutput) {
		this.formatOutput = formatOutput;
	}
	
}