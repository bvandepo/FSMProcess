/***
 * FSM
 @author Bertrand VANDEPORTAELE LAAS/CNRS 2016
 *  ***/
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

//CTRL-SHIFT 0 : automatisation des inclusions
//CTRL-SHIFT F : indentation et réorganisation

//TODO: comprendre pourquoi antlr ne genere par le fichier FsmParser.java  mais uniquement le class...

public class FsmProcess {

	static StringBuilder bufDot;
	static StringBuilder bufVhdl;
	static FiniteStateMachine fsm = new FiniteStateMachine();

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
	static public void generateDot() {
		bufDot.append("///////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
		bufDot.append("// Finite State Machine .dot diagram autogenerated by FsmProcess B. VANDEPORTAELE LAAS/CNRS 2016\n");
		bufDot.append("///////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
		bufDot.append("digraph finite_state_machine {\n");
		// bufDot.append("    	rankdir=LR;\n");
		// bufDot.append("    	size=\"10\";\n");
		bufDot.append("///////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
		bufDot.append("// Finite State Machine Name: ");
		bufDot.append(fsm.name);
		bufDot.append("\n");
		bufDot.append("///////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
		bufDot.append("//////////////////display  states//////////////////\n");
		// bufDot.append("splines=line;\n"); //
		// http://www.graphviz.org/content/attrs#kstyle it is a property of the
		// whole graph... not individual edges
		for (int n = 0; n < fsm.states.size(); n++) {
			// add states to the dot file
			bufDot.append("    	//State:\n");
			bufDot.append("    	");
			bufDot.append(fsm.states.get(n).name);
			if (fsm.states.get(n).isInit)
				bufDot.append(" [shape=doublecircle];\n");
			else
				bufDot.append(" [shape=circle];\n");
			// add actions states to the dot file if necessary
			int nbAttachedActions = fsm.states.get(n).attachedActions.size();
			if (nbAttachedActions != 0) {
				bufDot.append("    	//Action on state:\n");
				bufDot.append("    	stateaction");
				bufDot.append(fsm.states.get(n).name);
				bufDot.append("  [shape=box,label=  ");
				bufDot.append(" <<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">\n");
				for (int m = 0; m < nbAttachedActions; m++) {
					bufDot.append("    	    	<TR><TD>");
					bufDot.append(fsm.states.get(n).attachedActions.get(m).type);
					bufDot.append("</TD><TD>");
					bufDot.append(fsm.states.get(n).attachedActions.get(m).name);
					if (fsm.states.get(n).attachedActions.get(m).expression != "") {
						bufDot.append("=");
						bufDot.append(fsm.states.get(n).attachedActions.get(m).expression);
					}
					bufDot.append("</TD> </TR>\n");
				}
				bufDot.append("    	    	</TABLE>>  ];\n");
				bufDot.append("    	//attach the action on the state\n    	");
				bufDot.append(fsm.states.get(n).name);
				bufDot.append(" ->");
				bufDot.append("stateaction");
				bufDot.append(fsm.states.get(n).name);
				bufDot.append("  [arrowhead=none, len=0.01,weight=100 ]     ;\n"); // weight:
																					// http://www.graphviz.org/content/attrs#dweight
			}
		}
		bufDot.append("//////////////////display  transitions//////////////////\n");
		for (int n = 0; n < fsm.states.size(); n++) {
			int nbTransitions = fsm.states.get(n).transitionsFromThisState
					.size();
			if (nbTransitions != 0)
				for (int m = 0; m < nbTransitions; m++) {
					Transition t = fsm.states.get(n).transitionsFromThisState
							.get(m);
					bufDot.append("    	");
					bufDot.append(t.origin);
					bufDot.append(" -> ");
					bufDot.append(t.destination);
					bufDot.append("[shape=box, label=  <<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">\n");
					bufDot.append("    	    	<TR>   <TD COLSPAN=\"");
					int nbActionsInTransitions = t.attachedActions.size();
					if (nbActionsInTransitions == 0)
						bufDot.append("1");
					else
						bufDot.append("2");
					bufDot.append("\">");
					bufDot.append(t.condition);
					bufDot.append("</TD> </TR>\n");
					for (int l = 0; l < nbActionsInTransitions; l++) {
						bufDot.append("    	    	<TR><TD>");
						bufDot.append(t.attachedActions.get(l).type);
						bufDot.append("</TD><TD>");
						bufDot.append(t.attachedActions.get(l).name);
						if (t.attachedActions.size() != 0)
							if (!t.attachedActions.get(l).expression.equals("")) {
								bufDot.append("=");
								bufDot.append(t.attachedActions.get(l).expression);
							}
						bufDot.append("</TD> </TR>\n");
					}
					bufDot.append("    	    	</TABLE>>  ];\n");

				}
		}

		int nbResetTransitions = fsm.resetTransitions.size();
		if (nbResetTransitions != 0) {
			bufDot.append("//////////////////display reset transitions//////////////////\n");
			for (int k = 0; k < nbResetTransitions; k++) {
				ResetTransition rt = fsm.resetTransitions.get(k);
				// bufDot.append("    	node [shape=box] ");
				bufDot.append("    	emptystateforreset");
				bufDot.append(k); // generate a name from the k value
				bufDot.append(" [shape=box, style=\"invis\" ];\n");
				bufDot.append("    	emptystateforreset");
				bufDot.append(k);
				bufDot.append(" -> ");
				bufDot.append(rt.destination);
				bufDot.append("  ");
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
			}
		}

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

		bufDot.append("}\n");
	}

	// ////////////////////////////////////////////////
	static public Boolean checkModel() {
		// TODO: add optional output (bus or not) to display the number of the
		// state in binary

		// TODO: check if all states have a transition to them, only the initial
		// state can have no transition to it
		// check if inputs/outputs names are allowed, "in" and "out" are
		// forbidden as they are reserved keyword of vhdl
		// check doublons of actions, or states etc...

		// TODO: add variable bus size to inputs and outputs in the grammar...
		// hard...

		// TODO: add to the model some always true action (no state or
		// transition dependency, can be useful for reseting an AMAZE at any
		// time for instance
		// TODO: check repeatedly action are compatible with state and
		// transition actions (they are exclusive, except for R and S)
		// TODO: check that all transitions have origin and destination states
		// existing
		// TODO: have multi bits outputs (size could be automatically determined
		// or given in code
		// TODO: check that there is at least one output
		Boolean modelOk = true;
		// //////////////////////////////////////////////////////////////////:
		// check actions coherence. actions of a given name have to be
		// compatible
		// either, I or (R or S) -> then inform output .memorized fiels
		// F -> just store the action, no output
		// //////////////////////////////////////////////////////////////////:
		int nbActionTotal = fsm.actions.size();
		for (int k = 0; k < nbActionTotal; k++) {
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
					else if ((a.type.equals("R")) || (a.type.equals("S")))
						out.memorized = true;
				} else // check that the action is compatible with the output
				{
					if ((a.type.equals("I") && out.memorized)
							|| (a.type.equals("R") && !out.memorized)
							|| (a.type.equals("S") && !out.memorized)) {
						System.out.print("Error: Incompatible actions on ");
						System.out.print(a.name);
						System.out.print("   type: ");
						System.out.print(a.type);
						System.out.print("    expression: ");
						System.out.print(a.expression);
						System.out.print("    has already been detected as ");
						if (out.memorized == false)
							System.out.print("non ");
						System.out.println("memorized");
						modelOk = false;
					}
				}
			}
		}
		return modelOk;
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	static public void generateVhdlTestBench() {
		// TODO:generateVhdlTestBench()
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	static public void generateVhdl() {
		int numberOfStates = fsm.states.size();
		// http://www.tutorialspoint.com/java/lang/integer_highestonebit.htm
		// int numberOfBitsForStates =
		// Math.log(Integer.highestOneBit(numberOfStates - 1))/Math.log(2);
		int numberOfBitsForStates = Integer.toBinaryString(numberOfStates)
				.length();
		bufVhdl.append("-----------------------------------------------------------------------------------------------------------\n");
		bufVhdl.append("-- Finite State Machine .vhdl  autogenerated by FsmProcess B. VANDEPORTAELE LAAS-CNRS 2016\n");
		bufVhdl.append("-----------------------------------------------------------------------------------------------------------\n");
		bufVhdl.append("library	ieee;\nuse		ieee.std_logic_1164.all;\nuse		ieee.std_logic_unsigned.all;\nuse		ieee.std_logic_arith.all;\nentity ");
		bufVhdl.append(fsm.name);
		bufVhdl.append(" is\n	port (\n");
		bufVhdl.append("		ck,arazb								: in  std_logic;\n");
		// ////////////////listing of inputs/outputs//////////////////
		for (int n = 0; n < fsm.hmapInput.size(); n++) {
			bufVhdl.append("		");
			bufVhdl.append(fsm.inputs.get(n).name);
			bufVhdl.append(": in  std_logic;\n");
		}
		if (fsm.GenerateNumberOfStateOutput) {
			bufVhdl.append("		state_number: out std_logic_vector( ");
			bufVhdl.append(numberOfBitsForStates - 1);
			bufVhdl.append(" downto 0);\n");
		}
		for (int n = 0; n < fsm.hmapOutput.size(); n++) {
			bufVhdl.append("		");
			bufVhdl.append(fsm.outputs.get(n).name);
			bufVhdl.append(": out  std_logic");
			if (n != fsm.hmapOutput.size() - 1)
				bufVhdl.append(";\n");
			else
				bufVhdl.append(");\n");
		}
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
		bufVhdl.append("signal etat_present, etat_suivant : fsm_state;\n");
		// ////////////////listing of internal signals for memorized
		// outputs//////////////////
		for (int n = 0; n < fsm.outputs.size(); n++) {
			Output out = fsm.outputs.get(n);
			if (out.memorized) {
				bufVhdl.append("signal ");
				bufVhdl.append(out.name);
				bufVhdl.append("_set, ");
				bufVhdl.append(out.name);
				bufVhdl.append("_reset : std_logic;\n");
			}
		}
		// ////////////////let's animate all that stuff...//////////////////
		bufVhdl.append("------------------------Process for the memorization of the state----------------------\n");
		bufVhdl.append("begin\n");
		bufVhdl.append("process (ck, arazb)\nbegin\n    if (arazb='0') then etat_present <=");
		bufVhdl.append("state_");
		bufVhdl.append(fsm.states.get(0).name);
		bufVhdl.append(";\n");
		bufVhdl.append("    elsif ck'event and ck='1' then etat_present<=etat_suivant;\n");
		bufVhdl.append("    end if;\n");
		bufVhdl.append("end process;\n\n");
		bufVhdl.append("-------------------Combinatorial process for the evolution of the state------------------\n");
		bufVhdl.append("process (etat_present");
		for (int n = 0; n < fsm.inputs.size(); n++) {
			bufVhdl.append(", ");
			bufVhdl.append(fsm.inputs.get(n).name);
		}
		bufVhdl.append(")\n");
		bufVhdl.append("begin\n");
		bufVhdl.append("    case etat_present is\n");
		// pour chaque état, il peut y avoir plusieurs transitions, la première
		// if, les suivantes elsif et finalement en plus le maintien dans l'état
		// courant
		for (int n = 0; n < numberOfStates; n++) {
			bufVhdl.append("      when state_"); // prefix state name with
													// state_
			bufVhdl.append(fsm.states.get(n).name);
			int transitionFromThisStateNumber = fsm.states.get(n).transitionsFromThisState
					.size();
			bufVhdl.append(" => "); // if (transitionFromThisStateNumber==0)
									// //stay always in that state
			for (int m = 0; m < transitionFromThisStateNumber; m++) {
				if (m == 0)
					bufVhdl.append("   if ( ");
				else
					bufVhdl.append("                         elsif ( ");
				bufVhdl.append(" ( ");
				bufVhdl.append(fsm.states.get(n).transitionsFromThisState
						.get(m).condition);
				bufVhdl.append(" ) ");
				bufVhdl.append(" ='1' ) then etat_suivant <= state_");
				bufVhdl.append(fsm.states.get(n).transitionsFromThisState
						.get(m).destination);
				bufVhdl.append(";\n");
			}
			if (transitionFromThisStateNumber != 0)
				bufVhdl.append("                         else	");
			bufVhdl.append("etat_suivant <= state_");
			bufVhdl.append(fsm.states.get(n).name);
			bufVhdl.append(";\n");
			if (transitionFromThisStateNumber != 0)
				bufVhdl.append("                        end if;\n");
		}
		bufVhdl.append("--    when others => etat_suivant <= state_");
		bufVhdl.append(fsm.states.get(0).name);
		bufVhdl.append(";\n    end case;\nend process;\n");
		bufVhdl.append("------------------FLIP FLOPS FOR MEMORIZED OUTPUTS ------------\n");
		for (int n = 0; n < fsm.outputs.size(); n++) {
			Output out = fsm.outputs.get(n);
			if (out.memorized) {
				bufVhdl.append("------ FLIP FLOPS FOR ");
				bufVhdl.append(out.name);
				bufVhdl.append(" ------\n");
				bufVhdl.append("process (ck, arazb)\n");
				bufVhdl.append("begin\n");
				bufVhdl.append("   if (arazb='0') then ");
				bufVhdl.append(out.name);
				bufVhdl.append("<='0';\n");
				bufVhdl.append("   elsif ck'event and ck='1' then  \n");
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
				bufVhdl.append("      end if;\n");
				bufVhdl.append("    end if;\n");
				bufVhdl.append("end process;\n");
			}
		}
		for (int cpt = 0; cpt < 3; cpt++) {
			Boolean processMemorizedOutput;
			String typeFilter;
			String signalSuffix;
			switch (cpt) {
			case 0:
				processMemorizedOutput = false;
				typeFilter = "I";
				signalSuffix = "";
				bufVhdl.append("------------------ NON MEMORIZED OUTPUTS ------------\n");
				break;
			case 1:
				processMemorizedOutput = true;
				typeFilter = "R";
				signalSuffix = "_reset";
				bufVhdl.append("------------------  FLIP FLOPS MEMORIZED RESET OUTPUT CONTROL------------\n");
				break;
			case 2:
			default:
				processMemorizedOutput = true;
				typeFilter = "S";
				signalSuffix = "_set";
				bufVhdl.append("------------------  FLIP FLOPS MEMORIZED SET OUTPUT CONTROL------------\n");
				break;
			}
			for (int n = 0; n < fsm.outputs.size(); n++)
				// if (!fsm.outputs.get(n).memorized)
				if (fsm.outputs.get(n).memorized == processMemorizedOutput) {
					String currentOutputName = fsm.outputs.get(n).name;
					bufVhdl.append("    ");
					bufVhdl.append(currentOutputName);
					bufVhdl.append(signalSuffix);
					bufVhdl.append("  <=  ");
					// look for actions in the fsm that deals with this output
					for (int m = 0; m < fsm.states.size(); m++) {
						// ////////////////////action on
						// state//////////////////////
						int nbActionInThatState = fsm.states.get(m).attachedActions
								.size();
						if (nbActionInThatState != 0) {
							for (int l = 0; l < nbActionInThatState; l++) {
								Action a = fsm.states.get(m).attachedActions
										.get(l);
								if (a != null) {
									if (a.name.equals(currentOutputName)
											&& a.type.equals(typeFilter)) {
										if (!a.expression.equals("")) { // if
																		// there
																		// is an
																		// expression
											bufVhdl.append(" ( ");
											bufVhdl.append(a.expression);
											bufVhdl.append(" ) ");
										} else {
											bufVhdl.append(" '1' ");
										}
										bufVhdl.append("when ( (etat_present = ");
										bufVhdl.append("state_");
										bufVhdl.append(fsm.states.get(m).name);
										bufVhdl.append(") ");
										bufVhdl.append(")   else \n            ");
									}
								}
							}
						}
						// ////////////////////action on transition from this
						// state//////////////////////
						int nbTransitionInThatState = fsm.states.get(m).transitionsFromThisState
								.size();
						for (int j = 0; j < nbTransitionInThatState; j++) {
							Transition t = fsm.states.get(m).transitionsFromThisState
									.get(j);
							int nbActionInThatTransition = t.attachedActions
									.size();
							if (nbActionInThatTransition != 0) {
								for (int l = 0; l < nbActionInThatTransition; l++) {
									Action a = t.attachedActions.get(l);
									if (a != null) {
										if (a.name.equals(currentOutputName)
												&& a.type.equals(typeFilter)) {
											if (!a.expression.equals("")) {
												// if there is an expression
												bufVhdl.append(" ( ");
												bufVhdl.append(a.expression);
												bufVhdl.append(" ) ");
											} else {
												bufVhdl.append(" '1' ");
											}
											bufVhdl.append("when ( (etat_present = ");
											bufVhdl.append("state_");
											bufVhdl.append(fsm.states.get(m).name);
											bufVhdl.append(") ");
											bufVhdl.append(" and  ( ( ");
											bufVhdl.append(t.condition);
											bufVhdl.append(") = \'1\' )");
											bufVhdl.append(")  else \n            ");
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
						int nbActionInThatResetTransition = rt.attachedActions
								.size();
						if (nbActionInThatResetTransition != 0) {
							for (int l = 0; l < nbActionInThatResetTransition; l++) {
								Action a = rt.attachedActions.get(l);
								if (a != null) {
									if (a.name.equals(currentOutputName)
											&& a.type.equals(typeFilter)) {
										if (!a.expression.equals("")) {
											// if there is an expression
											bufVhdl.append(" ( ");
											bufVhdl.append(a.expression);
											bufVhdl.append(" ) ");
										} else {
											bufVhdl.append(" '1' ");
										}
										bufVhdl.append("when ( ( ");
										bufVhdl.append(rt.condition);
										bufVhdl.append(") = \'1\' )");
										bufVhdl.append("   else  \n            ");
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
							if (a.name.equals(currentOutputName)
									&& a.type.equals(typeFilter)) {
								if (!a.expression.equals("")) { // if there is
																// an expression
									bufVhdl.append(" ( ");
									bufVhdl.append(a.expression);
									bufVhdl.append(" ) ");
								} else {
									bufVhdl.append(" '1' ");
								}
								// this action is always true
								bufVhdl.append("when ( true)   else  \n            ");
							}
						}
					}
					// this is the default value for all outputs.
					// A S action is automatically added when there is no
					// corresponding R action and vice versa
					bufVhdl.append("\'0\'; \n");
				}
			// TODO utiliser String.format(longest..) pour avoir un affichage
			// aligné
		}
		if (fsm.GenerateNumberOfStateOutput) {
			bufVhdl.append("------------------  OUTPUTS FOR CURRENT STATE VISUALIZATION ------------\n");
			bufVhdl.append("	state_number <= \"");
			for (int i = 0; i < numberOfStates; i++) {
				if (i != 0)
					bufVhdl.append("                   else \"");
				bufVhdl.append(String.format(
						"%" + Integer.toString(numberOfBitsForStates) + "s",
						Integer.toBinaryString(i)).replace(" ", "0"));
				// bufVhdl.append( Integer.toBinaryString(i));
				bufVhdl.append("\" when ( etat_present = ");
				bufVhdl.append("state_");
				bufVhdl.append(fsm.states.get(i).name);
				bufVhdl.append(")\n");
			}
			bufVhdl.append("                   else \"");
			bufVhdl.append(String.format(
					"%" + Integer.toString(numberOfBitsForStates) + "s",
					Integer.toBinaryString(0)).replace(" ", "0"));

			bufVhdl.append("\";\n");
		}
		bufVhdl.append("end ar;\n");
	} // ///////////////////////////////////////////////////////////////

	static class ResetTransition {
		String destination;
		String condition;
		ArrayList<Action> attachedActions = new ArrayList<Action>();
	} // ///////////////////////////////////////////////////////////////

	static class Transition {
		String origin;
		String destination;
		String condition;
		ArrayList<Action> attachedActions = new ArrayList<Action>();
	} // //////////////////////////////////////////////////////////////////

	static class Action {
		String type; // I, R, S, F
		String name;
		String expression;
	} // //////////////////////////////////////////////////////////////////

	static class Input {
		String type;
		String name;
	}

	// //////////////////////////////////////////////////////////////////
	static class Output {
		String type;
		String name;
		Boolean memorized;
	}

	// //////////////////////////////////////////////////////////////////
	static class State {
		Boolean isInit; // initial state or not
		// String name=new String("");
		String name;
		// static ArrayList<Action> attachedActions=new ArrayList<Action>() ;
		ArrayList<Action> attachedActions = new ArrayList<Action>();
		// static ArrayList<Transition> transitionFromThisState=new
		// ArrayList<Transition>() ;
		ArrayList<Transition> transitionsFromThisState = new ArrayList<Transition>();
	}

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
	static class FiniteStateMachine {
		// member variables for storing the fsm model
		public String name;
		ArrayList<ResetTransition> resetTransitions = new ArrayList<ResetTransition>();
		ArrayList<State> states = new ArrayList<State>();
		HashMap<String, State> hmapState = new HashMap<String, State>();
		ArrayList<Input> inputs = new ArrayList<Input>();
		HashMap<String, Input> hmapInput = new HashMap<String, Input>();
		ArrayList<Output> outputs = new ArrayList<Output>();
		HashMap<String, Output> hmapOutput = new HashMap<String, Output>();
		ArrayList<Transition> transitions = new ArrayList<Transition>();
		HashMap<String, Transition> hmapTransition = new HashMap<String, Transition>();

		ArrayList<Action> repeatedlyActions = new ArrayList<Action>(); // global
																		// actions
																		// list

		ArrayList<Action> actions = new ArrayList<Action>(); // global actions
																// list
		// on stocke juste la liste pour pouvoir balayer et verifier les
		// compatibilités, pas besoin de hashtable
		// HashMap<String,Transition > hmapTransition = new
		// HashMap<String,Transition>();

		public static Boolean GenerateNumberOfStateOutput = true;

		// member variables for parsing
		public static State currentState = null;
		public static Action currentAction = null;
		public static Transition currentTransition = null;
		public static ResetTransition currentResetTransition = null;
		public static boolean currentTransitionIsReset; // to know when parsing
														// a condition if it
														// should be added to
														// ResetTransition or
														// Transition

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

		public boolean addState(String name, State s) {
			if (hmapState.containsKey(name) || hmapState.containsValue(s))
				return false; // if this has already been added, return false
			else {
				fsm.hmapState.put(name, s); // else add it to the hash map
				states.add(s); // and to the list
				return true;
			}
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
			if (hmapTransition.containsKey(name)
					|| hmapTransition.containsValue(t))
				return false; // if this has already been added, return false
			else {
				fsm.hmapTransition.put(name, t); // else add it to the hash map
				transitions.add(t); // and to the list
				return true;
			}
		}
	}

	// ///////////////////////////////////////////////////////////////
	// Il suffit de redéfinir la méthode equals de tes objets pour contrôler
	// comment fonctionne le contains (c'est lui qui est appelé pour faire la
	// comparaison).

	// ///////////////////////////////////////////////////////////////
	/*
	 * static class Graph { // I'm using org.antlr.v4.runtime.misc:
	 * OrderedHashSet, MultiMap Set<String> nodes = new
	 * OrderedHashSet<String>(); // list of functions MultiMap<String, String>
	 * edges = // caller->callee new MultiMap<String, String>();
	 * 
	 * public void edge(String source, String target) { edges.map(source,
	 * target); }
	 * 
	 * public String toString() { return "edges: " + edges.toString() +
	 * ", functions: " + nodes; } }
	 */
	// ///////////////////////////////////////////////////////////////
	static class FunctionListener extends FsmBaseListener {
		// //////////////////////////////////////////////////////////////
		public void enterInput(FsmParser.InputContext ctx) {
			Input i = new Input();
			i.name = ctx.children.get(0).getText();
			fsm.addInput(ctx.children.get(0).getText(), i);
		}

		// //////////////////////////////////////////////////////////////
		public void enterReset_transition(FsmParser.Reset_transitionContext ctx) {
			fsm.currentTransitionIsReset = true;
			ResetTransition rt = new ResetTransition();
			fsm.currentResetTransition = rt;
			rt.condition = "1"; // default condition
			rt.destination = ctx.children.get(1).getText();
			fsm.resetTransitions.add(rt);
		}

		// //////////////////////////////////////////////////////////////
		public void enterCondition(FsmParser.ConditionContext ctx) {
			String reconstructedCondition = new String("");
			int nbChildren = ctx.getChildCount();
			for (int n = 0; n < nbChildren; n++) // reconstruct the condition,
													// adding space characters
													// between terms.
			{
				reconstructedCondition += ctx.children.get(n).getText();
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
			t.origin = ctx.children.get(0).getText();
			t.destination = ctx.children.get(2).getText();
			// add the transition in its origin state, first get the state from
			// its name
			if (fsm.hmapState.get(t.origin) != null) // the state exists
				fsm.hmapState.get(t.origin).transitionsFromThisState.add(t);
			else {
				// TODO: deal with this error
			}
		}

		// //////////////////////////////////////////////////////////////
		public void enterAction_expression(
				FsmParser.Action_expressionContext ctx) {
			String reconstructedExpression = new String("");
			int nbChildren = ctx.getChildCount();
			for (int n = 0; n < nbChildren; n++) // reconstruct the condition,
													// adding space characters
													// between terms.
			{
				reconstructedExpression += ctx.children.get(n).getText();
				if (n != nbChildren - 1)
					reconstructedExpression += " ";
			}
			fsm.currentAction.expression = reconstructedExpression;
			// TODO: IL FAUT COMPLETER CAR ICI JE NE GERE QUE LE 1° TERME...
			// done?
		}

		// //////////////////////////////////////////////////////////////
		public void enterAction_id(FsmParser.Action_idContext ctx) {
			fsm.currentAction.name = ctx.children.get(0).getText();
			Output o = new Output();
			// TODO: deal with memorized outputs if R or S

			o.memorized = null; // default, will be defined later in the
								// analysis
			o.name = ctx.children.get(0).getText();
			// TODO: mettre les autres champs de o, peut être le faire ailleurs
			// TODO; gérer i l'action existe déjà, et/ou si le type d'action est
			// incompatible
			// ex: I puis R ou S
			fsm.addOutput(ctx.children.get(0).getText(), o);
		}

		// //////////////////////////////////////////////////////////////
		public void enterAction_type(FsmParser.Action_typeContext ctx) {
			fsm.currentAction.type = ctx.children.get(0).getText();
		}

		// //////////////////////////////////////////////////////////////
		public void enterTransition_action(
				FsmParser.Transition_actionContext ctx) {
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
		}

		// //////////////////////////////////////////////////////////////
		public void enterRepeatedly_action(
				FsmParser.Repeatedly_actionContext ctx) {
			Action a = new Action();
			fsm.currentAction = a;
			a.type = "I"; // default value if not specified
			a.name = ""; // default value if not specified
			a.expression = ""; // default value if not specified
			fsm.repeatedlyActions.add(a);
			fsm.actions.add(a);// add to the global action list
		}// //////////////////////////////////////////////////////////////

		public void enterState(FsmParser.StateContext ctx) {
			State s = new State();
			s.name = ctx.children.get(0).getText();
			if (fsm.states.size() == 0)
				s.isInit = true;
			else
				s.isInit = false;
			if (!fsm.addState(s.name, s)) {
				// TODO: error: state already exists
			}
			fsm.currentState = s;
		}
		// ///////////////////////////////////////////////////////////////
	}

	// ///////////////////////////////////////////////////////////////
	public static void main(String[] args) throws Exception {
		String inputFile = null;
		if (args.length > 0)
			inputFile = args[0];
		InputStream is = System.in;
		if (inputFile != null) {
			is = new FileInputStream(inputFile);
		}
		ANTLRInputStream input = new ANTLRInputStream(is);
		FsmLexer lexer = new FsmLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		FsmParser parser = new FsmParser(tokens);
		parser.setBuildParseTree(true);
		ParseTree tree = parser.file();
		ParseTreeWalker walker = new ParseTreeWalker();
		FunctionListener collector = new FunctionListener();
		bufVhdl = new StringBuilder();
		bufDot = new StringBuilder();
		walker.walk(collector, tree);

		String fsmInputName = args[0];

		// TODO: check the file exists:
		/*
		 * File f = new File(args[0]); System.out.println(f.getPath());
		 * System.out.println(f.getAbsolutePath()); try {
		 * System.out.println(f.getCanonicalPath()); } catch ( IOException e) {
		 * System.out.println("f.getCanonicalPath() failed"); }
		 */
		String fsmBaseName = fsmInputName;
		if (fsmBaseName.endsWith(".fsm")) {
			fsmBaseName = fsmBaseName.substring(0, fsmBaseName.length() - 4);
			if (fsmInputName.contains("/")) // it contains a unix based
											// directory name
				fsm.name = fsmInputName.substring(
						fsmInputName.lastIndexOf("/"),
						fsmInputName.length() - 4);
			else if (fsmInputName.contains("\\")) // it contains a windows based
													// directory name
				fsm.name = fsmInputName.substring(
						fsmInputName.lastIndexOf("\\"),
						fsmInputName.length() - 4); // it is a relative path
			else
				fsm.name = fsmInputName.substring(0, fsmInputName.length() - 4);
		}
		if (checkModel()) // could be optional to display and generate bad fsm
		{
			generateDot();
			saveToFile(bufDot.toString(), fsmBaseName.concat(".dot"));
			// System.out.println(bufDot.toString());
			generateVhdl();
			saveToFile(bufVhdl.toString(), fsmBaseName.concat(".vhd"));
			// System.out.println(bufVhdl.toString());
		}
	}
}
