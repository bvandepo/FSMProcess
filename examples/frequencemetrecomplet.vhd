
library	ieee;
use		ieee.std_logic_1164.all;
use		ieee.std_logic_unsigned.all;
use		ieee.std_logic_arith.all;

library work;
use work.multiplier_generic_N_32_pack.all;	
use work.diviseur_generic_N_64_pack.all;
use work.bin2bcd_generic_N_64_pack.all;
use work.frequencemetre2_pack.all;
use work.frequencemetrebcmd_pack.all;



entity frequencemetrecomplet is
port (
		CK               : in     std_logic;
		RESETN           : in     std_logic;
		SRESET           : in     std_logic;
		STATE_NUMBER     : out    std_logic_vector( 2 downto 0);
		SIG              : in     std_logic;
		DEMAND_MEASURE   : in     std_logic;
		RESULT_AVAILABLE : out    std_logic;
		RESULT_BCD       : out    std_logic_vector ( 76-1  downto  0 ) 
		);
end frequencemetrecomplet;

architecture ar of frequencemetrecomplet is
signal cpt_time, cpt_period: std_logic_vector(31 downto 0);
signal cpt_time64:  std_logic_vector(63 downto 0);
signal MUL_START:    std_logic;
signal MUL_RESULT:  std_logic_vector(63 downto 0);
signal MUL_RESULT_AVAILABLE:  std_logic;
signal DIV_START:  std_logic;
signal DIV_ERROR:  std_logic;  --todo
signal QUOTIENT,REMAINDER:  std_logic_vector(63 downto 0);
signal DIV_RESULT_AVAILABLE:  std_logic;
signal BIN2BCD_START:  std_logic;
signal BCD_VALUE:  std_logic_vector( 76-1 downto 0);
signal BIN2BCD_DONE:  std_logic;
signal FREQ_SRESET:  std_logic;
signal FREQ_OVERFLOW:  std_logic; --todo
signal FREQ_RESULT_AVAILABLE:  std_logic;
begin
--------------------------------------------------------------------------------
multiplier_generic_N_32_u0 : multiplier_generic_N_32
port map(
		CK => CK,
		RESETN => RESETN,
		state_number =>  open,
		A => cpt_period,
		B => x"0000C350",   --50000
--		B => x"00000032",   --50
		START => MUL_START,
		COMPUTE =>  open,
		MEMA =>  open,
		RESULT => MUL_RESULT,
		RESULT_AVAILABLE => MUL_RESULT_AVAILABLE,
		TEMP => open);

cpt_time64<="00000000000000000000000000000000" & cpt_time;
--ghdl does not support B => "00000000000000000000000000000000" & cpt_time,  ???
	
diviseur_generic_N_64_u0 : diviseur_generic_N_64
port map(
		CK => CK,
		RESETN => RESETN,
		state_number =>    open,
		A => MUL_RESULT,
		B => cpt_time64,
		START =>  DIV_START,
		COMPUTE => open,
		ERROR => DIV_ERROR,
		QUOTIENT => QUOTIENT,
		REMAINDER => REMAINDER,
		RESULT_AVAILABLE =>  DIV_RESULT_AVAILABLE);

bin2bcd_generic_N_64_u0 : bin2bcd_generic_N_64
port map(
		CK => CK,
		RESETN => RESETN,
		state_number =>  open,
		BIN_VALUE => QUOTIENT(63 downto 0), -- x"123456789ABCDEF0"
		START => BIN2BCD_START,
		BCD_VALUE => BCD_VALUE,
		COMPUTE => open,
		DONE => BIN2BCD_DONE,
		INCREMENTATION => open,
		LOAD => open,
		TMP_VALUE => open);

RESULT_BCD<=BCD_VALUE;
RESULT_AVAILABLE<=BIN2BCD_DONE;  --TODO: foireux!!!!

frequencemetre2_u0 : frequencemetre2
port map(
		CK => CK,
		RESETN => RESETN,
		state_number =>  open,
	DESIRED_WINDOW => x"00001388",  --5000 pour 100us
	--	DESIRED_WINDOW => x"004C4B40", --5.10^6 pour 100ms 
		MAX_WINDOW => x"02FAF080", --5.10^7 pour 1sec
		SIG => SIG,
		SRESET => FREQ_SRESET,
		CPT_PERIOD => open,
		CPT_TIME => open,
		CPT_TIME_ELAPSED => open,
		EN_CPT_PERIODS => open,
		FREQUENCY => open,
		FREQUENCY2 => open,
		OVERFLOW =>open, --signal memorisé
		PERIODS_OVERFLOW => open,
		RESULT_AVAILABLE => FREQ_RESULT_AVAILABLE,
		RISING_EDGE_SIG => open,
		SAVED_CPT_PERIOD => CPT_PERIOD,
		SAVED_CPT_TIME => CPT_TIME,
		SAVE_AND_COMPUTE => open,
		SRESET_CPT_PERIODS => open,
		SRESET_CPT_TIME => open,
		TIME_OVERFLOW =>  FREQ_OVERFLOW);
		
		
frequencemetrebcmd_u0 : frequencemetrebcmd
port map(
		CK => CK,
		RESETN => RESETN,
		state_number =>  state_number,
		BIN2BCD_DONE => BIN2BCD_DONE,
		DEMAND_MEASURE => DEMAND_MEASURE,
		DIV_RESULT_AVAILABLE => DIV_RESULT_AVAILABLE,
		FREQ_RESULT_AVAILABLE => FREQ_RESULT_AVAILABLE,
		MUL_RESULT_AVAILABLE => MUL_RESULT_AVAILABLE,
		SRESET => SRESET,
		BIN2BCD_START => BIN2BCD_START, 
		DIV_START => DIV_START,
		FREQ_SRESET => FREQ_SRESET,
		MUL_START => MUL_START);
		
end ar;

