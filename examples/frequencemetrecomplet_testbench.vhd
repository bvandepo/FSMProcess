--------------------------------------------------------------------------------
-- Company: 
-- Engineer:
--
-- Create Date:   21:00:14 02/17/2016
-- Design Name:   
-- Module Name:   /home/bvandepo/antlr/fsm/examples/frequencemetrecomplet_testbench.vhd
-- Project Name:  testfsm
-- Target Device:  
-- Tool versions:  
-- Description:   
-- 
-- VHDL Test Bench Created by ISE for module: frequencemetrecomplet
-- 
-- Dependencies:
-- 
-- Revision:
-- Revision 0.01 - File Created
-- Additional Comments:
--
-- Notes: 
-- This testbench has been automatically generated using types std_logic and
-- std_logic_vector for the ports of the unit under test.  Xilinx recommends
-- that these types always be used for the top-level I/O of a design in order
-- to guarantee that the testbench will bind correctly to the post-implementation 
-- simulation model.
--------------------------------------------------------------------------------
LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
 
-- Uncomment the following library declaration if using
-- arithmetic functions with Signed or Unsigned values
--USE ieee.numeric_std.ALL;
 
ENTITY frequencemetrecomplet_testbench IS
END frequencemetrecomplet_testbench;
 
ARCHITECTURE behavior OF frequencemetrecomplet_testbench IS 
 
    -- Component Declaration for the Unit Under Test (UUT)
 
    COMPONENT frequencemetrecomplet
    PORT(
         CK : IN  std_logic;
         RESETN : IN  std_logic;
         SRESET : IN  std_logic;
         STATE_NUMBER : OUT  std_logic_vector(2 downto 0);
         SIG : IN  std_logic;
         DEMAND_MEASURE : IN  std_logic;
         RESULT_AVAILABLE : OUT  std_logic;
         RESULT_BCD : OUT  std_logic_vector(39 downto 0)
        );
    END COMPONENT;
    

   --Inputs
   signal CK : std_logic := '0';
   signal RESETN : std_logic := '0';
   signal SRESET : std_logic := '0';
   signal SIG : std_logic := '0';
   signal DEMAND_MEASURE : std_logic := '0';

 	--Outputs
   signal STATE_NUMBER : std_logic_vector(2 downto 0);
   signal RESULT_AVAILABLE : std_logic;
   signal RESULT_BCD : std_logic_vector(39 downto 0);

--signals for current state name visualization:  
signal state_name : std_logic_vector(103 downto 0 );

   -- No clocks detected in port list. Replace <clock> below with 
   -- appropriate port name 
 
   constant CK_period : time := 20 ns;
 
BEGIN
 -----------------------------------------------------------------------------------------------------------
process (state_number)
begin
    case state_number is
      when "000"    => state_name <= "01000010010010010100111000110010010000100100001101000100001000000010000000100000001000000010000000100000"; -- BIN2BCD      
      when "001"    => state_name <= "01000011010011110101010101001110010101000100100101001110010001110010000000100000001000000010000000100000"; -- COUNTING     
      when "010"    => state_name <= "01000100010010010101011000100000001000000010000000100000001000000010000000100000001000000010000000100000"; -- DIV          
      when "011"    => state_name <= "01001101010101010100110000100000001000000010000000100000001000000010000000100000001000000010000000100000"; -- MUL          
      when "100"    => state_name <= "01010111010000010100100101010100010111110101010001001111010111110101001101010100010000010101001001010100"; -- WAIT_TO_START
      when others   => state_name <= "00100001010001010111001001110010011011110111001000100000001000000010000000100000001000000010000000100000"; -- !Error       
       end case;
end process;
-----------------------------------------------------------------------------------------------------------
	-- Instantiate the Unit Under Test (UUT)
   uut: frequencemetrecomplet PORT MAP (
          CK => CK,
          RESETN => RESETN,
          SRESET => SRESET,
          STATE_NUMBER => STATE_NUMBER,
          SIG => SIG,
          DEMAND_MEASURE => DEMAND_MEASURE,
          RESULT_AVAILABLE => RESULT_AVAILABLE,
          RESULT_BCD => RESULT_BCD
        );

   -- Clock process definitions
   CK_process :process
   begin
		CK <= '0';
		wait for CK_period/2;
		CK <= '1';
		wait for CK_period/2;
   end process;
 

   -- Stimulus process
   stim_proc: process
   begin		
      -- hold reset state for 100 ns.
		RESETN<='0';
      wait for 100 ns;	
	   RESETN<='1';
      wait for CK_period*10;

      -- insert stimulus here 
SIG<='0';
 SRESET<='0';
	wait for ck_period*1;
	DEMAND_MEASURE<='1';
	wait for ck_period*1;
	DEMAND_MEASURE<='0';
	
	
	--periode du signal à mesurer:  14,071 periodes ck systeme (à 20ns)= 281,42ns
        --frequence = 10^9 / (20*(8,256+5,815)) =3,55340771800156349939592068793973420510269348305  MhZ
	
for i in 0 to 100000000 loop
	sig<='1';
 --	wait for ck_period*8.256;
	wait for ck_period*123.256;

	sig<='0';
--	wait for ck_period*5.815;
	wait for ck_period*456.815;



END LOOP;
	wait for ck_period*80000000;
---------------------------------------	




      wait;
   end process;

END;
