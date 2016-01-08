/***
 * Excerpted from "The Definitive ANTLR 4 Reference",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/tpantlr2 for more book information.
 ***/
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.MultiMap;
import org.antlr.v4.runtime.misc.OrderedHashSet;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;

//TODO: comprendre pourquoi antlr ne genere par le fichier FsmParser.java  mais uniquement le class...

public class FsmProcess {

	static StringBuilder bufDot;
	static StringBuilder bufVhdl;	
	static ArrayList<String> inputList =new ArrayList<String>() ;
	static ArrayList<String> outputList =new ArrayList<String>() ;
	static ArrayList<String> stateList =new ArrayList<String>() ;
	static int nbTransitions=0;
	static ArrayList<Transition> transitionList =new ArrayList<Transition>() ;
	
	
	static int cptStates = 0;
	static int nbActionsInState;
	static int nbActionsInTransition;
	static Boolean inState = false; // set to true when inside a state, false  elsewhere
	static Boolean inTransition = false; // set to true when inside a transistion,false elsewhere
	static Boolean transitionIsReset=false;
	static Boolean inAction = false; // set to true when inside an action, false elsewhere
	static Boolean conditionDefined;
	static String currentStateName = null;
	static String actionType;
	static String actionName;
	static String actionExpression;
	static String conditionName;
	static String inputName;
	static String fsmName="default";
	//////////////////////////////////////////////////
	static public void generateVhdl()
	{
	bufVhdl.append("library	ieee;\nuse		ieee.std_logic_1164.all;\nuse		ieee.std_logic_unsigned.all;\nuse		ieee.std_logic_arith.all;\nentity ");
	bufVhdl.append(fsmName);
	bufVhdl.append(" is\n	port (\n");
	bufVhdl.append("		ck,arazb								: in  std_logic;\n");
	for (int n=0;n<inputList.size();n++)
		{
		bufVhdl.append("		");
		bufVhdl.append(inputList.get(n));
		bufVhdl.append(": in  std_logic;\n");
		}
	for (int n=0;n<outputList.size();n++)
	{
	bufVhdl.append("		");
	bufVhdl.append(outputList.get(n));
	bufVhdl.append(": out  std_logic");
	if (n!=outputList.size()-1)
		bufVhdl.append(";\n");
	else	
		bufVhdl.append(");\n");		
	}
	
	bufVhdl.append("end ");	
	bufVhdl.append(fsmName);
	bufVhdl.append(";\n\n");
	bufVhdl.append("architecture a of ");
	bufVhdl.append(fsmName);
	bufVhdl.append(" is \n");
	bufVhdl.append("type fsm_state is (");
	for (int n=0;n<stateList.size();n++)
	{
		//prefix state name with state_
		bufVhdl.append("state_");
		bufVhdl.append(stateList.get(n));
		if (n!=stateList.size()-1)
			bufVhdl.append(", ");
	}
	bufVhdl.append(");\n");
	bufVhdl.append("signal etat_present, etat_suivant : etat_mae;\n begin");
	
	bufVhdl.append("begin\n");
	bufVhdl.append("process (ck, arazb)\nbegin\n if (arazb='0') then etat_present <=");
	bufVhdl.append("state_");
	bufVhdl.append(stateList.get(0));
	bufVhdl.append(";\n");
	bufVhdl.append("elsif ck'event and ck='1' then etat_present<=etat_suivant;\n");
	bufVhdl.append("end if;\n");
	bufVhdl.append("end process;\n\n");
	
	bufVhdl.append("process (etat_present");
	for (int n=0;n<inputList.size();n++)
	{
		bufVhdl.append(", ");
		bufVhdl.append(inputList.get(n));
	}
	bufVhdl.append(")\n begin\n");
	bufVhdl.append("case etat_present is\n");
	//pour chaque état, il peut y avoir plusieurs transitions, la première if, les suivantes elsif et finalement en plus le maintien dans l'état courant
	for (int n=0;n<stateList.size();n++)
	{
		bufVhdl.append("when state_");	//prefix state name with state_
		bufVhdl.append(stateList.get(n));

		if (n==0)
			bufVhdl.append(" => if ( ");
		else
			bufVhdl.append(" elsif ( ");
				//TODO condition
		bufVhdl.append(" CONDITION ");
		bufVhdl.append(" ) then etat_suivant <= state_");
		bufVhdl.append(" ETAT_SUIVANT ");
		bufVhdl.append("else			etat_suivant <= state_");
		bufVhdl.append(stateList.get(n));
		bufVhdl.append(";\n end if;\n);\n" );
	}


/*
	when ")et0 => if (c(12)='0') then etat_suivant <=et1;
				else etat_suivant <=et0;
				end if;
	when et1 => if (c(11)='0')  then etat_suivant <=et2;
				else etat_suivant <=et1;
				end if;
	when et2 => if (c(13)='0') then etat_suivant <=et3;
				else etat_suivant <=et2;
				end if;
	when et3 => if (c(9)='0')  then etat_suivant <=et4;
				else etat_suivant <=et3;
				end if;
	when et4 => if (c(10)='0')  then etat_suivant <=et5;
				else etat_suivant <=et4;
				end if;		
	when et5 => if (c(8)='0')  then etat_suivant <=et0;
				else etat_suivant <=et5;
				end if;
				*/
	bufVhdl.append("when others => etat_suivant <= state_");
	bufVhdl.append(stateList.get(0));
	bufVhdl.append(";\nend case;\n end process;\n");



/*
	for (int n=0;n<nbTransitions;n++)
		{
		bufVhdl.append("		");
		bufVhdl.append(transitionList.get(n).origin);
		bufVhdl.append("		");
		bufVhdl.append(transitionList.get(n).condition);
		bufVhdl.append("		");
		bufVhdl.append(transitionList.get(n).destination);
		bufVhdl.append("		\n");
		}
*/

	
	System.out.println(bufVhdl);
	}
/////////////////////////////////////////////////////////////////
	static class Transition {
		String origin;
		String destination;
		String condition;
		static ArrayList<Action> attachedActions=new ArrayList<Action>() ;		
	}
	static class Action {
		String type;
		String name;
		String expression;
	}
	static class Input{
		String type;
		String name;
	}
	static class Output{
		String type;
		String name;
		Boolean memorized;
	}
	static class State{
		Boolean isInit; //initial state or not
		String name;
		static ArrayList<Action> attachedActions=new ArrayList<Action>() ;		
		static ArrayList<Transition> transitionFromThisState=new ArrayList<Transition>() ;		
	}
	static class FiniteStateMachine{
		String name;
		static ArrayList<State> states=new ArrayList<State>() ;		
		static ArrayList<Input> inputs=new ArrayList<Input>() ;		
		static ArrayList<Output> outputs=new ArrayList<Output>() ;		
	}
	/////////////////////////////////////////////////////////////////

	
	/////////////////////////////////////////////////////////////////
	static class Graph {
		// I'm using org.antlr.v4.runtime.misc: OrderedHashSet, MultiMap
		Set<String> nodes = new OrderedHashSet<String>(); // list of functions
		MultiMap<String, String> edges = // caller->callee
		new MultiMap<String, String>();

