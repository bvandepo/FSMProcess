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



parser grammar FsmParser;
// now I use non-combined grammar, the lexer rules are in the ModeFsmLexer.g4 file
// p277: Modes are not allowed within combined grammars, just lexer grammars.
// example: lexmagic/XMLParser.g4

options { tokenVocab=FsmLexer; } // use tokens from ModeFsmLexer.g4

fsmgeneric: stat+;

stat:  assignExpr*   printExpr;

printExpr:  numericexpr SEMICOLON*;
assignExpr: id EQUAL numericexpr SEMICOLON;
  
numericbinaryoperatorB:  PLUS | MINUS;
numericbinaryoperatorA: SLASH | STAR | PERCENT | POWER;

logoperator  : L O G  ; 
numericunaryoperator:  PLUS | MINUS;
numericexpr:  numericexpr numericbinaryoperatorA numericexpr  # MulDivModPow
     		| numericexpr numericbinaryoperatorB numericexpr  # AddSub
			| numericunaryoperator numericexpr                # ChangeSign  
     		| logoperator  parenthesisopen numericexpr COMMA numericexpr parenthesisclose   #Log 
       	    | parenthesisopen numericexpr parenthesisclose    # parens
     		| positive_integer                                # int
   		    | id											  # identifier
			;


fsmfile  : (line)+ ;          //a state machine is many lines, the fsm name is mandatory as the first entry

//////keywords at parser level/////////////////////////////////////
 
letter: A | B | C | D | E | F | G | H | I | J | K | L | M | N | O | P | Q | R | S | T | U | V | W | X | Y | Z;
 
action_type : R|S|M|I|F;

parenthesisopen: PARENTHESISOPEN;    //keep it as parser rules to be abble to process it in the listener functions
parenthesisclose: PARENTHESISCLOSE;
/**
and         :  A N D;
nand        :  N A N D;
or          :  O R ;
nor         :  N O R ;
xor         :  X O R;
xnor        :  X N O R ;
not         :  N O T ;
*/
//operators :  and | or | xor | xnor | not | PLUS | MINUS | STAR | SLASH | DOUBLEEQUAL | NOTEQUAL ;//to complete with all needed operators 
//unary_operators :   not | PLUS | MINUS   ;//to complete with all needed operators 
//binary_operators :  and | or | xor | xnor | PLUS | MINUS | STAR | SLASH | DOUBLEEQUAL | NOTEQUAL ;//to complete with all needed operators 

operators :  AND | OR | XOR | XNOR | NOT | PLUS | MINUS | STAR | SLASH | DOUBLEEQUAL | NOTEQUAL ;//to complete with all needed operators 
unary_operators :   NOT | PLUS | MINUS   ;//to complete with all needed operators 
binary_operators :  AND | OR | XOR | XNOR |    PLUS | MINUS | STAR | SLASH | DOUBLEEQUAL | NOTEQUAL ;//to complete with all needed operators 

in : I N;
out : O U T;
inout : I N O U T;
buffer : B U F F E R;
linkage : L I N K A G E; 

to: TO ;
downto:  DOWNTO;		      

std_logic : S T D UNDERSCORE L O G I C ;
std_logic_vector : S T D UNDERSCORE L O G I C UNDERSCORE V E C T O R;
	
integer : I N T E G E R;
natural:  N A T U R A L;
positive: P O S I T I V E;
real:     R E A L;

positive_integer   :    (PLUS)? DIGIT+    ;  
negative_integer   :    (MINUS)? DIGIT+    ;  

/** "a numeral [-]?(.[0-9]+ | [0-9]+(.[0-9]*)? )" */ 
number   : MINUS? ( DOT DIGIT+ | DIGIT+ ( DOT DIGIT* )? ) ;
 
constant: positive_integer ;

/////identifiers ///////////////////////////

id   :  (letter ) ( letter | DIGIT | UNDERSCORE)*    ;
signal_id: (letter ) ( letter | DIGIT | UNDERSCORE)*    ;
input: signal_id;  
state_id:( letter | DIGIT | UNDERSCORE)+    ; //at least one char
action_id: signal_id; 

/////////////////////////////////////////////

//* A line in a fsm file can be one of these statements  */
line : state  
     | transition
     | reset_transition
     | repeatedly_action
     | reset_asynchronous
     | clock_definition
     | multi_transitions_directive
     | multi_transitions_to_same_directive
     | multi_state_action_directive
     | pragma_directive;


pragma_directive: 
       pragma_dot_directive
     | pragma_vhdl_directive;

