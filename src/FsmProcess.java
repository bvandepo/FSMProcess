/***
 * FSM
 @author Bertrand VANDEPORTAELE LAAS/CNRS 2016
 *  ***/

import gnu.getopt.Getopt;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.ScrollPane;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
 
// sudo apt-get install libgetopt-java
//et ajouter dans referenced Libraries /usr/share/gnu-getopt-1.0.13.jar

//WHEN CHANGES ARE MADE TO THE LEXER OR PARSER GRAMMAR, CALL ant.sh script and 
//in eclipse press Project -> clean. Then go to Package Explorer->Fsm->fsm  (no its another location....) and press F5 to refresh

//To generate the jar using eclipse:
//F5 to refresh eclipse project
//File->export->Java->Runnable JAR file
//Launch configuration: chose FsmProcess - Fsm
//Export destination /home/bvandepo/antlr/fsmjar/FsmProcess.jar
//Package required libraries into generated JAR
//Finish
//to save the export info, click save as ANT script and browse: /home/bvandepo/antlr/fsm/exportjar.xml
//click ok if warnings
//to run: cd ~/antlr/fsmjar
//        java -jar FsmProcess.jar train.fsm 

//CTRL-SHIFT 0 : automatisation des inclusions
//CTRL-SHIFT F : indentation et réorganisation

//Java->Code style->Formatter -> Edit 
//Line Wrapping->Maximum line width = 140
//profile name: Eclipse [bvdp]

// TODO: demote to signal de bus définis par pragma ou alors pragma pour définir signaux

// TODO: inclure numero de version dans l'affichage
// TODO: est ce que j'ai le droit d'integrer le jar d'antlr dans mon
// jar?
// TODO: quest ce que je dois afficher relativement à l'utilisation de
// GetOpt et antlr
// TODO: rendre la generation de la doc sous windows possible / \
// TODO: insérer des \n ou \r en fin de ligne selon l'os

// TODO : inout????
// TODO check to et downto que les bornes sont compatibles

//TODO: merger inputs et outputs; ca permettrait de résoudre le pb:
// bizarre sans ce pragma, probleme... dans filteredge...
//#pragma_vhdl_allow_automatic_buffering
//%S,delay_ended=COUNT_EQUAL;  
//%R,delay_ended=srazcpt; 

//TODO: pour les AMZI, ajouter une commande qui permet de les définir active à 0... rien ne change dans le code sauf ca...

//TODO; ajouter gestion des GENERIC et aussi dans testbench... (avec un pragma dans le fsm

//TODO: ajouter gestion de multiples testbenches

//TODO: ajouter pragma pour imposer le Statenumber d'un etat d'apres son nom
//TODO: comprendre pourquoi antlr ne genere par le fichier FsmParser.java  mais uniquement le class...

//TODO: gérer que l'on puisse ajouter des commentaires dans les pragmas entity
//TODO: parser le pragma entity pour detecter E/S (ou alors add/remove..) + bus

// TODO: add pragma for dot to colorize some node or arcs and make
// animations in gif

// TODO: add xilinx synthesis in command line to test the generated vhd
// files automatically

// TODO: catch error from the parser:
// and stop here if error!!!!

// TODO: definition de constante (bit/valeurs)

// DOC DOT: http://www.graphviz.org/Documentation/dotguide.pdf
// graphviz.org/Documentation.php
// http://www.graphviz.org/doc/info/lang.html
// http://www.tonyballantyne.com/graphs.html

// TODO: pour les comparaisons de valeurs sur des bus d'entrée, utiliser
// une syntaxe particulière (ajouter à la grammaire) puis générer dans
// le vhdl un signal qui prendra la valeur 1 quand la condition est
// vraie

// TODO; regler les problèmes de fichier unix/windows pour ne pas avoir
// à utiliser unix2dos

// HARD TODOS:

// TODO: pragma pour enlever des entrees/sorties, changer leur taille

// TODO: Ajouter la notion d'overide: pour regler ls AMZI à 1 par
// défaut, les AMZE en AMUE et donner des valeurs d'init aux sorties M
// et
// pour spécifier que des E ou S sont des bus de n bits

// TODO: que faire quand une action sur état est incompatible avec une
// action sur une transition émanant de cet état??? -> afficher erreur
// ou warning
// -->in this release, priority= regularly > reset transition >
// transition > state

// TODO DOC: dans la doc sur les (reset) transitions, bien indiquer que
// l'ordre de définition ne définit en rien les priorités (pour les
// actions!!!!!).
// Indique que l'on peut utiliser les priorités pour simplifier le
// modele, par exemple priorité 1 pour une condition complique et
// condition 1 pour une moins prioritaire qui sera effective que si la +
// prioritaire ne l'est pas

// TODO: add variable bus size to inputs and outputs in the grammar...
// hard... : have multi bits outputs (size could be automatically
// determined
// or given in code
// TODO: ajouter automatiquement les définition de port par pragma 
// aux listes Inputs/outputs!!!!!!->
// ajouter des champs pour les vecteurs
// si le signal existe dejà, updater le caractère vecteur et la taille
// si pas d'action associée, vérifier qu'il n'y a pas de code vhdl
// caculé par fsm ou alors juste <='0' ?
// TODO: check the file exists BEFORE REMOVING:
// TODO: gestion des erreurs de parsing: // if (false) {
// System.out.println(pragma);
//
// // DEBUG harder way
// Token semi = ctx.getStop();
// int i = semi.getTokenIndex();
// System.out.print("detected a Pragma_directive at token ");
// System.out.println(i);
//
// // intégralité des tokens
// // System.out.println(tokens.getText());
// // the pragma token
// System.out.println(tokens.get(i));
//
// // to localize in the input file for errors
// System.out.println(tokens.get(i).getLine());
// System.out.println(tokens.get(i).getCharPositionInLine());
//
// // whats inside token
// System.out.println(tokens.get(i).getText());
//
// // whats inside token without #pragma{ and #pragma}
// pragma = tokens.get(i).getText();
// }

//////////////////////////////////////////////////////////
// doc:
// http://codes-sources.commentcamarche.net/faq/360-swinguez-jframe-jpanel-jcomponent-layoutmanager-borderlayout
// get from:
// http://stackoverflow.com/questions/14353302/displaying-image-in-java
// http://stackoverflow.com/questions/15685502/jframe-mouse-click-using-jcomponent-and-mouselistener
// http://chortle.ccsu.edu/java5/notes/chap56/ch56_11.html
// Panel:
// https://docs.oracle.com/javase/7/docs/api/javax/swing/JScrollPane.html
// voir:
// http://docs.oracle.com/javase/tutorial/uiswing/examples/components/index.html#SplitPaneDemo
// exemples SplitPaneDemo [Launch] Split Pane Demo Project
// SplitPaneDemo.java image files How to Use Split Panes
// SplitPaneDemo2 [Launch] Split Pane 2 Demo Project SplitPaneDemo2.java
// image files How to Use Split Panes
// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
// FILE CHANGE DETECTION FOR INTERACTIVE MODE got from:
// http://stackoverflow.com/questions/16251273/can-i-watch-for-single-file-change-with-watchservice-not-the-whole-directory

// //////////////////////////////////////////////////////////////////
// //////////////////////////////////////////////////////////////////
// doc de ArrayList :
// http://imss-www.upmf-grenoble.fr/prevert/Prog/Java/Conteneurs/ArrayList.html
// et
// https://openclassrooms.com/courses/apprenez-a-programmer-en-java/les-collections-d-objets

// hash map:
// http://stackoverflow.com/questions/14836870/java-arraylist-adding-object-using-string-name
// Map<String, Integer> map = new HashMap<String, Integer>();
// which will contain the mapping of itemName to respective Item, and then
// getting the Item for a particular itemName is as simple as
// map.get(itemName).
// exemple: http://beginnersbook.com/2013/12/hashmap-in-java-with-example/
// doc complete:
// https://docs.oracle.com/javase/7/docs/api/java/util/HashMap.html

//////GETOPT
// look at~/antlr/getopts/gnu/getopt/Getopt.java
// https://en.wikipedia.org/wiki/Getopt
// There is no implementation of getopt in the Java standard library.
// Several open source modules exist, including gnu.getopt.Getopt, which
// is ported from GNU getopt,[3] and Apache Commons CLI.[4]

// ftp://ftp.urbanophile.com/pub/arenn/software/sources/java-getopt-1.0.13.tar.gz
// http://pkgs.fedoraproject.org/repo/pkgs/gnu-getopt/java-getopt-1.0.13.tar.gz/46336d9bc055900f0320e5c378d7bfb2/

// http://www.urbanophile.com/arenn/hacking/getopt/
// http://www.urbanophile.com/arenn/hacking/download.html

// https://www.gnu.org/software/gnuprologjava/api/gnu/getopt/Getopt.html

// http://www.java2s.com/Code/Java/Development-Class/HandlesprogramargumentslikeUnixgetopt.htm
// moins bien

//////////////////////////////////////////////////////////

public class FsmProcess {
	public static JFrame frame = null;
	public static JPanel panel;
	public static JLabel labelResultsCompile;
	public static JLabel labelDisplayImage = null;
	public static FlowLayout flayout = null;
	public static ScrollPane scrollPanel;
	public static BufferedImage resizedImage;
	public static Graphics2D g;
	public static ImageIcon icon;
	public static BufferedImage img;

	public static Boolean autoResize = false;
	public static int imageSizeWidth = 0;
	public static int imageSizeHeight = 0;
	public static double percentWidth = 0;
	public static double percentHeight = 0;
	public static int xscroll = 0;
	public static int yscroll = 0;

	// static int WIN_WIDTH = 1685;
	// static int WIN_HEIGHT = 1000;
	// static int WIN_ORG_X = -800;
	// static int WIN_ORG_Y = 0;
	// monoscreen 13"
	static int WIN_ORG_X = 0;
	static int WIN_ORG_Y = 50;
	static int WIN_WIDTH = 1285;
	static int WIN_HEIGHT = 750;
	private static FileWatcher watcher;
	// number of different text files to display on the gui
	static int NB_DISPLAY_TYPES = 6;
	static int displayState = 0; // Dot image view by default

	// ////////////////////////////////////////////////////////////////////
	public FsmProcess() {
	}

