grammar Fsm;
file  : (line)+ ;          //a state machine is many lines, the fsm name is mandatory as the first entry

line : state 
     | transition
     | reset_transition
     | repeatedly_action
     | reset_asynchronous
     | clock_definition
     ;

state   :  id (':' state_action)* ';'   ;         	//state (with  action(s))

clock_definition: '/' input_clock ';' ;

input_clock:  id;

reset_asynchronous:   '=>' id ('?' condition_reset_asynchronous)? (',' level_reset_asynchronous)?  (':' action_reset_asynchronous)* ';'   ;        

level_reset_asynchronous : NUMBER ;

condition_reset_asynchronous: input_async_reset;

input_async_reset: id;


action_reset_asynchronous : action_id_reset_asynchronous  ('=' action_expression_reset_asynchronous)? ;

action_expression_reset_asynchronous : element ( element)*  ;
 

repeatedly_action : '%' (action_type ',')? action_id ('=' action_expression)? ';' ;

state_action :  (action_type ',')? action_id ('=' action_expression)? ;
transition_action :  (action_type ',')? action_id ('=' action_expression)? ;
 
action_type : 'R' | 'S' | 'M' | 'I' | 'F';

transition   	  :  id '->' id ('?' condition)? (':' transition_action)* ';'   ;         	//transition (with  action(s)) 
reset_transition  :   '->' id ('?' condition)? (':' transition_action)* ';'   ;         	//transition (with  action(s)) 


condition :  element ( element)*  ;
expression :  id ( id)*  ;

action_id_reset_asynchronous:  ID  | NUMBER
   ;
action_id: ID  | NUMBER
   ;

action_expression:  element ( element)*  ;

operators :  'and' | 'or' | 'xor' | 'not' | 'xnor' |  'AND' | 'OR' | 'XOR' | 'NOT' | 'XNOR' | '+' | '-' | '*' | '/' | '==' | '!=' ; //to complete with all needed operators 

element: operators  | constant | input;

constant: NUMBER ;

input: id;


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