pragma_vhdl_directive:
       pragma_vhdl_pre_entity_directive
     | pragma_vhdl_entity_directive
     | pragma_vhdl_architecture_pre_begin_directive
     | pragma_vhdl_architecture_post_begin_directive
     | pragma_vhdl_promote_to_buffer_directive
     | pragma_vhdl_demote_to_signal_directive
     | pragma_vhdl_allow_automatic_buffering
     | pragma_vhdl_set_bit_size_for_output_state_number
     | pragma_vhdl_generic_directive
     | pragma_vhdl_testbench
     | pragma_vhdl_init_testbench     
     | pragma_vhdl_testbench_pre_begin_directive
     ;

pragma_dot_directive:
       pragma_dot_global_directive;
   
multi_state_action_directive:   
   (multi_transitions_base_state_name)?  PARENTHESISOPEN multi_transitions_first_state_number to multi_transitions_last_state_number PARENTHESISCLOSE (COLON multi_state_action)+ SEMICOLON;
   
multi_transitions_directive:
SHARP (multi_transitions_base_state_name)?  PARENTHESISOPEN multi_transitions_first_state_number to multi_transitions_last_state_number PARENTHESISCLOSE ( STAR  multi_transitions_priority)? (CONDITION condition_multi_transitions)? (COLON transition_action)* SEMICOLON;
multi_transitions_base_state_name:state_id;
multi_transitions_first_state_number:positive_integer;
multi_transitions_last_state_number:positive_integer;
multi_transitions_priority:positive_integer;
condition_multi_transitions: boolean_operation;

multi_transitions_to_same_directive:
SHARP (multi_transitions_base_state_name)?  PARENTHESISOPEN multi_transitions_first_state_number to multi_transitions_last_state_number PARENTHESISCLOSE ARROW multi_transitions_destination_state ( STAR  multi_transitions_priority)? (CONDITION condition_multi_transitions)? (COLON transition_action)* SEMICOLON;
multi_transitions_destination_state: state_id;
 
state   :  state_id ( COLON state_action)* SEMICOLON    ;         	//state (with  action(s))
clock_definition: SLASH  input_clock SEMICOLON ;
input_clock:  signal_id;
reset_asynchronous:   DOUBLEARROW state_id (CONDITION condition_reset_asynchronous)? ( COMMA level_reset_asynchronous)?  (COLON action_reset_asynchronous)* SEMICOLON  ;        

level_reset_asynchronous : DIGIT ;
condition_reset_asynchronous: input_async_reset;
input_async_reset: signal_id;


action_reset_asynchronous : action_id_reset_asynchronous  ( EQUAL  action_expression_reset_asynchronous)? ;

action_expression_reset_asynchronous : boolean_operation;
 
repeatedly_action : PERCENT   (action_type COMMA)? action_id ( EQUAL action_expression)? SEMICOLON ;

// different rules to trigger different listener functions
state_action      :  (action_type COMMA)? action_id (EQUAL action_expression)? ;
multi_state_action:  (action_type COMMA)? action_id (EQUAL action_expression)? ;
transition_action :  (action_type COMMA)? action_id (EQUAL action_expression)? ;
 
transition   	  :  state_id  ARROW  state_id ( STAR  transition_priority)? ('?' condition)? (COLON transition_action)* SEMICOLON   ;         	//transition (with  action(s)) 
reset_transition  :   '->' state_id (STAR reset_transition_priority)? ('?' condition) (COLON transition_action)* SEMICOLON   ;         	//transition (with  action(s)), the condition is mandatory for reset transition


transition_priority: positive_integer;
reset_transition_priority: positive_integer;
condition :  boolean_operation;
 
action_id_reset_asynchronous:  signal_id ;

action_expression:  boolean_operation;

boolean_operation: (unary_operators)? expr (binary_operators expr)*  ;

expr:    (parenthesisopen expr parenthesisclose)  
	  |  expr  binary_operators expr 
	  |  unary_operators expr
	  |  input
	  |  constant
	  ;
    
pragma_vhdl_pre_entity_directive                : PRAGMA_VHDL_PRE_ENTITY_DIRECTIVE 
				                                  PRAGMA_WITH_BEGINING_AND_ENDING;
pragma_vhdl_entity_directive                    : PRAGMA_VHDL_ENTITY_DIRECTIVE 
						                          interface_port_declaration
                                                  (interface_port_declaration)*
				                                  PRAGMA_ENDING;
pragma_vhdl_architecture_pre_begin_directive    : PRAGMA_VHDL_ARCHITECTURE_PRE_BEGIN_DIRECTIVE 
				                                  PRAGMA_WITH_BEGINING_AND_ENDING;
