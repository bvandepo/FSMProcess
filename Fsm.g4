grammar Fsm;
file  : fsm_name (line)+ ;          //a state machine is many lines, the fsm name is mandatory as the first entry

line : state 
     | transition
     | reset_transition
     ;

state   :  id (':' action)* ';'   ;         	//state (with  action(s))

action :  (action_type ',')? action_id ('=' action_expression)? ;
 
action_type : 'R' | 'S' | 'I' | 'F';

transition   	  :  id '->' id ('?' condition)? (':' action)* ';'   ;         	//transition (with  action(s)) 
reset_transition  :   '->' id ('?' condition)? (':' action)* ';'   ;         	//transition (with  action(s)) 


condition :  element ( element)*  ;
expression :  id ( id)*  ;

action_id: ID  | NUMBER
   ;

action_expression:  element ( element)*  ;

operators : 'AND' | 'OR' | 'XOR' | 'NOT' | 'XNOR' | '+' | '-' | '*' | '/' | '==' | '!=' ; //to complete with all needed operators 

element: operators | input;

input: id;


fsm_name: id;

id : ID  | NUMBER
   ;


//id : ID | STRING | HTML_STRING | NUMBER
//   ;

/** "a numeral [-]?(.[0-9]+ | [0-9]+(.[0-9]*)? )" */ NUMBER
   : '-'? ( '.' DIGIT+ | DIGIT+ ( '.' DIGIT* )? )
   ;


fragment DIGIT
   : [0-9]
   ;

/** "any double-quoted string ("...") possibly containing escaped quotes" */ STRING
   : '"' ( '\\"' | . )*? '"'
   ;

/** "Any string of alphabetic ([a-zA-Z\200-\377]) characters, underscores
 *  ('_') or digits ([0-9]), not beginning with a digit"
 */ ID
   : LETTER ( LETTER | DIGIT )*
   ;
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

COMMENT
   : '/*' .*? '*/' -> skip
   ;

LINE_COMMENT
   : '//' .*? '\r'? '\n' -> skip
   ;

/** "a '#' character is considered a line output from a C preprocessor (e.g.,
 *  # 34 to indicate line 34 ) and discarded"
 */ PREPROC
   : '#' .*? '\n' -> skip
   ;

WS
   : [ \t\n\r]+ -> skip
   ;


BlockComment
    :   '/*' .*? '*/'
        -> skip
    ;

LineComment
    :   '//' ~[\r\n]*
        -> skip
    ;




