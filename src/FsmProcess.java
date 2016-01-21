/***
 * FSM
 @author Bertrand VANDEPORTAELE LAAS/CNRS 2016
 *  ***/
//import java.io.BufferedReader;
import gnu.getopt.Getopt;
//import gnu.getopt.LongOpt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

// sudo apt-get install libgetopt-java
//et ajouter dans referenced Libraries /usr/share/gnu-getopt-1.0.13.jar

//TODO: move src files in src to avoir .git in jar file

//add an arg to the app to set Boolean ignoreErrors -> getopt

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

//TODO: comprendre pourquoi antlr ne genere par le fichier FsmParser.java  mais uniquement le class...

// ///////////////////////////////////////////////////////////////
// Il suffit de redéfinir la méthode equals de tes objets pour contrôler
// comment fonctionne le contains (c'est lui qui est appelé pour faire la
// comparaison).

/*
 //////////////////////////////////////////////////////////
 class FilteredStream extends InputStream {
 public InputStream inputStream;
 public PipedOutputStream pipedOutputStream;
 public PipedInputStream pipedInputStream;
 public BufferedReader inBuf;
 public int state;

 public FilteredStream(InputStream i) {
 inputStream = i;
 pipedOutputStream = new PipedOutputStream();
 pipedInputStream = new PipedInputStream(pipedOutputStream); // FIFO
 inBuf = new BufferedReader(new Reader(inputStream));
 state = 0;
 }

 @Override
 public int read() throws IOException {

 String line = inBuf.readLine();
 if (line.contains("<<FSM")) {
 state = 1;
 } else if (line.contains("/FSM>>")) {
 state = 0;
 // //appeler parsing par antlr qui travaille sur pipedInputStream
 }
 if (state == 1)
 pipedOutputStream.write(line.getBytes()); // to antlr , qui s'abonne
 else
 return pipedInputStream.read();
 }

 }*/
//////////////////////////////////////////////////////////
public class FsmProcess {

	static String fsmInputName;
	static StringBuilder bufDot;
	static StringBuilder bufVhdl;
	static StringBuilder bufLogInfo;
	static StringBuilder bufLogWarning;
	static StringBuilder bufLogError;

	static FiniteStateMachine fsm = new FiniteStateMachine();

	// static public void processASingleFSM(String fsmBaseName) {}

	// ///////////////////////////////////////////////

	static public void GenerateFiles(String fsmInputName) {
		// by default, the input stream is System.in
		InputStream is = System.in;
		if (fsmInputName != null)
			try {
				is = new FileInputStream(fsmInputName);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		String fsmBaseName = fsmInputName.substring(0, fsmInputName.length() - 4); // file
																					// location
																					// and
																					// name
																					// without
																					// extension
		// extract fsm.name from the fsmInputName file name
		if (fsmInputName.contains("/")) // it contains a unix based
										// directory name
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FsmLexer lexer = new FsmLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		FsmParser parser = new FsmParser(tokens);
		parser.setBuildParseTree(true);
		ParseTree tree = parser.file();
		ParseTreeWalker walker = new ParseTreeWalker();
		FunctionListener collector = new FunctionListener();
		bufVhdl = new StringBuilder();
		bufDot = new StringBuilder();
		bufLogInfo = new StringBuilder();
		bufLogWarning = new StringBuilder();
		bufLogError = new StringBuilder();

		walker.walk(collector, tree);
		if (checkModel() || fsm.optionsIgnoreErrors)
		// if ignoreErrors, then display and generate fsm that have errors
		{
			generateDot();
			saveToFile(bufDot.toString(), fsmBaseName.concat(".dot")); //
			System.out.println(bufDot.toString());
			generateVhdl();
			saveToFile(bufVhdl.toString(), fsmBaseName.concat(".vhd"));
			bufVhdl.setLength(0);
			generatePackageVhdl();
			saveToFile(bufVhdl.toString(), fsmBaseName.concat("_pack.vhd"));
			bufVhdl.setLength(0);
			generatePortMapVhdl();
			saveToFile(bufVhdl.toString(), fsmBaseName.concat("_portmap.vhd"));
			saveToFile(bufLogError.toString() + "\n\n" + bufLogWarning.toString() + "\n\n" + bufLogInfo.toString(),
					fsmBaseName.concat(".log"));
		}
	}

	// ///////////////////////////////////////////////

	static public void processTheDoc() {
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
			e.printStackTrace();
		}
	}

