parser grammar FsmParser;
// now I use non-combined grammar, the lexer rules are in the ModeFsmLexer.g4 file
// p277: Modes are not allowed within combined grammars, just lexer grammars.
// example: lexmagic/XMLParser.g4

options { tokenVocab=FsmLexer; } // use tokens from ModeFsmLexer.g4


fsmfile  : (line)+ ;          //a state machine is many lines, the fsm name is mandatory as the first entry

line : state 
     | transition
     | reset_transition
     | repeatedly_action
     | reset_asynchronous
     | clock_definition
     | pragma_directive;

pragma_directive:
       pragma_vhdl_directive;

pragma_vhdl_directive:
       pragma_vhdl_pre_entity_directive
     | pragma_vhdl_entity_directive
     | pragma_vhdl_architecture_pre_begin_directive
     | pragma_vhdl_architecture_post_begin_directive
     | pragma_vhdl_promote_buffered_directive
     | pragma_vhdl_demote_to_signal_directive
     | pragma_vhdl_allow_automatic_buffering
     | pragma_vhdl_set_bit_size_for_output_state_number
     ;

 
state   :  id ( COLON state_action)* SEMICOLON    ;         	//state (with  action(s))
clock_definition: SLASH  input_clock SEMICOLON ;
input_clock:  id;
reset_asynchronous:   DOUBLEARROW id (CONDITION condition_reset_asynchronous)? ( COMMA level_reset_asynchronous)?  (COLON action_reset_asynchronous)* SEMICOLON  ;        

level_reset_asynchronous : NUMBER ;

condition_reset_asynchronous: input_async_reset;

input_async_reset: id;


action_reset_asynchronous : action_id_reset_asynchronous  ( EQUAL  action_expression_reset_asynchronous)? ;

action_expression_reset_asynchronous : element ( element)*  ;
 

repeatedly_action : REPEATACTION   (action_type COMMA)? action_id ( EQUAL action_expression)? SEMICOLON ;

state_action :  (action_type COMMA)? action_id (EQUAL action_expression)? ;
transition_action :  (action_type COMMA)? action_id (EQUAL action_expression)? ;
 
action_type : R|S|M|I|F;

transition   	  :  id  ARROW  id ( STAR  transition_priority)? ('?' condition)? (COLON transition_action)* SEMICOLON   ;         	//transition (with  action(s)) 
reset_transition  :   '->' id (STAR reset_transition_priority)? ('?' condition) (COLON transition_action)* SEMICOLON   ;         	//transition (with  action(s)), the condition is mandatory for reset transition


transition_priority: NUMBER;

reset_transition_priority: NUMBER;

condition :  element ( element)*  ;
expression :  id ( id)*  ;

action_id_reset_asynchronous:  ID  | NUMBER
   ;
action_id: ID  | NUMBER
   ;

action_expression:  element ( element)*  ;

operators :  AND | OR | XOR | XNOR | NOT | PLUS | MINUS | STAR | SLASH | DOUBLEEQUAL | NOTEQUAL ;//to complete with all needed operators 

element: operators  | constant | input | parenthesis;

parenthesis : PARENTHESISOPEN | PARENTHESISCLOSE ;
	 
constant: NUMBER ;

input: id;


id : ID  | NUMBER
   ;


//id : ID | STRING | HTML_STRING | NUMBER
//   ;


//pragma_directive   : PRAGMA;


pragma_vhdl_pre_entity_directive              : PRAGMA_VHDL_PRE_ENTITY_DIRECTIVE 
				                PRAGMA_WITH_BEGINING_AND_ENDING;
pragma_vhdl_entity_directive                  : PRAGMA_VHDL_ENTITY_DIRECTIVE 
				                PRAGMA_WITH_BEGINING_AND_ENDING;
pragma_vhdl_architecture_pre_begin_directive  : PRAGMA_VHDL_ARCHITECTURE_PRE_BEGIN_DIRECTIVE 
				                PRAGMA_WITH_BEGINING_AND_ENDING;
pragma_vhdl_architecture_post_begin_directive : PRAGMA_VHDL_ARCHITECTURE_POST_BEGIN_DIRECTIVE 
				                PRAGMA_WITH_BEGINING_AND_ENDING;
pragma_vhdl_promote_buffered_directive        : PRAGMA_VHDL_PROMOTE_BUFFERED_DIRECTIVE 
						output_to_promote_buffered (COMMA output_to_promote_buffered)?
				                PRAGMA_ENDING;
pragma_vhdl_demote_to_signal_directive        : PRAGMA_VHDL_DEMOTE_TO_SIGNAL_DIRECTIVE 
				        	output_to_demote_to_signal (COMMA output_to_demote_to_signal)?
				                PRAGMA_ENDING;
pragma_vhdl_allow_automatic_buffering         : PRAGMA_VHDL_ALLOW_AUTOMATIC_BUFFERING_DIRECTIVE ;
pragma_vhdl_set_bit_size_for_output_state_number: PRAGMA_VHDL_SET_BIT_SIZE_FOR_OUTPUT_STATE_NUMBER 
						  bit_size_for_output_state_number
						  PRAGMA_ENDING;

bit_size_for_output_state_number: NUMBER;

output_to_promote_buffered: ID   ;
output_to_demote_to_signal: ID   ;



//pragma_directive   : BEGIN_PRAGMA  END_PRAGMA ; 
//pragma_directive   : BEGIN_PRAGMA PRAGMADATA END_PRAGMA ; 


//  {System.out.println("found an pragma");} -> channel(PRAGMAS_CHANNEL);


// PragmaDirective   : '#pragma'  ~[\r\n]*   {System.out.println("found an pragma");} -> channel(PRAGMAS_CHANNEL);
// PragmaMultilineDirective   : '#pragma{'  .*? '#pragma}'   {System.out.println("found a multiline pragma");} -> channel(PRAGMAS_CHANNEL);

 














 

