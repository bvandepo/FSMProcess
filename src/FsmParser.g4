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
     ;

pragma_dot_directive:
       pragma_dot_global_directive;
   
multi_transitions_directive:
SHARP (multi_transitions_base_state_name)?  PARENTHESISOPEN multi_transitions_first_state_number to multi_transitions_last_state_number PARENTHESISCLOSE ( STAR  multi_transitions_priority)? (CONDITION condition_multi_transitions)?  SEMICOLON;
multi_transitions_base_state_name:id;
multi_transitions_first_state_number:positive_integer;
multi_transitions_last_state_number:positive_integer;
multi_transitions_priority:positive_integer;
condition_multi_transitions: element ( element)*  ;

multi_transitions_to_same_directive:
SHARP (multi_transitions_base_state_name)?  PARENTHESISOPEN multi_transitions_first_state_number to multi_transitions_last_state_number PARENTHESISCLOSE ARROW multi_transitions_destination_state ( STAR  multi_transitions_priority)? (CONDITION condition_multi_transitions)?  SEMICOLON;
multi_transitions_destination_state: id;

 
state   :  id ( COLON state_action)* SEMICOLON    ;         	//state (with  action(s))
clock_definition: SLASH  input_clock SEMICOLON ;
input_clock:  id;
reset_asynchronous:   DOUBLEARROW id (CONDITION condition_reset_asynchronous)? ( COMMA level_reset_asynchronous)?  (COLON action_reset_asynchronous)* SEMICOLON  ;        

level_reset_asynchronous : positive_integer ;

condition_reset_asynchronous: input_async_reset;

input_async_reset: id;


action_reset_asynchronous : action_id_reset_asynchronous  ( EQUAL  action_expression_reset_asynchronous)? ;

action_expression_reset_asynchronous : element ( element)*  ;
 

repeatedly_action : REPEATACTION   (action_type COMMA)? action_id ( EQUAL action_expression)? SEMICOLON ;

state_action :  (action_type COMMA)? action_id (EQUAL action_expression)? ;
transition_action :  (action_type COMMA)? action_id (EQUAL action_expression)? ;
 
action_type : tr|ts|tm|ti|tf;

transition   	  :  id  ARROW  id ( STAR  transition_priority)? ('?' condition)? (COLON transition_action)* SEMICOLON   ;         	//transition (with  action(s)) 
reset_transition  :   '->' id (STAR reset_transition_priority)? ('?' condition) (COLON transition_action)* SEMICOLON   ;         	//transition (with  action(s)), the condition is mandatory for reset transition


transition_priority: positive_integer;

reset_transition_priority: positive_integer;

condition :  element ( element)*  ;
expression :  id ( id)*  ;

action_id_reset_asynchronous:  id  | positive_integer
   ;
action_id: id  | positive_integer
   ;

action_expression:  element ( element)*  ;

//operators :  AND | OR | XOR | XNOR | NOT | PLUS | MINUS | STAR | SLASH | DOUBLEEQUAL | NOTEQUAL ;//to complete with all needed operators 
operators :  and | or | xor | xnor | not | plus | minus | star | slash | doubleequal | notequal ;//to complete with all needed operators 


and         :  A N D;
nand        :  N A N D;
or          :  O R ;
nor         :  N O R ;
xor         :  X O R;
xnor        :  X N O R ;
not         :  N O T ;
plus        :  PLUS ; 
minus       :  MINUS ; 
doubleequal :  DOUBLEEQUAL;
notequal    :  NOTEQUAL;
star        :  STAR;
slash       :  SLASH;
 

tr: R;
ts: S;
tm: M;
ti: I;
tf: F;


in : I N;
out : O U T;
inout : I N O U T;
buffer : B U F F E R;
linkage : L I N K A G E; 

to: T O ;
downto:  D O W N T O;		      

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

element: operators  | constant | input | parenthesis;

parenthesis : PARENTHESISOPEN | PARENTHESISCLOSE ;
	 
constant: positive_integer ;

input: id;

letter: A | B | C | D | E | F | G | H | I | J | K | L | M | N | O | P | Q | R | S | T | U | V | W | X | Y | Z;

id : ( (letter | DIGIT) ( letter | DIGIT | UNDERSCORE)* )  | tr | ts | tm | ti | tf   ;
//  id :  ( letter | DIGIT | UNDERSCORE)+ ; 
    
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
pragma_vhdl_testbench                           : PRAGMA_VHDL_TESTBENCH_BEGIN_DIRECTIVE 
				                                  PRAGMA_WITH_BEGINING_AND_ENDING;
pragma_vhdl_generic_directive                   : PRAGMA_VHDL_GENERIC_DIRECTIVE 
                                                  generic_declaration
					                              (generic_declaration)*
				                                  PRAGMA_ENDING;
pragma_dot_global_directive                     : PRAGMA_DOT_GLOBAL_DIRECTIVE  
                                                  PRAGMA_WITH_BEGINING_AND_ENDING;
bit_size_for_output_state_number:                 positive_integer;

input_or_output_to_promote_to_buffer: id   ;
input_or_output_to_demote_to_signal: id   ;
 
//  {System.out.println("found an pragma");} -> channel(PRAGMAS_CHANNEL);
// PragmaDirective   : '#pragma'  ~[\r\n]*   {System.out.println("found an pragma");} -> channel(PRAGMAS_CHANNEL);
// PragmaMultilineDirective   : '#pragma{'  .*? '#pragma}'   {System.out.println("found a multiline pragma");} -> channel(PRAGMAS_CHANNEL);

//parsing of added entity pragmas

generic_declaration  : id_generic COLON type_generic COLONEQUAL default_generic SEMICOLON;

id_generic: id | tr | ts | tm | ti | tf;

type_generic: integer | positive | natural | real;

default_generic: id | number | positive_integer;

interface_port_declaration  : interface_name (COMMA interface_name)* COLON interface_port_mode interface_port_type SEMICOLON;
 
interface_port_mode   : in | out | inout | buffer | linkage ;

interface_port_type:    interface_port_type_std_logic
		              | interface_port_type_std_logic_vector  PARENTHESISOPEN bus_begin to_or_down_to bus_end PARENTHESISCLOSE;
to_or_down_to :   to | downto;		      

bus_begin:   positive_integer 
          |  ( id_generic  (minus| plus)? (negative_integer | positive_integer) );  //if the - or + sign is attached to the number
bus_end:     positive_integer 
          |  ( id_generic  (minus| plus)? (negative_integer| positive_integer) );  //if the - or + sign is attached to the 

interface_port_type_std_logic: std_logic;
interface_port_type_std_logic_vector: std_logic_vector ;

interface_name : id | tr | ts | tm | ti | tf;




 

