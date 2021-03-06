//Copyright (c) 2016, Bertrand Vandeportaele, LAAS/CNRS
//All rights reserved.
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright
//  notice, this list of conditions and the following disclaimer.
//* Redistributions in binary form must reproduce the above copyright
//  notice, this list of conditions and the following disclaimer in the
//  documentation and/or other materials provided with the distribution.
//* Neither the name of the University of California, Berkeley nor the
//  names of its contributors may be used to endorse or promote products
//  derived from this software without specific prior written permission.
//
//THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
//EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
//DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
//(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
//LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
//ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.



lexer grammar FsmLexer;


tokens {  WHITESPACE_CHANNEL, COMMENTS_CHANNEL,PRAGMAS_CHANNEL}

// -----------------------Default mode rules  -----------------------

SEMICOLON            : ';'   ;
COLON                : ':'   ;
SLASH                : '/'   ; 
ANTISLASH            : '\\'  ; 
SHARP                : '#'   ; 
CONDITION            : '?'   ;
COMMA                : ','   ;
EQUAL                : '='   ;
PERCENT              : '%'   ;
ARROW                : '->'  ;
DOUBLEARROW          : '=>'  ;
STAR                 : '*'   ;
COLONEQUAL           : ':='  ;
DOT                  : '.'   ;
DOUBLEEQUAL          :  '==' ;
NOTEQUAL             :  '!=' ;
PLUS                 :  '+'  ; 
MINUS                :  '-'  ; 
UNDERSCORE           :  '_'  ; 
LEFT_CURLY_BRACE     :  '{'  ;
RIGHT_CURLY_BRACE    :  '}'  ;
PARENTHESISOPEN      :  '('  ;
PARENTHESISCLOSE     :  ')'  ;
POWER                :  '^'  ;

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

//Added space to avoid tokenizing when these appears inside names
//Added + to detect the keyword even if they are separated by many spaces
AND         :  WS A N D WS;
NAND        :  WS N A N D WS;
OR          :  WS O R  WS;
NOR         :  WS N O R WS;
XOR         :  WS X O R WS;
XNOR        :  WS X N O R WS;
NOT         :  N O T WS;
//TEXT  : ~'<'+ ;                         // clump all text together

TO: WS T O WS;
DOWNTO: WS D O W N T O WS;		      




/** "any double-quoted string ("...") possibly containing escaped quotes" */ 
STRING   : '"' ( '\\"' | . )*? '"'    ;

/** "Any string of alphabetic ([a-zA-Z\200-\377]) characters, underscores
 *  ('_') or digits ([0-9]), not beginning with a digit"
 */ 
//ID   : LETTER ( LETTER | DIGIT )*    ;


//obligé de faire le token à ce niveau la, sinon ACTION_NON_MEMORISEE est vu comme 
//ACTION_NON_MEM OR ISEE
 //ID   :  (LETTER | DIGIT) ( LETTER | DIGIT | UNDERSCORE)*    ;
 
// ID   :  (LETTER | DIGIT) ( LETTER | DIGIT | UNDERSCORE)*    ;
//  ID   :  (LETTER ) ( LETTER | DIGIT | UNDERSCORE)*    ;
 
 
 //ID   :   DIGIT   ;
 

//doivent être décrit après ID !!!
   DIGIT  : [0-9]   ;

 
// LETTER   : [a-zA-Z\u0080-\u00FF_]   ;

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
PRAGMA_VHDL_INIT_TESTBENCH_BEGIN_DIRECTIVE     :  '#pragma_vhdl_init_testbench' ;
PRAGMA_VHDL_TESTBENCH_BEGIN_DIRECTIVE          :  '#pragma_vhdl_testbench' ;
PRAGMA_VHDL_TESTBENCH_PRE_BEGIN_DIRECTIVE      :  '#pragma_vhdl_testbench_pre_begin' ;
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
 


/*
EXPONENT
  :  ('E'|'e') ( '+' | '-' )? INTEGER
  ;


HEXDIGIT
    :	('A'..'F'|'a'..'f')
    ;


INTEGER
  :  DIGIT ( '_' | DIGIT )*
  ;

 

BASED_INTEGER
  : EXTENDED_DIGIT ('_' | EXTENDED_DIGIT)*
  ;

EXTENDED_DIGIT
  : (DIGIT | LETTER)
  ;

APOSTROPHE
  : '\''
  ;
  
  */


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