	/*
	 * try { InputStream docStream = new FileInputStream(source); } catch
	 * (FileNotFoundException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * // FilteredStream doc = new FilteredStream(docStream); }
	 * 
	 * /* // is = new FileInputStream();
	 * 
	 * try { String ligne; BufferedReader fichier = new BufferedReader(new
	 * FileReader(source)); FilteredStream fs=new FilteredStream (new
	 * FileInputStream(new File(source))); ile ((ligne = fichier.readLine()) !=
	 * null) { System.out.println(ligne);
	 * 
	 * // ligne. } fichier.close(); } catch (Exception e) { e.printStackTrace();
	 * }
	 */

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
	static public void generateDot() {
		// compute size of node from the longest state name
		Double nodeWidth = 0.5 + State.longestName * 0.12;
		bufDot.append("///////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
		bufDot.append("// Finite State Machine .dot diagram autogenerated by FsmProcess B. VANDEPORTAELE LAAS/CNRS 2016\n");
		bufDot.append("///////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
		bufDot.append("digraph finite_state_machine {\n");
		// should be setable in the command line
		bufDot.append("    	rankdir=LR;\n");
		// bufDot.append("    	rankdir=TB;\n");
		// TODO: read it from optional command line args
		bufDot.append("    	ranksep=0.5;\n"); // separation between different
												// ranks
		// TODO: read it from optional command line args
		bufDot.append("    	nodesep=0.1;\n"); // separation between nodes in
												// same rank (state and
												// corresponding action)
												// between 3 and 0.1
		// TODO: read it from optional command line args
		// bufDot.append("    	size=\"10\";\n");
		// bufDot.append("    	ranksep=equally;\n");
		bufDot.append("///////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
		bufDot.append("// Finite State Machine Name: ");
		bufDot.append(fsm.name);
		bufDot.append("\n");
		bufDot.append("///////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
		bufDot.append("//////////////////display  states//////////////////\n");
		// bufDot.append("splines=line;\n"); //
		// http://www.graphviz.org/content/attrs#kstyle it is a property of the
		// whole graph... not individual edges
		int cptResetStates = 0;
		for (int n = 0; n < fsm.states.size(); n++) {
			int nbAttachedActions = fsm.states.get(n).attachedActions.size();
			// add states to the dot file
			bufDot.append("    	//---------State:   ");
			bufDot.append(fsm.states.get(n).name);
			bufDot.append("    -----------------\n");
			bufDot.append(" {");
			// very important, thanks to this, the state and action nodes are on
			// the same line or column, orthogonally of the rankdir
			bufDot.append(" rank = same;\n");
			bufDot.append("    	");
			bufDot.append(fsm.states.get(n).name);
			if (fsm.states.get(n).isInit) {
				bufDot.append(" [shape=doublecircle, fixedsize=true, width=");
				bufDot.append(nodeWidth);
				bufDot.append(" ];\n");
			} else {
				bufDot.append(" [shape=circle,fixedsize=true,width=");
				bufDot.append(nodeWidth);
				bufDot.append(" ];\n");
			}
			// add actions states to the dot file if necessary
			if (nbAttachedActions != 0) {
				bufDot.append("    	//Action on state:\n");
				bufDot.append("    	stateaction");
				bufDot.append(fsm.states.get(n).name);
				bufDot.append("  [shape=box,label=  ");
				bufDot.append(" <<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">\n");
				for (int m = 0; m < nbAttachedActions; m++)
					GenerateDotForAction(fsm.states.get(n).attachedActions.get(m));
				bufDot.append("    	    	</TABLE>>  ];\n");
				bufDot.append("    	//attach the action on the state\n    	");
				bufDot.append(fsm.states.get(n).name);
				bufDot.append(" ->");
				bufDot.append("stateaction");
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
		bufDot.append("//////////////////display  transitions//////////////////\n");
		for (int n = 0; n < fsm.states.size(); n++) {
			int nbTransitions = fsm.states.get(n).transitionsFromThisState.size();
			if (nbTransitions != 0)
				for (int m = 0; m < nbTransitions; m++) {
					Transition t = fsm.states.get(n).transitionsFromThisState.get(m);
					bufDot.append("    	");
					bufDot.append(t.origin);
					bufDot.append(" -> ");
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
						bufDot.append("taillabel= <<font color=\"red\">");
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
		bufDot.append(" {");
		// very important, thanks to this, the state and action nodes are on the
		// same line or column, orthogonally of the rankdir
		bufDot.append(" rank = same;\n");
		int nbRepeatedlyActions = fsm.repeatedlyActions.size();
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
		bufDot.append("}\n"); // end of the DOT file content
	}

	// ////////////////////////////////////////////////
	static public Boolean checkModel() {
		// this method returns false and exit as soon as there is a critical
		// error

		// EASY TODOS:

		// DOC DOT: http://www.graphviz.org/Documentation/dotguide.pdf
		// graphviz.org/Documentation.php
		// http://www.graphviz.org/doc/info/lang.html
		// http://www.tonyballantyne.com/graphs.html
		// TODO: pour les pragma, se baser sur la grammaire du C:
		// https://github.com/antlr/grammars-v4/blob/master/c/C.g4
		// par exemple pour les chaines de caractères, les espaces ne sont pas
		// ignorés!
		/*
		 * PragmaDirective : '#' Whitespace? 'pragma' Whitespace ~[\r\n]* ->
		 * skip ;
		 * 
		 * 
		 * 
		 * StringLiteral : EncodingPrefix? '"' SCharSequence? '"' ; fragment
		 * EncodingPrefix : 'u8' | 'u' | 'U' | 'L' ; fragment SCharSequence :
		 * SChar+ ; fragment SChar : ~["\\\r\n] | EscapeSequence ;
		 * 
		 * 
		 * la regle suivante arrive après:
		 * 
		 * Whitespace : [ \t]+ -> skip ;
		 */

		// TODO: indiquer dans le fichier package le codage utilisé pour les
		// sorties state number

		// TODO Add an option to generate dot and vhdl even if there is some
		// errors (separate critials) to be able to get some wrong drawings or
		// code

		// TODO: modifier pour avoir des actions à 1 par defaut

		// TODO: pour les comparaisons de valeurs sur des bus d'entrée, utiliser
		// une syntaxe particulière (ajouter à la grammaire) puis générer dans
		// le vhdl un signal qui prendra la valeur 1 quand la condition est
		// vraie

		// TODO: show infos, warnings errors etc in a buffer instead of
		// system...
		// to speed up and so it can be logged and hidden if necessary (pipe?)

		// TODO; regler les problèmes de fichier unix/windows pour ne pas avoir
		// à utiliser unix2dos

		// HARD TODOS:

		// TODO: pragma pour enlever des entrees/sorties, changer leur taille

		// TODO: Ajouter la notion d'overide: pour regler ls AMZI à 1 par
		// défaut, les AMZE en AMUE et donner des valeurs d'init aux sorties M
		// et
		// pour spécifier que des E ou S sont des bus de n bits
		// TODO: in pragma, allow to fix the number of bits for state_number, so
		// the interface doesn't change when states are added. Add a check that
		// the pragma is set high enough...

		// TODO: make Boolean ResetTransitionInhibatesTransitionActions = true;
		// and Boolean ResetTransitionInhibatesActionsOnStates = true
		// configurable through pragma

		// TODO: que faire quand une action sur état est incompatible avec une
		// action sur une transition émanant de cet état???

		// TODO: passer l'expression pour les sorties I et M à gauche du when
		// (permettra gestion de bus) et regler la valeur par defaut à others 0
		// si bus..

		// TODODOC: dans la doc sur les (reset) transitions, bien indiquer que
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
		fsm.numberOfBitsForStates = Integer.toBinaryString(numberOfStates - 1).length();

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
			bufLogError.append("Critical Error: The model contains no state... \n");
			modelOk = false;
		}

		if (fsm.outputs.size() == 0) {
			bufLogError.append("Critical Error: The model contains no outputs... \n");
			modelOk = false; // its allowed to have no inputs
		}

		if (fsm.numberOfResetAsynchronousDefinitions > 1)
			bufLogWarning.append("Warning:   Asynchronous reset has been redefined more than one time...\n");

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

		// Detect transitions from same a state to itself
		for (int n = 0; n < numberOfTransitions; n++) {
			Transition t1 = fsm.transitions.get(n);
			if (t1.origin.equals(t1.destination)) {
				bufLogWarning.append("Warning: There is a transition between state ");
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
		// check that the action on a same output are all compatible
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
					}
					modelOk = false;
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
		// Collections.sort(fsm.transitions); //it can not be done globaly but
		// it has to be done state by state
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
						}
					}
			}
		}
		// cheat...
		// modelOk = true;
		if (modelOk)
			bufLogInfo.append("Info: No Critial error: Output files can be generated\n\n");
		else
			bufLogInfo.append("Info: At least one Critial error: Output files canNOT be generated\n\n");

		return modelOk;
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	static public void generateVhdlTestBench() {
		// TODO:generateVhdlTestBench()
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	static public void generateInterfaceVhdl() {
		bufVhdl.append("port (\n");
		bufVhdl.append("		");
		bufVhdl.append(fillStringWithSpace2(fsm.clkSignalName, Input.longestName));
		bufVhdl.append(" : in  std_logic;\n");
		bufVhdl.append("		");
		bufVhdl.append(fillStringWithSpace2(fsm.aResetSignalName, Input.longestName));
		bufVhdl.append(" : in  std_logic;\n");
		if (fsm.GenerateNumberOfStateOutput) {
			bufVhdl.append("		");
			bufVhdl.append(fillStringWithSpace2("STATE_NUMBER", Input.longestName));
			bufVhdl.append(" : out std_logic_vector( ");
			bufVhdl.append(fsm.numberOfBitsForStates - 1);
			bufVhdl.append(" downto 0);\n");
		}
		// ////////////////listing of inputs/outputs//////////////////
		for (int n = 0; n < fsm.hmapInput.size(); n++) {
			bufVhdl.append("		");
			bufVhdl.append(fsm.inputs.get(n).paddedName);
			bufVhdl.append(" : in  std_logic;\n");
		}

		for (int n = 0; n < fsm.hmapOutput.size(); n++) {
			bufVhdl.append("		");
			bufVhdl.append(fsm.outputs.get(n).paddedName);
			bufVhdl.append(" : out  std_logic");
			if (n != fsm.hmapOutput.size() - 1)
				bufVhdl.append(";\n");
			else
				bufVhdl.append(");\n");
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	static public void generatePackageVhdl() {
		bufVhdl.append("library IEEE;\n");
		bufVhdl.append("use IEEE.STD_LOGIC_1164.all;\n");
		bufVhdl.append("package ");
		bufVhdl.append(fsm.name);
		bufVhdl.append("_pack is\n");
		bufVhdl.append("component ");
		bufVhdl.append(fsm.name);
		bufVhdl.append("\n");
		generateInterfaceVhdl();
		bufVhdl.append("end component;\n");
		bufVhdl.append("end \n");
		bufVhdl.append(fsm.name);
		bufVhdl.append("_pack;\n");
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	static public void generatePortMapVhdl() {
		bufVhdl.append("library work;\n");
		bufVhdl.append("use work.");
		bufVhdl.append(fsm.name);
		bufVhdl.append("_pack.all;\n\n");
		bufVhdl.append(fsm.name);
		bufVhdl.append("_u0 : ");
		bufVhdl.append(fsm.name);
		bufVhdl.append("\n");
		bufVhdl.append("port map(\n");
		bufVhdl.append("		");
		bufVhdl.append(fsm.clkSignalName);
		bufVhdl.append(" => ");
		bufVhdl.append(fsm.clkSignalName);
		bufVhdl.append(",\n");
		bufVhdl.append("		");
		bufVhdl.append(fsm.aResetSignalName);
		bufVhdl.append(" => ");
		bufVhdl.append(fsm.aResetSignalName);
		bufVhdl.append(",\n");
		// ////////////////listing of inputs/outputs//////////////////
		for (int n = 0; n < fsm.hmapInput.size(); n++) {
			bufVhdl.append("		");
			bufVhdl.append(fsm.inputs.get(n).name);
			bufVhdl.append(" => ");
			bufVhdl.append(fsm.inputs.get(n).name);
			bufVhdl.append(",\n");
		}
		if (fsm.GenerateNumberOfStateOutput)
			bufVhdl.append("		state_number =>  state_number,\n");
		for (int n = 0; n < fsm.hmapOutput.size(); n++) {
			bufVhdl.append("		");
			bufVhdl.append(fsm.outputs.get(n).name);
			bufVhdl.append(" => ");
			bufVhdl.append(fsm.outputs.get(n).name);
			if (n != fsm.hmapOutput.size() - 1)
				bufVhdl.append(",\n");
			else
				bufVhdl.append(");\n");
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	static public void generateVhdl() {
		int numberOfStates = fsm.states.size();

		bufVhdl.append("-----------------------------------------------------------------------------------------------------------\n");
		bufVhdl.append("-- Finite State Machine .vhdl  autogenerated by FsmProcess B. VANDEPORTAELE LAAS-CNRS 2016\n");
		bufVhdl.append("-----------------------------------------------------------------------------------------------------------\n");
		bufVhdl.append("library	ieee;\nuse		ieee.std_logic_1164.all;\nuse		ieee.std_logic_unsigned.all;\nuse		ieee.std_logic_arith.all;\nentity ");
		bufVhdl.append(fsm.name);
		bufVhdl.append(" is\n");
		generateInterfaceVhdl();
		bufVhdl.append("end ");
		bufVhdl.append(fsm.name);
		bufVhdl.append(";\n\n");
		bufVhdl.append("architecture ar of ");
		bufVhdl.append(fsm.name);
		bufVhdl.append(" is \n");
		// ////////////////listing of possible values for state
		// names//////////////////
		bufVhdl.append("type fsm_state is (");
		for (int n = 0; n < numberOfStates; n++) {
			// prefix state name with state_
			bufVhdl.append("state_");
			bufVhdl.append(fsm.states.get(n).name);
			if (n != numberOfStates - 1)
				bufVhdl.append(", ");
		}
		bufVhdl.append(");\n");
		bufVhdl.append("signal current_state, next_state : fsm_state;\n");
		// ////////////////listing of internal signals for memorized
		// outputs//////////////////
		for (int n = 0; n < fsm.outputs.size(); n++) {
			Output out = fsm.outputs.get(n);
			if (out.memorized) {
				bufVhdl.append("signal ");
				bufVhdl.append(out.name);
				bufVhdl.append("_set, ");
				bufVhdl.append(out.name);
				bufVhdl.append("_reset, ");
				bufVhdl.append(out.name);
				bufVhdl.append("_mem : std_logic;\n");
				bufVhdl.append("signal ");
				bufVhdl.append(out.name);
				bufVhdl.append("_mem_value : std_logic;\n");
			}
		}
		bufVhdl.append("signal value_one_internal: std_logic := '1';  --signal used internally to ease operation on conditions, to have a std_logic type '1' value\n");
		int numberOfResetTransitions = fsm.resetTransitions.size();
		if (fsm.resetTransitionInhibatesTransitionActions || fsm.resetTransitionInhibatesActionsOnStates) {
			if (numberOfResetTransitions > 0) {
				bufVhdl.append("signal not_any_s_reset_internal: std_logic ;  --signal used internally to ease inhibition of actions when reset transitions occurs\n");
			}
		}
		// ////////////////let's animate all that stuff...//////////////////
		bufVhdl.append("---------------------------------------------------------------------------------------\n");
		bufVhdl.append("begin\n");
		if (fsm.resetTransitionInhibatesTransitionActions || fsm.resetTransitionInhibatesActionsOnStates) {
			if (numberOfResetTransitions > 0) {
				bufVhdl.append("-----------------------Combination of sreset signal(s) to inhibate actions on states and/or transitions--------------\n");
				bufVhdl.append("not_any_s_reset_internal<= '1' when ");
				bufVhdl.append(fsm.resetConditionComplement);
				bufVhdl.append(" else\n                           '0';\n");
			}
		}
		bufVhdl.append("------------------------Process for the memorization of the state----------------------\n");
		bufVhdl.append("process (");
		bufVhdl.append(fsm.clkSignalName);
		bufVhdl.append(", ");
		bufVhdl.append(fsm.aResetSignalName);
		bufVhdl.append(")\nbegin\n    if (");
		bufVhdl.append(fsm.aResetSignalName);
		bufVhdl.append("='");
		bufVhdl.append(fsm.aResetSignalLevel);
		bufVhdl.append("') then current_state <=");
		bufVhdl.append("state_");
		bufVhdl.append(fsm.resetAsynchronousState.name);
		bufVhdl.append(";\n");
		bufVhdl.append("    elsif ");
		bufVhdl.append(fsm.clkSignalName);
		bufVhdl.append("'event and ");
		bufVhdl.append(fsm.clkSignalName);
		bufVhdl.append("='1' then current_state<=next_state;\n");
		bufVhdl.append("    end if;\n");
		bufVhdl.append("end process;\n\n");
		bufVhdl.append("-------------------Combinatorial process for the evolution of the state------------------\n");
		bufVhdl.append("process (current_state");
		for (int n = 0; n < fsm.inputs.size(); n++) {
			bufVhdl.append(", ");
			bufVhdl.append(fsm.inputs.get(n).name);
		}
		bufVhdl.append(")\n");
		bufVhdl.append("begin\n");

		int nbResetTransitions = fsm.resetTransitions.size();
		if (nbResetTransitions != 0) {
			bufVhdl.append("-----------Synchronous RESETs has higher priority than standard transitions\n");
			for (int m = 0; m < nbResetTransitions; m++) {
				if (m == 0)
					bufVhdl.append("   if    ( ");
				else
					bufVhdl.append("   elsif ( ");

				if (fsm.resetTransitions.get(m).condition.equals("1"))
					bufVhdl.append(" true "); // that would be dumb because the
												// system should always be
												// resetted, but if it is asked,
												// I do it...
				else {
					bufVhdl.append(" ( ");
					bufVhdl.append(fsm.resetTransitions.get(m).condition);
					bufVhdl.append(" ) ");
					bufVhdl.append(" = '1' ");
				}
				bufVhdl.append(")\n    then next_state <= state_");
				bufVhdl.append(fsm.resetTransitions.get(m).destination);
				bufVhdl.append(";");
				// show the priority order if its not the default value
				if (fsm.resetTransitions.get(m).priorityOrder != 1000000) {
					bufVhdl.append(" -- priority order set to: ");
					bufVhdl.append(fsm.resetTransitions.get(m).priorityOrder);
				}
				bufVhdl.append("\n");
			}
			// bufVhdl.append("   end if; --no else, so next_state is not modified here if there is no synchronous reset\n");
			bufVhdl.append("   else \n");
		}
		// else
		// bufVhdl.append("   if ( ");
		bufVhdl.append("------------------------------standard transitions---------------------\n");

		bufVhdl.append("    case current_state is\n");
		// pour chaque état, il peut y avoir plusieurs transitions, la première
		// if, les suivantes elsif et finalement en plus le maintien dans l'état
		// courant
		for (int n = 0; n < numberOfStates; n++) {
			bufVhdl.append("      when state_"); // prefix state name with
													// state_
			bufVhdl.append(fsm.states.get(n).paddedName);
			int transitionFromThisStateNumber = fsm.states.get(n).transitionsFromThisState.size();
			bufVhdl.append(" => "); // if (transitionFromThisStateNumber==0)
									// //stay always in that state
			for (int m = 0; m < transitionFromThisStateNumber; m++) {
				if (m == 0)
					bufVhdl.append(" if ( ");
				else {
					bufVhdl.append(fillStringWithSpace2("", State.longestName + 22));
					bufVhdl.append("elsif ( ");
				}
				if (fsm.states.get(n).transitionsFromThisState.get(m).condition.equals("1"))
					bufVhdl.append(" true ");
				else {
					bufVhdl.append(" ( ");

					bufVhdl.append(fsm.states.get(n).transitionsFromThisState.get(m).condition);
					bufVhdl.append(" ) ");

					bufVhdl.append(" = '1' ");
				}
				bufVhdl.append(") then next_state <= state_");
				bufVhdl.append(fsm.states.get(n).transitionsFromThisState.get(m).destination);
				bufVhdl.append(";");
				// show the priority order if its not the default value
				if (fsm.states.get(n).transitionsFromThisState.get(m).priorityOrder != 1000000) {
					bufVhdl.append(" -- priority order set to: ");
					bufVhdl.append(fsm.states.get(n).transitionsFromThisState.get(m).priorityOrder);
				}
				bufVhdl.append("\n");
			}
			if (transitionFromThisStateNumber != 0) {
				bufVhdl.append(fillStringWithSpace2("", State.longestName + 22));
				bufVhdl.append("else	");
			}
			bufVhdl.append("next_state <= state_");
			bufVhdl.append(fsm.states.get(n).name);
			bufVhdl.append(";\n");
			if (transitionFromThisStateNumber != 0) {
				bufVhdl.append(fillStringWithSpace2("", State.longestName + 22));
				bufVhdl.append("end if;\n");
			}
		}
		bufVhdl.append("--    when others => next_state <= state_");
		bufVhdl.append(fsm.states.get(0).name);
		bufVhdl.append(";\n    end case;\n");
		// if there has been some synchronous reset transition, end if should be
		// added
		if (nbResetTransitions != 0)
			bufVhdl.append("   end if;\n");

		bufVhdl.append("end process;\n");
		bufVhdl.append("------------------FLIP FLOPS FOR MEMORIZED OUTPUTS ------------\n");
		for (int n = 0; n < fsm.outputs.size(); n++) {
			Output out = fsm.outputs.get(n);
			if (out.memorized) {
				bufVhdl.append("------ FLIP FLOPS FOR ");
				bufVhdl.append(out.name);
				bufVhdl.append(" ------\n");
				bufVhdl.append("process (");
				bufVhdl.append(fsm.clkSignalName);
				bufVhdl.append(", ");
				bufVhdl.append(fsm.aResetSignalName);
				bufVhdl.append(")\n");
				bufVhdl.append("begin\n");
				bufVhdl.append("   if (");
				bufVhdl.append(fsm.aResetSignalName);
				bufVhdl.append("='");
				bufVhdl.append(fsm.aResetSignalLevel);
				bufVhdl.append("') then ");
				bufVhdl.append(out.name);
				bufVhdl.append("<=");
				if (out.asyncResetExpression == null)
					bufVhdl.append("'0'");
				else if (out.asyncResetExpression.equals("0"))
					bufVhdl.append("'0'");
				else if (out.asyncResetExpression.equals("1"))
					bufVhdl.append("'1'");
				else
					bufVhdl.append(out.asyncResetExpression);
				bufVhdl.append(";\n");
				bufVhdl.append("    elsif ");
				bufVhdl.append(fsm.clkSignalName);
				bufVhdl.append("'event and ");
				bufVhdl.append(fsm.clkSignalName);
				bufVhdl.append("='1' then  \n");
				bufVhdl.append("      if ");
				bufVhdl.append(out.name);
				bufVhdl.append("_set ='1' then ");
				bufVhdl.append(out.name);
				bufVhdl.append("<='1';\n");
				bufVhdl.append("      elsif ");
				bufVhdl.append(out.name);
				bufVhdl.append("_reset ='1' then ");
				bufVhdl.append(out.name);
				bufVhdl.append("<='0';\n");

				bufVhdl.append("      elsif ");
				bufVhdl.append(out.name);
				bufVhdl.append("_mem ='1' then ");
				bufVhdl.append(out.name);
				bufVhdl.append("<= ");
				bufVhdl.append(out.name);
				bufVhdl.append("_mem_value;\n");

				bufVhdl.append("      end if;\n");
				bufVhdl.append("    end if;\n");
				bufVhdl.append("end process;\n");
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
				bufVhdl.append("------------------ NON MEMORIZED OUTPUTS ------------\n");
				break;
			case 1:
				processMemorizedOutput = true;
				typeFilter = "R"; // Reset or memorized copy
				signalSuffix = "_reset";
				bufVhdl.append("------------------  FLIP FLOPS MEMORIZED RESET OUTPUT CONTROL------------\n");
				break;
			case 2:
				processMemorizedOutput = true;
				typeFilter = "S"; // Set or memorized copy
				signalSuffix = "_set";
				bufVhdl.append("------------------  FLIP FLOPS MEMORIZED SET OUTPUT CONTROL------------\n");
				break;
			case 3:
				processMemorizedOutput = true;
				typeFilter = "M"; // Memorize copy
				signalSuffix = "_mem";
				bufVhdl.append("------------------  FLIP FLOPS MEMORIZED OUTPUT CONTROL------------\n");
				break;
			case 4:
			default:
				processMemorizedOutput = true;
				typeFilter = "M"; // Memorize copy
				signalSuffix = "_mem_value";
				bufVhdl.append("------------------  FLIP FLOPS MEMORIZED OUTPUT VALUE CONTROL------------\n");
				break;
			}
			for (int n = 0; n < fsm.outputs.size(); n++)
				if (fsm.outputs.get(n).memorized == processMemorizedOutput) {
					String currentOutputName = fsm.outputs.get(n).name;
					bufVhdl.append("    ");
					bufVhdl.append(fsm.outputs.get(n).name);
					bufVhdl.append(signalSuffix);
					bufVhdl.append(fillStringWithSpace2("", Output.longestName - fsm.outputs.get(n).name.length()));
					bufVhdl.append("  <=  ");
					// look for actions in the fsm that deals with this output
					for (int m = 0; m < fsm.states.size(); m++) {
						// ////////////////////action on
						// state//////////////////////
						int nbActionInThatState = fsm.states.get(m).attachedActions.size();
						if (nbActionInThatState != 0) {
							for (int l = 0; l < nbActionInThatState; l++) {
								Action a = fsm.states.get(m).attachedActions.get(l);
								if (a != null) {
									if (a.name.equals(currentOutputName) && (a.type.equals(typeFilter))) {
										// if there is an expression
										if ((!a.expression.equals("")) && (cptActionType != 3)) {
											bufVhdl.append(" ( ");
											bufVhdl.append(a.expression);
											bufVhdl.append(" ) ");
										} else {
											bufVhdl.append(" '1' ");
										}
										bufVhdl.append("when ( (current_state = ");
										bufVhdl.append("state_");
										bufVhdl.append(fsm.states.get(m).name);
										bufVhdl.append(") ");
										if (fsm.resetTransitionInhibatesActionsOnStates == true)
											bufVhdl.append(a.condition);
										bufVhdl.append(")   else  --action on state ");
										bufVhdl.append(fsm.states.get(m).name);
										bufVhdl.append("\n");
										bufVhdl.append(fillStringWithSpace2("", Output.longestName + 10 + signalSuffix.length()));
									}
								}
							}
						}
						// ////////////////////action on transition from this
						// state//////////////////////
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
												bufVhdl.append(" ( ");
												bufVhdl.append(a.expression);
												bufVhdl.append(" ) ");
											} else {
												bufVhdl.append(" '1' ");
											}
											bufVhdl.append("when ( (current_state = ");
											bufVhdl.append("state_");
											bufVhdl.append(fsm.states.get(m).name);
											bufVhdl.append(") ");
											bufVhdl.append(" and  (");
											// if (t.condition.equals("1"))
											// bufVhdl.append(" true ");
											// else {
											bufVhdl.append(" ( ");
											// bufVhdl.append(t.condition);
											bufVhdl.append(t.conditionWithPriorities);
											bufVhdl.append(") = \'1\' ");
											// }
											bufVhdl.append(") ");
											if (fsm.resetTransitionInhibatesTransitionActions == true)
												bufVhdl.append(a.condition);
											bufVhdl.append(")  else --action on transition from state ");
											bufVhdl.append(t.origin);
											bufVhdl.append(" to ");
											bufVhdl.append(t.destination);
											bufVhdl.append("\n");
											bufVhdl.append(fillStringWithSpace2("", Output.longestName + 10 + signalSuffix.length()));
										}
									}
								}
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
											bufVhdl.append(" ( ");
											bufVhdl.append(a.expression);
											bufVhdl.append(" ) ");
										} else {
											bufVhdl.append(" '1' ");
										}
										// rt.conditionWithPriorities should
										// be!='1' by
										// construction
										bufVhdl.append("when ( ( ");
										bufVhdl.append(rt.conditionWithPriorities);
										bufVhdl.append(") = \'1\' )");
										bufVhdl.append("   else --action on reset transition to state ");
										bufVhdl.append(rt.destination);
										bufVhdl.append(" \n");
										bufVhdl.append(fillStringWithSpace2("", Output.longestName + 10 + signalSuffix.length()));
									}
								}
							}
						}
					}
					// ////////////////////repeatedly action from any
					// state//////////////////////
					int nbRepeatedlyAction = fsm.repeatedlyActions.size();
					for (int j = 0; j < nbRepeatedlyAction; j++) {
						Action a = fsm.repeatedlyActions.get(j);
						if (a != null) {
							if (a.name.equals(currentOutputName) && (a.type.equals(typeFilter))) {
								if ((!a.expression.equals("")) && (cptActionType != 3)) {
									// if there is an expression
									bufVhdl.append(" ( ");
									bufVhdl.append(a.expression);
									bufVhdl.append(" ) ");
								} else {
									bufVhdl.append(" '1' ");
								}
								// this action is always true
								bufVhdl.append("when ( true)   else  \n");
								bufVhdl.append(fillStringWithSpace2("", Output.longestName + 10 + signalSuffix.length()));
							}
						}
					}
					// this is the default value for all outputs.
					// A S action is automatically added when there is no
					// corresponding R action and vice versa
					bufVhdl.append(" \'0\'; \n");
					// TODO: deal with a generic value here
				}
		}
		if (fsm.GenerateNumberOfStateOutput) {
			bufVhdl.append("------------------  OUTPUTS FOR CURRENT STATE VISUALIZATION ------------\n");
			bufVhdl.append("	state_number <= \"");
			for (int i = 0; i < numberOfStates; i++) {
				if (i != 0)
					bufVhdl.append("                   else \"");
				bufVhdl.append(String.format("%" + Integer.toString(fsm.numberOfBitsForStates) + "s", Integer.toBinaryString(i)).replace(
						" ", "0"));
				// bufVhdl.append( Integer.toBinaryString(i));
				bufVhdl.append("\" when ( current_state = ");
				bufVhdl.append("state_");
				bufVhdl.append(fsm.states.get(i).name);
				bufVhdl.append(")\n");
			}
			bufVhdl.append("                   else \"");
			bufVhdl.append(String.format("%" + Integer.toString(fsm.numberOfBitsForStates) + "s", Integer.toBinaryString(0)).replace(" ",
					"0"));

			bufVhdl.append("\";\n");
		}
		bufVhdl.append("end ar;\n");

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
		int priorityOrder = 1000000; // by default, low priority, 1 is the
										// higher priority

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
		int priorityOrder = 1000000; // by default, low priority, 1 is the
										// higher priority

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
		static int longestName = 0;
		String type;
		String name;
		String paddedName;

		public int compareTo(Input o) {
			Input a = (Input) o;
			return name.compareTo(a.name);// par ordre alphabétique
		}
	}

	// //////////////////////////////////////////////////////////////////
	static class Output implements Comparable<Output> {
		// to store the longest names
		static int longestName = 12; // to be at least as long as "STATE_NUMBER"
		String type;
		String name;
		String paddedName;
		Boolean memorized;
		String asyncResetExpression = null;

		public int compareTo(Output o) {
			Output a = (Output) o;
			return name.compareTo(a.name);// par ordre alphabétique
		}
	}

	// //////////////////////////////////////////////////////////////////
	static class State implements Comparable<State> {
		// to store the longest names
		static int longestName = 12; // to be at least as long as "STATE_NUMBER"
		Boolean isInit; // initial state or not
		// String name=new String("");
		String name;
		String paddedName;
		// static ArrayList<Action> attachedActions=new ArrayList<Action>() ;
		ArrayList<Action> attachedActions = new ArrayList<Action>();
		// static ArrayList<Transition> transitionFromThisState=new
		// ArrayList<Transition>() ;
		ArrayList<Transition> transitionsFromThisState = new ArrayList<Transition>();
		// true if there is no (reset) transition or asynchronous reset to that
		// state
		Boolean isAlone;
		int nbTimeFoundInFSMFile = 0;

		public int compareTo(State o) {
			State a = (State) o;
			return name.compareTo(a.name);// par ordre alphabétique
		}
	}

	// //////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////
	// doc de ArrayList :
	// http://imss-www.upmf-grenoble.fr/prevert/Prog/Java/Conteneurs/ArrayList.html
	// et
	// https://openclassrooms.com/courses/apprenez-a-programmer-en-java/les-collections-d-objets
	/*
	 * public class ArrayListWithAccesFromNames<E> extends ArrayList<E>
	 * implementsList<E>, RandomAccess, Cloneable, java.io.Serializable{
	 * 
	 * 
	 * }
	 */
	// hash map:
	// http://stackoverflow.com/questions/14836870/java-arraylist-adding-object-using-string-name
	// Map<String, Integer> map = new HashMap<String, Integer>();
	// which will contain the mapping of itemName to respective Item, and then
	// getting the Item for a particular itemName is as simple as
	// map.get(itemName).
	// exemple: http://beginnersbook.com/2013/12/hashmap-in-java-with-example/
	// doc complete:
	// https://docs.oracle.com/javase/7/docs/api/java/util/HashMap.html

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
		// member variables for storing the fsm model
		public String name;
		public String clkSignalName = "CK"; // caps to be compared with parsed
											// names
		public Boolean clkSignalNameSpecified = false;
		public String aResetSignalName = "ARAZB"; // caps to be compared with
													// parsed names
		public String aResetSignalLevel = "0";

		ArrayList<ResetTransition> resetTransitions = new ArrayList<ResetTransition>();
		ArrayList<State> states = new ArrayList<State>();
		HashMap<String, State> hmapState = new HashMap<String, State>();
		ArrayList<Input> inputs = new ArrayList<Input>();
		HashMap<String, Input> hmapInput = new HashMap<String, Input>();
		ArrayList<Output> outputs = new ArrayList<Output>();
		HashMap<String, Output> hmapOutput = new HashMap<String, Output>();
		ArrayList<Transition> transitions = new ArrayList<Transition>();
		HashMap<String, Transition> hmapTransition = new HashMap<String, Transition>();
		// global actions list
		ArrayList<Action> actions = new ArrayList<Action>();
		// each Action in actions list is either one of the 2 following:
		ArrayList<Action> repeatedlyActions = new ArrayList<Action>();
		ArrayList<Action> noRepeatedlyActions = new ArrayList<Action>();

		ArrayList<Output> outputsWithAsynchronousResetValue = new ArrayList<Output>();

		// on stocke juste la liste pour pouvoir balayer et verifier les
		// compatibilités, pas besoin de hashtable
		// HashMap<String,Transition > hmapTransition = new
		// HashMap<String,Transition>();

		public Boolean GenerateNumberOfStateOutput = true;
		public int numberOfBitsForStates;
		// member variables for parsing
		int numberOfResetAsynchronousDefinitions = 0;

		public State resetAsynchronousState = null;

		// if set to true, will make the actions on transition to be inhibated
		// during reset
		Boolean resetTransitionInhibatesTransitionActions = true;
		// if set to true, will make the actions on state to be inhibated
		// during reset
		Boolean resetTransitionInhibatesActionsOnStates = true;
		// this string is computed only if there are some reset transition(s)
		// and either resetTransitionInhibatesTransitionActions or
		// resetTransitionInhibatesActionsOnStates is true
		String resetConditionComplement;

		public State currentState = null;
		public Action currentAction = null;
		public Output currentOutput = null;
		public Transition currentTransition = null;
		public ResetTransition currentResetTransition = null;
		public boolean currentTransitionIsReset;

		public ArrayList<String> inputsOrderedNamesList = new ArrayList<String>();
		public ArrayList<String> outputsOrderedNamesList = new ArrayList<String>();

		public Boolean optionsIgnoreErrors = false;

		List<String> forbiddenNamesVHDL = Arrays.asList("ABS", "ACCESS", "AFTER", "ALIAS", "ALL", "AND", "ARCHITECTURE", "ARRAY", "ASSERT",
				"ATTRIBUTE", "BEGIN", "BLOCK", "BODY", "BUFFER", "BUS", "CASE", "COMPONENT", "CONFIGURATION", "CONSTANT", "DISCONNECT",
				"DOWNTO", "ELSE", "ELSIF", "END", "ENTITY", "EXIT", "FILE", "FOR", "FUNCTION", "GENERATE", "GENERIC", "GROUP", "GUARDED",
				"IF", "IMPURE", "IN", "INERTIAL", "INOUT", "IS", "LABEL", "LIBRARY", "LINKAGE", "LITERAL", "LOOP", "MAP", "MOD", "NAND",
				"NEW", "NEXT", "NOR", "NOT", "NULL", "OF", "ON", "OPEN", "OR", "OTHERS", "OUT", "PACKAGE", "PORT", "POSTPONED",
				"PROCEDURE", "PROCESS", "PURE", "RANGE", "RECORD", "REGISTER", "REJECT", "REM", "REPORT", "RETURN", "ROL", "ROR", "SELECT",
				"SEVERITY", "SIGNAL", "SHARED", "SLA", "SLL", "SRA", "SRL", "SUBTYPE", "THEN", "TO", "TRANSPORT", "TYPE", "UNAFFECTED",
				"UNITS", "UNTIL", "USE", "VARIABLE", "WAIT", "WHEN", "WHILE", "WITH", "XNOR", "XOR");
		// TODO complete lists
		// TODO: add methods to generate string that lists the words for the
		// documentation
		List<String> forbiddenNamesFSM = Arrays.asList("value_one_internal", "not_any_s_reset_internal", "STATE_NUMBER");
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

		// to know when parsing a condition if it should be added to
		// ResetTransition or Transition

		// public static int cptStates = 0;
		// public static Boolean inState = false; // set to true when inside a
		// state, false elsewhere
		// public static Boolean inTransition = false; // set to true when
		// inside a transistion,false elsewhere
		// public static Boolean transitionIsReset=false;
		// public static Boolean inAction = false; // set to true when inside an
		// action, false elsewhere
		// public static Boolean conditionDefined;
		// public static String currentStateName = null;

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
					bufLogInfo.append("Info: Asynchronous reset state is defined to ");
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
	static class FunctionListener extends FsmBaseListener {
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
			// its name
			// if the states do not yet exist, create them
			State so = fsm.getStateOrCreateAndAdd(t.origin);
			fsm.getStateOrCreateAndAdd(t.destination);

			so.transitionsFromThisState.add(t);
			fsm.transitions.add(t); // also add it to the global transitions
									// list
		}

		// //////////////////////////////////////////////////////////////
		public void enterReset_transition_priority(FsmParser.Reset_transition_priorityContext ctx) {
			// int priority = ctx.children.get(0).getText().;
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
			for (int n = 0; n < nbChildren; n++)
			// reconstruct the condition, adding space characters between terms.
			{
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
			for (int n = 0; n < nbChildren; n++)
			// reconstruct the condition, adding space characters between terms.
			{
				reconstructedExpression += ctx.children.get(n).getText().toUpperCase();
				if (n != nbChildren - 1)
					reconstructedExpression += " ";
			}
			// TODO: pour les bus???
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
			if (fsm.currentOutput == null) {
				Output o = new Output();
				o.memorized = null; // default, will be defined later in the
				// analysis
				o.name = outputName;
				fsm.addOutput(outputName, o);
				fsm.currentOutput = o;
			}
		}

		// //////////////////////////////////////////////////////////////
		public void enterAction_type(FsmParser.Action_typeContext ctx) {
			fsm.currentAction.type = ctx.children.get(0).getText().toUpperCase();
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
		}// //////////////////////////////////////////////////////////////

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
	}

	// ///////////////////////////////////////////////////////////////
	public static void parseArgs(String[] args) {
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

		int c;
		String arg;
		Getopt g = new Getopt("FsmProcess", args, "if:");
		g.setOpterr(false); // We'll do our own error handling
		while ((c = g.getopt()) != -1)
			switch (c) {
			case 'i':
				System.out.print("Ignore Errors\n");
				fsm.optionsIgnoreErrors = true;
				break;
			case 'f':
				arg = g.getOptarg();
				System.out.print("Input file: ");
				System.out.print(arg);
				System.out.print("\n");
				fsmInputName = arg;
				break;
			default:
				System.out.println("getopt() returned " + c);
				break;
			}
	}

	// ///////////////////////////////////////////////////////////////
	public static void main(String[] args) throws Exception {

		/*
		 * if (args.length > 0) inputFile = args[0]; InputStream is = System.in;
		 * if (inputFile != null) { is = new FileInputStream(inputFile); }
		 */
		System.out.println("FsmProcess B. Vandeportaele LAAS/CNRS 2016\n");
		System.out.println("usage: FsmProcess fichier.fsm\n\n");

		// generateCounter(10);

		parseArgs(args);

		// TODO: check the file exists:
		/*
		 * File f = new File(args[0]); System.out.println(f.getPath());
		 * System.out.println(f.getAbsolutePath()); try {
		 * System.out.println(f.getCanonicalPath()); } catch ( IOException e) {
		 * System.out.println("f.getCanonicalPath() failed"); }
		 */

		if (fsmInputName.endsWith(".fsm"))
			GenerateFiles(fsmInputName);
		else if (fsmInputName.endsWith("doc.txt"))
			processTheDoc();
		else {
			System.out.println("Error: Please provide the filename of the fsm file to process with .fsm extension");
			return;
		}

	}
}
