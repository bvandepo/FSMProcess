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
     | multi_transitions_directive
     | multi_transitions_to_same_directive
     | pragma_directive;

pragma_directive:
       pragma_vhdl_directive;

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
     ;



multi_transitions_directive:
SHARP (multi_transitions_base_state_name)?  PARENTHESISOPEN multi_transitions_first_state_number TO multi_transitions_last_state_number PARENTHESISCLOSE ( STAR  multi_transitions_priority)? (CONDITION condition_multi_transitions)?  SEMICOLON;
multi_transitions_base_state_name:id;
multi_transitions_first_state_number:POSITIVE_INTEGER;
multi_transitions_last_state_number:POSITIVE_INTEGER;
multi_transitions_priority:POSITIVE_INTEGER;
condition_multi_transitions: element ( element)*  ;

multi_transitions_to_same_directive:
SHARP (multi_transitions_base_state_name)?  PARENTHESISOPEN multi_transitions_first_state_number TO multi_transitions_last_state_number PARENTHESISCLOSE ARROW multi_transitions_destination_state ( STAR  multi_transitions_priority)? (CONDITION condition_multi_transitions)?  SEMICOLON;
multi_transitions_destination_state: id;

 
state   :  id ( COLON state_action)* SEMICOLON    ;         	//state (with  action(s))
clock_definition: SLASH  input_clock SEMICOLON ;
input_clock:  id;
reset_asynchronous:   DOUBLEARROW id (CONDITION condition_reset_asynchronous)? ( COMMA level_reset_asynchronous)?  (COLON action_reset_asynchronous)* SEMICOLON  ;        

level_reset_asynchronous : POSITIVE_INTEGER ;

condition_reset_asynchronous: input_async_reset;

input_async_reset: id;


action_reset_asynchronous : action_id_reset_asynchronous  ( EQUAL  action_expression_reset_asynchronous)? ;

action_expression_reset_asynchronous : element ( element)*  ;
 

repeatedly_action : REPEATACTION   (action_type COMMA)? action_id ( EQUAL action_expression)? SEMICOLON ;

state_action :  (action_type COMMA)? action_id (EQUAL action_expression)? ;
transition_action :  (action_type COMMA)? action_id (EQUAL action_expression)? ;
 
action_type : TR|TS|TM|TI|TF;

transition   	  :  id  ARROW  id ( STAR  transition_priority)? ('?' condition)? (COLON transition_action)* SEMICOLON   ;         	//transition (with  action(s)) 
reset_transition  :   '->' id (STAR reset_transition_priority)? ('?' condition) (COLON transition_action)* SEMICOLON   ;         	//transition (with  action(s)), the condition is mandatory for reset transition


transition_priority: POSITIVE_INTEGER;

reset_transition_priority: POSITIVE_INTEGER;

condition :  element ( element)*  ;
expression :  id ( id)*  ;

action_id_reset_asynchronous:  ID  | POSITIVE_INTEGER
   ;
action_id: ID  | POSITIVE_INTEGER
   ;

action_expression:  element ( element)*  ;

operators :  AND | OR | XOR | XNOR | NOT | PLUS | MINUS | STAR | SLASH | DOUBLEEQUAL | NOTEQUAL ;//to complete with all needed operators 

element: operators  | constant | input | parenthesis;

parenthesis : PARENTHESISOPEN | PARENTHESISCLOSE ;
	 
constant: POSITIVE_INTEGER ;

input: id;


id : ID  | POSITIVE_INTEGER | TR | TS | TM | TI | TF
   ;


//id : ID | STRING | HTML_STRING | NUMBER
//   ;


//pragma_directive   : PRAGMA;


pragma_vhdl_pre_entity_directive              : PRAGMA_VHDL_PRE_ENTITY_DIRECTIVE 
				                PRAGMA_WITH_BEGINING_AND_ENDING;
 