	// ////////////////////////////////////////////////////////////////////
	static void ImageUpdate() {
		String fileName = imageFileName;
		try {
			img = ImageIO.read(new File(fileName));
		} catch (IOException e) {
			System.err.println(e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Update();
	}

	// ////////////////////////////////////////////////////////////////////
	static void Update() {
		StringBuilder buf = bufVhdl;
		labelResultsCompile.setText(bufLogFinal.toString());
		int IMG_WIDTH = WIN_WIDTH - 10;
		int IMG_HEIGHT = WIN_HEIGHT - 80;
		scrollPanel.setSize(WIN_WIDTH - 30, WIN_HEIGHT - 80);
		StringBuilder text = new StringBuilder();
		switch (displayState) {
		case 0: // display the dot image
			imageSizeWidth = img.getWidth();
			imageSizeHeight = img.getHeight();
			if (autoResize) {
				IMG_WIDTH = WIN_WIDTH - 30 - 5;
				IMG_HEIGHT = WIN_HEIGHT - 80;
				double aspectRatioOrg = (double) img.getWidth() / (double) img.getHeight();
				double aspectRatioDest = (double) IMG_WIDTH / (double) IMG_HEIGHT;
				int IMG_WIDTH_dest = IMG_WIDTH;
				int IMG_HEIGHT_dest = IMG_HEIGHT;
				if (aspectRatioOrg > aspectRatioDest)
					IMG_HEIGHT_dest = (int) ((double) IMG_WIDTH_dest / aspectRatioOrg);
				else
					IMG_WIDTH_dest = (int) ((double) IMG_HEIGHT_dest * aspectRatioOrg);
				imageSizeWidth = IMG_WIDTH_dest;
				imageSizeHeight = IMG_HEIGHT_dest;
				resizedImage = new BufferedImage(IMG_WIDTH_dest, IMG_HEIGHT_dest, img.getType());
				g = resizedImage.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
				g.drawImage(img, 0, 0, IMG_WIDTH_dest, IMG_HEIGHT_dest, 0, 0, img.getWidth(), img.getHeight(), null);
				g.dispose();
				icon = new ImageIcon(resizedImage);
			} else {
				icon = new ImageIcon(img);
				xscroll = (int) (percentWidth * img.getWidth() / 100.);
				yscroll = (int) (percentHeight * img.getHeight() / 100.);
				// without this, the zoomed zone has its upper left corner at
				// the
				// click position, with this, the zoomed zone has its upper left
				// corner at the central position
				xscroll -= IMG_WIDTH / 2;
				yscroll -= IMG_HEIGHT / 2;
				// System.out.print("xscroll: ");
				// System.out.print(xscroll);
				// System.out.print("   yscroll: ");
				// System.out.println(yscroll);
			}
			labelDisplayImage.setIcon(icon);
			labelDisplayImage.setText(null);
			labelDisplayImage.setHorizontalAlignment(SwingConstants.LEFT);
			labelDisplayImage.setVerticalAlignment(SwingConstants.TOP);
			// probleme au moment ou setScrollPosition s'execute, le scroll est
			// encore desactivé car on est en mode autoresize.., il faut
			// appeler le p.revalidate() avant pour que le scroller prenne
			// la bonne taille
			scrollPanel.revalidate();
			if (!autoResize) {
				scrollPanel.setScrollPosition(xscroll, yscroll);
			}
			frame.setTitle(fsm.name + ".dot");
			break;
		case 1:// display log text
			text.append("<html>");
			text.append("<font color=#ff0000>");
			text.append(bufLogError.toString().replace("\n", "<br>"));
			text.append("</font><br><font color=#0000ff>");
			text.append(bufLogWarning.toString().replace("\n", "<br>"));
			text.append("</font><br><font color=#000000>");
			text.append(bufLogInfo.toString().replace("\n", "<br>"));
			text.append("</font>");
			frame.setTitle(fsm.name + ".log");
			break;
		case 2: // display vhdl text
			buf = bufVhdl;
			frame.setTitle(fsm.name + ".vhd");
			break;
		case 3: // display vhdl text
			buf = bufVhdlPortMap;
			frame.setTitle(fsm.name + "_portmap.vhd");
			break;
		case 4: // display vhdl text
			buf = bufVhdlPack;
			frame.setTitle(fsm.name + "_pack.vhd");
			break;
		case 5: // display vhdl text
			buf = bufVhdlTb;
			frame.setTitle(fsm.name + "_tb.vhd");
			break;
		default: // display text
			break;
		}
		if (displayState > 1) { // VHDL
			int lineNumber = 0;
			text.append("<html>");
			// replace special html caracters. the & has to be processed firstly
			// so the added one used to escape the special caracters won't be
			// replaced...
			// warning: this methods don't preserve the number of spaces
			String textVhdl = buf.toString().replace("&", "&#38;").replace("<", "&#60;").replace(">", "&#62;").replace("\"", "&#34;")
					.replace("\t", "&#160;&#160;&#160;&#160;");
			for (String v : textVhdl.split("\n")) {
				Boolean lineCommented = false;
				lineNumber++;
				text.append(String.format("%5s : ", Integer.toString(lineNumber)));
				for (String w : v.split(" ")) {
					if (lineCommented)
						text.append(w);
					else if ((w.length() >= 2) && w.substring(0, 2).equals("--")) {
						text.append("<font color=#00ff00>");
						text.append(w);
						lineCommented = true;
					} else {
						Boolean isVhdlReservedWord = false;
						for (int i = 0; i < fsm.forbiddenNamesVHDL.size(); i++) {
							if (w.compareToIgnoreCase(fsm.forbiddenNamesVHDL.get(i)) == 0) {
								isVhdlReservedWord = true;
								break;
							}
						}
						if (isVhdlReservedWord) {
							text.append("<font color=#ff0000>");
							text.append(w);
							text.append("</font>");
						} else {
							text.append(w);
						}
					}
					text.append(" ");
				}
				if (lineCommented)
					text.append("</font>");
				text.append("<br>");
			}
		}
		if (displayState != 0) { // LOG OR VHDL
			labelDisplayImage.setIcon(null);
			labelDisplayImage.setText(text.toString());
			labelDisplayImage.setHorizontalAlignment(SwingConstants.LEFT);
			labelDisplayImage.setVerticalAlignment(SwingConstants.TOP);
			scrollPanel.revalidate();
		}
	}

	// /////////////////////////////////////////////////////////
	private static void createAndShowGUI() {
		// Create and set up the window.
		frame = new JFrame(fsm.name);
		frame.setSize(WIN_WIDTH, WIN_HEIGHT);
		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				Component c = (Component) evt.getSource();
				WIN_WIDTH = c.getWidth();
				WIN_HEIGHT = c.getHeight();
				System.out.println(WIN_WIDTH);
				System.out.println(WIN_HEIGHT);
				Update(); // redraw the GUI, no reloading of the image
			}
		});
		// TO move it to the screen top left position of the window
		frame.setLocation(WIN_ORG_X, WIN_ORG_Y);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setLayout(new FlowLayout()); // set the layout manager
		labelResultsCompile = new JLabel(bufLogFinal.toString());
		labelResultsCompile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				displayState = (displayState + 1) % NB_DISPLAY_TYPES;
				Update(); // redraw the GUI, no reloading of the image
			}
		});
		panel = new JPanel(); // Panel
		panel.add(labelResultsCompile); // add the label to the JFrame
		flayout = new FlowLayout(FlowLayout.CENTER);
		panel.setLayout(flayout); // attache le layoutManager au panel
		frame.setContentPane(panel);
		scrollPanel = new ScrollPane();
		scrollPanel.setSize(WIN_WIDTH - 30, WIN_HEIGHT - 80);
		panel.add(scrollPanel);
		// frame.setSize(WIN_WIDTH, WIN_HEIGHT);
		labelDisplayImage = new JLabel();
		labelDisplayImage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (displayState == 0) {
					autoResize = !autoResize;
					percentWidth = 100.0 * e.getX() / (double) imageSizeWidth;
					percentHeight = 100.0 * e.getY() / (double) imageSizeHeight;
					// System.out.print("percentWidth: ");
					// System.out.print(percentWidth);
					// System.out.print("   percentHeight: ");
					// System.out.println(percentHeight);
				} else {
					displayState = (displayState + 1) % NB_DISPLAY_TYPES;
				}
				Update(); // redraw the GUI, no reloading of the image
			}
		});
		scrollPanel.add(labelDisplayImage);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ImageUpdate(); // redraw the GUI
		// Display the window.
		// frame.pack(); //do this only to redispatch the element in the JFframe
		frame.setVisible(true);
		// setup the FileWatcher to detect changes on the fsm file
		File f = new File(fsmInputName);
		watcher = new FileWatcher(f);
		watcher.start();
	}

	// ////////////////////////////////////////////////////////
	public static Boolean optionsIgnoreErrors = false;
	public static Boolean optionsDisplayResultImage = false;
	public static Boolean optionsComputeResultImage = false;
	public static Boolean optionsRealtime = false;
	public static String imageFileName;

	// ////////////////////////////////////////////////////////
	static public class FileWatcher extends Thread {
		private final File file;
		private AtomicBoolean stop = new AtomicBoolean(false);

		public FileWatcher(File file) {
			this.file = file;
			System.out.print("LOOKING FOR CHANGE ON FILE: ");
			System.out.println(file);
		}

		public boolean isStopped() {
			return stop.get();
		}

		public void stopThread() {
			stop.set(true);
		}

		public void doOnChange() {
			// Do whatever action you want here
			System.out.print("FILE ");
			System.out.println(file);
			System.out.print(" CHANGED!!!!!!");
			GenerateFiles(fsmInputName);
			// Ask the swing GUI to display the new image
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					ImageUpdate();
				}
			});
		}

		@Override
		public void run() {
			try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
				// System.out.println(" WatchService watcher = FileSystems.getDefault().newWatchService()");
				Path path = file.toPath().getParent();
				// TODO: detect if file is relative!!!
				// if (path==null) //Probleme si nom de fichier sans le dossier
				// devant...
				System.out.print("path: ");
				System.out.println(path);
				path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
				while (!isStopped()) {
					WatchKey key;
					try {
						key = watcher.poll(25, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						System.err.println(e);
						return;
					}
					if (key == null) {
						Thread.yield();
						continue;
					}
					for (WatchEvent<?> event : key.pollEvents()) {
						WatchEvent.Kind<?> kind = event.kind();
						@SuppressWarnings("unchecked")
						WatchEvent<Path> ev = (WatchEvent<Path>) event;
						Path filename = ev.context();
						if (kind == StandardWatchEventKinds.OVERFLOW) {
							Thread.yield();
							continue;
						} else if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY && filename.toString().equals(file.getName())) {
							doOnChange();
						}
						boolean valid = key.reset();
						if (!valid) {
							break;
						}
					}
					Thread.yield();
				}
			} catch (Throwable e) {
				System.err.println(e);
				// Log or rethrow the error
			}
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
	static String softNameAndVersion = "FsmProcess V 1.0";
	static String fsmInputName;
	static StringBuilder bufDot;
	static StringBuilder bufVhdl;
	static StringBuilder bufVhdlTb;
	static StringBuilder bufVhdlPortMap;
	static StringBuilder bufVhdlPack;
	static StringBuilder bufLogInfo;
	static StringBuilder bufLogWarning;
	static StringBuilder bufLogError;
	static StringBuilder bufLogFinal;
	static FiniteStateMachine fsm;

	static public void EraseFile(String name) {
		try {
			File file = new File(name);
			if (file.delete()) {
				// System.out.println(file.getName() + " is deleted!");
			} else {
				// System.out.println("Delete operation is failed.");
			}
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////

	static public void GenerateFiles(String fsmInputName) {
		fsm = new FiniteStateMachine();
		// by default, the input stream is System.in
		InputStream is = System.in;
		if (fsmInputName != null)
			try {
				is = new FileInputStream(fsmInputName);
			} catch (FileNotFoundException e) {
				System.err.println(e);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		// file location and name without extension
		String fsmBaseName = fsmInputName.substring(0, fsmInputName.length() - 4);
		// extract fsm.name from the fsmInputName file name
		// does it contains a unix based directory name?
		if (fsmInputName.contains("/"))
			fsm.name = fsmInputName.substring(fsmInputName.lastIndexOf("/") + 1, fsmInputName.length() - 4);
		else if (fsmInputName.contains("\\")) // it contains a windows based
												// directory name
			// it is a relative path
			fsm.name = fsmInputName.substring(fsmInputName.lastIndexOf("\\") + 1, fsmInputName.length() - 4);
		else
			fsm.name = fsmInputName.substring(0, fsmInputName.length() - 4);
		System.out.print("Processing the file: ");
		System.out.print(fsmInputName);
		System.out.print("\n");
		ANTLRInputStream input = null;
		try {
			input = new ANTLRInputStream(is);
		} catch (IOException e) {
			System.err.println(e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// erase old files
		EraseFile(fsmBaseName.concat(".dot"));
		EraseFile(fsmBaseName.concat("_pack.vhd"));
		EraseFile(fsmBaseName.concat(".vhd"));
		EraseFile(fsmBaseName.concat("_portmap.vhd"));
		EraseFile(fsmBaseName.concat(".log"));
		imageFileName = fsmBaseName.concat(".").concat(fsm.imageFileExtension);
		EraseFile(imageFileName);
		FsmLexer lexer = new FsmLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		FsmParser parser = new FsmParser(tokens);
		parser.setBuildParseTree(true);
		ParseTree tree = parser.fsmfile();
		ParseTreeWalker walker = new ParseTreeWalker();

		FunctionListener collector = new FunctionListener(tokens);
		bufVhdl = new StringBuilder();
		bufVhdlTb = new StringBuilder();
		bufVhdlPortMap = new StringBuilder();
		bufVhdlPack = new StringBuilder();
		bufDot = new StringBuilder();
		bufLogInfo = new StringBuilder();
		bufLogWarning = new StringBuilder();
		bufLogError = new StringBuilder();
		bufLogFinal = new StringBuilder();

		walker.walk(collector, tree);

		if (checkModel() || optionsIgnoreErrors)
		// if ignoreErrors, then display and generate fsm that have errors
		{
			generateDot();
			saveToFile(bufDot.toString(), fsmBaseName.concat(".dot"));
			generateVhdl(bufVhdl);
			saveToFile(bufVhdl.toString(), fsmBaseName.concat(".vhd"));
			generatePackageVhdl(bufVhdlPack);
			saveToFile(bufVhdlPack.toString(), fsmBaseName.concat("_pack.vhd"));
			generatePortMapFileVhdl(bufVhdlPortMap);
			saveToFile(bufVhdlPortMap.toString(), fsmBaseName.concat("_portmap.vhd"));
			generateVhdlTestBench(bufVhdlTb);
			// TODO: compute name even if there is directory name before
			saveToFile(bufVhdlTb.toString(), fsmBaseName.concat("_tb.vhd"));
			saveToFile(
					bufLogFinal.toString() + "\n\n\n" + bufLogError.toString() + "\n\n" + bufLogWarning.toString() + "\n\n"
							+ bufLogInfo.toString(), fsmBaseName.concat(".log"));

			if (optionsComputeResultImage) {
				// Execute external program to compute gif image
				// http://ydisanto.developpez.com/tutoriels/java/runtime-exec/
				String cmd = "dot -T" + fsm.imageFileExtension + " " + fsmBaseName.concat(".dot") + " -o " + imageFileName;
				try {
					Runtime r = Runtime.getRuntime();
					Process p = r.exec(cmd);
					// wait until graphwiz dot has finished
					p.waitFor();
				} catch (Exception e) {
					System.err.println("Error executing: " + cmd + e.toString());
				}
			}
		}
	}

	// ///////////////////////////////////////////////

	static public void processTheDoc() {
		// TODO: don't use absolute file locations
		String source = "/home/bvandepo/antlr/fsm/doc.txt";
		String directoryName = "/home/bvandepo/antlr/fsm/doc_generated/";
		System.out.print("Generating the FSMProcess Documentation from ");
		System.out.print(source);
		System.out.print(" to : ");
		System.out.print(directoryName);
		System.out.println("");
		// if the directory does not exist, create it
		File theDir = new File(directoryName); // TODO != pour windows?
		if (!theDir.exists()) {
			System.out.println("creating directory: " + directoryName);
			theDir.mkdir();
		}
		try {
			String lineToRead;
			BufferedReader fichier = new BufferedReader(new FileReader(source));
			while ((lineToRead = fichier.readLine()) != null) {
				// System.out.println(lineToRead);
				if (lineToRead.contains("<<FSM") && lineToRead.contains(">>")) {
					// get the fsm filename
					String fsmDestName = directoryName
							+ lineToRead.substring(lineToRead.lastIndexOf("<<FSM") + 6, lineToRead.lastIndexOf(">>"));
					System.out.println(fsmDestName);
					String lineToWrite = lineToRead.substring(lineToRead.lastIndexOf(">>") + 2, lineToRead.length());
					System.out.println(lineToWrite);
					String buf = "";
					buf += lineToWrite + "\n";
					Boolean ended = false;
					while (!ended) {
						if ((lineToRead = fichier.readLine()) != null) {
							if (lineToRead.contains("<</FSM>>")) {
								ended = true;
								// get the end of the line before "<</FSM>>"
								lineToWrite = lineToRead.substring(0, lineToRead.lastIndexOf("<</FSM>>"));
							} else
								lineToWrite = lineToRead;
							System.out.println(lineToWrite);
							buf += lineToWrite + "\n";
						} else
							ended = true;
					}
					saveToFile(buf, fsmDestName);
				}
			}
			fichier.close();
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////
	static public String intToDecimalString(int i, int digits) {
		return String.format("%" + digits + "s", Integer.toString(i)).replace(" ", "0");
	}

	// ///////////////////////////////////////////////
	static public void generateCounter(int maxValue) {
		StringBuilder bufGenerated = new StringBuilder();
		// the coding of the state on the same number of digits helps the
		// synthesizer
		String t;
		int nbdigits = 3;
		for (int i = 0; i < maxValue; i++) {
			t = intToDecimalString(i, nbdigits);
			t += "->";
			t += intToDecimalString(i + 1, nbdigits);
			t += "?EN;\n";
			bufGenerated.append(t);
		}
		t = intToDecimalString(maxValue, nbdigits);
		t += "->";
		t += intToDecimalString(0, nbdigits);
		t += "?EN:ENDED;\n";
		bufGenerated.append(t);
		t = "->";
		t += intToDecimalString(0, nbdigits);
		t += "?SRESET;\n";
		bufGenerated.append(t);
		saveToFile(bufGenerated.toString(), "/home/bvandepo/antlr/examples/test.fsm");
	}

	// ///////////////////////////////////////////////
	static public void saveToFile(String buf, String name) {
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileOutputStream(name));
			out.print(buf);
			out.close();
		} catch (IOException e) {
			System.err.println(e);
		} finally {
			if (out != null)
				out.close();
		}
	}

	// ////////////////////////////////////////////////
	static public void GenerateDotForAction(Action a) {
		bufDot.append("    	    	<TR><TD>");
		bufDot.append(a.type);
		if (!a.expression.equals("")) {
			if (!(a.type.equals("S") || a.type.equals("R"))) {
				bufDot.append("</TD><TD COLSPAN=\"2\">");
				bufDot.append(a.name);
				bufDot.append("=");
				bufDot.append(a.expression);
			} else {
				// here we need 3 columns
				bufDot.append("</TD><TD >");
				bufDot.append(a.name);
				bufDot.append("</TD><TD >");
				bufDot.append(a.expression);
			}
		} else {
			bufDot.append("</TD><TD COLSPAN=\"2\">");
			bufDot.append(a.name);
		}
		bufDot.append("</TD> </TR>\n");
	}

	// ////////////////////////////////////////////////
	static public void GenerateHeader(StringBuilder buf, String text, char commentChar) {
		for (int i = 0; i < 80; i++)
			buf.append(commentChar);
		buf.append("\n");
		buf.append(commentChar);
		buf.append(commentChar);
		buf.append(" Finite State Machine ");
		buf.append(text);
		buf.append(" autogenerated by ");
		buf.append(softNameAndVersion);
		buf.append(" B. VANDEPORTAELE LAAS-CNRS 2016\n");
		for (int i = 0; i < 80; i++)
			buf.append(commentChar);
		buf.append("\n");
	}

	// ////////////////////////////////////////////////

	static public void generateDot() {
		GenerateHeader(bufDot, ".dot diagram", '/');
		// compute size of node from the longest state name
		Double nodeWidth = 0.5 + State.longestName * 0.13;
		bufDot.append("digraph finite_state_machine {\n");
		// TODO: deal with dot pragmas to set size etc...
		// http://stackoverflow.com/questions/17719467/graphviz-ignores-size-attribute-a4-page
		// bufDot.append(" ratio=\"fill\";\n");
		// int w = 1680;
		// int h = 1024;
		// double sc = 50.0;
		// bufDot.append(" size=\"");
		// bufDot.append(w / sc);
		// bufDot.append(",");
		// bufDot.append(w / sc);
		// bufDot.append(" !\";\n");
		// bufDot.append(" margin=0;\n");
		// bufDot.append(" node[shape=point, height=0.02, width=0.01];\n");
		bufDot.append("    	rankdir=LR;\n");
		// separation between different ranks
		bufDot.append("    	ranksep=0.5;\n");
		// separation between nodes in same rank (state and corresponding
		// action) between 3 and 0.1
		bufDot.append("    	nodesep=0.1;\n");
		// bufDot.append("    	size=\"10\";\n");
		// bufDot.append("    	ranksep=equally;\n");
		if (!fsm.pragmaDotGlobal.equals("")) {
			bufDot.append("////////// added through pragma, replaces previous set parameters  ////////////////////////////////////////\n");
			bufDot.append(fsm.pragmaDotGlobal);
			bufDot.append("\n");
		}
		bufDot.append("///////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
		bufDot.append("// Finite State Machine Name: ");
		bufDot.append(fsm.name);
		bufDot.append("\n");
		bufDot.append("///////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
		int cptResetStates = 0;
		if (fsm.states.size() != 0) {
			bufDot.append("//////////////////display  states//////////////////\n");
			// bufDot.append("splines=line;\n"); //
			// http://www.graphviz.org/content/attrs#kstyle it is a property of
			// the whole graph... not individual edges
			for (int n = 0; n < fsm.states.size(); n++) {
				int nbAttachedActions = fsm.states.get(n).attachedActions.size();
				// add states to the dot file
				bufDot.append("    	//---------State:   ");
				bufDot.append(fsm.states.get(n).name);
				bufDot.append("    -----------------\n");
				bufDot.append(" {");
				// very important, thanks to this, the state and action nodes
				// are on the same line or column, orthogonally of the rankdir
				bufDot.append(" rank = same;\n");
				bufDot.append("    	");
				bufDot.append("state_"); // Append state before the node name to
											// be able to deal with node names
											// like 0bis
				bufDot.append(fsm.states.get(n).name);
				if (fsm.states.get(n).isInit)
					bufDot.append(" [shape=doublecircle, fixedsize=true, width=");
				else
					bufDot.append(" [shape=circle,fixedsize=true,width=");
				bufDot.append(nodeWidth);
				// specify the name of the node as a label to be able to deal
				// with node names like 0bis
				bufDot.append(", label= \"");
				bufDot.append(fsm.states.get(n).name);
				bufDot.append("\"");
				bufDot.append(" ];\n");
				// add actions states to the dot file if necessary
				if (nbAttachedActions != 0) {
					bufDot.append("    	//Action on state:\n");
					bufDot.append("    	stateaction_");
					bufDot.append(fsm.states.get(n).name);
					bufDot.append("  [shape=box,label=  ");
					bufDot.append(" <<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">\n");
					for (int m = 0; m < nbAttachedActions; m++)
						GenerateDotForAction(fsm.states.get(n).attachedActions.get(m));
					bufDot.append("    	    	</TABLE>>  ];\n");
					bufDot.append("    	//attach the action on the state\n    	");
					bufDot.append("state_");
					bufDot.append(fsm.states.get(n).name);
					bufDot.append(" ->");
					bufDot.append("stateaction_");
					bufDot.append(fsm.states.get(n).name);
					// bufDot.append("  [arrowhead=none, len=0.01,weight=100 ]     ;\n");
					bufDot.append("  [arrowhead=none ]     ;\n");
				}
				int nbResetTransitions = fsm.resetTransitions.size();
				for (int k = 0; k < nbResetTransitions; k++) {
					ResetTransition rt = fsm.resetTransitions.get(k);
					if (rt.destination.equals(fsm.states.get(n).name)) {
						bufDot.append("    	//display reset transition to that state\n    	");
						bufDot.append("    	r");
						cptResetStates++;
						bufDot.append(cptResetStates);
						bufDot.append("[style=\"dashed\", shape=box, label=  <<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">\n");
						bufDot.append("    	    	<TR>   <TD COLSPAN=\"");
						int nbActionsInResetTransitions = rt.attachedActions.size();
						if (nbActionsInResetTransitions == 0)
							bufDot.append("1");
						else
							bufDot.append("2");
						bufDot.append("\">");
						bufDot.append(rt.condition);
						bufDot.append("</TD> </TR>\n");
						for (int l = 0; l < nbActionsInResetTransitions; l++) {
							bufDot.append("    	    	<TR><TD>");
							bufDot.append(rt.attachedActions.get(l).type);
							bufDot.append("</TD><TD>");
							bufDot.append(rt.attachedActions.get(l).name);
							if (rt.attachedActions.size() != 0)
								if (!rt.attachedActions.get(l).expression.equals("")) {
									bufDot.append("=");
									bufDot.append(rt.attachedActions.get(l).expression);
								}
							bufDot.append("</TD> </TR>\n");
						}
						bufDot.append("    	    	</TABLE>>  ];\n");
						bufDot.append("    	//attach the reset transition to the state\n    	");
						bufDot.append("    	r");
						bufDot.append(cptResetStates);
						bufDot.append(" -> ");
						bufDot.append("state_");
						bufDot.append(rt.destination);
						bufDot.append("  ");
						bufDot.append(" [ ");
						// show the priority order if its not the default value
						if (rt.priorityOrder != 1000000) {
							bufDot.append("headlabel= <<font color=\"red\">");
							bufDot.append(rt.priorityOrder);
							bufDot.append("</font>>, ");
						}
						bufDot.append("style=\"dashed\"];\n");
					}
				}
				bufDot.append(" }; \n"); // close rank...
			}
		}
		bufDot.append("//////////////////display  transitions//////////////////\n");
		for (int n = 0; n < fsm.states.size(); n++) {
			int nbTransitions = fsm.states.get(n).transitionsFromThisState.size();
			if (nbTransitions != 0)
				for (int m = 0; m < nbTransitions; m++) {
					Transition t = fsm.states.get(n).transitionsFromThisState.get(m);
					bufDot.append("    	");
					bufDot.append("state_");
					bufDot.append(t.origin);
					bufDot.append(" -> ");
					bufDot.append("state_");
					bufDot.append(t.destination);
					// to add a line between the box and the transition edge
					// bufDot.append("[shape=box, decorate=true, label=  <<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">\n");
					// bufDot.append("[shape=box,  label=  <<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">\n");
					// headport=n, to ask that the edge arrive from the top of
					// the state
					// tailport=s, to ask that the edge arrive from the top of
					// the state
					// weight=1 :cost to stretch an edge, high cost=straight and
					// short lines
					bufDot.append("[ ");
					// show the priority order if its not the default value
					if (t.priorityOrder != 1000000) {
						bufDot.append("taillabel= <<font color=\"green\">");
						bufDot.append(t.priorityOrder);
						bufDot.append("</font>>, ");
					}
					bufDot.append("shape=box,  label=  <<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">\n");
					// weight=0.1,headport=w, tailport=e, minlen=2, maxlen=0.1
					bufDot.append("    	    	<TR><TD COLSPAN=\"");
					int nbActionsInTransitions = t.attachedActions.size();
					if (nbActionsInTransitions == 0)
						bufDot.append("2");
					else
						bufDot.append("3");
					bufDot.append("\">");
					bufDot.append(t.condition);
					bufDot.append("</TD> </TR>\n");
					for (int l = 0; l < nbActionsInTransitions; l++)
						GenerateDotForAction(t.attachedActions.get(l));
					bufDot.append("    	    	</TABLE>>  ];\n");
				}
		}
		if (fsm.numberOfResetAsynchronousDefinitions > 0) {
			bufDot.append("//////////////////display asynchronous reset transition//////////////////\n");
			bufDot.append("    	r");
			cptResetStates++;
			bufDot.append(cptResetStates);
			bufDot.append("[style=\"bold\", shape=box, label=  <<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">\n");
			bufDot.append("    	    	<TR>   <TD COLSPAN=\"");
			int nbActionsInAsynchronousReset = fsm.outputsWithAsynchronousResetValue.size();
			if (nbActionsInAsynchronousReset == 0)
				bufDot.append("1");
			else
				bufDot.append("2");
			bufDot.append("\"> ");
			bufDot.append(fsm.aResetSignalName);
			bufDot.append(" = ");
			bufDot.append(fsm.aResetSignalLevel);
			bufDot.append("</TD> </TR>\n");
			for (int l = 0; l < nbActionsInAsynchronousReset; l++) {
				bufDot.append("    	    	<TR><TD>");
				bufDot.append(fsm.outputsWithAsynchronousResetValue.get(l).name);
				bufDot.append("</TD><TD>");
				bufDot.append(fsm.outputsWithAsynchronousResetValue.get(l).asyncResetExpression);
				bufDot.append("</TD> </TR>\n");
			}
			bufDot.append("    	    	</TABLE>>  ];\n");
			bufDot.append("    	r");
			bufDot.append(cptResetStates);
			bufDot.append(" -> ");
			bufDot.append(fsm.resetAsynchronousState.name);
			bufDot.append(" [style=\"bold\"];\n  ");
		}
		// to make repeatedly actions and clkSignalName on the same rank
		// very important, thanks to this, the state and action nodes are on the
		// same line or column, orthogonally of the rankdir
		int nbRepeatedlyActions = fsm.repeatedlyActions.size();
		if (fsm.clkSignalNameSpecified || (nbRepeatedlyActions != 0)) {
			bufDot.append(" {");
			bufDot.append(" rank = same;\n");
			if (nbRepeatedlyActions != 0) {
				bufDot.append("//////////////////display repeatedly actions//////////////////\n");
				bufDot.append("    	emptyrepeatedly");
				bufDot.append("  ");
				bufDot.append("[  shape= 	octagon, label=  <<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">\n");
				bufDot.append("    	    	<TR>   <TD COLSPAN=\"");
				bufDot.append("2");
				bufDot.append("\">");
				bufDot.append(" Always "); // rt.condition);
				bufDot.append("</TD> </TR>\n");
				for (int l = 0; l < nbRepeatedlyActions; l++) {
					Action a = fsm.repeatedlyActions.get(l);
					bufDot.append("    	    	<TR><TD>");
					bufDot.append(a.type);
					bufDot.append("</TD><TD>");
					bufDot.append(a.name);
					if (!a.expression.equals("")) {
						bufDot.append("=");
						bufDot.append(a.expression);
					}
					bufDot.append("</TD> </TR>\n");
				}
				bufDot.append("    	    	</TABLE>>  ];\n");
			}
			if (fsm.clkSignalNameSpecified) {
				bufDot.append("///////Show the clock signal name if it has been specified////////\n");
				bufDot.append("    	emptyclock");
				bufDot.append("  ");
				bufDot.append("[  shape= 	diamond, label=  <<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">\n");
				bufDot.append("    	    	<TR>   <TD COLSPAN=\"");
				bufDot.append("1");
				bufDot.append("\">");
				bufDot.append(" Clock signal: ");
				bufDot.append(fsm.clkSignalName);
				bufDot.append("</TD> </TR>\n");
				bufDot.append("    	    	</TABLE>>  ];\n");
			}
			bufDot.append(" };");
		}
		bufDot.append("}\n"); // end of the DOT file content
	}

	// ////////////////////////////////////////////////
	static public Boolean checkModel() {
		Boolean modelOk = true; // until we found some errors....
		// //////////////////////////////////////////////////////////////////:
		// check actions coherence. actions of a given name have to be
		// compatible
		// either, I or (R or S) -> then inform output .memorized fields
		// F -> just store the action, no output
		// //////////////////////////////////////////////////////////////////:
		int numberOfStates = fsm.states.size();
		int numberOfOutputs = fsm.outputs.size();
		int numberOfInputs = fsm.inputs.size();
		int numberOfResetTransitions = fsm.resetTransitions.size();
		int numberOfTransitions = fsm.transitions.size();
		int numberOfActionsTotal = fsm.actions.size();
		int numberOfRepeatedlyActions = fsm.repeatedlyActions.size();
		int numberOfNoRepeatedlyActions = fsm.noRepeatedlyActions.size();
		// compute the number of bits to define the state in binary coding
		// generate STATE_NUMBER: out std_logic_vector( 0 downto 0); for 1 or 2
		// states but it is working

		// compute the number of bits that are really required to code the state
		fsm.numberOfBitsForStatesMax = Integer.toBinaryString(numberOfStates - 1).length();
		if (fsm.numberOfBitsForStates == 0) { // not given in the fsm file
			fsm.numberOfBitsForStates = fsm.numberOfBitsForStatesMax;
		} else {
			if (fsm.numberOfBitsForStates < fsm.numberOfBitsForStatesMax) {
				bufLogWarning
						.append("Warning:   The specified size for STATE_NUMBER is not high enough to represent all its possible values. The value will be truncated to the specified number of bits\n");
				fsm.numberOfBitsForStatesMax = fsm.numberOfBitsForStates;
			}
		}
		// Do padding
		for (int i = 0; i < numberOfStates; i++)
			fsm.states.get(i).paddedName = fillStringWithSpace2(fsm.states.get(i).name, State.longestName);
		if (Input.longestName > Output.longestName)
			Output.longestName = Input.longestName;
		else
			Input.longestName = Output.longestName;
		for (int i = 0; i < numberOfInputs; i++)
			fsm.inputs.get(i).paddedName = fillStringWithSpace2(fsm.inputs.get(i).name, Input.longestName);
		for (int i = 0; i < numberOfOutputs; i++)
			fsm.outputs.get(i).paddedName = fillStringWithSpace2(fsm.outputs.get(i).name, Output.longestName);
		// TODO: continue
		if (numberOfStates == 0) {
			bufLogWarning.append("Warning:   The model contains no state... \n");
		}
		if (fsm.outputs.size() == 0) {
			bufLogWarning.append("Warning:   The model contains no output... \n");
		}
		if (fsm.inputs.size() == 0) {
			bufLogWarning.append("Warning:   The model contains no input... \n");
		}
		if (fsm.numberOfResetAsynchronousDefinitions > 1)
			bufLogWarning.append("Warning:   Asynchronous reset has been redefined more than one time...\n");

		// check that the action on a same output are all compatible and update
		// the memorized field of outputs
		for (int k = 0; k < numberOfActionsTotal; k++) {
			Action a = fsm.actions.get(k);
			if (!a.type.equals("F")) // don't care about function call, that are
										// not relevant here
			{
				Output out = fsm.getOutputFromName(a.name);
				// is it the first time this output is encountered?
				if (out.type == null) {
					out.type = a.type;
					if (a.type.equals("I"))
						out.memorized = false;
					else if ((a.type.equals("R")) || (a.type.equals("S")) || (a.type.equals("M")))
						out.memorized = true;
				} else // check that the action is compatible with the output
				{
					if ((a.type.equals("I") && out.memorized) || (a.type.equals("R") && !out.memorized)
							|| (a.type.equals("S") && !out.memorized) || (a.type.equals("M") && !out.memorized)) {
						bufLogError.append("Error: Incompatible actions on ");
						bufLogError.append(a.name);
						bufLogError.append("   type: ");
						bufLogError.append(a.type);
						bufLogError.append("    expression: ");
						bufLogError.append(a.expression);
						bufLogError.append("    has already been detected as ");
						if (out.memorized == false)
							bufLogError.append("non ");
						bufLogError.append("memorized\n");
						modelOk = false;
					}
				}
			}
		}
		// Promote outputs to buffered if they appears also as input, then
		// remove them from the input list
		// //////////////////////////////

		if (fsm.bufferedOutputsAllowed) {
			for (int m = 0; m < numberOfOutputs; m++) {
				Output o = fsm.outputs.get(m);
				String name = o.name;
				if (fsm.hmapInput.containsKey(name)) {
					// if (o.memorized == false) {
					// bufLogError.append("Critical Error :   The use of the NON memorized output ");
					// bufLogError.append(name);
					// bufLogError.append(" is forbidden in conditions or expression.\n");
					// modelOk = false;
					// } else {
					Input i = fsm.hmapInput.get(name);
					// TODO: do we have topropagate the fact that the signal is
					// used as input...
					// not easy because it can appears in process sensivity list
					// but be use as output in pragma, so should not auto
					// generate code for affectation
					// if (i.isUsedInFSm) o.isUsedInFSm = true;
					if (i.isUsedAsInputInFSm)
						o.isUsedAsInputInFSm = true;
					fsm.inputs.remove(i);
					fsm.hmapInput.remove(i.name);
					numberOfInputs--;
					o.isBuffer = true;
					// bufLogInfo.append("Info:   Memorized output ");
					bufLogInfo.append("Info:   Output ");
					bufLogInfo.append(name);
					bufLogInfo
							.append(" has been promoted to buffer because it is used in conditions or expression. So it is also removed from the list of inputs.\n");
				}
			}
		}
		// //////////////////////////////

		// check if inputs/outputs/states names are allowed, "in" and "out" are
		// forbidden as they are reserved keyword of vhdl
		// also other verifications that there could not be misleading between
		// names
		for (int m = 0; m < numberOfStates; m++) {
			String name = fsm.states.get(m).name;
			String type = "State";
			modelOk &= fsm.checkNameIsNotForbidden(name, type);
			modelOk &= fsm.checkIfNameNotAnInput(name, type);
			modelOk &= fsm.checkIfNameNotAnOutput(name, type);
			modelOk &= fsm.checkIfNameIsAsynchronousReset(name, type);
			modelOk &= fsm.checkIfNameIsClock(name, type);
		}
		for (int m = 0; m < numberOfInputs; m++) {
			String name = fsm.inputs.get(m).name;
			String type = "Input";
			modelOk &= fsm.checkNameIsNotForbidden(name, type);
			modelOk &= fsm.checkIfNameNotAnOutput(name, type);
			modelOk &= fsm.checkIfNameIsAsynchronousReset(name, type);
			modelOk &= fsm.checkIfNameIsClock(name, type);
		}
		for (int m = 0; m < numberOfOutputs; m++) {
			String name = fsm.outputs.get(m).name;
			String type = "Output";
			modelOk &= fsm.checkNameIsNotForbidden(name, type);
			modelOk &= fsm.checkIfNameIsAsynchronousReset(name, type);
			modelOk &= fsm.checkIfNameIsClock(name, type);
		}

		// Detect transitions from a state to itself
		for (int n = 0; n < numberOfTransitions; n++) {
			Transition t1 = fsm.transitions.get(n);
			if (t1.origin.equals(t1.destination)) {
				bufLogWarning.append("Warning:   There is a transition between state ");
				bufLogWarning.append(t1.origin);
				bufLogWarning.append(" and itself ");
				if (t1.attachedActions.size() == 0) {
					bufLogWarning.append(" and no action on it, so it is completely useless...\n");
				} else {
					bufLogWarning.append(" and action(s) on it.\n     You should consider using a conditional state(s) action\n");
				}
			}
		}
		// Try to regroup transitions from same states to sames states and
		// having same condition
		// for (int n = 0; n < numberOfTransitions - 1; n++) {
		// for (int m = n + 1; m < numberOfTransitions;) {
		if (true) {
			int n = 0;
			while (n < fsm.transitions.size() - 1) {
				int m = n + 1;
				while (m < fsm.transitions.size()) {
					Transition t1 = fsm.transitions.get(n);
					Transition t2 = fsm.transitions.get(m);
					if (t1.origin.equals(t2.origin) && t1.destination.equals(t2.destination) && t1.condition.equals(t2.condition)) {
						bufLogWarning.append("Warning: Transition from state ");
						bufLogWarning.append(t1.origin);
						bufLogWarning.append(" to state ");
						bufLogWarning.append(t1.destination);
						bufLogWarning.append(" with condition ");
						bufLogWarning.append(t1.condition);
						bufLogWarning.append(" appears more than once.\n     FSMProcess is trying to regroup the corresponding actions.");
						bufLogWarning.append("\n");
						if (t1.priorityOrder != t2.priorityOrder) {
							bufLogWarning.append("     Priority order is different for the two transitions, taking value ");
							bufLogWarning.append(t1.priorityOrder);
							bufLogWarning.append("\n");
						}
						for (int l = 0; l < t2.attachedActions.size(); l++) {
							t1.attachedActions.add(t2.attachedActions.get(l));
							bufLogWarning.append("       Migrating action: ");
							bufLogWarning.append(t2.attachedActions.get(l).type);
							bufLogWarning.append(" ");
							bufLogWarning.append(t2.attachedActions.get(l).name);
							bufLogWarning.append(" ");
							bufLogWarning.append(t2.attachedActions.get(l).expression);
							bufLogWarning.append(" ");
							bufLogWarning.append(t2.attachedActions.get(l).condition);
							bufLogWarning.append("\n");
						}
						for (int l = 0; l < t2.attachedActions.size(); l++)
							t2.attachedActions.remove(0);
						// remove the corresponding transition, from the state
						State s = fsm.getStateFromName(t2.origin);
						s.transitionsFromThisState.remove(t2);
						// remove the transition from the global list
						fsm.transitions.remove(t2);
						numberOfTransitions--;
						// don't increment m because the transition has been
						// removed, so the index should be processed again
					} else
						m++; // only if the transition has not been erased
				}
				n++;
			}
		}
		// TO STOP AT CRITICAL ERRORS!!!!
		// if (modelOk == false)
		// return false; // go no further
		// check if all states have a (reset) transition or asynchronous reset
		// to them, only the initial state can have no transition to it
		for (int m = 0; m < numberOfStates; m++) {
			State s = fsm.states.get(m);
			if (s.isInit)
				s.isAlone = false; // it's an asynchronous reset state
			else
				s.isAlone = true; // until we found the opposite...
		}
		for (int n = 0; n < numberOfResetTransitions; n++) {
			ResetTransition rt = fsm.resetTransitions.get(n);
			State s = fsm.hmapState.get(rt.destination);
			if (s != null) // the state exists
				s.isAlone = false;
		}
		for (int n = 0; n < numberOfTransitions; n++) {
			Transition t = fsm.transitions.get(n);
			State s = fsm.hmapState.get(t.destination);
			if (s != null) // the state exists
				s.isAlone = false;
		}
		for (int m = 0; m < numberOfStates; m++) {
			State s = fsm.states.get(m);
			if (s.isAlone) {
				bufLogWarning.append("Warning: State ");
				bufLogWarning.append(s.name);
				bufLogWarning.append(" is not accessible, a transition to that state should be added\n");
			}
		}
		// Check repeatedly action are compatible with state and
		// transition actions (they are exclusive, except for R and S)
		for (int m = 0; m < numberOfRepeatedlyActions; m++) {
			Action ar = fsm.repeatedlyActions.get(m);
			for (int n = 0; n < numberOfNoRepeatedlyActions; n++) {
				Action an = fsm.noRepeatedlyActions.get(n);
				if (ar.name.equals(an.name)) {

					if (ar.type.equals("R") || ar.type.equals("S")) {
						bufLogWarning.append("Warning: Actions ");
						bufLogWarning.append(ar.name);
						bufLogWarning
								.append("  is defined as Set or Reset in a repeatedly action and is also defined in states or transitions. This is allowed, however dangerous...\n");
					} else {
						bufLogError.append("Error: Actions ");
						bufLogError.append(ar.name);
						bufLogError.append("  is defined as a repeatedly action and also on states or transitions. This is forbidden.\n");
						modelOk = false;
					}
				}
			}
		}
		// enforce that a M action have to have an expression (it memorizes
		// an input!!!!)
		for (int m = 0; m < numberOfActionsTotal; m++) {
			Action a = fsm.actions.get(m);
			if (a.type.equals("M") && (a.expression.equals(""))) {
				bufLogError.append("Critical Error: The M action ");
				bufLogError.append(a.name);
				bufLogError.append(" has no expression.");
				bufLogError.append(a.expression);
				bufLogError.append(" \n");
				modelOk = false;
			}
		}
		// TODO: enforce that Action in a same state (and transition from that
		// state) can not use the same outputs, except if there is only R
		// actions and S actions whose expression are exclusive

		// compute the ordered lists of inputs/outputs/states by names
		// http://imss-www.upmf-grenoble.fr/prevert/Prog/Java/CoursJava/interface.html
		Collections.sort(fsm.inputs);
		Collections.sort(fsm.outputs);
		Collections.sort(fsm.states);

		// /////////////////// Transitions priorities ///////
		// sort the transition by priority for each state (origin)
		for (int m = 0; m < numberOfStates; m++) {
			int numberOfTransitionsFromThisState = fsm.states.get(m).transitionsFromThisState.size();
			Collections.sort(fsm.states.get(m).transitionsFromThisState);
			for (int n = 0; n < numberOfTransitionsFromThisState; n++) {
				Transition t = fsm.states.get(m).transitionsFromThisState.get(n);
				t.conditionWithPriorities = " ( ";
				if (t.condition.equals("1"))
					t.conditionWithPriorities += " value_one_internal ";
				else
					t.conditionWithPriorities += t.condition;
				t.conditionWithPriorities += " ) ";
			}
			// loop inside the list and detect every adjacent elements with the
			// same priority
			for (int n = 1; n < numberOfTransitionsFromThisState; n++) {
				Transition t1 = fsm.states.get(m).transitionsFromThisState.get(n - 1);
				Transition t2 = fsm.states.get(m).transitionsFromThisState.get(n);
				if (t1.priorityOrder == t2.priorityOrder) {
					bufLogWarning.append("Warning: Some  transitions from State: ");
					bufLogWarning.append(fsm.states.get(m).name);
					bufLogWarning
							.append(" have the same priority.\n     Check that the expressions are mutually exclusive or add priorities in the model\n");
				} else {// compute t2.conditionWithPriorities from the t1 one
					bufLogInfo.append("Info: Transition to state ");
					bufLogInfo.append(t2.destination);
					bufLogInfo.append(" with condition: ");
					bufLogInfo.append(t2.conditionWithPriorities);
					bufLogInfo.append(" has been upgraded to condition: ");
					t2.conditionWithPriorities += " AND NOT " + t1.conditionWithPriorities + " ";
					bufLogInfo.append(t2.conditionWithPriorities);
					bufLogInfo.append(" because of higher priority transition(s) to the same state\n");
				}
			}
		}
		// /////////////////// resetTransitions priorities ///////
		Collections.sort(fsm.resetTransitions);
		// now that the (reset) transitions are sorted, lets compute
		// fsm.resetTransitions.conditionWithPriorities accordingly
		for (int n = 0; n < numberOfResetTransitions; n++) {
			ResetTransition rt = fsm.resetTransitions.get(n);
			rt.conditionWithPriorities = " ( ";
			if (rt.condition.equals("1"))
				rt.conditionWithPriorities += " value_one_internal ";
			else
				rt.conditionWithPriorities += rt.condition;
			rt.conditionWithPriorities += " ) ";
		}
		// loop inside the list and detect every adjacent elements with the same
		// priority
		for (int n = 1; n < numberOfResetTransitions; n++) {
			ResetTransition rt1 = fsm.resetTransitions.get(n - 1);
			ResetTransition rt2 = fsm.resetTransitions.get(n);
			if (rt1.priorityOrder == rt2.priorityOrder) {
				bufLogWarning
						.append("Warning: Some reset transitions have the same priority, check that the expressions are mutually exclusive or add priorities in the model\n");
			} else {// compute rt2.conditionWithPriorities from the rt1 one
				bufLogInfo.append("Info: Reset transition to state ");
				bufLogInfo.append(rt2.destination);
				bufLogInfo.append(" with condition: ");
				bufLogInfo.append(rt2.conditionWithPriorities);
				bufLogInfo.append(" has been upgraded to condition: ");
				rt2.conditionWithPriorities += " AND NOT " + rt1.conditionWithPriorities + " ";
				bufLogInfo.append(rt2.conditionWithPriorities);
				bufLogInfo.append(" because of higher priority reset transition(s) to the same state\n");
			}
		}
		// add a post processing such that reset transitions have to
		// inhibate standard transition and state actions from ALL states
		// if configured so...
		if (fsm.resetTransitionInhibatesTransitionActions || fsm.resetTransitionInhibatesActionsOnStates) {
			if (numberOfResetTransitions > 0) {
				fsm.resetConditionComplement = "";
				for (int n = 0; n < numberOfResetTransitions; n++) {
					ResetTransition rt = fsm.resetTransitions.get(n);
					if (n != 0)
						fsm.resetConditionComplement += " AND ";
					fsm.resetConditionComplement += "  (  NOT ( ";
					fsm.resetConditionComplement += rt.condition;
					fsm.resetConditionComplement += " ) = '1' ) ";
				}
				// add this condition to the needed action.condition
				if (fsm.resetTransitionInhibatesTransitionActions)
					for (int l = 0; l < numberOfStates; l++) {
						State s = fsm.states.get(l);
						int numberOfTransitionsFromThisState = s.transitionsFromThisState.size();
						for (int m = 0; m < numberOfTransitionsFromThisState; m++) {
							Transition t = s.transitionsFromThisState.get(m);
							int numberOfActionsInThisTransition = t.attachedActions.size();
							for (int n = 0; n < numberOfActionsInThisTransition; n++) {
								Action a = t.attachedActions.get(n);
								bufLogInfo.append("Info: Action on Transition from state ");
								bufLogInfo.append(t.origin);
								bufLogInfo.append(" to state ");
								bufLogInfo.append(t.destination);
								bufLogInfo.append(" with condition: ");
								bufLogInfo.append(t.conditionWithPriorities);
								bufLogInfo.append(" and with expression: ");
								bufLogInfo.append(a.expression);
								bufLogInfo.append(" has been upgraded with supplementary condition: ");
								bufLogInfo.append(fsm.resetConditionComplement);
								bufLogInfo.append(" because of a reset transition(s)\n");
								a.condition = " AND (not_any_s_reset_internal = '1' ) ";
							}
						}
					}
				if (fsm.resetTransitionInhibatesActionsOnStates)
					for (int l = 0; l < numberOfStates; l++) {
						State s = fsm.states.get(l);
						int numberOfActionsInThisState = s.attachedActions.size();
						for (int m = 0; m < numberOfActionsInThisState; m++) {
							Action a = s.attachedActions.get(m);
							bufLogInfo.append("Info: Action on state ");
							bufLogInfo.append(s.name);
							bufLogInfo.append(" with expression: ");
							bufLogInfo.append(a.expression);
							bufLogInfo.append(" has been upgraded with supplementary condition: ");
							bufLogInfo.append(fsm.resetConditionComplement);
							bufLogInfo.append(" because of a reset transition(s)\n");
							a.condition = " AND (not_any_s_reset_internal = '1' ) ";
							// TODO; also add all the possible transitions from
							// that state and inhibate action on state. Usefull
							// for memorized outputs, see <<FSM
							// priority_action_2.fsm>> in the doc
						}
					}
			}
		}
		// this has to be done after regrouping inputs and outputs having the
		// same names...
		// generate lists of outputs that are demoted to signal through
		// pragma
		for (int i = 0; i < fsm.outputs.size(); i++)
			if (fsm.outputs.get(i).isDemotedToSignal) {
				fsm.demotedToSignalOutputs.add(fsm.outputs.get(i));
			} else
				fsm.realOutputs.add(fsm.outputs.get(i));
		// generate lists of inputs that are demoted to signal through
		// pragma
		for (int i = 0; i < fsm.inputs.size(); i++)
			if (fsm.inputs.get(i).isDemotedToSignal) {
				fsm.demotedToSignalInputs.add(fsm.inputs.get(i));
			} else
				fsm.realInputs.add(fsm.inputs.get(i));
		computeStateCodingVhdl();
		if (modelOk)
			bufLogFinal.append("Info:   No Critial error: Output files can be generated\n\n");
		else
			bufLogFinal.append("Info:   At least one Critial error: Output files canNOT be generated correctly\n\n");
		return modelOk;
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	static public String generateBinaryAsciiString(String s) {
		String sout;
		sout = "";
		for (int i = 0; i < s.length(); i++) {
			sout += String.format("%8s", Integer.toBinaryString(s.charAt(i))).replace(" ", "0");
		}
		return sout;
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	static public void generateVhdlTestBenchAsciiStates(StringBuilder buf) {
		int numberOfStates = fsm.states.size();
		buf.append("-----------------------------------------------------------------------------------------------------------\n");
		buf.append("process (state_number)\n");
		buf.append("begin\n");
		buf.append("    case state_number is\n");
		for (int n = 0; n < numberOfStates; n++) {
			buf.append("      when \"");
			buf.append(fsm.states.get(n).stateCode);
			buf.append("\"    => state_name <= \"");
			buf.append(generateBinaryAsciiString(fsm.states.get(n).nameDisplay));
			buf.append("\"; -- ");
			buf.append(fsm.states.get(n).nameDisplay);
			buf.append("\n");
		}
		buf.append("      when others   => state_name <= \"");
		buf.append(generateBinaryAsciiString(fsm.stateNameDisplayError));
		buf.append("\"; -- ");
		buf.append(fsm.stateNameDisplayError);
		buf.append("\n ");
		buf.append("      end case;\n");
		buf.append("end process;\n");
		buf.append("-----------------------------------------------------------------------------------------------------------\n");
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	static public void generateVhdlTestBench(StringBuilder buf) {
		GenerateHeader(buf, "testbench.vhdl", '-');
		buf.append("library ieee;\nuse        ieee.std_logic_1164.all;\nuse        ieee.std_logic_unsigned.all;\nuse        ieee.std_logic_arith.all;\n");
		if (!fsm.pragmaVhdlPreEntity.equals("")) {
			buf.append("------------------------------pragma_vhdl_pre_entity-------------------------------------------------------\n");
			buf.append(fsm.pragmaVhdlPreEntity);
			buf.append("--------------------------end of pragma_vhdl_pre_entity----------------------------------------------------\n");
		}
		buf.append("-- Uncomment the following library declaration if using\n");
		buf.append("-- arithmetic functions with Signed or Unsigned values\n");
		buf.append("--USE ieee.numeric_std.ALL;\n\n");
		buf.append("ENTITY ");
		buf.append(fsm.name);
		buf.append("_tb IS\n");
		buf.append("END ");
		buf.append(fsm.name);
		buf.append("_tb ;\n\n");
		buf.append("ARCHITECTURE behavior OF ");
		buf.append(fsm.name);
		buf.append("_tb IS\n");
		buf.append("-- Component Declaration for the Unit Under Test (UUT)\n");
		generateComponentVhdl(buf, false);
		buf.append("\n");
		generateSignalsForInterfaceVhdl(buf);
		buf.append("\n--Clock period, should be settable through pragma_vhdl to get real time in simu\n");
		buf.append("constant ck_period : time := 100 ns;\n\n");
		buf.append("BEGIN\n");
		buf.append("-- Instantiate the Unit Under Test (UUT)\n");
		generatePortMapVhdl(buf);
		generateVhdlTestBenchAsciiStates(buf);
		buf.append("\n-- Clock process definitions\n");
		buf.append("ck_process :process\n");
		buf.append("begin\n");
		buf.append(fsm.clkSignalName);
		buf.append(" <= '0';\n");
		buf.append("wait for ck_period/2;\n");
		buf.append(fsm.clkSignalName);
		buf.append(" <= '1';\n");
		buf.append("wait for ck_period/2;\n");
		buf.append("end process;\n");
		buf.append("\n");
		buf.append("-- Stimulus process\n");
		buf.append("stim_proc: process\n");
		buf.append("--(ck,resetn)\n");
		buf.append("  begin	\n");
		buf.append("-- hold reset state for 100 ns.\n");
		buf.append(fsm.aResetSignalName);
		buf.append(" <='");
		buf.append(fsm.aResetSignalLevel);
		buf.append("';\n");
		buf.append("wait for 100 ns;\n");
		buf.append(fsm.aResetSignalName);
		buf.append(" <= NOT '");
		buf.append(fsm.aResetSignalLevel);
		buf.append("';\n");
		buf.append("wait for ck_period*2;\n");
		buf.append("-- stimuli get from the testbench pragma:\n");
		buf.append("------------------------------pragma_vhdl_testbench-------------------------------------------------------\n");
		buf.append(fsm.pragmaVhdlTestbench);
		buf.append("--------------------------end of pragma_vhdl_testbench----------------------------------------------------\n");
		buf.append(" wait;\n");
		buf.append("end process;\n");
		buf.append("END;\n");
	}

	// ////////////////////////////////////////////////////////////////////////////////////

	static public void computeStateCodingVhdl() {
		int numberOfStates = fsm.states.size();
		// compute longest state name, at least 1 chars to display Error as !
		int longest = 1;
		for (int i = 0; i < numberOfStates; i++) {
			String code = String.format("%" + Integer.toString(fsm.numberOfBitsForStatesMax) + "s", Integer.toBinaryString(i)).replace(" ",
					"0");
			if (fsm.numberOfBitsForStatesMax > fsm.numberOfBitsForStates)
				fsm.states.get(i).stateCode = code.substring(fsm.numberOfBitsForStatesMax - fsm.numberOfBitsForStates,
						fsm.numberOfBitsForStatesMax);
			else
				fsm.states.get(i).stateCode = code;
			int sz = fsm.states.get(i).name.length();
			if (sz > longest)
				longest = sz;
		}
		for (int i = 0; i < numberOfStates; i++) {
			fsm.states.get(i).nameDisplay = fillStringWithSpace2(fsm.states.get(i).name, longest);
		}
		String codeError = String.format("%" + Integer.toString(fsm.numberOfBitsForStatesMax) + "s", Integer.toBinaryString(1))
				.replace(" ", "1").replace("0", "1");// all at '1'
		if (fsm.numberOfBitsForStatesMax > fsm.numberOfBitsForStates)
			fsm.stateCodeError = codeError
					.substring(fsm.numberOfBitsForStatesMax - fsm.numberOfBitsForStates, fsm.numberOfBitsForStatesMax);
		else
			fsm.stateCodeError = codeError;
		// Error is displayed at least as "!", if there is more room, display
		// "!Error" as much as possible
		String err = "!Error";
		if (longest < 6)
			fsm.stateNameDisplayError = err.substring(0, longest);
		else
			fsm.stateNameDisplayError = fillStringWithSpace2(err, longest);
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	static public void generateStateCodingVhdl(StringBuilder buf, Boolean comment) {
		buf.append("------------------  OUTPUTS FOR CURRENT STATE VISUALIZATION ------------\n");
		if (comment)
			buf.append("--");
		buf.append("	state_number <= \"");
		int numberOfStates = fsm.states.size();
		for (int i = 0; i < numberOfStates; i++) {
			if (i != 0) {
				if (comment)
					buf.append("--");
				buf.append("                   else \"");
			}
			buf.append(fsm.states.get(i).stateCode);
			buf.append("\" when ( current_state = ");
			buf.append("state_");
			buf.append(fsm.states.get(i).name);
			buf.append(")\n");
		}
		if (comment)
			buf.append("--");
		buf.append("                   else \"");
		buf.append(fsm.stateCodeError);
		buf.append("\";   -- coding for error\n");
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	static public void generateSignalsForInterfaceVhdl(StringBuilder buf) {
		// ////////////////listing of inputs/outputs//////////////////
		buf.append("signal ");
		buf.append(fsm.clkSignalName);
		buf.append(" :    std_logic;\n");
		buf.append("signal ");
		buf.append(fsm.aResetSignalName);
		buf.append(" :    std_logic;\n");
		if (fsm.GenerateNumberOfStateOutput && (fsm.states.size() != 0)) {
			buf.append("signal		");
			buf.append(" STATE_NUMBER");
			buf.append(" :    std_logic_vector( ");
			buf.append(fsm.numberOfBitsForStates - 1);
			buf.append(" downto 0);\n");
		}
		// ////////////////listing of inputs/outputs//////////////////
		buf.append("--signals for inputs:  \n");
		for (int n = 0; n < fsm.realInputs.size(); n++) {
			buf.append("signal ");
			buf.append(fsm.realInputs.get(n).paddedName);
			buf.append(" :   ");
			buf.append(fsm.realInputs.get(n).interfacePortTypes);
			buf.append(";\n");
		}
		buf.append("--signals for outputs:  \n");
		for (int n = 0; n < fsm.realOutputs.size(); n++) {
			buf.append("signal ");
			buf.append(fsm.realOutputs.get(n).paddedName);
			buf.append(" : ");
			buf.append(fsm.realOutputs.get(n).interfacePortTypes);
			buf.append(";\n");
		}
		buf.append("--signals for current state name visualization:  \n");
		buf.append("signal state_name : std_logic_vector(");
		buf.append((fsm.stateNameDisplayError.length() * 8) - 1);
		buf.append(" downto 0 );\n");
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	static public void generateInterfaceVhdl(StringBuilder buf) {
		// generics
		if (fsm.genericDeclarations.size() > 0) {
			buf.append("generic (\n		");
			for (int i = 0; i < fsm.genericDeclarations.size(); i++) {
				if (i > 0)
					buf.append(";\n		");
				buf.append(fsm.genericDeclarations.get(i).Name);
				buf.append(" : ");
				buf.append(fsm.genericDeclarations.get(i).Type);
				buf.append(" := ");
				buf.append(fsm.genericDeclarations.get(i).Default);
			}
			buf.append(");\n");
		}
		// ports
		buf.append("port (\n");
		if (!fsm.pragmaVhdlEntity.equals("")) {
			buf.append("------------------------------pragma_vhdl_entity-----------------------------------------------------------\n");
			buf.append(fsm.pragmaVhdlEntity);
			buf.append("--------------------------end of pragma_vhdl_entity--------------------------------------------------------\n");
		}
		buf.append("		");
		buf.append(fillStringWithSpace2(fsm.clkSignalName, Input.longestName));
		buf.append(" : in     std_logic;\n");
		buf.append("		");
		buf.append(fillStringWithSpace2(fsm.aResetSignalName, Input.longestName));
		buf.append(" : in     std_logic");

		if ((fsm.GenerateNumberOfStateOutput && (fsm.states.size() != 0)) || (fsm.realInputs.size() > 0) || (fsm.realOutputs.size() > 0))
			buf.append(";\n");
		if (fsm.GenerateNumberOfStateOutput && (fsm.states.size() != 0)) {
			buf.append("		");
			buf.append(fillStringWithSpace2("STATE_NUMBER", Input.longestName));
			buf.append(" : out    std_logic_vector( ");
			buf.append(fsm.numberOfBitsForStates - 1);
			buf.append(" downto 0)");
			if ((fsm.realInputs.size() > 0) || (fsm.realOutputs.size() > 0))
				buf.append(";\n");
		}
		// ////////////////listing of inputs/outputs//////////////////
		for (int n = 0; n < fsm.realInputs.size(); n++) {
			buf.append("		");
			buf.append(fsm.realInputs.get(n).paddedName);
			buf.append(" : in     ");
			buf.append(fsm.realInputs.get(n).interfacePortTypes);
			if ((n != fsm.realInputs.size() - 1) || ((fsm.realOutputs.size() > 0)))
				buf.append(";\n");
		}
		for (int n = 0; n < fsm.realOutputs.size(); n++) {
			buf.append("		");
			buf.append(fsm.realOutputs.get(n).paddedName);
			buf.append(" : ");
			if (fsm.realOutputs.get(n).isBuffer)
				buf.append("buffer ");
			else
				buf.append("out    ");
			buf.append(fsm.realOutputs.get(n).interfacePortTypes);
			if (n != fsm.realOutputs.size() - 1)
				buf.append(";\n");
		}
		buf.append(");\n");
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	static public void generateComponentVhdl(StringBuilder buf, Boolean comment) {
		buf.append("component ");
		buf.append(fsm.name);
		buf.append("\n");
		generateInterfaceVhdl(buf);
		buf.append("end component;\n");
		if (fsm.GenerateNumberOfStateOutput && (fsm.states.size() != 0) && comment)
			generateStateCodingVhdl(buf, true);
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	static public void generatePackageVhdl(StringBuilder buf) {
		GenerateHeader(buf, "vhdl package file", '-');
		buf.append("library IEEE;\n");
		buf.append("use IEEE.STD_LOGIC_1164.all;\n");
		buf.append("package ");
		buf.append(fsm.name);
		buf.append("_pack is\n");
		generateComponentVhdl(buf, true);
		buf.append("end \n");
		buf.append(fsm.name);
		buf.append("_pack;\n");
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	static public void generatePortMapVhdl(StringBuilder buf) {
		GenerateHeader(buf, "port map vhdl file", '-');
		buf.append(fsm.name);
		buf.append("_u0 : ");
		buf.append(fsm.name);
		buf.append("\n");
		// generics
		if (fsm.genericDeclarations.size() > 0) {
			buf.append("generic map (\n		");
			for (int i = 0; i < fsm.genericDeclarations.size(); i++) {
				if (i > 0)
					buf.append(",\n		");
				buf.append(fsm.genericDeclarations.get(i).Name);
				buf.append(" => ");
				buf.append(fsm.genericDeclarations.get(i).Default);
			}
			buf.append(")\n");
		}
		// ports
		buf.append("port map(\n");
		if (!fsm.pragmaVhdlEntity.equals("")) {
			buf.append("------------------------------pragma_vhdl_entity-----------------------------------------------------------\n");
			buf.append(fsm.pragmaVhdlEntity);
			buf.append("--------------------------end of pragma_vhdl_entity--------------------------------------------------------\n");
		}
		buf.append("		");
		buf.append(fsm.clkSignalName);
		buf.append(" => ");
		buf.append(fsm.clkSignalName);
		buf.append(",\n");
		buf.append("		");
		buf.append(fsm.aResetSignalName);
		buf.append(" => ");
		buf.append(fsm.aResetSignalName);
		if ((fsm.GenerateNumberOfStateOutput && (fsm.states.size() != 0)) || (fsm.realInputs.size() > 0) || (fsm.realOutputs.size() > 0))
			buf.append(",\n");
		// ////////////////listing of inputs/outputs//////////////////
		if (fsm.GenerateNumberOfStateOutput && (fsm.states.size() != 0)) {
			buf.append("		state_number =>  state_number");
			if ((fsm.realInputs.size() > 0) || (fsm.realOutputs.size() > 0))
				buf.append(",\n");
		}
		for (int n = 0; n < fsm.realInputs.size(); n++) {
			buf.append("		");
			buf.append(fsm.realInputs.get(n).name);
			buf.append(" => ");
			buf.append(fsm.realInputs.get(n).name);
			if ((n != fsm.realInputs.size() - 1) || ((fsm.realInputs.size() > 0)))
				buf.append(",\n");
		}
		for (int n = 0; n < fsm.realOutputs.size(); n++) {
			buf.append("		");
			buf.append(fsm.realOutputs.get(n).name);
			buf.append(" => ");
			buf.append(fsm.realOutputs.get(n).name);
			if (n != fsm.realOutputs.size() - 1)
				buf.append(",\n");
		}
		buf.append(");\n");
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	static public void generatePortMapFileVhdl(StringBuilder buf) {
		buf.append("library work;\n");
		buf.append("use work.");
		buf.append(fsm.name);
		buf.append("_pack.all;\n\n");
		generatePortMapVhdl(buf);
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	static public void generateVhdl(StringBuilder buf) {
		int numberOfStates = fsm.states.size();
		GenerateHeader(buf, "vhdl file", '-');
		buf.append("library	ieee;\nuse		ieee.std_logic_1164.all;\nuse		ieee.std_logic_unsigned.all;\nuse		ieee.std_logic_arith.all;\n");
		if (!fsm.pragmaVhdlPreEntity.equals("")) {
			buf.append("------------------------------pragma_vhdl_pre_entity-------------------------------------------------------\n");
			buf.append(fsm.pragmaVhdlPreEntity);
			buf.append("--------------------------end of pragma_vhdl_pre_entity----------------------------------------------------\n");
		}
		buf.append("entity ");
		buf.append(fsm.name);
		buf.append(" is\n");
		generateInterfaceVhdl(buf);
		buf.append("end ");
		buf.append(fsm.name);
		buf.append(";\n\n");
		buf.append("architecture ar of ");
		buf.append(fsm.name);
		buf.append(" is \n");
		if (!fsm.pragmaVhdlArchitecturePreBegin.equals("")) {
			buf.append("------------------------------pragma_vhdl_architecture_pre_begin-------------------------------------------\n");
			buf.append(fsm.pragmaVhdlArchitecturePreBegin);
			buf.append("--------------------------end of pragma_vhdl_architecture_pre_begin----------------------------------------\n");
		}
		if (fsm.states.size() != 0) {
			// ////// listing of possible values for state names
			buf.append("type fsm_state is (");
			// print the asynchronous reset first, ISE needs it to code it state
			// 0 and not emit a warning about it.
			buf.append("state_");
			buf.append(fsm.resetAsynchronousState.name);
			// print the others states names
			int numberOfStatesStill = numberOfStates - 1;
			int n = 0;
			while (numberOfStatesStill > 0) {
				if (!fsm.states.get(n).name.equalsIgnoreCase(fsm.resetAsynchronousState.name)) {
					buf.append(", ");
					buf.append("state_"); // prefix state name with state_
					buf.append(fsm.states.get(n).name);
					numberOfStatesStill--;
				}
				n++;
			}
			buf.append(");\n");
			buf.append("signal current_state, next_state : fsm_state;\n");
		}
		// //////// listing of internal signals for memorized outputs/
		for (int n = 0; n < fsm.outputs.size(); n++) {
			Output out = fsm.outputs.get(n);
			if ((out.memorized) && (out.isUsedAsOutputInFSm)) {
				buf.append("signal ");
				buf.append(out.name);
				buf.append("_set, ");
				buf.append(out.name);
				buf.append("_reset, ");
				buf.append(out.name);
				buf.append("_mem : std_logic;\n");
				buf.append("signal ");
				buf.append(out.name);
				buf.append("_mem_value : std_logic;\n");
			}
		}
		buf.append("signal value_one_internal: std_logic;  --signal used internally to ease operation on conditions, to have a std_logic type '1' value\n");
		int numberOfResetTransitions = fsm.resetTransitions.size();
		if (fsm.resetTransitionInhibatesTransitionActions || fsm.resetTransitionInhibatesActionsOnStates) {
			if (numberOfResetTransitions > 0) {
				buf.append("signal not_any_s_reset_internal: std_logic ;  --signal used internally to ease inhibition of actions when reset transitions occurs\n");
			}
		}
		if (fsm.demotedToSignalOutputs.size() > 0) {
			buf.append("-----------------outputs demoted to internal signals through pragma-------------------------------------\n");
			for (int n = 0; n < fsm.demotedToSignalOutputs.size(); n++) {
				if (fsm.demotedToSignalOutputs.get(n).isUsedAsOutputInFSm) {
					buf.append("signal ");
					buf.append(fsm.demotedToSignalOutputs.get(n).paddedName);
					buf.append(" : ");
					buf.append(" std_logic");
					buf.append(";\n");
				}
			}
		}
		if (fsm.demotedToSignalInputs.size() > 0) {
			buf.append("-----------------inputs demoted to internal signals through pragma-------------------------------------\n");
			for (int n = 0; n < fsm.demotedToSignalInputs.size(); n++) {
				if (fsm.demotedToSignalInputs.get(n).isUsedAsInputInFSm) {
					buf.append("signal ");
					buf.append(fsm.demotedToSignalInputs.get(n).paddedName);
					buf.append(" : ");
					buf.append(" std_logic");
					buf.append(";\n");
				}
			}
		}
		// ////////////////let's animate all that stuff...//////////////////
		buf.append("---------------------------------------------------------------------------------------\n");
		buf.append("begin\n");
		if (!fsm.pragmaVhdlArchitecturePostBegin.equals("")) {
			buf.append("------------------------------pragma_vhdl_architecture_post_begin-----------------------------------------\n");
			buf.append(fsm.pragmaVhdlArchitecturePostBegin);
			buf.append("--------------------------end of pragma_vhdl_architecture_post_begin--------------------------------------\n");
		}
		buf.append("value_one_internal <='1';\n");
		if (fsm.resetTransitionInhibatesTransitionActions || fsm.resetTransitionInhibatesActionsOnStates) {
			if (numberOfResetTransitions > 0) {
				buf.append("-----------------------Combination of sreset signal(s) to inhibate actions on states and/or transitions--------------\n");
				buf.append("not_any_s_reset_internal<= '1' when ");
				buf.append(fsm.resetConditionComplement);
				buf.append(" else\n                           '0';\n");
			}
		}
		if (fsm.states.size() != 0) {
			buf.append("------------------------Process for the memorization of the state----------------------\n");
			buf.append("process (");
			buf.append(fsm.clkSignalName);
			buf.append(", ");
			buf.append(fsm.aResetSignalName);
			buf.append(")\nbegin\n    if (");
			buf.append(fsm.aResetSignalName);
			buf.append("='");
			buf.append(fsm.aResetSignalLevel);
			buf.append("') then current_state <=");
			buf.append("state_");
			buf.append(fsm.resetAsynchronousState.name);
			buf.append(";\n");
			buf.append("    elsif ");
			buf.append(fsm.clkSignalName);
			buf.append("'event and ");
			buf.append(fsm.clkSignalName);
			buf.append("='1' then current_state<=next_state;\n");
			buf.append("    end if;\n");
			buf.append("end process;\n\n");
			buf.append("-------------------Combinatorial process for the evolution of the state------------------\n");
			buf.append("process (current_state");
			// check inputs that are used by the fsm as inputs
			for (int n = 0; n < fsm.inputs.size(); n++) {
				if (fsm.inputs.get(n).isUsedAsInputInFSm) {
					buf.append(", ");
					buf.append(fsm.inputs.get(n).name);
				}
			}
			// check also buffered outputs that are used by the fsm as
			// inputs
			for (int n = 0; n < fsm.outputs.size(); n++) {
				if (fsm.outputs.get(n).isUsedAsInputInFSm) {
					buf.append(", ");
					buf.append(fsm.outputs.get(n).name);
				}
			}
			if (fsm.bufferedOutputsAllowed) {
				for (int n = 0; n < fsm.outputs.size(); n++) {
					if ((fsm.outputs.get(n).isBuffer) && (fsm.outputs.get(n).isUsedAsOutputInFSm)) {
						buf.append(", ");
						buf.append(fsm.outputs.get(n).name);
					}
				}
			}
			buf.append(")\n");
			buf.append("begin\n");
			int nbResetTransitions = fsm.resetTransitions.size();
			if (nbResetTransitions != 0) {
				buf.append("-----------Synchronous RESETs has higher priority than standard transitions\n");
				for (int m = 0; m < nbResetTransitions; m++) {
					if (m == 0)
						buf.append("   if    ( ");
					else
						buf.append("   elsif ( ");
					if (fsm.resetTransitions.get(m).condition.equals("1"))
						// that would be dumb because the system should always
						// be resetted...
						buf.append(" true ");
					else {
						buf.append(" ( ");
						buf.append(fsm.resetTransitions.get(m).condition);
						buf.append(" ) ");
						buf.append(" = '1' ");
					}
					buf.append(")\n    then next_state <= state_");
					buf.append(fsm.resetTransitions.get(m).destination);
					buf.append(";");
					// show the priority order if its not the default value
					if (fsm.resetTransitions.get(m).priorityOrder != 1000000) {
						buf.append(" -- priority order set to: ");
						buf.append(fsm.resetTransitions.get(m).priorityOrder);
					}
					buf.append("\n");
				}
				buf.append("   else \n");
			}
			buf.append("------------------------------standard transitions---------------------\n");
			buf.append("    case current_state is\n");
			// pour chaque état, il peut y avoir plusieurs transitions, la
			// première if, les suivantes elsif et finalement en plus le
			// maintien dans l'état courant
			for (int n = 0; n < numberOfStates; n++) {
				buf.append("      when state_");
				buf.append(fsm.states.get(n).paddedName);
				int transitionFromThisStateNumber = fsm.states.get(n).transitionsFromThisState.size();
				buf.append(" => ");
				for (int m = 0; m < transitionFromThisStateNumber; m++) {
					if (m == 0)
						buf.append(" if ( ");
					else {
						buf.append(fillStringWithSpace2("", State.longestName + 22));
						buf.append("elsif ( ");
					}
					if (fsm.states.get(n).transitionsFromThisState.get(m).condition.equals("1"))
						buf.append(" true ");
					else {
						buf.append(" ( ");
						buf.append(fsm.states.get(n).transitionsFromThisState.get(m).condition);
						buf.append(" ) ");
						buf.append(" = '1' ");
					}
					buf.append(") then next_state <= state_");
					buf.append(fsm.states.get(n).transitionsFromThisState.get(m).destination);
					buf.append(";");
					// show the priority order if its not the default value
					if (fsm.states.get(n).transitionsFromThisState.get(m).priorityOrder != 1000000) {
						buf.append(" -- priority order set to: ");
						buf.append(fsm.states.get(n).transitionsFromThisState.get(m).priorityOrder);
					}
					buf.append("\n");
				}
				if (transitionFromThisStateNumber != 0) {
					buf.append(fillStringWithSpace2("", State.longestName + 22));
					buf.append("else	");
				}
				buf.append("next_state <= state_");
				buf.append(fsm.states.get(n).name);
				buf.append(";\n");
				if (transitionFromThisStateNumber != 0) {
					buf.append(fillStringWithSpace2("", State.longestName + 22));
					buf.append("end if;\n");
				}
			}
			buf.append("--    when others => next_state <= state_");
			buf.append(fsm.states.get(0).name);
			buf.append(";\n    end case;\n");
			// if there has been some synchronous reset transition, end if
			// should be added
			if (nbResetTransitions != 0)
				buf.append("   end if;\n");
			buf.append("end process;\n");
		}
		buf.append("------------------FLIP FLOPS FOR MEMORIZED OUTPUTS ------------\n");
		for (int n = 0; n < fsm.outputs.size(); n++) {
			Output out = fsm.outputs.get(n);
			if ((out.memorized) && (out.isUsedAsOutputInFSm)) {
				buf.append("------ FLIP FLOPS FOR ");
				buf.append(out.name);
				buf.append(" ------\n");
				buf.append("process (");
				buf.append(fsm.clkSignalName);
				buf.append(", ");
				buf.append(fsm.aResetSignalName);
				buf.append(")\n");
				buf.append("begin\n");
				buf.append("   if (");
				buf.append(fsm.aResetSignalName);
				buf.append("='");
				buf.append(fsm.aResetSignalLevel);
				buf.append("') then ");
				buf.append(out.name);
				buf.append("<=");
				if (out.asyncResetExpression == null)
					buf.append("'0'");
				else if (out.asyncResetExpression.equals("0"))
					buf.append("'0'");
				else if (out.asyncResetExpression.equals("1"))
					buf.append("'1'");
				else
					buf.append(out.asyncResetExpression);
				buf.append(";\n");
				buf.append("    elsif ");
				buf.append(fsm.clkSignalName);
				buf.append("'event and ");
				buf.append(fsm.clkSignalName);
				buf.append("='1' then  \n");
				buf.append("      if ");
				buf.append(out.name);
				buf.append("_set ='1' then ");
				buf.append(out.name);
				buf.append("<='1';\n");
				buf.append("      elsif ");
				buf.append(out.name);
				buf.append("_reset ='1' then ");
				buf.append(out.name);
				buf.append("<='0';\n");
				buf.append("      elsif ");
				buf.append(out.name);
				buf.append("_mem ='1' then ");
				buf.append(out.name);
				buf.append("<= ");
				buf.append(out.name);
				buf.append("_mem_value;\n");
				buf.append("      end if;\n");
				buf.append("    end if;\n");
				buf.append("end process;\n");
			}
		}
		for (int cptActionType = 0; cptActionType < 5; cptActionType++) {
			Boolean processMemorizedOutput;
			String typeFilter;
			String signalSuffix;
			switch (cptActionType) {
			case 0:
				processMemorizedOutput = false;
				typeFilter = "I"; // only I
				signalSuffix = "";
				buf.append("------------------ NON MEMORIZED OUTPUTS ------------\n");
				break;
			case 1:
				processMemorizedOutput = true;
				typeFilter = "R"; // Reset or memorized copy
				signalSuffix = "_reset";
				buf.append("------------------  FLIP FLOPS MEMORIZED RESET OUTPUT CONTROL------------\n");
				break;
			case 2:
				processMemorizedOutput = true;
				typeFilter = "S"; // Set or memorized copy
				signalSuffix = "_set";
				buf.append("------------------  FLIP FLOPS MEMORIZED SET OUTPUT CONTROL------------\n");
				break;
			case 3:
				processMemorizedOutput = true;
				typeFilter = "M"; // Memorize copy
				signalSuffix = "_mem";
				buf.append("------------------  FLIP FLOPS MEMORIZED OUTPUT CONTROL------------\n");
				break;
			case 4:
			default:
				processMemorizedOutput = true;
				typeFilter = "M"; // Memorize copy
				signalSuffix = "_mem_value";
				buf.append("------------------  FLIP FLOPS MEMORIZED OUTPUT VALUE CONTROL------------\n");
				break;
			}
			for (int n = 0; n < fsm.outputs.size(); n++)
				if ((fsm.outputs.get(n).memorized == processMemorizedOutput) && (fsm.outputs.get(n).isUsedAsOutputInFSm)) {
					String currentOutputName = fsm.outputs.get(n).name;
					buf.append("    ");
					buf.append(fsm.outputs.get(n).name);
					buf.append(signalSuffix);
					buf.append(fillStringWithSpace2("", Output.longestName - fsm.outputs.get(n).name.length()));
					buf.append("  <=  ");
					// look for actions in the fsm that deals with this output
					// /////// repeatedly action from any state//
					int nbRepeatedlyAction = fsm.repeatedlyActions.size();
					for (int j = 0; j < nbRepeatedlyAction; j++) {
						Action a = fsm.repeatedlyActions.get(j);
						if (a != null) {
							if (a.name.equals(currentOutputName) && (a.type.equals(typeFilter))) {
								if ((!a.expression.equals("")) && (cptActionType != 3)) {
									// if there is an expression
									buf.append(" ( ");
									buf.append(a.expression);
									buf.append(" ) ");
								} else {
									buf.append(" '1' ");
								}
								// this action is always true
								buf.append("when ( true)   else  \n");
								buf.append(fillStringWithSpace2("", Output.longestName + 10 + signalSuffix.length()));
							}
						}
					}
					// ////////////////////action on reset transition from any
					// state//////////////////////
					int nbResetTransition = fsm.resetTransitions.size();
					for (int j = 0; j < nbResetTransition; j++) {
						ResetTransition rt = fsm.resetTransitions.get(j);
						int nbActionInThatResetTransition = rt.attachedActions.size();
						if (nbActionInThatResetTransition != 0) {
							for (int l = 0; l < nbActionInThatResetTransition; l++) {
								Action a = rt.attachedActions.get(l);
								if (a != null) {
									if (a.name.equals(currentOutputName) && (a.type.equals(typeFilter))) {
										if ((!a.expression.equals("")) && (cptActionType != 3)) {
											// if there is an expression
											buf.append(" ( ");
											buf.append(a.expression);
											buf.append(" ) ");
										} else {
											buf.append(" '1' ");
										}
										// rt.conditionWithPriorities should
										// be!='1' by construction
										buf.append("when ( ( ");
										buf.append(rt.conditionWithPriorities);
										buf.append(") = \'1\' )");
										buf.append("   else --action on reset transition to state ");
										buf.append(rt.destination);
										buf.append(" \n");
										buf.append(fillStringWithSpace2("", Output.longestName + 10 + signalSuffix.length()));
									}
								}
							}
						}
					}
					for (int m = 0; m < fsm.states.size(); m++) {
						// ///// action on transition from this state///
						int nbTransitionInThatState = fsm.states.get(m).transitionsFromThisState.size();
						for (int j = 0; j < nbTransitionInThatState; j++) {
							Transition t = fsm.states.get(m).transitionsFromThisState.get(j);
							int nbActionInThatTransition = t.attachedActions.size();
							if (nbActionInThatTransition != 0) {
								for (int l = 0; l < nbActionInThatTransition; l++) {
									Action a = t.attachedActions.get(l);
									if (a != null) {
										if (a.name.equals(currentOutputName) && (a.type.equals(typeFilter))) {
											if ((!a.expression.equals("")) && (cptActionType != 3)) {
												// if there is an expression
												buf.append(" ( ");
												buf.append(a.expression);
												buf.append(" ) ");
											} else {
												buf.append(" '1' ");
											}
											buf.append("when ( (current_state = ");
											buf.append("state_");
											buf.append(fsm.states.get(m).name);
											buf.append(") ");
											buf.append(" and  (");
											buf.append(" ( ");
											buf.append(t.conditionWithPriorities);
											buf.append(") = \'1\' ");
											buf.append(") ");
											if (fsm.resetTransitionInhibatesTransitionActions == true)
												buf.append(a.condition);
											buf.append(")  else --action on transition from state ");
											buf.append(t.origin);
											buf.append(" to ");
											buf.append(t.destination);
											buf.append("\n");
											buf.append(fillStringWithSpace2("", Output.longestName + 10 + signalSuffix.length()));
										}
									}
								}
							}
						}
						// ///// action on state/////////////
						int nbActionInThatState = fsm.states.get(m).attachedActions.size();
						if (nbActionInThatState != 0) {
							for (int l = 0; l < nbActionInThatState; l++) {
								Action a = fsm.states.get(m).attachedActions.get(l);
								if (a != null) {
									if (a.name.equals(currentOutputName) && (a.type.equals(typeFilter))) {
										// if there is an expression
										if ((!a.expression.equals("")) && (cptActionType != 3)) {
											buf.append(" ( ");
											buf.append(a.expression);
											buf.append(" ) ");
										} else {
											buf.append(" '1' ");
										}
										buf.append("when ( (current_state = ");
										buf.append("state_");
										buf.append(fsm.states.get(m).name);
										buf.append(") ");
										if (fsm.resetTransitionInhibatesActionsOnStates == true)
											buf.append(a.condition);
										buf.append(")   else  --action on state ");
										buf.append(fsm.states.get(m).name);
										buf.append("\n");
										buf.append(fillStringWithSpace2("", Output.longestName + 10 + signalSuffix.length()));
									}
								}
							}
						}
					}
					// this is the default value for all outputs.
					// A S action is automatically added when there is no
					// corresponding R action and vice versa
					buf.append(" \'0\'; \n");
					// TODO: deal with a non default value here, to get outputs
					// at 1 by default for instance
				}
		}
		if (fsm.GenerateNumberOfStateOutput && (fsm.states.size() != 0)) {
			generateStateCodingVhdl(buf, false);
		}
		buf.append("end ar;\n");

	} // ///////////////////////////////////////////////////////////////

	static class ResetTransition implements Comparable<Object> {
		String destination;
		// this string is the expression read from the fsm file
		String condition;
		String paddedCondition;
		// this string is equal to expression if there is no priority between
		// action on (reset) transitions from(to) the same state. if properties
		// are defined, the condition of lower priority action is computed
		// using the complements of higher priority condition
		String conditionWithPriorities;
		ArrayList<Action> attachedActions = new ArrayList<Action>();
		// by default, low priority, 1 is the higher priority
		int priorityOrder = 1000000;

		public int compareTo(Object o) {
			ResetTransition a = (ResetTransition) o;
			return priorityOrder - a.priorityOrder;
		}
	} // ///////////////////////////////////////////////////////////////

	static class Transition implements Comparable<Transition> {
		String origin;
		String destination;
		// this string is the expression read from the fsm file
		String condition;
		String paddedCondition;
		// this string is equal to expression if there is no priority between
		// action on (reset) transitions from(to) the same state. if properties
		// are defined, the condition of lower priority action is computed
		// using the complements of higher priority condition
		String conditionWithPriorities;
		ArrayList<Action> attachedActions = new ArrayList<Action>();
		// by default, low priority, 1 is the higher priority
		int priorityOrder = 1000000;

		public int compareTo(Transition o) {
			Transition a = (Transition) o;
			return priorityOrder - a.priorityOrder;
		}
	} // //////////////////////////////////////////////////////////////////

	static class Action {
		String type; // I, R, S, M, F
		String name;
		String paddedName;
		String expression; // would be a bus expression in the future
		String paddedExpression;
		String condition = ""; // is always a boolean expression, mainly to
								// inhibate the action. for instance inhibate
								// action when a synchronous reset occurs
		String paddedCondition;
	} // //////////////////////////////////////////////////////////////////

	static class Input implements Comparable<Input> {
		// to store the longest names
		public static int longestName = 0;
		String type;
		String name;
		String paddedName;
		// true if the input as been demoted to be only used internaly and not
		// generate a real input in the entity
		Boolean isDemotedToSignal = false;
		// set to true when the input is effectively used in the FSM, not only
		// in pragma
		Boolean isUsedAsInputInFSm = false;
		String interfacePortTypes = "std_logic"; // default

		public int compareTo(Input o) {
			Input a = (Input) o;
			return name.compareTo(a.name);// par ordre alphabétique
		}
	}

	// //////////////////////////////////////////////////////////////////
	static class Output implements Comparable<Output> {
		// to store the longest names
		public static int longestName = 12; // to be at least as long as
											// "STATE_NUMBER"
		String type = "I";
		String name;
		String paddedName;
		Boolean memorized = false;
		String asyncResetExpression = null;
		Boolean isBuffer = false;// to allow the reuse of memorized outputs as
									// inputs in conditions and expressions
		Boolean isDemotedToSignal = false; // true if the output as been demoted
											// to be only used internaly and not
											// generate a real output in the
											// entity
		// set to true when the output is effectively used in the FSM, not only
		// in pragma
		Boolean isUsedAsOutputInFSm = false;
		// for buffer output that are used in conditions by the fsm
		Boolean isUsedAsInputInFSm = false;
		String interfacePortTypes = "std_logic"; // default

		public int compareTo(Output o) {
			Output a = (Output) o;
			return name.compareTo(a.name);// par ordre alphabétique
		}
	}

	// //////////////////////////////////////////////////////////////////
	static class State implements Comparable<State> {
		// to store the longest names
		public static int longestName = 1;
		Boolean isInit; // initial state or not
		String name;
		String paddedName;
		ArrayList<Action> attachedActions = new ArrayList<Action>();
		ArrayList<Transition> transitionsFromThisState = new ArrayList<Transition>();
		// true if there is no (reset) transition or asynchronous reset to that
		// state
		Boolean isAlone;
		int nbTimeFoundInFSMFile = 0;
		// to store the binary value that is put on state_number output when
		// this state is active
		public String stateCode = "";
		public String nameDisplay = "";

		public int compareTo(State o) {
			State a = (State) o;
			return name.compareTo(a.name);// par ordre alphabétique
		}
	}

	// //////////////////////////////////////////////////////////////////

	final static String fillStringWithSpace2(String stringToFill, int minLength) {
		if (stringToFill.length() >= minLength)
			return stringToFill;
		else {
			StringBuilder builder = new StringBuilder(stringToFill);
			while (builder.length() < minLength)
				builder = builder.append(" ");
			return builder.toString();
		}
	}

	static class FiniteStateMachine {
		public FiniteStateMachine() {
			// set sub members values
			State.longestName = 1;
			Output.longestName = 12;
			Input.longestName = 1;
			// set members values
			clkSignalName = "CK";
			aResetSignalName = "ARAZB";
			clkSignalNameSpecified = false;
			pragmaVhdlPreEntity = "";
			pragmaVhdlEntity = "";
			pragmaVhdlArchitecturePreBegin = "";
			pragmaVhdlArchitecturePostBegin = "";
			pragmaVhdlTestbench = "";
			pragmaDotGlobal = "";
			stateCodeError = "";
			bufferedOutputsAllowed = false;
			resetTransitions = new ArrayList<ResetTransition>();
			states = new ArrayList<State>();
			hmapState = new HashMap<String, State>();
			inputs = new ArrayList<Input>();
			hmapInput = new HashMap<String, Input>();
			outputs = new ArrayList<Output>();
			hmapOutput = new HashMap<String, Output>();
			transitions = new ArrayList<Transition>();
			hmapTransition = new HashMap<String, Transition>();
			actions = new ArrayList<Action>();
			repeatedlyActions = new ArrayList<Action>();
			noRepeatedlyActions = new ArrayList<Action>();
			outputsWithAsynchronousResetValue = new ArrayList<Output>();
			listOfInterfaceNamesToAddThroughPragma = new ArrayList<String>();
			listOfInterfacePortTypesToAddThroughPragma = new ArrayList<String>();
			realOutputs = new ArrayList<Output>();
			demotedToSignalOutputs = new ArrayList<Output>();
			realInputs = new ArrayList<Input>();
			demotedToSignalInputs = new ArrayList<Input>();
			genericDeclarations = new ArrayList<GenericDeclaration>();
			GenerateNumberOfStateOutput = true;
			numberOfBitsForStates = 0;
			numberOfResetAsynchronousDefinitions = 0;
			resetAsynchronousState = null;
			resetTransitionInhibatesTransitionActions = true;
			resetTransitionInhibatesActionsOnStates = true;
			currentState = null;
			currentAction = null;
			currentOutput = null;
			currentInput = null;
			currentTransition = null;
			currentResetTransition = null;
			inputsOrderedNamesList = new ArrayList<String>();
			outputsOrderedNamesList = new ArrayList<String>();
			imageFileExtension = "gif";
		}

		// member variables for storing the fsm model
		public String name;
		public String clkSignalName;// = "CK"; // caps to be compared with
									// parsed
									// names
		public Boolean clkSignalNameSpecified;// = false;
		public String aResetSignalName;// = "ARAZB"; // caps to be compared with
										// parsed names
		public String aResetSignalLevel = "0";

		// to contain added vhdl code
		public String pragmaVhdlPreEntity;
		public String pragmaVhdlEntity;
		public String pragmaVhdlArchitecturePreBegin;
		public String pragmaVhdlArchitecturePostBegin;
		public String pragmaVhdlTestbench;
		public String pragmaDotGlobal;
		// to store the binary value that is put on state_number output when
		// error
		public String stateCodeError;
		public String stateNameDisplayError;

		// global flag to allow the reuse of memorized outputs as inputs in
		// conditions and expressions
		public Boolean bufferedOutputsAllowed;// = false;

		ArrayList<ResetTransition> resetTransitions;// = new
													// ArrayList<ResetTransition>();
		ArrayList<State> states;// = new ArrayList<State>();
		HashMap<String, State> hmapState;// = new HashMap<String, State>();
		ArrayList<Input> inputs;// = new ArrayList<Input>();
		HashMap<String, Input> hmapInput;// = new HashMap<String, Input>();
		ArrayList<Output> outputs;// = new ArrayList<Output>();
		HashMap<String, Output> hmapOutput;// = new HashMap<String, Output>();
		ArrayList<Transition> transitions;// = new ArrayList<Transition>();
		HashMap<String, Transition> hmapTransition;// = new HashMap<String,
													// Transition>();
		// global actions list
		ArrayList<Action> actions;// = new ArrayList<Action>();
		// each Action in actions list is either one of the 2 following:
		ArrayList<Action> repeatedlyActions;// = new ArrayList<Action>();
		ArrayList<Action> noRepeatedlyActions;// = new ArrayList<Action>();

		ArrayList<Output> outputsWithAsynchronousResetValue;// = new
															// ArrayList<Output>();

		ArrayList<String> listOfInterfaceNamesToAddThroughPragma;// = new
																	// ArrayList<String>();
		ArrayList<String> listOfInterfacePortTypesToAddThroughPragma;// = new
																		// ArrayList<String>();
		String InterfacePortModeToAddThroughPragma;

		// each output in outputs list is also located in one of this two lists
		ArrayList<Output> realOutputs;// = new ArrayList<Output>();
		ArrayList<Output> demotedToSignalOutputs;// = new ArrayList<Output>();

		// each input in inputs list is also located in one of this two lists
		ArrayList<Input> realInputs;// = new ArrayList<Input>();
		ArrayList<Input> demotedToSignalInputs;// = new ArrayList<Input>();

		ArrayList<GenericDeclaration> genericDeclarations;// = new
															// ArrayList<GenericDeclaration>();
		GenericDeclaration currentGenericDeclarations;

		public Boolean GenerateNumberOfStateOutput;// = true;
		// default value if not given in the fsm file, can be set through pragma
		public int numberOfBitsForStates;// = 0;
		// value computed from the actual number of state or the given
		// numberOfBitsForStates it is bigger
		public int numberOfBitsForStatesMax;
		// member variables for parsing
		int numberOfResetAsynchronousDefinitions;// = 0;

		public State resetAsynchronousState;// = null;

		// if set to true, will make the actions on transition to be inhibated
		// during reset
		Boolean resetTransitionInhibatesTransitionActions;// = true;
		// if set to true, will make the actions on state to be inhibated
		// during reset
		Boolean resetTransitionInhibatesActionsOnStates;// = true;
		// this string is computed only if there are some reset transition(s)
		// and either resetTransitionInhibatesTransitionActions or
		// resetTransitionInhibatesActionsOnStates is true
		String resetConditionComplement;

		public State currentState;// = null;
		public Action currentAction;// = null;
		public Output currentOutput;// = null;
		public Input currentInput;// = null;
		public Transition currentTransition;// = null;
		public ResetTransition currentResetTransition;// = null;
		public boolean currentTransitionIsReset;

		public ArrayList<String> inputsOrderedNamesList;// = new
														// ArrayList<String>();
		public ArrayList<String> outputsOrderedNamesList;// = new
															// ArrayList<String>();

		public String imageFileExtension;// = "gif";

		List<String> forbiddenNamesVHDL = Arrays.asList("ABS", "ACCESS", "AFTER", "ALIAS", "ALL", "AND", "ARCHITECTURE", "ARRAY", "ASSERT",
				"ATTRIBUTE", "BEGIN", "BLOCK", "BODY", "BUFFER", "BUS", "CASE", "COMPONENT", "CONFIGURATION", "CONSTANT", "DISCONNECT",
				"DOWNTO", "ELSE", "ELSIF", "END", "ENTITY", "EXIT", "FILE", "FOR", "FUNCTION", "GENERATE", "GENERIC", "GROUP", "GUARDED",
				"IF", "IMPURE", "IN", "INERTIAL", "INOUT", "IS", "LABEL", "LIBRARY", "LINKAGE", "LITERAL", "LOOP", "MAP", "MOD", "NAND",
				"NEW", "NEXT", "NOR", "NOT", "NULL", "OF", "ON", "OPEN", "OR", "OTHERS", "OUT", "PACKAGE", "PORT", "POSTPONED",
				"PROCEDURE", "PROCESS", "PURE", "RANGE", "RECORD", "REGISTER", "REJECT", "REM", "REPORT", "RETURN", "ROL", "ROR", "SELECT",
				"SEVERITY", "SIGNAL", "SHARED", "SLA", "SLL", "SRA", "SRL", "SUBTYPE", "THEN", "TO", "TRANSPORT", "TYPE", "UNAFFECTED",
				"UNITS", "UNTIL", "USE", "VARIABLE", "WAIT", "WHEN", "WHILE", "WITH", "XNOR", "XOR", "TIME");
		// TODO complete lists
		// TODO: add methods to generate string that lists the words for the
		// documentation
		List<String> forbiddenNamesFSM = Arrays.asList("value_one_internal", "not_any_s_reset_internal", "STATE_NUMBER",
				"#pragma_vhdl_pre_entity{", "#pragma_vhdl_entity{", "#pragma_vhdl_architecture_pre_begin{",
				"#pragma_vhdl_architecture_post_begin{", "#pragma_vhdl_promote_buffered{", "#pragma_vhdl_demote_output_to_signal{",
				"#pragma_vhdl_demote_input_to_signal{", "#pragma_vhdl_allow_automatic_buffering",
				"#pragma_vhdl_set_bit_size_for_output_state_number{", "}#pragma", "i", "I", "r", "R", "s", "S", "m", "M", "f", "F");
		List<String> forbiddenNamesC = Arrays.asList();
		List<String> forbiddenNamesVerilog = Arrays.asList();

		// VHDL list gotten from:
		// http://www.csee.umbc.edu/portal/help/VHDL/reserved.html
		public Boolean checkNameIsNotForbidden(String name, String type) {
			if (forbiddenNamesVHDL.contains(name)) {
				bufLogError.append("Critical Error: ");
				bufLogError.append(type);
				bufLogError.append(" ");
				bufLogError.append(name);
				bufLogError.append(" is a reserved VHDL word and should not be used.\n");
				return false;
			} else if (forbiddenNamesFSM.contains(name)) {
				bufLogError.append("Critical Error: ");
				bufLogError.append(type);
				bufLogError.append(" ");
				bufLogError.append(name);
				bufLogError.append(" is a reserved FSM word and should not be used.\n");
				return false;
			} else if (forbiddenNamesC.contains(name)) {
				bufLogError.append("Critical Error: ");
				bufLogError.append(type);
				bufLogError.append(" ");
				bufLogError.append(name);
				bufLogError.append(" is a reserved C word and should not be used.\n");
				return false;
			} else if (forbiddenNamesVerilog.contains(name)) {
				bufLogError.append("Critical Error: ");
				bufLogError.append(type);
				bufLogError.append(" ");
				bufLogError.append(name);
				bufLogError.append(" is a reserved Verilog word and should not be used.\n");
				return false;
			} else
				return true;
		}

		public Boolean checkIfNameNotAnInput(String name, String type) {
			if (hmapInput.containsKey(name)) {
				bufLogError.append("Critical Error: ");
				bufLogError.append(type);
				bufLogError.append(" ");
				bufLogError.append(name);
				bufLogError.append(" is already defined as an input.\n");
				return false;
			} else
				return true;
		}

		public Boolean checkIfNameNotAnOutput(String name, String type) {
			if (hmapOutput.containsKey(name)) {
				bufLogError.append("Critical Error: ");
				bufLogError.append(type);
				bufLogError.append(" ");
				bufLogError.append(name);
				bufLogError.append(" is already defined as an output.\n");
				return false;
			} else
				return true;
		}

		public Boolean checkIfNameIsAsynchronousReset(String name, String type) {
			if (name.equals(aResetSignalName)) {
				bufLogError.append("Critical Error: ");
				bufLogError.append(type);
				bufLogError.append(" ");
				bufLogError.append(name);
				bufLogError.append(" is already defined as the Asynchronous Reset input.\n");
				return false;
			} else
				return true;
		}

		public Boolean checkIfNameIsClock(String name, String type) {
			if (name.equals(clkSignalName)) {
				bufLogError.append("Critical Error: ");
				bufLogError.append(type);
				bufLogError.append(" ");
				bufLogError.append(name);
				bufLogError.append(" is already defined as the Clock input.\n");
				return false;
			} else
				return true;
		}

		public State getStateFromName(String name) {
			if (!hmapState.containsKey(name))
				return null;
			else
				return hmapState.get(name);
		}

		public void setThisStateAsResetAsynchronousState(String reset_asynchronous_state_new_name) {
			// is there already a defined resetAsynchronousState
			if (fsm.resetAsynchronousState != null) {
				fsm.numberOfResetAsynchronousDefinitions++;
				bufLogInfo.append("Info: Previous asynchronous reset state ");
				bufLogInfo.append(fsm.resetAsynchronousState.name);
				bufLogInfo.append(" replaced by ");
				bufLogInfo.append(reset_asynchronous_state_new_name);
				bufLogInfo.append(" \n");
				// the previous fsm.resetAsynchronousState is not anymore
				resetAsynchronousState.isInit = false;
			}
			// create the state if necessary
			State sd = fsm.getStateOrCreateAndAdd(reset_asynchronous_state_new_name);
			sd.isInit = true;
			resetAsynchronousState = sd;
		}

		public State getStateOrCreateAndAdd(String name) {
			// if this has already been added, return it
			State s = hmapState.get(name);
			if (s == null) {
				s = new State();
				s.name = name;
				if (states.size() == 0) {
					bufLogInfo.append("Info:   Asynchronous reset state is defined to ");
					bufLogInfo.append(name);
					bufLogInfo.append(" \n");
					s.isInit = true;
					resetAsynchronousState = s;
				} else
					s.isInit = false;
				fsm.hmapState.put(name, s); // else add it to the hash map
				states.add(s); // and to the list
				if (s.name.length() > State.longestName)
					State.longestName = s.name.length();
			}
			return s;
		}

		public Input getInputFromName(String name) {
			if (!hmapInput.containsKey(name))
				return null;
			else
				return hmapInput.get(name);
		}

		public boolean addInput(String name, Input i) {
			if (hmapInput.containsKey(name) || hmapInput.containsValue(i))
				return false; // if this has already been added, return false
			else {
				fsm.hmapInput.put(name, i); // else add it to the hash map
				inputs.add(i); // and to the list
				if (i.name.length() > Input.longestName)
					Input.longestName = i.name.length();
				return true;
			}
		}

		public Output getOutputFromName(String name) {
			if (!hmapOutput.containsKey(name))
				return null;
			else
				return hmapOutput.get(name);
		}

		public boolean addOutput(String name, Output o) {
			if (hmapOutput.containsKey(name) || hmapOutput.containsValue(o))
				return false; // if this has already been added, return false
			else {
				fsm.hmapOutput.put(name, o); // else add it to the hash map
				outputs.add(o); // and to the list
				if (o.name.length() > Output.longestName)
					Output.longestName = o.name.length();
				return true;
			}
		}

		public Transition getTransitionFromName(String name) {
			if (!hmapTransition.containsKey(name))
				return null;
			else
				return hmapTransition.get(name);
		}

		public boolean addTransition(String name, Transition t) {
			if (hmapTransition.containsKey(name) || hmapTransition.containsValue(t))
				return false; // if this has already been added, return false
			else {
				fsm.hmapTransition.put(name, t); // else add it to the hash map
				transitions.add(t); // and to the list
				return true;
			}
		}
	}

	// ///////////////////////////////////////////////////////////////
	static class MultiTransitions {
		// class used to parse muliple transitions defined at once before they
		// are added in the fsm
		String baseStateName;
		String condition;
		Integer priority, first, last;
		// for multiple transition to a same state, store the name of the
		// destination name
		String destinationState;
	}

	// ///////////////////////////////////////////////////////////////
	static class GenericDeclaration {
		String Name;
		String Type;
		String Default;
	}

	// ///////////////////////////////////////////////////////////////
	static class FunctionListener extends FsmParserBaseListener {
		// TODO: keep the tokens list availabe to get them and extract some info
		// (such as line numbers for errors, pragmas etc...
		BufferedTokenStream tokens;

		MultiTransitions multiTransitions = new MultiTransitions();

		// TODO: make it clearer::::::
		static String pragmaCleaned;

		public FunctionListener(BufferedTokenStream tokens) {
			this.tokens = tokens;
		}

		// ///////////////////////////////////////////////////////////////
		public void enterReset_asynchronous(FsmParser.Reset_asynchronousContext ctx) {
			String reset_asynchronous_state_new_name = ctx.children.get(1).getText().toUpperCase();
			fsm.setThisStateAsResetAsynchronousState(reset_asynchronous_state_new_name);
		}

		// ///////////////////////////////////////////////////////////////
		public void enterCondition_reset_asynchronous(FsmParser.Condition_reset_asynchronousContext ctx) {
			fsm.aResetSignalName = ctx.children.get(0).getText().toUpperCase();
		}

		// ///////////////////////////////////////////////////////////////
		public void enterClock_definition(FsmParser.Clock_definitionContext ctx) {
			if (fsm.clkSignalNameSpecified) {
				bufLogWarning.append("Warning:   Clock signal has been redefined more than one time...\n");
			}
			fsm.clkSignalName = ctx.children.get(1).getText().toUpperCase();
			fsm.clkSignalNameSpecified = true;
			bufLogInfo.append("Info:   Clock signal is defined as: ");
			bufLogInfo.append(fsm.clkSignalName);
			bufLogInfo.append("\n");
		}

		// ///////////////////////////////////////////////////////////////
		public void enterInput(FsmParser.InputContext ctx) {
			Input i = new Input();
			i.name = ctx.children.get(0).getText().toUpperCase();
			i.isUsedAsInputInFSm = true;
			fsm.addInput(ctx.children.get(0).getText().toUpperCase(), i);
		}

		// ///////////////////////////////////////////////////////////////
		public void enterReset_transition(FsmParser.Reset_transitionContext ctx) {
			fsm.currentTransitionIsReset = true;
			ResetTransition rt = new ResetTransition();
			fsm.currentResetTransition = rt;
			rt.condition = "1"; // default condition
			rt.destination = ctx.children.get(1).getText().toUpperCase();
			// add the transition in its origin state, first get the state from
			// its name if the states do not yet exist, create them
			fsm.getStateOrCreateAndAdd(rt.destination);
			fsm.resetTransitions.add(rt);
		}

		// ///////////////////////////////////////////////////////////////
		public void enterCondition(FsmParser.ConditionContext ctx) {
			String reconstructedCondition = new String("");
			int nbChildren = ctx.getChildCount();
			for (int n = 0; n < nbChildren; n++)
			// reconstruct the condition, adding space characters between terms.
			{
				reconstructedCondition += ctx.children.get(n).getText().toUpperCase();
				if (n != nbChildren - 1)
					reconstructedCondition += " ";
			}
			// put the condition at the right place in the fsm
			if (fsm.currentTransitionIsReset)
				fsm.currentResetTransition.condition = reconstructedCondition;
			else
				fsm.currentTransition.condition = reconstructedCondition;
		}

		// //////////////////////////////////////////////////////////////
		public void enterTransition(FsmParser.TransitionContext ctx) {
			fsm.currentTransitionIsReset = false;
			Transition t = new Transition();
			t.condition = "1"; // default
			fsm.currentTransition = t;
			t.origin = ctx.children.get(0).getText().toUpperCase();
			t.destination = ctx.children.get(2).getText().toUpperCase();
			// add the transition in its origin state, first get the state from
			// its name if the states do not yet exist, create them
			State so = fsm.getStateOrCreateAndAdd(t.origin);
			fsm.getStateOrCreateAndAdd(t.destination);
			so.transitionsFromThisState.add(t);
			// also add it to the global transitions list
			fsm.transitions.add(t);
		}

		// //////////////////////////////////////////////////////////////
		public void enterReset_transition_priority(FsmParser.Reset_transition_priorityContext ctx) {
			fsm.currentResetTransition.priorityOrder = Integer.parseInt(ctx.children.get(0).getText());
		}

		// //////////////////////////////////////////////////////////////
		public void enterTransition_priority(FsmParser.Transition_priorityContext ctx) {
			fsm.currentTransition.priorityOrder = Integer.parseInt(ctx.children.get(0).getText());
		}

		// //////////////////////////////////////////////////////////////
		public void enterAction_expression(FsmParser.Action_expressionContext ctx) {
			String reconstructedExpression = new String("");
			int nbChildren = ctx.getChildCount();
			for (int n = 0; n < nbChildren; n++) {
				// reconstruct the condition, adding space characters between
				// terms.
				reconstructedExpression += ctx.children.get(n).getText().toUpperCase();
				if (n != nbChildren - 1)
					reconstructedExpression += " ";
			}
			fsm.currentAction.expression = reconstructedExpression;
		}

		// //////////////////////////////////////////////////////////////
		public void enterLevel_reset_asynchronous(FsmParser.Level_reset_asynchronousContext ctx) {
			fsm.aResetSignalLevel = ctx.children.get(0).getText().toUpperCase();
			if (!fsm.aResetSignalLevel.equals("0") && !fsm.aResetSignalLevel.equals("1")) {
				bufLogWarning
						.append("Warning:   Asynchronous reset level for input should be either O or 1 because modern FPGAs don't have circuitry to route another signal.\n");
			}
			fsm.numberOfResetAsynchronousDefinitions++;
		}

		// //////////////////////////////////////////////////////////////
		public void enterAction_expression_reset_asynchronous(FsmParser.Action_expression_reset_asynchronousContext ctx) {
			String reconstructedExpression = new String("");
			int nbChildren = ctx.getChildCount();
			for (int n = 0; n < nbChildren; n++) {
				// reconstruct the condition, adding space characters between
				// terms.
				reconstructedExpression += ctx.children.get(n).getText().toUpperCase();
				if (n != nbChildren - 1)
					reconstructedExpression += " ";
			}
			if (!reconstructedExpression.equals("0") && !reconstructedExpression.equals("1")) {
				bufLogWarning.append("Warning:   Asynchronous reset value for output ");
				bufLogWarning.append(fsm.currentOutput.name);
				bufLogWarning.append(" should not be ");
				bufLogWarning.append(reconstructedExpression);
				bufLogWarning.append(" but either O or 1 because modern FPGAs don't have circuitry to route another signal.\n");
				bufLogWarning
						.append("     Moreover, the inputs in the expression will not be added automatically to the process sensitivity list, probably resulting in errors or warnings.\n");
			}
			fsm.currentOutput.asyncResetExpression = reconstructedExpression;
			fsm.currentOutput.isUsedAsOutputInFSm = true;
		}

		// //////////////////////////////////////////////////////////////
		public void enterAction_id_reset_asynchronous(FsmParser.Action_id_reset_asynchronousContext ctx) {
			String outputName = ctx.children.get(0).getText().toUpperCase();
			fsm.currentOutput = fsm.getOutputFromName(outputName);
			if (fsm.currentOutput == null) {
				Output o = new Output();
				o.name = outputName;
				fsm.addOutput(outputName, o);
				fsm.currentOutput = o;
			}
			// there is no yet Action_expression_reset_asynchronous, only its
			// name, so lets give a defaut "1" expression, that can be updated
			// later
			fsm.currentOutput.isUsedAsOutputInFSm = true;
			fsm.currentOutput.asyncResetExpression = "1"; // default
			fsm.outputsWithAsynchronousResetValue.add(fsm.currentOutput);
			fsm.currentOutput.memorized = true; // check that the output is
												// memorized
		}

		// //////////////////////////////////////////////////////////////
		public void enterAction_id(FsmParser.Action_idContext ctx) {
			String outputName = ctx.children.get(0).getText().toUpperCase();
			fsm.currentAction.name = outputName;
			fsm.currentOutput = fsm.getOutputFromName(outputName);
			// Create the output corresponding to the action if it does not yet
			// exist
			if (fsm.currentOutput == null) {
				Output o = new Output();
				o.name = outputName;
				if (fsm.currentAction.type.equals("M") || fsm.currentAction.type.equals("R") || fsm.currentAction.type.equals("S"))
					o.memorized = true;
				else
					o.memorized = false;
				fsm.addOutput(outputName, o);
				fsm.currentOutput = o;
				o.isUsedAsOutputInFSm = true;
			}
		}

		// //////////////////////////////////////////////////////////////
		public void enterAction_type(FsmParser.Action_typeContext ctx) {
			fsm.currentAction.type = ctx.children.get(0).getText().toUpperCase().toUpperCase();
		}

		// //////////////////////////////////////////////////////////////
		public void enterTransition_action(FsmParser.Transition_actionContext ctx) {
			Action a = new Action();
			fsm.currentAction = a;
			a.type = "I"; // default value if not specified
			a.name = ""; // default value if not specified
			a.expression = ""; // default value if not specified
			if (!fsm.currentTransitionIsReset)
				fsm.currentTransition.attachedActions.add(a);
			else
				fsm.currentResetTransition.attachedActions.add(a);
			fsm.actions.add(a); // add to the global action list
			fsm.noRepeatedlyActions.add(a);
		}

		// //////////////////////////////////////////////////////////////
		public void enterState_action(FsmParser.State_actionContext ctx) {
			Action a = new Action();
			fsm.currentAction = a;
			a.type = "I"; // default value if not specified
			a.name = ""; // default value if not specified
			a.expression = ""; // default value if not specified
			fsm.currentState.attachedActions.add(a);
			fsm.actions.add(a);// add to the global action list
			fsm.noRepeatedlyActions.add(a);
		}

		// //////////////////////////////////////////////////////////////
		public void enterRepeatedly_action(FsmParser.Repeatedly_actionContext ctx) {
			Action a = new Action();
			fsm.currentAction = a;
			a.type = "I"; // default value if not specified
			a.name = ""; // default value if not specified
			a.expression = ""; // default value if not specified
			fsm.repeatedlyActions.add(a);
			fsm.actions.add(a);// add to the global action list
		}

		// //////////////////////////////////////////////////////////////

		public void enterState(FsmParser.StateContext ctx) {
			String name = ctx.children.get(0).getText().toUpperCase();
			State s = fsm.getStateOrCreateAndAdd(name);
			s.nbTimeFoundInFSMFile++;
			if (s.nbTimeFoundInFSMFile > 1) {
				bufLogWarning.append("Warning: State ");
				bufLogWarning.append(name);
				bufLogWarning.append(" has already been defined in the model when found in the fsm file.\n");
			}
			fsm.currentState = s;
		}

		// ///////////////////////////////////////////////////////////////
		public void enterInterface_name(FsmParser.Interface_nameContext ctx) {
			String interface_name = ctx.children.get(0).getText();
			fsm.listOfInterfaceNamesToAddThroughPragma.add(interface_name.toUpperCase());
		}

		// ///////////////////////////////////////////////////////////////
		public void enterInterface_port_mode(FsmParser.Interface_port_modeContext ctx) {
			String interface_port_mode = ctx.children.get(0).getText();
			// convert to toUpperCase to compare it with IN, OUT, BUFFER...
			fsm.InterfacePortModeToAddThroughPragma = interface_port_mode.toUpperCase();
			// prepare type list
			fsm.listOfInterfacePortTypesToAddThroughPragma.clear();
		}

		// ///////////////////////////////////////////////////////////////
		public void enterInterface_port_type(FsmParser.Interface_port_typeContext ctx) {
			int nbTokens = ctx.children.size();
			for (int i = 0; i < nbTokens; i++) {
				String stToken = ctx.children.get(i).getText();
				fsm.listOfInterfacePortTypesToAddThroughPragma.add(stToken);
			}
		}

		// ///////////////////////////////////////////////////////////////
		public void enterInterface_port_declaration(FsmParser.Interface_port_declarationContext ctx) {
			fsm.listOfInterfaceNamesToAddThroughPragma.clear();
		}

		// ///////////////////////////////////////////////////////////////
		public void exitInterface_port_declaration(FsmParser.Interface_port_declarationContext ctx) {
			int nb = fsm.listOfInterfaceNamesToAddThroughPragma.size();
			for (int k = 0; k < nb; k++) {
				String name = fsm.listOfInterfaceNamesToAddThroughPragma.get(k);
				int nbtokens = fsm.listOfInterfacePortTypesToAddThroughPragma.size();
				String interfacePortTypes = "";
				for (int j = 0; j < nbtokens; j++) {
					interfacePortTypes += fsm.listOfInterfacePortTypesToAddThroughPragma.get(j);
					interfacePortTypes += " ";
				}
				if (fsm.InterfacePortModeToAddThroughPragma.equals("IN")) {
					Input i = fsm.getInputFromName(name);
					if (i == null) {
						i = new Input();
						i.name = fsm.listOfInterfaceNamesToAddThroughPragma.get(k);
						i.type = "I";
						i.isDemotedToSignal = false;
						fsm.addInput(i.name, i);
					}
					i.interfacePortTypes = interfacePortTypes;
				} else if (fsm.InterfacePortModeToAddThroughPragma.equals("OUT")
						|| fsm.InterfacePortModeToAddThroughPragma.equals("BUFFER")) {
					Output o = fsm.getOutputFromName(name);
					if (o == null) {
						o = new Output();
						o.name = fsm.listOfInterfaceNamesToAddThroughPragma.get(k);
						o.type = "I";
						o.isDemotedToSignal = false;
						fsm.addOutput(o.name, o);
					}
					if (fsm.InterfacePortModeToAddThroughPragma.equals("BUFFER"))
						o.isBuffer = true;
					o.interfacePortTypes = interfacePortTypes;
				}
			}
		}

		// ///////////////////////////////////////////////////////////////
		public void exitInterface_port_type(FsmParser.Interface_port_typeContext ctx) {
		}

		// ///////////////////////////////////////////////////////////////
		public void exitPragma_vhdl_entity_directive(FsmParser.Pragma_vhdl_entity_directiveContext ctx) {
		}

		// ///////////////////////////////////////////////////////////////
		public void enterPragma_vhdl_entity_directive(FsmParser.Pragma_vhdl_entity_directiveContext ctx) {
		}

		// ///////////////////////////////////////////////////////////////
		public void enterPragma_vhdl_architecture_pre_begin_directive(FsmParser.Pragma_vhdl_architecture_pre_begin_directiveContext ctx) {
			String pragma = ctx.children.get(1).getText();
			String pragmaCleaned = pragma.substring(1, pragma.length() - 8);
			fsm.pragmaVhdlArchitecturePreBegin += pragmaCleaned;
		}

		// ///////////////////////////////////////////////////////////////
		public void enterPragma_vhdl_architecture_post_begin_directive(FsmParser.Pragma_vhdl_architecture_post_begin_directiveContext ctx) {
			String pragma = ctx.children.get(1).getText();
			String pragmaCleaned = pragma.substring(1, pragma.length() - 8);
			fsm.pragmaVhdlArchitecturePostBegin += pragmaCleaned;
		}

		// ///////////////////////////////////////////////////////////////
		public void enterPragma_vhdl_testbench(FsmParser.Pragma_vhdl_testbenchContext ctx) {
			String pragma = ctx.children.get(1).getText();
			String pragmaCleaned = pragma.substring(1, pragma.length() - 8);
			fsm.pragmaVhdlTestbench += pragmaCleaned;
		}

		// ///////////////////////////////////////////////////////////////

		public void enterPragma_vhdl_pre_entity_directive(FsmParser.Pragma_vhdl_pre_entity_directiveContext ctx) {
			// simple way to get the pragma
			String pragma = ctx.children.get(1).getText();
			String pragmaCleaned = pragma.substring(1, pragma.length() - 8);
			fsm.pragmaVhdlPreEntity += pragmaCleaned;
		}

		// ///////////////////////////////////////////////////////////////
		public void enterInput_or_output_to_promote_to_buffer(FsmParser.Input_or_output_to_promote_to_bufferContext ctx) {
			String inputOrOutputName = ctx.children.get(0).getText().toUpperCase();
			fsm.currentOutput = fsm.getOutputFromName(inputOrOutputName);
			if (fsm.currentOutput == null) {
				fsm.currentInput = fsm.getInputFromName(inputOrOutputName);
				if (fsm.currentInput == null) {
					bufLogWarning.append("Warning: The pragma that would promote ");
					bufLogWarning.append(inputOrOutputName);
					bufLogWarning.append(" cannot be taken into account as this name is not yet defined as an input or output...\n");
				} else {
					// create a new output with the same name, the 2 will be
					// merge in checkModel()
					Output o = new Output();
					o.name = inputOrOutputName;
					fsm.addOutput(inputOrOutputName, o);
					fsm.currentOutput = o;
					// TODO: if the inputs appears later to be used in
					// memorized, it will generate an error.... because this
					// created temp output is 'I' by default
					bufLogInfo.append("Info: The input ");
					fsm.currentOutput.isBuffer = true;
					fsm.currentOutput.isUsedAsOutputInFSm = false;
					bufLogInfo.append(fsm.currentOutput.name);
					bufLogInfo.append(" has been promoted to a buffered output through pragma directive.\n");
				}
			} else {
				bufLogInfo.append("Info: The output ");
				fsm.currentOutput.isBuffer = true;
				fsm.currentOutput.isUsedAsOutputInFSm = true;
				bufLogInfo.append(fsm.currentOutput.name);
				bufLogInfo.append(" has been promoted to a buffered output through pragma directive.\n");
			}
		}

		// ///////////////////////////////////////////////////////////////
		public void enterInput_or_output_to_demote_to_signal(FsmParser.Input_or_output_to_demote_to_signalContext ctx) {
			String inputOrOutputName = ctx.children.get(0).getText().toUpperCase();
			fsm.currentOutput = fsm.getOutputFromName(inputOrOutputName);
			if (fsm.currentOutput == null) {
				fsm.currentInput = fsm.getInputFromName(inputOrOutputName);
				if (fsm.currentInput == null) {
					bufLogWarning.append("Warning: The pragma that would demote ");
					bufLogWarning.append(inputOrOutputName);
					bufLogWarning.append(" cannot be taken into account as this name is not yet defined as an input or output...\n");
					return;
				} else {
					fsm.currentInput.isDemotedToSignal = true;
					bufLogInfo.append("Info: The input ");
					bufLogInfo.append(fsm.currentInput.name);
					bufLogInfo.append(" has been demoted to an internal signal through pragma directive.\n");
				}
			} else {
				fsm.currentOutput.isUsedAsOutputInFSm = true;
				fsm.currentOutput.isDemotedToSignal = true;
				bufLogInfo.append("Info: The output ");
				bufLogInfo.append(fsm.currentOutput.name);
				bufLogInfo.append(" has been demoted to an internal signal through pragma directive.\n");
			}
		}

		// ///////////////////////////////////////////////////////////////
		public void enterPragma_vhdl_allow_automatic_buffering(FsmParser.Pragma_vhdl_allow_automatic_bufferingContext ctx) {
			fsm.bufferedOutputsAllowed = true;
			bufLogInfo.append("Info: Automatic buffering of outputs used as inputs have been granted through pragma directive.\n");
		}

		// ///////////////////////////////////////////////////////////////
		public void enterBit_size_for_output_state_number(FsmParser.Bit_size_for_output_state_numberContext ctx) {
			int value = Integer.parseInt(ctx.children.get(0).getText());
			if (value > 0 && value < 1000) {
				fsm.numberOfBitsForStates = value;
				bufLogInfo.append("Info: The number of bits for current state number visualization has been set to ");
				bufLogInfo.append(fsm.numberOfBitsForStates);
				bufLogInfo.append(" through pragma  directive\n");
			} else {
				bufLogWarning
						.append("Warning: The number of bits for current state number visualization set through pragma  directive should be between 1 and 1000\n");
			}
		}

		// ///////////////////////////////////////////////////////////////
		public void enterMulti_transitions_directive(FsmParser.Multi_transitions_directiveContext ctx) {
			// set default values if they are not set by optional parameters
			multiTransitions.baseStateName = "";
			multiTransitions.priority = 1000000; // default value
			multiTransitions.condition = "1";
		}

		// ///////////////////////////////////////////////////////////////
		public void enterMulti_transitions_base_state_name(FsmParser.Multi_transitions_base_state_nameContext ctx) {
			multiTransitions.baseStateName = ctx.children.get(0).getText().toUpperCase();
		}

		// ///////////////////////////////////////////////////////////////
		public void enterMulti_transitions_first_state_number(FsmParser.Multi_transitions_first_state_numberContext ctx) {
			multiTransitions.first = Integer.parseInt(ctx.children.get(0).getText());
		}

		// ///////////////////////////////////////////////////////////////
		public void enterMulti_transitions_last_state_number(FsmParser.Multi_transitions_last_state_numberContext ctx) {
			multiTransitions.last = Integer.parseInt(ctx.children.get(0).getText());
		}

		// ///////////////////////////////////////////////////////////////
		public void enterMulti_transitions_priority(FsmParser.Multi_transitions_priorityContext ctx) {
			multiTransitions.priority = Integer.parseInt(ctx.children.get(0).getText());
		}

		// ///////////////////////////////////////////////////////////////
		public void enterCondition_multi_transitions(FsmParser.Condition_multi_transitionsContext ctx) {
			String reconstructedCondition = new String("");
			int nbChildren = ctx.getChildCount();
			for (int n = 0; n < nbChildren; n++) {
				// reconstruct the condition, adding space characters between
				// terms.
				reconstructedCondition += ctx.children.get(n).getText().toUpperCase();
				if (n != nbChildren - 1)
					reconstructedCondition += " ";
			}
			multiTransitions.condition = reconstructedCondition;
		}

		// ///////////////////////////////////////////////////////////////
		public void exitMulti_transitions_directive(FsmParser.Multi_transitions_directiveContext ctx) {
			// do the actual job once all parameters have been gathered
			// ascending or descending order?
			Integer increment = +1;
			if (multiTransitions.first > multiTransitions.last) {
				increment = -1;
			}
			for (int i = multiTransitions.first; i != multiTransitions.last; i += increment) {
				Transition t = new Transition();
				t.condition = multiTransitions.condition;
				t.priorityOrder = multiTransitions.priority;
				t.origin = multiTransitions.baseStateName + Integer.toString(i);
				t.destination = multiTransitions.baseStateName + Integer.toString(i + increment);
				State s1 = fsm.getStateOrCreateAndAdd(t.origin);
				fsm.getStateOrCreateAndAdd(t.destination);
				s1.transitionsFromThisState.add(t);
				// also add it to the global transitions / list
				fsm.transitions.add(t);
			}
		}

		// ///////////////////////////////////////////////////////////////
		public void enterMulti_transitions_to_same_directive(FsmParser.Multi_transitions_to_same_directiveContext ctx) {
			// set default values if they are not set by optional parameters
			multiTransitions.baseStateName = "";
			multiTransitions.priority = 1000000; // default value
			multiTransitions.condition = "1";
		}

		// ///////////////////////////////////////////////////////////////

		public void enterMulti_transitions_destination_state(FsmParser.Multi_transitions_destination_stateContext ctx) {
			multiTransitions.destinationState = ctx.children.get(0).getText().toUpperCase();
		}

		// ///////////////////////////////////////////////////////////////

		public void exitMulti_transitions_to_same_directive(FsmParser.Multi_transitions_to_same_directiveContext ctx) {
			// do the actual job once all parameters have been gathered
			// ascending or descending order?
			Integer increment = +1;
			if (multiTransitions.first > multiTransitions.last) {
				increment = -1;
			}
			Boolean over = false;
			int i = multiTransitions.first;
			while (!over) {
				Transition t = new Transition();
				t.condition = multiTransitions.condition;
				t.priorityOrder = multiTransitions.priority;
				t.origin = multiTransitions.baseStateName + Integer.toString(i);
				t.destination = multiTransitions.destinationState;
				State s1 = fsm.getStateOrCreateAndAdd(t.origin);
				fsm.getStateOrCreateAndAdd(t.destination);
				s1.transitionsFromThisState.add(t);
				fsm.transitions.add(t); // also add it to the global
										// transitions
										// list
				if (i == multiTransitions.last)
					over = true;
				i += increment;
			}
		}

		// ///////////////////////////////////////////////////////////////
		public void exitGeneric_declaration(FsmParser.Generic_declarationContext ctx) {
			fsm.genericDeclarations.add(fsm.currentGenericDeclarations);
		}

		// ///////////////////////////////////////////////////////////////
		public void enterId_generic(FsmParser.Id_genericContext ctx) {
			fsm.currentGenericDeclarations = new GenericDeclaration();
			fsm.currentGenericDeclarations.Name = ctx.children.get(0).getText().toUpperCase();
		}

		// ///////////////////////////////////////////////////////////////
		public void enterType_generic(FsmParser.Type_genericContext ctx) {
			fsm.currentGenericDeclarations.Type = ctx.children.get(0).getText().toUpperCase();
		}

		// ///////////////////////////////////////////////////////////////
		public void enterDefault_generic(FsmParser.Default_genericContext ctx) {
			fsm.currentGenericDeclarations.Default = ctx.children.get(0).getText().toUpperCase();
		}

		// ///////////////////////////////////////////////////////////////

		public void enterPragma_dot_global_directive(FsmParser.Pragma_dot_global_directiveContext ctx) {
			// simple way to get the pragma
			String pragma = ctx.children.get(1).getText();
			String pragmaCleaned = pragma.substring(1, pragma.length() - 8);
			fsm.pragmaDotGlobal += pragmaCleaned;
		}
		// ///////////////////////////////////////////////////////////////

	}

	// ///////////////////////////////////////////////////////////////
	public static void parseArgs(String[] args) {
		int c;
		String arg;
		Getopt g = new Getopt("FsmProcess", args, "if:crp:");
		g.setOpterr(false); // We'll do our own error handling
		while ((c = g.getopt()) != -1)
			switch (c) {
			case 'p':
				arg = g.getOptarg().replace('x', ' ');
				Scanner sc = new Scanner(arg);
				try {
					WIN_ORG_X = sc.nextInt();
					WIN_ORG_Y = sc.nextInt();
					WIN_WIDTH = sc.nextInt();
					WIN_HEIGHT = sc.nextInt();
				} catch (Exception e) {
					System.out.println("Error: argument -f should be followed by a quadruplet of integer separated by x characters");
					System.out.println(e.toString());
					System.out.println(" ");
					System.out.println("printStackTrace");
					e.printStackTrace();
				}
				System.out.print("Desired windows position: ");
				System.out.print(WIN_ORG_X);
				System.out.print(" x ");
				System.out.println(WIN_ORG_Y);
				System.out.print("Desired windows size: ");
				System.out.print(WIN_WIDTH);
				System.out.print(" x ");
				System.out.println(WIN_HEIGHT);
				break;
			case 'i':
				System.out.print(" Option: Ignore Errors\n");
				optionsIgnoreErrors = true;
				break;
			case 'c':
				System.out.print(" Option: Compute result image\n");
				optionsComputeResultImage = true;
				break;
			case 'f':
				arg = g.getOptarg();
				System.out.print("Input file: ");
				System.out.print(arg);
				System.out.print("\n");
				fsmInputName = arg;
				break;
			case 'r':
				System.out.print(" Option: Realtime process of the input file\n");
				optionsRealtime = true;
				optionsDisplayResultImage = true;
				optionsComputeResultImage = true;
				break;
			default:
				System.out.println("getopt() returned " + c);
				break;
			}
	}

	// ///////////////////////////////////////////////////////////////
	public static void main(String[] args) throws Exception {
		System.out.println(softNameAndVersion);
		System.out.println("Created by B. Vandeportaele IUT GEII TOULOUSE, LAAS/CNRS 01/2016");
		System.out.println("usage: java -jar FsmProcess.jar -i -c -d -r -f fichier.fsm");
		System.out.println("    -i: ignore error in the model and try to continue");
		System.out.println("    -c: create output gif image file from the dot file");
		System.out.println("    -f filename.fsm: provide the input file name to process");
		System.out
				.println("    -r: realtime process, regenerate the files when the input file content changes and  display the output gif image");
		System.out.println("  THE GENERATED FILES SHOULD NOT BE EDITED BY HAND, AS THEY MAY BE DELETED AUTOMATICALLY!!!!");
		parseArgs(args);

		// File f = new File(args[0]); System.out.println(f.getPath());
		// System.out.println(f.getAbsolutePath()); try {
		// System.out.println(f.getCanonicalPath()); } catch ( IOException e) {
		// System.out.println("f.getCanonicalPath() failed"); }
		if (fsmInputName.endsWith(".fsm"))
			GenerateFiles(fsmInputName);
		else if (fsmInputName.endsWith("doc.txt"))
			processTheDoc();
		else {
			System.out.println("Error: Please provide the filename of the fsm file to process with .fsm extension");
			return;
		}
		if (optionsRealtime) {
			// Schedule a job for the event-dispatching thread:
			// creating and showing this application's GUI.
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					createAndShowGUI();
				}
			});
		}
	}
}