		public void edge(String source, String target) {
			edges.map(source, target);
		}

		public String toString() {
			return "edges: " + edges.toString() + ", functions: " + nodes;
		}
	 }
	/////////////////////////////////////////////////////////////////
	static class FunctionListener extends FsmBaseListener {
		Graph graph = new Graph();
		// String currentFunctionName = null;
		
		////////////////////////////////////////////////////////////////
		public void enterInput(FsmParser.InputContext ctx){
			inputName = ctx.children.get(0).getText();
			if (! inputList.contains(inputName))
				inputList.add(inputName);			
		}
		////////////////////////////////////////////////////////////////
		public void exitInput(FsmParser.InputContext ctx){
			
		}
		////////////////////////////////////////////////////////////////
		
		////////////////////////////////////////////////////////////////
		public void  enterFsm_name(FsmParser.Fsm_nameContext ctx){
			fsmName=ctx.getText();
			bufDot.append("// Finite State Machine Name: ");
			bufDot.append(fsmName);
			bufDot.append("\n");
			bufDot.append("///////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
		}
		////////////////////////////////////////////////////////////////
		public void enterReset_transition(FsmParser.Reset_transitionContext ctx)
		{			
			transitionIsReset=true;
			inTransition = true;
			conditionDefined=false;
			conditionName="1"; //default
			nbActionsInTransition=0;
			bufDot.append("    	node [shape=box] ");
			bufDot.append("    	emptystateforreset");
			bufDot.append(currentStateName);
			bufDot.append("[shape=box, style=\"invis\" ];\n");
			bufDot.append("    	emptystateforreset");
			bufDot.append(currentStateName);
 			bufDot.append(" -> ");
			bufDot.append(ctx.children.get(1).getText());
		}
		////////////////////////////////////////////////////////////////		
		public 	void exitReset_transition(FsmParser.Reset_transitionContext ctx)
		{
			inTransition = false;
			if (nbActionsInTransition==0) //the table and condition have not yet been generated by the first action
			{
				bufDot.append("[style=\"dashed\", shape=box, label=  <<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\"> <TR>   <TD COLSPAN=\"1\">");
				bufDot.append(conditionName);
				bufDot.append("</TD> </TR>");
			}
			bufDot.append("  </TABLE>>  ];\n");	
			//TODO ajouter une transition à la liste avec org=NULL
		}
		////////////////////////////////////////////////////////////////
		public void  enterCondition(FsmParser.ConditionContext ctx){
			conditionDefined=true;
			//ca vire les espaces
			//conditionName=ctx.getText();
			//System.out.println("conditionName:");
			//System.out.println(conditionName);
			conditionName="";
		    int nbChildren= ctx.getChildCount();
		    for (int n=0;n<nbChildren;n++)
		    {
		    	conditionName+=ctx.children.get(n).getText();
		    	if (n!=nbChildren-1)
		    		conditionName+=" ";
		    }
		    
		    transitionList.get(nbTransitions).condition=conditionName;
		    
		}
		////////////////////////////////////////////////////////////////
		public 	void exitCondition(FsmParser.ConditionContext ctx){
	 	}
		////////////////////////////////////////////////////////////////
		public void enterTransition(FsmParser.TransitionContext ctx) {
			transitionIsReset=false;
			inTransition = true;
			conditionDefined=false;
			conditionName="1"; //default
			nbActionsInTransition=0;
			
			Transition t= new Transition();
			t.origin=ctx.children.get(0).getText();
			t.destination=ctx.children.get(2).getText();
			transitionList.add(t);
			
			bufDot.append("    	");
			bufDot.append(ctx.children.get(0).getText());
			bufDot.append(" -> ");
			bufDot.append(ctx.children.get(2).getText());
		}
		////////////////////////////////////////////////////////////////
		public void exitTransition(FsmParser.TransitionContext ctx) {
			inTransition = false;
			if (nbActionsInTransition==0) //the table and condition have not yet been generated by the first action
			{
				bufDot.append("[shape=box, label=  <<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\"> <TR>   <TD COLSPAN=\"1\">");
				bufDot.append(conditionName);
				bufDot.append("</TD> </TR>");
			}
			bufDot.append("  </TABLE>>  ];\n");
		 		
			nbTransitions++;
		}
		////////////////////////////////////////////////////////////////
		public void enterAction_expression(FsmParser.Action_expressionContext ctx) {
			if (inState || inTransition) {
				actionExpression = ctx.children.get(0).getText();
				//TODO IL FAUT COMPLETER CAR ICI JE NE GERE QUE LE 1° TERME...
			}
		}	
		////////////////////////////////////////////////////////////////
		public void exitAction_expression(FsmParser.Action_expressionContext ctx) {
		}
		////////////////////////////////////////////////////////////////
		public void enterAction_id(FsmParser.Action_idContext ctx) {
			if (inState || inTransition )
			{
				actionName = ctx.children.get(0).getText();
				if (! outputList.contains(actionName))
					outputList.add(actionName);
			}
 		}
		////////////////////////////////////////////////////////////////
		public void exitAction_id(FsmParser.Action_idContext ctx) {
		}
		////////////////////////////////////////////////////////////////
		public void enterAction_type(FsmParser.Action_typeContext ctx) {
			actionType = ctx.children.get(0).getText();
		}
		////////////////////////////////////////////////////////////////
		public void exitAction_type(FsmParser.Action_typeContext ctx) {
		}
		////////////////////////////////////////////////////////////////
		public void enterAction(FsmParser.ActionContext ctx) {
			actionType = "I"; // default value if not specified
			actionName = "";
			actionExpression = "";
			inAction = true; 
			if (inState) { 
				nbActionsInState++;
				if (nbActionsInState == 1) {
					bufDot.append("//Action on state:\n");
					bufDot.append("    	stateaction");
					bufDot.append(currentStateName);
					bufDot.append("  [shape=box,label=  ");
					bufDot.append(" <<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">\n");
				}
			}
			else if (inTransition) { 
				nbActionsInTransition++;
				if (nbActionsInTransition == 1) 
				{ 
					bufDot.append("[");
					if (transitionIsReset==true)
						bufDot.append(" style=\"dashed\", ");
					bufDot.append("shape=box, label=  <<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\"> <TR>   <TD COLSPAN=\"2\">");
					bufDot.append(conditionName);
					bufDot.append("</TD> </TR>");
				}				
			}
		}
		////////////////////////////////////////////////////////////////
		public void exitAction(FsmParser.ActionContext ctx) {
			inAction = false;
			if (inState || inTransition) 
			{
				bufDot.append("<TR><TD>");
				bufDot.append(actionType);
				bufDot.append("</TD><TD>");
				bufDot.append(actionName);
				if (actionExpression != "") 
				{
					bufDot.append("=");
					bufDot.append(actionExpression);
				}
				bufDot.append("</TD> </TR>");
			}
		}	
		////////////////////////////////////////////////////////////////
		public void enterState(FsmParser.StateContext ctx) {
			cptStates++;
			inState = true;
			bufDot.append("    	");
			currentStateName = ctx.children.get(0).getText();
			bufDot.append(currentStateName);
			if (cptStates == 1)
				bufDot.append(" [shape=doublecircle];\n");
			else
				bufDot.append(" [shape=circle];\n");
			nbActionsInState = 0;
			
			if (! stateList.contains(currentStateName))
				stateList.add(currentStateName);	
			
			// is there some action on this states
			/*
			 * int nbChildren= ctx.getChildCount();
			 * System.out.print("nbChildren:   ");
			 * System.out.println(nbChildren); for (int i=0;i<nbChildren;i++) {
			 * System.out.print(ctx.children.get(i).getText());
			 * System.out.print("   "); }
			 * System.out.println(ctx.getText());
			 */
		}
		/////////////////////////////////////////////////////////////////
		public void exitState(FsmParser.StateContext ctx) {
		// System.out.println("exit state");
		inState = false;
		if (nbActionsInState != 0) 
			{
			bufDot.append("</TABLE>>  ];\n");
			bufDot.append("//attach the action on the state\n    	");
			bufDot.append(currentStateName);
			bufDot.append(" ->");
			bufDot.append("stateaction");
			bufDot.append(currentStateName);
			bufDot.append("  [arrowhead=none]     ;\n");
			}
		}
		/////////////////////////////////////////////////////////////////
	}
	/////////////////////////////////////////////////////////////////
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
		// show tree in text form
		// System.out.println(tree.toStringTree(parser));

		ParseTreeWalker walker = new ParseTreeWalker();
		FunctionListener collector = new FunctionListener();
		bufVhdl = new StringBuilder();
		
		bufDot = new StringBuilder();
		bufDot.append("///////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
		bufDot.append("// Finite State Machine .dot diagram autogenerated by FsmProcess B. VANDEPORTAELE LAAS/CNRS 2016\n");
		bufDot.append("///////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
		bufDot.append("digraph finite_state_machine {\n");
		bufDot.append("    	rankdir=LR;\n");
		bufDot.append("    	size=\"10\";\n");
		bufDot.append("///////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
		walker.walk(collector, tree);
		// pour affichage en chaine
		// System.out.println(collector.graph.toString());
		// pour affichage en .DOT
		// System.out.println(collector.graph.toDOT());

		// Here's another example that uses StringTemplate to generate output
		// System.out.println(collector.graph.toST().render());


		
		bufDot.append("}\n");
		System.out.println(bufDot.toString());
	
		generateVhdl();
		System.out.println(bufVhdl);
			
/*
		System.out.println("Liste des actions");
		for (int n=0;n<outputList.size();n++)
			System.out.println(outputList.get(n));
		System.out.println("Liste des entrées");
		for (int n=0;n<inputList.size();n++)
			System.out.println(inputList.get(n));
	*/
	}
}
