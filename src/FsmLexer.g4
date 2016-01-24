lexer grammar FsmLexer;


tokens {  WHITESPACE_CHANNEL, COMMENTS_CHANNEL,PRAGMAS_CHANNEL}

// -----------------------Default mode rules  -----------------------

SEMICOLON            : ';'   ;
COLON                : ':'   ;
SLASH                : '/'   ; 
CONDITION            : '?'   ;
SEPARATOR            : ','   ;
EQUAL                : '='   ;
REPEATACTION         : '%'   ;
ARROW                : '->'  ;
DOUBLEARROW          : '=>'  ;
STAR                 : '*'   ;


AND         :  'and'  | 'AND';
OR          :  'or'   | 'OR' ;
XNOR        :  'xnor' | 'XNOR' ;
XOR         :  'xor'  | 'XOR';
NOT         :  'not'  | 'NOT';
PLUS        :  '+' ; 
MINUS       :  '-'; 
DOUBLEEQUAL :  '==';
NOTEQUAL    :  '!=';

//TODO: TO COMPLETE WITH C AND VHDL OPERATORS, ALSO IN THE PARSER GRAMMAR 


R: 'R';
S: 'S';
M: 'M';
I: 'I';
F: 'F';

PARENTHESISOPEN: '(';
PARENTHESISCLOSE:')' ;


//TEXT  : ~'<'+ ;                         // clump all text together



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


PRAGMA_VHDL_PRE_ENTITY_DIRECTIVE:  '#pragma_vhdl_pre_entity' ;
PRAGMA_VHDL_ENTITY_DIRECTIVE :  '#pragma_vhdl_entity' ;
PRAGMA_VHDL_ARCHITECTURE_PRE_BEGIN_DIRECTIVE :  '#pragma_vhdl_architecture_pre_begin' ;
PRAGMA_VHDL_ARCHITECTURE_POST_BEGIN_DIRECTIVE  :  '#pragma_vhdl_architecture_post_begin' ;


PRAGMA_WITH_BEGINING_AND_ENDING: '{' SOMECARS  '}#pragma' ; 


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