pragma_vhdl_entity_directive                  : PRAGMA_VHDL_ENTITY_DIRECTIVE 
						interface_port_declaration
                                                (interface_port_declaration)*
				                PRAGMA_ENDING;
pragma_vhdl_architecture_pre_begin_directive  : PRAGMA_VHDL_ARCHITECTURE_PRE_BEGIN_DIRECTIVE 
				                PRAGMA_WITH_BEGINING_AND_ENDING;
pragma_vhdl_architecture_post_begin_directive : PRAGMA_VHDL_ARCHITECTURE_POST_BEGIN_DIRECTIVE 
				                PRAGMA_WITH_BEGINING_AND_ENDING;
pragma_vhdl_promote_to_buffer_directive       : PRAGMA_VHDL_PROMOTE_TO_BUFFER_DIRECTIVE 
						input_or_output_to_promote_to_buffer (COMMA input_or_output_to_promote_to_buffer)*
				                PRAGMA_ENDING;
pragma_vhdl_demote_to_signal_directive        : PRAGMA_VHDL_DEMOTE_TO_SIGNAL_DIRECTIVE 
				        	input_or_output_to_demote_to_signal (COMMA input_or_output_to_demote_to_signal)*
				                PRAGMA_ENDING;
pragma_vhdl_allow_automatic_buffering         : PRAGMA_VHDL_ALLOW_AUTOMATIC_BUFFERING_DIRECTIVE ;
pragma_vhdl_set_bit_size_for_output_state_number: PRAGMA_VHDL_SET_BIT_SIZE_FOR_OUTPUT_STATE_NUMBER 
						  bit_size_for_output_state_number
						  PRAGMA_ENDING;
pragma_vhdl_testbench                         : PRAGMA_VHDL_TESTBENCH_BEGIN_DIRECTIVE 
				                PRAGMA_WITH_BEGINING_AND_ENDING;

pragma_vhdl_generic_directive                 : PRAGMA_VHDL_GENERIC_DIRECTIVE 
                                                generic_declaration
						(generic_declaration)*
				                PRAGMA_ENDING;



bit_size_for_output_state_number: POSITIVE_INTEGER;

input_or_output_to_promote_to_buffer: ID   ;
input_or_output_to_demote_to_signal: ID   ;
 

//pragma_directive   : BEGIN_PRAGMA  END_PRAGMA ; 
//pragma_directive   : BEGIN_PRAGMA PRAGMADATA END_PRAGMA ; 


//  {System.out.println("found an pragma");} -> channel(PRAGMAS_CHANNEL);


// PragmaDirective   : '#pragma'  ~[\r\n]*   {System.out.println("found an pragma");} -> channel(PRAGMAS_CHANNEL);
// PragmaMultilineDirective   : '#pragma{'  .*? '#pragma}'   {System.out.println("found a multiline pragma");} -> channel(PRAGMAS_CHANNEL);

 

//parsing of added entity pragmas

generic_declaration  : id_generic COLON type_generic COLONEQUAL default_generic SEMICOLON;

id_generic: ID | TR | TS | TM | TI | TF;
type_generic: INTEGER;
default_generic: ID | NUMBER | POSITIVE_INTEGER;


interface_port_declaration  : interface_name (COMMA interface_name)* COLON interface_port_mode interface_port_type SEMICOLON;
 

interface_port_mode
  : IN
  | OUT
  | INOUT
  | BUFFER
  | LINKAGE
  ;

interface_port_type:   interface_port_type_std_logic
		     | interface_port_type_std_logic_vector  PARENTHESISOPEN bus_begin to_or_down_to bus_end PARENTHESISCLOSE;


to_or_down_to :   TO
                | DOWNTO;		      

bus_begin: POSITIVE_INTEGER; //TODO:gérer N-1 downto 0 par ex...
bus_end: POSITIVE_INTEGER;

interface_port_type_std_logic: STD_LOGIC;
interface_port_type_std_logic_vector: STD_LOGIC_VECTOR ;

interface_name : ID | TR | TS | TM | TI | TF;




 

