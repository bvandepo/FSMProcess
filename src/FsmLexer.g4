lexer grammar FsmLexer;


tokens {  WHITESPACE_CHANNEL, COMMENTS_CHANNEL,PRAGMAS_CHANNEL}

// -----------------------Default mode rules  -----------------------

SEMICOLON            : ';'   ;
COLON                : ':'   ;
SLASH                : '/'   ; 
ANTISLASH            : '\\'   ; 
SHARP                : '#'   ; 
CONDITION            : '?'   ;
COMMA                : ','   ;
EQUAL                : '='   ;
REPEATACTION         : '%'   ;
ARROW                : '->'  ;
DOUBLEARROW          : '=>'  ;
STAR                 : '*'   ;
COLONEQUAL           : ':='  ;
DOT                  : '.'   ;
DOUBLEEQUAL :  '==';
NOTEQUAL    :  '!=';
PLUS        :  '+' ; 
MINUS       :  '-'; 


// case insensitive chars
  A:('a'|'A');
  B:('b'|'B');
  C:('c'|'C');
  D:('d'|'D');
  E:('e'|'E');
  F:('f'|'F');
  G:('g'|'G');
  H:('h'|'H');
  I:('i'|'I');
  J:('j'|'J');
  K:('k'|'K');
  L:('l'|'L');
  M:('m'|'M');
  N:('n'|'N');
  O:('o'|'O');
  P:('p'|'P');
  Q:('q'|'Q');
  R:('r'|'R');
  S:('s'|'S');
  T:('t'|'T');
  U:('u'|'U');
  V:('v'|'V');
  W:('w'|'W');
  X:('x'|'X');
  Y:('y'|'Y');
  Z:('z'|'Z');
  UNDERSCORE:'_';
 
LEFT_CURLY_BRACE: '{';
RIGHT_CURLY_BRACE: '}';
  
PARENTHESISOPEN: '(';
PARENTHESISCLOSE:')' ;


//TEXT  : ~'<'+ ;                         // clump all text together

 DIGIT  : [0-9]   ;

/** "any double-quoted string ("...") possibly containing escaped quotes" */ 
STRING   : '"' ( '\\"' | . )*? '"'    ;

/** "Any string of alphabetic ([a-zA-Z\200-\377]) characters, underscores
 *  ('_') or digits ([0-9]), not beginning with a digit"
 */ 
//ID   : LETTER ( LETTER | DIGIT )*    ;
 
 LETTER   : [a-zA-Z\u0080-\u00FF_]   ;

/** "HTML strings, angle brackets must occur in matched pairs, and
 *  unescaped newlines are allowed."
 */ HTML_STRING
   : '<' ( TAG | ~ [<>] )* '>'
   ;

fragment TAG  : '<' .*? '>' ;


PRAGMA_VHDL_PRE_ENTITY_DIRECTIVE               :  '#pragma_vhdl_pre_entity' ;
PRAGMA_VHDL_ENTITY_DIRECTIVE                   :  '#pragma_vhdl_entity{' ;
PRAGMA_VHDL_ARCHITECTURE_PRE_BEGIN_DIRECTIVE   :  '#pragma_vhdl_architecture_pre_begin' ;
PRAGMA_VHDL_ARCHITECTURE_POST_BEGIN_DIRECTIVE  :  '#pragma_vhdl_architecture_post_begin' ;
PRAGMA_VHDL_PROMOTE_TO_BUFFER_DIRECTIVE        :  '#pragma_vhdl_promote_to_buffer{' ;
PRAGMA_VHDL_DEMOTE_TO_SIGNAL_DIRECTIVE         :  '#pragma_vhdl_demote_to_signal{' ;
PRAGMA_VHDL_ALLOW_AUTOMATIC_BUFFERING_DIRECTIVE:  '#pragma_vhdl_allow_automatic_buffering' ;
PRAGMA_VHDL_SET_BIT_SIZE_FOR_OUTPUT_STATE_NUMBER: '#pragma_vhdl_set_bit_size_for_output_state_number{' ;
PRAGMA_VHDL_TESTBENCH_BEGIN_DIRECTIVE          :  '#pragma_vhdl_testbench' ;
PRAGMA_VHDL_GENERIC_DIRECTIVE                  :  '#pragma_vhdl_generic_directive{' ;


PRAGMA_WITH_BEGINING_AND_ENDING: '{' SOMECARS  '}#pragma' ; 
//PRAGMA_WITH_BEGINING_AND_ENDING: '{'    '}#pragma' ; 

PRAGMA_DOT_GLOBAL_DIRECTIVE                    :  '#pragma_dot_global_directive' ;

PRAGMA_ENDING: '}#pragma' ; 
 
//the same rule, but displays pragma mode when matched
//PRAGMA: '#pragma{' SOMECARS  '#pragma}' {System.out.println(" pragma mode");} ; 

Whitespace   :   [ \t]+            -> channel(WHITESPACE_CHANNEL) ;
WS 	     :[ \t\n\r]+           -> channel(WHITESPACE_CHANNEL) ;
 

COMMENT      : '/*' .*? '*/'       -> channel(COMMENTS_CHANNEL)   ;
LINE_COMMENT : '//' .*? '\r'? '\n' -> channel(COMMENTS_CHANNEL)   ; 
 



// -----------------------Pragma mode rules  -----------------------
//mode PRAGMA_MODE;
//END_PRAGMA    : '{' SOMECARS  '#pragma}' {System.out.println("exit pragma mode");} ->popMode ; 

SOMECARS: ANYCAR  -> channel(PRAGMAS_CHANNEL) ;
//SOMECARS is a token that contains all the pragma
//Impossible to attach  action to a fragment

fragment ANYCAR : (.)+? ;   
//fragment ANYCAR : ('a'..'z' | '\n' | 'r' | ' ' | '0'..'9')* ;

//I need a fragment in order to avoid error "non-fragment lexer rule 'ANYCHARS' can match the empty string"
//A fragment will not produce any token

