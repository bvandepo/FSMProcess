// Generated from Fsm.g4 by ANTLR 4.5.1
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class FsmParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, NUMBER=27, STRING=28, ID=29, HTML_STRING=30, COMMENT=31, 
		LINE_COMMENT=32, PREPROC=33, WS=34, BlockComment=35, LineComment=36;
	public static final int
		RULE_file = 0, RULE_line = 1, RULE_state = 2, RULE_state_action = 3, RULE_transition_action = 4, 
		RULE_action_type = 5, RULE_transition = 6, RULE_reset_transition = 7, 
		RULE_condition = 8, RULE_expression = 9, RULE_action_id = 10, RULE_action_expression = 11, 
		RULE_operators = 12, RULE_element = 13, RULE_input = 14, RULE_id = 15;
	public static final String[] ruleNames = {
		"file", "line", "state", "state_action", "transition_action", "action_type", 
		"transition", "reset_transition", "condition", "expression", "action_id", 
		"action_expression", "operators", "element", "input", "id"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "':'", "';'", "','", "'='", "'R'", "'S'", "'I'", "'F'", "'->'", 
		"'?'", "'and'", "'or'", "'xor'", "'not'", "'xnor'", "'AND'", "'OR'", "'XOR'", 
		"'NOT'", "'XNOR'", "'+'", "'-'", "'*'", "'/'", "'=='", "'!='"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, "NUMBER", "STRING", "ID", "HTML_STRING", "COMMENT", 
		"LINE_COMMENT", "PREPROC", "WS", "BlockComment", "LineComment"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Fsm.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public FsmParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class FileContext extends ParserRuleContext {
		public List<LineContext> line() {
			return getRuleContexts(LineContext.class);
		}
		public LineContext line(int i) {
			return getRuleContext(LineContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).exitFile(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_file);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(33); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(32);
				line();
				}
				}
				setState(35); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__8) | (1L << NUMBER) | (1L << ID))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LineContext extends ParserRuleContext {
		public StateContext state() {
			return getRuleContext(StateContext.class,0);
		}
		public TransitionContext transition() {
			return getRuleContext(TransitionContext.class,0);
		}
		public Reset_transitionContext reset_transition() {
			return getRuleContext(Reset_transitionContext.class,0);
		}
		public LineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_line; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).enterLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).exitLine(this);
		}
	}

	public final LineContext line() throws RecognitionException {
		LineContext _localctx = new LineContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_line);
		try {
			setState(40);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(37);
				state();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(38);
				transition();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(39);
				reset_transition();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StateContext extends ParserRuleContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public List<State_actionContext> state_action() {
			return getRuleContexts(State_actionContext.class);
		}
		public State_actionContext state_action(int i) {
			return getRuleContext(State_actionContext.class,i);
		}
		public StateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_state; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).enterState(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).exitState(this);
		}
	}

	public final StateContext state() throws RecognitionException {
		StateContext _localctx = new StateContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_state);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			id();
			setState(47);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(43);
				match(T__0);
				setState(44);
				state_action();
				}
				}
				setState(49);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(50);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class State_actionContext extends ParserRuleContext {
		public Action_idContext action_id() {
			return getRuleContext(Action_idContext.class,0);
		}
		public Action_typeContext action_type() {
			return getRuleContext(Action_typeContext.class,0);
		}
		public Action_expressionContext action_expression() {
			return getRuleContext(Action_expressionContext.class,0);
		}
		public State_actionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_state_action; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).enterState_action(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).exitState_action(this);
		}
	}

	public final State_actionContext state_action() throws RecognitionException {
		State_actionContext _localctx = new State_actionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_state_action);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(55);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7))) != 0)) {
				{
				setState(52);
				action_type();
				setState(53);
				match(T__2);
				}
			}

			setState(57);
			action_id();
			setState(60);
			_la = _input.LA(1);
			if (_la==T__3) {
				{
				setState(58);
				match(T__3);
				setState(59);
				action_expression();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Transition_actionContext extends ParserRuleContext {
		public Action_idContext action_id() {
			return getRuleContext(Action_idContext.class,0);
		}
		public Action_typeContext action_type() {
			return getRuleContext(Action_typeContext.class,0);
		}
		public Action_expressionContext action_expression() {
			return getRuleContext(Action_expressionContext.class,0);
		}
		public Transition_actionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_transition_action; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).enterTransition_action(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).exitTransition_action(this);
		}
	}

	public final Transition_actionContext transition_action() throws RecognitionException {
		Transition_actionContext _localctx = new Transition_actionContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_transition_action);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(65);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7))) != 0)) {
				{
				setState(62);
				action_type();
				setState(63);
				match(T__2);
				}
			}

			setState(67);
			action_id();
			setState(70);
			_la = _input.LA(1);
			if (_la==T__3) {
				{
				setState(68);
				match(T__3);
				setState(69);
				action_expression();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Action_typeContext extends ParserRuleContext {
		public Action_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).enterAction_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).exitAction_type(this);
		}
	}

	public final Action_typeContext action_type() throws RecognitionException {
		Action_typeContext _localctx = new Action_typeContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_action_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TransitionContext extends ParserRuleContext {
		public List<IdContext> id() {
			return getRuleContexts(IdContext.class);
		}
		public IdContext id(int i) {
			return getRuleContext(IdContext.class,i);
		}
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public List<Transition_actionContext> transition_action() {
			return getRuleContexts(Transition_actionContext.class);
		}
		public Transition_actionContext transition_action(int i) {
			return getRuleContext(Transition_actionContext.class,i);
		}
		public TransitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_transition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).enterTransition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).exitTransition(this);
		}
	}

	public final TransitionContext transition() throws RecognitionException {
		TransitionContext _localctx = new TransitionContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_transition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			id();
			setState(75);
			match(T__8);
			setState(76);
			id();
			setState(79);
			_la = _input.LA(1);
			if (_la==T__9) {
				{
				setState(77);
				match(T__9);
				setState(78);
				condition();
				}
			}

			setState(85);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(81);
				match(T__0);
				setState(82);
				transition_action();
				}
				}
				setState(87);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(88);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Reset_transitionContext extends ParserRuleContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public List<Transition_actionContext> transition_action() {
			return getRuleContexts(Transition_actionContext.class);
		}
		public Transition_actionContext transition_action(int i) {
			return getRuleContext(Transition_actionContext.class,i);
		}
		public Reset_transitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_reset_transition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).enterReset_transition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).exitReset_transition(this);
		}
	}

	public final Reset_transitionContext reset_transition() throws RecognitionException {
		Reset_transitionContext _localctx = new Reset_transitionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_reset_transition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			match(T__8);
			setState(91);
			id();
			setState(94);
			_la = _input.LA(1);
			if (_la==T__9) {
				{
				setState(92);
				match(T__9);
				setState(93);
				condition();
				}
			}

			setState(100);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(96);
				match(T__0);
				setState(97);
				transition_action();
				}
				}
				setState(102);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(103);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConditionContext extends ParserRuleContext {
		public List<ElementContext> element() {
			return getRuleContexts(ElementContext.class);
		}
		public ElementContext element(int i) {
			return getRuleContext(ElementContext.class,i);
		}
		public ConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).enterCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).exitCondition(this);
		}
	}

	public final ConditionContext condition() throws RecognitionException {
		ConditionContext _localctx = new ConditionContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_condition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(105);
			element();
			setState(109);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << T__22) | (1L << T__23) | (1L << T__24) | (1L << T__25) | (1L << NUMBER) | (1L << ID))) != 0)) {
				{
				{
				setState(106);
				element();
				}
				}
				setState(111);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public List<IdContext> id() {
			return getRuleContexts(IdContext.class);
		}
		public IdContext id(int i) {
			return getRuleContext(IdContext.class,i);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).exitExpression(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(112);
			id();
			setState(116);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NUMBER || _la==ID) {
				{
				{
				setState(113);
				id();
				}
				}
				setState(118);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Action_idContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(FsmParser.ID, 0); }
		public TerminalNode NUMBER() { return getToken(FsmParser.NUMBER, 0); }
		public Action_idContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action_id; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).enterAction_id(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).exitAction_id(this);
		}
	}

	public final Action_idContext action_id() throws RecognitionException {
		Action_idContext _localctx = new Action_idContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_action_id);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(119);
			_la = _input.LA(1);
			if ( !(_la==NUMBER || _la==ID) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Action_expressionContext extends ParserRuleContext {
		public List<ElementContext> element() {
			return getRuleContexts(ElementContext.class);
		}
		public ElementContext element(int i) {
			return getRuleContext(ElementContext.class,i);
		}
		public Action_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).enterAction_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).exitAction_expression(this);
		}
	}

	public final Action_expressionContext action_expression() throws RecognitionException {
		Action_expressionContext _localctx = new Action_expressionContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_action_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(121);
			element();
			setState(125);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << T__22) | (1L << T__23) | (1L << T__24) | (1L << T__25) | (1L << NUMBER) | (1L << ID))) != 0)) {
				{
				{
				setState(122);
				element();
				}
				}
				setState(127);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OperatorsContext extends ParserRuleContext {
		public OperatorsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operators; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).enterOperators(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).exitOperators(this);
		}
	}

	public final OperatorsContext operators() throws RecognitionException {
		OperatorsContext _localctx = new OperatorsContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_operators);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(128);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << T__22) | (1L << T__23) | (1L << T__24) | (1L << T__25))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ElementContext extends ParserRuleContext {
		public OperatorsContext operators() {
			return getRuleContext(OperatorsContext.class,0);
		}
		public InputContext input() {
			return getRuleContext(InputContext.class,0);
		}
		public ElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_element; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).enterElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).exitElement(this);
		}
	}

	public final ElementContext element() throws RecognitionException {
		ElementContext _localctx = new ElementContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_element);
		try {
			setState(132);
			switch (_input.LA(1)) {
			case T__10:
			case T__11:
			case T__12:
			case T__13:
			case T__14:
			case T__15:
			case T__16:
			case T__17:
			case T__18:
			case T__19:
			case T__20:
			case T__21:
			case T__22:
			case T__23:
			case T__24:
			case T__25:
				enterOuterAlt(_localctx, 1);
				{
				setState(130);
				operators();
				}
				break;
			case NUMBER:
			case ID:
				enterOuterAlt(_localctx, 2);
				{
				setState(131);
				input();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InputContext extends ParserRuleContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public InputContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_input; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).enterInput(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).exitInput(this);
		}
	}

	public final InputContext input() throws RecognitionException {
		InputContext _localctx = new InputContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_input);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(134);
			id();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(FsmParser.ID, 0); }
		public TerminalNode NUMBER() { return getToken(FsmParser.NUMBER, 0); }
		public IdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_id; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).enterId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FsmListener ) ((FsmListener)listener).exitId(this);
		}
	}

	public final IdContext id() throws RecognitionException {
		IdContext _localctx = new IdContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_id);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136);
			_la = _input.LA(1);
			if ( !(_la==NUMBER || _la==ID) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3&\u008d\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\3\2\6\2$\n"+
		"\2\r\2\16\2%\3\3\3\3\3\3\5\3+\n\3\3\4\3\4\3\4\7\4\60\n\4\f\4\16\4\63\13"+
		"\4\3\4\3\4\3\5\3\5\3\5\5\5:\n\5\3\5\3\5\3\5\5\5?\n\5\3\6\3\6\3\6\5\6D"+
		"\n\6\3\6\3\6\3\6\5\6I\n\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b\5\bR\n\b\3\b\3\b"+
		"\7\bV\n\b\f\b\16\bY\13\b\3\b\3\b\3\t\3\t\3\t\3\t\5\ta\n\t\3\t\3\t\7\t"+
		"e\n\t\f\t\16\th\13\t\3\t\3\t\3\n\3\n\7\nn\n\n\f\n\16\nq\13\n\3\13\3\13"+
		"\7\13u\n\13\f\13\16\13x\13\13\3\f\3\f\3\r\3\r\7\r~\n\r\f\r\16\r\u0081"+
		"\13\r\3\16\3\16\3\17\3\17\5\17\u0087\n\17\3\20\3\20\3\21\3\21\3\21\2\2"+
		"\22\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \2\5\3\2\7\n\4\2\35\35\37\37"+
		"\3\2\r\34\u008c\2#\3\2\2\2\4*\3\2\2\2\6,\3\2\2\2\b9\3\2\2\2\nC\3\2\2\2"+
		"\fJ\3\2\2\2\16L\3\2\2\2\20\\\3\2\2\2\22k\3\2\2\2\24r\3\2\2\2\26y\3\2\2"+
		"\2\30{\3\2\2\2\32\u0082\3\2\2\2\34\u0086\3\2\2\2\36\u0088\3\2\2\2 \u008a"+
		"\3\2\2\2\"$\5\4\3\2#\"\3\2\2\2$%\3\2\2\2%#\3\2\2\2%&\3\2\2\2&\3\3\2\2"+
		"\2\'+\5\6\4\2(+\5\16\b\2)+\5\20\t\2*\'\3\2\2\2*(\3\2\2\2*)\3\2\2\2+\5"+
		"\3\2\2\2,\61\5 \21\2-.\7\3\2\2.\60\5\b\5\2/-\3\2\2\2\60\63\3\2\2\2\61"+
		"/\3\2\2\2\61\62\3\2\2\2\62\64\3\2\2\2\63\61\3\2\2\2\64\65\7\4\2\2\65\7"+
		"\3\2\2\2\66\67\5\f\7\2\678\7\5\2\28:\3\2\2\29\66\3\2\2\29:\3\2\2\2:;\3"+
		"\2\2\2;>\5\26\f\2<=\7\6\2\2=?\5\30\r\2><\3\2\2\2>?\3\2\2\2?\t\3\2\2\2"+
		"@A\5\f\7\2AB\7\5\2\2BD\3\2\2\2C@\3\2\2\2CD\3\2\2\2DE\3\2\2\2EH\5\26\f"+
		"\2FG\7\6\2\2GI\5\30\r\2HF\3\2\2\2HI\3\2\2\2I\13\3\2\2\2JK\t\2\2\2K\r\3"+
		"\2\2\2LM\5 \21\2MN\7\13\2\2NQ\5 \21\2OP\7\f\2\2PR\5\22\n\2QO\3\2\2\2Q"+
		"R\3\2\2\2RW\3\2\2\2ST\7\3\2\2TV\5\n\6\2US\3\2\2\2VY\3\2\2\2WU\3\2\2\2"+
		"WX\3\2\2\2XZ\3\2\2\2YW\3\2\2\2Z[\7\4\2\2[\17\3\2\2\2\\]\7\13\2\2]`\5 "+
		"\21\2^_\7\f\2\2_a\5\22\n\2`^\3\2\2\2`a\3\2\2\2af\3\2\2\2bc\7\3\2\2ce\5"+
		"\n\6\2db\3\2\2\2eh\3\2\2\2fd\3\2\2\2fg\3\2\2\2gi\3\2\2\2hf\3\2\2\2ij\7"+
		"\4\2\2j\21\3\2\2\2ko\5\34\17\2ln\5\34\17\2ml\3\2\2\2nq\3\2\2\2om\3\2\2"+
		"\2op\3\2\2\2p\23\3\2\2\2qo\3\2\2\2rv\5 \21\2su\5 \21\2ts\3\2\2\2ux\3\2"+
		"\2\2vt\3\2\2\2vw\3\2\2\2w\25\3\2\2\2xv\3\2\2\2yz\t\3\2\2z\27\3\2\2\2{"+
		"\177\5\34\17\2|~\5\34\17\2}|\3\2\2\2~\u0081\3\2\2\2\177}\3\2\2\2\177\u0080"+
		"\3\2\2\2\u0080\31\3\2\2\2\u0081\177\3\2\2\2\u0082\u0083\t\4\2\2\u0083"+
		"\33\3\2\2\2\u0084\u0087\5\32\16\2\u0085\u0087\5\36\20\2\u0086\u0084\3"+
		"\2\2\2\u0086\u0085\3\2\2\2\u0087\35\3\2\2\2\u0088\u0089\5 \21\2\u0089"+
		"\37\3\2\2\2\u008a\u008b\t\3\2\2\u008b!\3\2\2\2\21%*\619>CHQW`fov\177\u0086";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}