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

// case insensitive chars
fragment A:('a'|'A');
fragment B:('b'|'B');
fragment C:('c'|'C');
fragment D:('d'|'D');
fragment E:('e'|'E');
fragment F:('f'|'F');
fragment G:('g'|'G');
fragment H:('h'|'H');
fragment I:('i'|'I');
fragment J:('j'|'J');
fragment K:('k'|'K');
fragment L:('l'|'L');
fragment M:('m'|'M');
fragment N:('n'|'N');
fragment O:('o'|'O');
fragment P:('p'|'P');
fragment Q:('q'|'Q');
fragment R:('r'|'R');
fragment S:('s'|'S');
fragment T:('t'|'T');
fragment U:('u'|'U');
fragment V:('v'|'V');
fragment W:('w'|'W');
fragment X:('x'|'X');
fragment Y:('y'|'Y');
fragment Z:('z'|'Z');
fragment UNDERSCORE:'_';


AND         :  A N D;
NAND        :  N A N D;
OR          :  O R ;
NOR         :  N O R ;
XOR         :  X O R;
XNOR        :  X N O R ;
NOT         :  N O T ;
PLUS        :  '+' ; 
MINUS       :  '-'; 
DOUBLEEQUAL :  '==';
NOTEQUAL    :  '!=';

//TODO: TO COMPLETE WITH C AND VHDL OPERATORS, ALSO IN THE PARSER GRAMMAR 

TR: R;
TS: S;
TM: M;
TI: I;
TF: F;


IN : I N;
OUT : O U T;
INOUT : I N O U T;
BUFFER : B U F F E R;
LINKAGE : L I N K A G E; 

TO: T O ;
DOWNTO:  D O W N T O;		      

STD_LOGIC : S T D UNDERSCORE L O G I C ;
STD_LOGIC_VECTOR : S T D UNDERSCORE L O G I C UNDERSCORE V E C T O R;
	
INTEGER : I N T E G E R;
NATURAL:  N A T U R A L;
POSITIVE: P O S I T I V E;
REAL:     R E A L;
 
PARENTHESISOPEN: '(';
PARENTHESISCLOSE:')' ;


//TEXT  : ~'<'+ ;                         // clump all text together



POSITIVE_INTEGER   :    (PLUS)? DIGIT+    ;  
NEGATIVE_INTEGER   :    (MINUS)? DIGIT+    ;  


/** "a numeral [-]?(.[0-9]+ | [0-9]+(.[0-9]*)? )" */ 
NUMBER
   : '-'? ( '.' DIGIT+ | DIGIT+ ( '.' DIGIT* )? )
   ;




fragment DIGIT
   : [0-9]
   ;

/** "any double-quoted string ("...") possibly containing escaped quotes" */ 
STRING   : '"' ( '\\"' | . )*? '"'    ;

/** "Any string of alphabetic ([a-zA-Z\200-\377]) characters, underscores
 *  ('_') or digits ([0-9]), not beginning with a digit"
 */ 
ID   : LETTER ( LETTER | DIGIT )*    ;

/*
 ID_WITH_SPACE
   : LETTER ( ' ' | LETTER | DIGIT )*
   ;
*/

fragment LETTER
   : [a-zA-Z\u0080-\u00FF_]
   ;

/** "HTML strings, angle brackets must occur in matched pairs, and
 *  unescaped newlines are allowed."
 */ HTML_STRING
   : '<' ( TAG | ~ [<>] )* '>'
   ;

fragment TAG
   : '<' .*? '>'
   ;


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

PRAGMA_DOT_GLOBAL_DIRECTIVE                    :  '#pragma_dot_global_directive' ;

//LEFT_CURLY_BRACE: '{';
//RIGHT_CURLY_BRACE: '}';

PRAGMA_ENDING: '}#pragma' ; 




// PRAGMA: '#pragma{' SOMECARS  '#pragma}' ; 

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