pragma_vhdl_architecture_post_begin_directive   : PRAGMA_VHDL_ARCHITECTURE_POST_BEGIN_DIRECTIVE 
				                                  PRAGMA_WITH_BEGINING_AND_ENDING;
pragma_vhdl_promote_to_buffer_directive         : PRAGMA_VHDL_PROMOTE_TO_BUFFER_DIRECTIVE 
						                          input_or_output_to_promote_to_buffer (COMMA input_or_output_to_promote_to_buffer)*
				                                  PRAGMA_ENDING;
pragma_vhdl_demote_to_signal_directive          : PRAGMA_VHDL_DEMOTE_TO_SIGNAL_DIRECTIVE 
				        	                      input_or_output_to_demote_to_signal (COMMA input_or_output_to_demote_to_signal)*
				                                  PRAGMA_ENDING;
pragma_vhdl_allow_automatic_buffering           : PRAGMA_VHDL_ALLOW_AUTOMATIC_BUFFERING_DIRECTIVE ;

pragma_vhdl_set_bit_size_for_output_state_number: PRAGMA_VHDL_SET_BIT_SIZE_FOR_OUTPUT_STATE_NUMBER
						                          bit_size_for_output_state_number
					                              PRAGMA_ENDING;
pragma_vhdl_init_testbench                      : PRAGMA_VHDL_INIT_TESTBENCH_BEGIN_DIRECTIVE 
				                                  PRAGMA_WITH_BEGINING_AND_ENDING;					                              
pragma_vhdl_testbench                           : PRAGMA_VHDL_TESTBENCH_BEGIN_DIRECTIVE 
				                                  PRAGMA_WITH_BEGINING_AND_ENDING;
pragma_vhdl_testbench_pre_begin_directive       : PRAGMA_VHDL_TESTBENCH_PRE_BEGIN_DIRECTIVE 
				                                  PRAGMA_WITH_BEGINING_AND_ENDING;				                                  
pragma_vhdl_generic_directive                   : PRAGMA_VHDL_GENERIC_DIRECTIVE 
                                                  generic_declaration
					                              (generic_declaration)*
				                                  PRAGMA_ENDING;
pragma_dot_global_directive                     : PRAGMA_DOT_GLOBAL_DIRECTIVE  
                                                  PRAGMA_WITH_BEGINING_AND_ENDING;
bit_size_for_output_state_number:                 positive_integer;

input_or_output_to_promote_to_buffer: signal_id   ;
input_or_output_to_demote_to_signal: signal_id   ;
 
//  {System.out.println("found an pragma");} -> channel(PRAGMAS_CHANNEL);
// PragmaDirective   : '#pragma'  ~[\r\n]*   {System.out.println("found an pragma");} -> channel(PRAGMAS_CHANNEL);
// PragmaMultilineDirective   : '#pragma{'  .*? '#pragma}'   {System.out.println("found a multiline pragma");} -> channel(PRAGMAS_CHANNEL);

//parsing of added entity pragmas

generic_declaration  : generic_id COLON type_generic COLONEQUAL default_generic SEMICOLON;


generic_id: id | R | S | M | I | F;   //used for definition of bus ex n in (n-1 downto 0) 

type_generic: integer | positive | natural | real;

default_generic: id | number | positive_integer;

interface_port_declaration  : interface_name (COMMA interface_name)* COLON interface_port_mode interface_port_type SEMICOLON;
 
interface_port_mode   : in | out | inout | buffer | linkage ;

interface_port_type:    interface_port_type_std_logic
		              | interface_port_type_std_logic_vector  PARENTHESISOPEN bus_begin to_or_down_to bus_end PARENTHESISCLOSE;
to_or_down_to :   to | downto;		      
 
number_of_bit_with_optional_generic_prefix:    (unary_num_operators)? expr_num (binary_num_operators expr)*  ;

expr_num:    (parenthesisopen expr_num parenthesisclose)  
	  |  expr_num  binary_num_operators expr_num 
	  |  unary_num_operators expr_num
	  |  generic_id
	  |  constant
	  ;
      
unary_num_operators :   PLUS | MINUS   ;//to complete with all needed operators 
binary_num_operators :  PLUS | MINUS | STAR | SLASH ;//to complete with all needed operators 
          
bus_begin:  number_of_bit_with_optional_generic_prefix;
bus_end:  number_of_bit_with_optional_generic_prefix;

interface_port_type_std_logic: std_logic;
interface_port_type_std_logic_vector: std_logic_vector ;

interface_name : signal_id ;




 

