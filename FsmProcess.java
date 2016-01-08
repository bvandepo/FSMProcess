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
//import org.stringtemplate.v4.ST;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Set;

public class FsmProcess {

	static StringBuilder buf;

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
		/*
		 * public String toDOT() { StringBuilder buf = new StringBuilder();
		 * buf.append("digraph G {\n"); buf.append("  ranksep=.25;\n");
		 * buf.append("  edge [arrowsize=.5]\n");
		 * buf.append("  node [shape=circle, fontname=\"ArialNarrow\",\n");
		 * buf.append("        fontsize=12, fixedsize=true, height=.45];\n");
		 * buf.append("  "); for (String node : nodes) { // print all nodes
		 * first buf.append(node); buf.append("; "); } buf.append("\n"); for
		 * (String src : edges.keySet()) { for (String trg : edges.get(src)) {
		 * buf.append("  "); buf.append(src); buf.append(" -> ");
		 * buf.append(trg); buf.append(";\n"); } } buf.append("}\n"); return
		 * buf.toString();
		 * 
		 * }
		 */
	}

	// //////////////////////////////////////////////////////////////

	static class FunctionListener extends FsmBaseListener {
		Graph graph = new Graph();
		// String currentFunctionName = null;
		int cptStates = 0;
		int nbActionInStates;
		Boolean inState = false; // set to true when inside a state, false
									// elsewhere
		Boolean inTransition = false; // set to true when inside a transistion,
										// false elsewhere
		Boolean inAction = false; // set to true when inside an action, false
									// elsewhere
		String currentStateName = null;
		String actionType;
		// String actionNameEtc;
		String actionName;
		String actionExpression;

		////////////////////////////////////////////////////////////////
		public void enterTransition(FsmParser.TransitionContext ctx) {
			inTransition = true;
			/*
			 * System.out.println("enter transition"); System.out.println();
			 */
		}
		////////////////////////////////////////////////////////////////
		public void exitTransition(FsmParser.TransitionContext ctx) {
			inTransition = false;
			/*
			 * System.out.println("exit transition"); System.out.println();
			 */
		}
		////////////////////////////////////////////////////////////////
		public void enterAction_expression(
				FsmParser.Action_expressionContext ctx) {
			if (inState) {
				actionExpression = ctx.children.get(0).getText();
			}
			// System.out.println("actionName= ");
			// System.out.println(actionName);
		}	
		////////////////////////////////////////////////////////////////
		public void exitAction_expression(FsmParser.Action_expressionContext ctx) {
		}
		////////////////////////////////////////////////////////////////
		public void enterAction_id(FsmParser.Action_idContext ctx) {
			if (inState)
			{
				actionName = ctx.children.get(0).getText();
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
				nbActionInStates++;
				if (nbActionInStates == 1) {
					buf.append("//définit l'action sur état\n");
					buf.append("stateaction");
					buf.append(currentStateName);
					buf.append("  [shape=box,label=  \n");
					buf.append("<<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">\n");
				}
			}
		}
		////////////////////////////////////////////////////////////////
		public void exitAction(FsmParser.ActionContext ctx) {
			inAction = false;
			if (inState) {
				// currentStateName = ctx.ID().getText();
				// System.out.println("exit action");
				buf.append("<TR><TD>");
				buf.append(actionType);
				buf.append("</TD><TD>");
				buf.append(actionName);
				if (actionExpression != "") {
					buf.append("=");
					buf.append(actionExpression);
				}
				buf.append("</TD> </TR>");
			}
		}	
		////////////////////////////////////////////////////////////////
		public void enterState(FsmParser.StateContext ctx) {
			// System.out.println("enter state");
			cptStates++;
			inState = true;
			buf.append("       ");
			currentStateName = ctx.children.get(0).getText();
			buf.append(currentStateName);
			if (cptStates == 1)
				buf.append(" [shape=doublecircle];\n");
			else
				buf.append(" [shape=circle];\n");
			nbActionInStates = 0;

			// is there some action on this states
			/*
			 * int nbChildren= ctx.getChildCount();
			 * System.out.print("nbChildren:   ");
			 * System.out.println(nbChildren); for (int i=0;i<nbChildren;i++) {
			 * System.out.print(ctx.children.get(i).getText());
			 * System.out.print("   "); }
			 */

			/*
			 * System.out.println(ctx.getText());
			 */
		}
		/////////////////////////////////////////////////////////////////
		public void exitState(FsmParser.StateContext ctx) {
		// System.out.println("exit state");
		inState = false;
		if (nbActionInStates != 0) 
			{
			buf.append("  </TABLE>>  ];\n");
			buf.append(" //attache l'action sur état\n");
			buf.append(currentStateName);
			buf.append(" ->");
			buf.append("stateaction");
			buf.append(currentStateName);
			buf.append("  [arrowhead=none]     ;\n");
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

		buf = new StringBuilder();
		buf.append("///////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
		buf.append("// Finite State Machine .dot diagram autogenerated by FsmProcess B. VANDEPORTAELE LAAS/CNRS 2016\n");
		buf.append("///////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
		buf.append("digraph finite_state_machine {\n");
		buf.append("  rankdir=LR;\n");
		buf.append("  size=\"10\"\n\n");
		buf.append("///////////////////////////////////////\n");

		walker.walk(collector, tree);
		// pour affichage en chaine
		// System.out.println(collector.graph.toString());
		// pour affichage en .DOT
		// System.out.println(collector.graph.toDOT());

		// Here's another example that uses StringTemplate to generate output
		// System.out.println(collector.graph.toST().render());

		buf.append("}\n");
		System.out.println(buf.toString());
	}
}
